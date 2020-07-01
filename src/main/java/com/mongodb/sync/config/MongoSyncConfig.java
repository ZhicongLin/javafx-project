package com.mongodb.sync.config;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Description: 系统配置
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
public class MongoSyncConfig {

	// 线程池管理
	private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 16, 10000, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<>());

	/**
	 * 开启线程并执行指定操作
	 * @param runner
	 */
	public static void exec(Runnable runner) {
		executor.execute(runner);
	}

}
