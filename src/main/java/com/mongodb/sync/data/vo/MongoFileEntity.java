package com.mongodb.sync.data.vo;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.sync.data.entity.RepoFile;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Description: 文件持久化类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-3-12.1	hunt		2014-3-12		Create
 * </pre>
 * @date 2014-3-12
 */
@Slf4j
@Setter
@Getter
public class MongoFileEntity {

	private final GridFSDBFile dbFile;

	private RepoFile repoFile;

	public MongoFileEntity(GridFSDBFile dbFile) {
		this.dbFile = dbFile;
	}

	/**
	 * 该文件对外的ID
	 *
	 * @return
	 */
	public String getId() {
		return this.repoFile.getUuid();
	}

	public String getPhysicalID() {
		return this.dbFile.getId().toString();
	}

	public String getFileName() {
		return this.dbFile.getFilename();
	}

	public long getChunkSize() {
		return this.dbFile.getChunkSize();
	}

	public long getFileChunkSize() {
		Long fileSize = this.repoFile.getFileSize();
		return fileSize != null ? fileSize : 0L;
	}

	public long getLength() {
		return this.dbFile.getLength();
	}

	public void setRepoFile(RepoFile repoFile) {
		this.repoFile = repoFile;
		dbFile.put("filename", repoFile.getFileName());// 设置逻辑文件名
	}

}
