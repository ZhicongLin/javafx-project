package com.mongodb.sync.data.event.processor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.mongodb.sync.data.entity.RepoFileSyncResult;
import com.mongodb.sync.data.event.DiffFileEvent;
import com.mongodb.sync.data.event.SaveEvent;
import com.mongodb.sync.data.mongo.DynamicUtils;
import com.mongodb.sync.data.repository.RepoFileSyncResultRepository;
import com.mongodb.sync.module.view.TabView;

import lombok.extern.slf4j.Slf4j;

/**
 * Description: 错误事件处理器
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
@Component
public class DiffFileEventProcessor {
	private static final Map<String, List<RepoFileSyncResult>> resultMaps = new HashMap<>();
	@Resource
	RepoFileSyncResultRepository repoFileSyncResultRepository;

	@EventListener(DiffFileEvent.class)
	public void processor(DiffFileEvent event) {
		RepoFileSyncResult result = new RepoFileSyncResult();
		result.setUuid(DynamicUtils.getRandomUUID());
		result.setCreateTime(new Timestamp(System.currentTimeMillis()));
		result.setRepoFileUuid(event.getUuid());
		event.getMyTab().getProgress().getDoFailed().add(event.getUuid());
		if (event.getType() == DiffFileEvent.NULL) {
			log.info("文件[{}]不存在！", event.getFileName());
			result.setMessage("MongoDB中文件[" + event.getPhysicalFileId() + " => " + event.getFileName() + "]不存在");
			result.setResult(DiffFileEvent.NULL);
		}
		if (event.getType() == DiffFileEvent.MD5_FAILED) {
			log.info("文件[{}]MD5不一致！", event.getFileName());
			result.setMessage("文件[" + event.getPhysicalFileId() + " => " + event.getFileName() + "]MD5上传前后不一致");
			result.setResult(DiffFileEvent.MD5_FAILED);
		}
		if (event.getType() == DiffFileEvent.FAILED) {
			log.info("文件{}上传失败！", event.getFileName());
			result.setMessage("文件[" + event.getPhysicalFileId() + " => " + event.getFileName() + "]上传失败");
			result.setResult(DiffFileEvent.FAILED);
		}
		if (event.getType() == DiffFileEvent.DIFF) {
			log.info("文件[{}]大小不一致！", event.getFileName());
			result.setMessage("MongoDB与数据库的文件大小不一致！");
			result.setResult(DiffFileEvent.DIFF);
			result.setChunkSize(event.getDbSize());
			result.setFileSize(event.getFileSize());
		}
		event.getMyTab().addMessage(result.getMessage(), false);
		Integer count = this.repoFileSyncResultRepository.countByResultAndRepoFileUuid(result.getResult(),
				result.getRepoFileUuid());
		if (count == 0) {
			this.addError(event.getMyTab().getId(), result);
		}
	}

	private synchronized void addError(String key, RepoFileSyncResult result) {
		List<RepoFileSyncResult> resultList = resultMaps.get(key);
		if (resultList == null) {
			resultList = new ArrayList<>();
		}
		resultList.add(result);
		resultMaps.put(key, resultList);
	}

	@EventListener(SaveEvent.class)
	public void saveProcessor(SaveEvent event) {
		TabView myTab = event.getMyTab();
		List<RepoFileSyncResult> resultList = resultMaps.get(myTab.getId());
		if (resultList == null || resultList.isEmpty()) {
			myTab.getProgress().setTotalTime(System.currentTimeMillis() - event.getTime());
			myTab.addMessage("任务执行完毕！", true);
			return;
		}
		myTab.addMessage("任务执行完毕！保存问题记录中...", true);
		this.repoFileSyncResultRepository.saveAll(resultList);
		myTab.addMessage("问题记录保存成功！（共" + resultList.size() + "条）", true);
		resultMaps.remove(myTab.getId());
		myTab.getProgress().setTotalTime(System.currentTimeMillis() - event.getTime());
	}
}
