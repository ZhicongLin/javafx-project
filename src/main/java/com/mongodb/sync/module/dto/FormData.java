package com.mongodb.sync.module.dto;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Getter;
import lombok.Setter;

/**
 * Description: 表单数据
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
public class FormData {

	private String uuid;

	private String treeName;

	private String sourceHost;
	private String sourcePort;
	private String sourceUser;
	private String sourcePwd;
	private String sourceDb;
	private String sourceTenant;

	private String targetHost;
	private String targetPort;
	private String targetUser;
	private String targetPwd;
	private String targetDb;
	// private String targetFolder;
	@JSONField(serialize = false)
	private long startTime;
	@JSONField(serialize = false)
	private long endTime;

}
