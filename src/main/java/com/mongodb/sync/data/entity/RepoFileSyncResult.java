package com.mongodb.sync.data.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

/**
 * Description: 错误信息实体类
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
@Setter
@Getter
@Entity(name = "REPO_FILE_SYNC_RESULT")
public class RepoFileSyncResult {
	@Id
	private String uuid;
	private Long fileSize;
	private Integer result;
	private String message;
	private Long chunkSize;
	private String repoFileUuid;
	private Timestamp createTime;
}
