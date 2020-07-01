package com.mongodb.sync.data.event;

import com.mongodb.sync.module.view.TabView;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Description: 错误事件
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
@AllArgsConstructor
public class DiffFileEvent {
	public static final int NULL = 0;
	public static final int DIFF = 1;
	public static final int FAILED = 2;
	public static final int MD5_FAILED = 3;
	private String uuid;
	private String physicalFileId;
	private int type;
	private long dbSize;
	private long fileSize;
	private String msg;
	private String fileName;
	private TabView myTab;
	private int totalSize;

	public DiffFileEvent(String uuid, String physicalFileId, int type) {
		this.uuid = uuid;
		this.physicalFileId = physicalFileId;
		this.type = type;
	}
}
