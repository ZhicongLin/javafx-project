package com.mongodb.sync.data.service.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.sync.data.entity.RepoFile;
import com.mongodb.sync.data.event.DiffFileEvent;
import com.mongodb.sync.data.event.SaveEvent;
import com.mongodb.sync.data.mongo.MongoConnect;
import com.mongodb.sync.data.mongo.MongoInfo;
import com.mongodb.sync.data.repository.FileUpgradeFactory;
import com.mongodb.sync.data.repository.RepoFileRepository;
import com.mongodb.sync.data.service.RepoFileService;
import com.mongodb.sync.data.vo.MongoFileEntity;
import com.mongodb.sync.data.vo.Server;
import com.mongodb.sync.module.view.TabView;

import lombok.extern.slf4j.Slf4j;

/**
 * Description: 同步数据操作实现
 *
 * @author linzc
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本           修改人       修改日期         修改内容
 * 2020/5/28.1       linzc    2020/5/28           Create
 * </pre>
 * @date 2020/5/28
 */
@Slf4j
@Service
@Transactional
public class RepoFileServiceImpl implements RepoFileService {

    @Resource
    private ApplicationEventPublisher publisher;
    @Resource
    private RepoFileRepository repoFileRepository;
    /**
     * 同步数据
     *
     * @param sourceServer 源数据库信息
     * @param targetServer 目标数据库信息
     * @param myTab
     * @param startTime
     * @param endTime
     */
    @Override
    public void sync(Server sourceServer, Server targetServer, TabView myTab, Timestamp startTime, Timestamp endTime) {
        myTab.startSynData();
        long beginTime = System.currentTimeMillis();
        List<RepoFile> repoFiles = this.repoFileRepository
                .findAllByCreateTimeGreaterThanAndCreateTimeLessThanEqualOrderByCreateTime(startTime, endTime);
        if (repoFiles.isEmpty()) {
            myTab.emptySynData();
            return;
        }
        myTab.addMessage("查到等待执行数据" + repoFiles.size() + "条", true);
        myTab.getProgress().setTotalSize(repoFiles.size());
        Vector<String> uploadIds = new Vector<>();
        repoFiles.parallelStream().forEach(repoFile -> {
            try {
                if (!myTab.isDoWork()) {
                    return;
                }
                myTab.getProgress().getDoUuid().add(repoFile.getUuid());
                final String physicalFileId = repoFile.getPhysicalFileId();
                final MongoInfo sourceInfo = MongoConnect.connect(sourceServer);
                if (sourceInfo == null) {
                    return;
                }
                final String filePath = getFilePath(sourceServer.getTenant());
                final MongoFileEntity sourceEntity = findFileById(sourceInfo.getDb(), filePath, physicalFileId);
                final long time = System.currentTimeMillis();
                if (sourceEntity == null) { //空文件，直接反馈，并保存空文件数据
                    sendSimpleEvent(myTab, physicalFileId, repoFile.getUuid(), DiffFileEvent.NULL, repoFile.getFileName());
                } else {
                    sourceEntity.setRepoFile(repoFile);
                    log.info("开始同步【{}】, dbSize={}, mongoDbSize={}", sourceEntity.getFileName(),
                            sourceEntity.getFileChunkSize(), sourceEntity.getChunkSize());
                    if (sourceEntity.getLength() != repoFile.getFileSize()) { //文件不一致
                        sendDiffEvent(myTab, repoFile, physicalFileId, sourceEntity);
                    } else {
                        if (uploadIds.contains(physicalFileId)) {
                            myTab.getProgress().getDoSuccess().add(repoFile.getUuid());
                            return;
                        }
                        uploadIds.add(physicalFileId);
                        saveFile(targetServer, sourceEntity, myTab, time);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                myTab.addMessage("同步异常【" + repoFile.getFileName() + "】" + e.getMessage(), false);
            }
        });
        publisher.publishEvent(new SaveEvent(myTab, beginTime));
    }

    private String getFilePath(String tenant) {
        if (StringUtils.isBlank(tenant)) {
            return "fs";
        }
        return "fs." + tenant;
    }

    private MongoFileEntity findFileById(DB db, String filePath, String id) throws Exception {
        if (StringUtils.isBlank(filePath)) {
            filePath = "fs";
        }
        final GridFS gridFS = new GridFS(db, filePath);
        final GridFSDBFile dbFile = gridFS.findOne(new BasicDBObject("_id", id));
        if (dbFile == null) {
            return null;
        }
        return new MongoFileEntity(dbFile);
    }

    private void saveFile(Server server, MongoFileEntity entity, TabView myTab, long time) {
        final String host = server.getHost();
        final GridFSDBFile dbFile = entity.getDbFile();
        final String md5 = FileUpgradeFactory.upgrade(host + "/repository/file/mongo/upgrade", dbFile, entity.getId());
        if (md5 == null) {
            sendSimpleEvent(myTab, entity.getPhysicalID(), entity.getId(), DiffFileEvent.FAILED, entity.getFileName());
            return;
        }
        final String dbFileMD5 = dbFile.getMD5();
        if (!dbFileMD5.equals(md5)) { //上传前后MD5是否一致
            sendSimpleEvent(myTab, entity.getPhysicalID(), entity.getId(), DiffFileEvent.MD5_FAILED, entity.getFileName());
        } else {
            final long totalTime = System.currentTimeMillis() - time;
            myTab.addMessage("同步成功【" + entity.getFileName() + "】,耗时" + totalTime + "ms", true);
            myTab.getProgress().getDoSuccess().add(entity.getId());
            log.info("同步成功【{}】,耗时{}ms", entity.getFileName(), totalTime);
        }
    }

    //发送大小不一致的事件
    private void sendDiffEvent(TabView myTab, RepoFile repoFile, String physicalFileId, MongoFileEntity sourceEntity) {
        final DiffFileEvent fileEvent = new DiffFileEvent(repoFile.getUuid(), physicalFileId,
                DiffFileEvent.DIFF);
        fileEvent.setDbSize(sourceEntity.getLength());
        fileEvent.setFileSize(sourceEntity.getFileChunkSize());
        fileEvent.setFileName(sourceEntity.getFileName());
        fileEvent.setMyTab(myTab);
        this.publisher.publishEvent(fileEvent);
    }

    //发送普通事件 （空文件/MD5不一致）
    private void sendSimpleEvent(TabView myTab, String physicalFileId, String uuid, int aNull, String fileName) {
        DiffFileEvent fileEvent = new DiffFileEvent(uuid, physicalFileId, aNull);
        fileEvent.setFileName(fileName);
        fileEvent.setMyTab(myTab);
        this.publisher.publishEvent(fileEvent);
    }

}
