package com.mongodb.sync.data.service;

import java.sql.Timestamp;

import com.mongodb.sync.data.vo.Server;
import com.mongodb.sync.module.view.TabView;

/**
 * Description: 接口： 同步数据
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
public interface RepoFileService {

	/**
	 * 同步数据
	 * @param sourceServer 源数据库信息
	 * @param targetServer 目标数据库信息
	 * @param myTab
	 * @param startTime
	 * @param endTime
	 */
	void sync(Server sourceServer, Server targetServer, TabView myTab, Timestamp startTime, Timestamp endTime);
}
