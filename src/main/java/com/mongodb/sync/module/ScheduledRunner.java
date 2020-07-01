package com.mongodb.sync.module;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;

/**
 * Description: 定时器管理
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
public class ScheduledRunner {
	private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();

	/**
	 * 每秒执行一次
	 * @param runnable 执行的任务
	 */
	public static void runLater(Runnable runnable) {
		EXECUTOR.scheduleWithFixedDelay(() -> Platform.runLater(runnable), 1000L, 1000L, TimeUnit.MILLISECONDS);
	}

	/**
	 * 根据配置时间执行任务
	 * @param runnable 执行的任务
	 * @param initialDelay 首次执行间隔
	 * @param delay 执行间隔
	 */
	public static void runLater(Runnable runnable, long initialDelay, long delay) {
		EXECUTOR.scheduleWithFixedDelay(() -> Platform.runLater(runnable), initialDelay, delay, TimeUnit.MILLISECONDS);
	}
}
