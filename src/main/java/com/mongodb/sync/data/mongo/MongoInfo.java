package com.mongodb.sync.data.mongo;

import com.mongodb.DB;
import com.mongodb.Mongo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description: mongo连接池信息
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
@Getter
@AllArgsConstructor
public class MongoInfo {
	private final Mongo mongo;
	private final DB db;
}