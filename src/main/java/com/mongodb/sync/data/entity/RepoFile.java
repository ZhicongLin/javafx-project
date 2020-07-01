package com.mongodb.sync.data.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

/**
 * Description: 文件信息实体类
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
@Entity
@Table(name = "REPO_FILE")
@Setter
@Getter
public class RepoFile implements Serializable {
	@Id
	private String uuid;
	@Column(name = "CREATE_TIME")
	private Timestamp createTime;
	@Column(name = "PHYSICAL_FILE_ID")
	private String physicalFileId;
	@Column(name = "FILE_NAME")
	private String fileName;
	@Transient
	private String filePath;
	@Column(name = "FILE_SIZE")
	private Long fileSize;
}
