package com.mongodb.sync.data.mongo;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;
import com.mongodb.sync.data.vo.Server;

/**
 * Description: Mongo连接工具
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
public class MongoConnect {

	private static final Map<String, MongoInfo> connections = new HashMap<>();

	/**
	 * 获取Mongo链接
	 *
	 * @param server 链接参数
	 */
	public static MongoInfo connect(Server server) {
		final String key = server.getKey();
		final MongoInfo mongoInfo = connections.get(key);
		if (mongoInfo != null) {
			return mongoInfo;
		}

		final MongoOptions options = new MongoOptions();
		options.autoConnectRetry = true;
		options.socketKeepAlive = true;
		Mongo mongo = null;
		try {
			mongo = new Mongo(new ServerAddress(server.getHost(), server.getPort()), options);
			final DB db = mongo.getDB(server.getDbName());
			if (StringUtils.isNotBlank(server.getUserName()) && StringUtils.isNotBlank(server.getPassword())) {
				db.authenticate(server.getUserName(), server.getPassword().toCharArray());
			}
			final MongoInfo info = new MongoInfo(mongo, db);
			connections.putIfAbsent(key, info);
			return info;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

}
