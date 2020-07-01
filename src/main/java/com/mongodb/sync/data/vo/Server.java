package com.mongodb.sync.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 连接服务器的信息
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
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Server {

	private String host;
	private int port;
	private String userName;
	private String password;
	private String dbName;
	private String tenant;

	// private String binFolder;
	public Server(String host, int port, String dbName) {
		this.host = host;
		this.port = port;
		this.dbName = dbName;
	}

	public String getKey() {
		return host + ":" + port + ":" + userName + ":" + password + ":" + dbName;
	}
}
