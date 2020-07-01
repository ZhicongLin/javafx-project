package com.mongodb.sync.module.view;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PostConstruct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.mongodb.sync.MongoSyncApplication;

import de.felixroske.jfxsupport.AbstractFxmlView;

/**
 * Description: 请求超时管理器
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
@Component
@Slf4j
public class TimeOutViewManager implements Runnable {

	private Class<? extends AbstractFxmlView> currentView;
	private Integer currentLeft = -1;
	private final List<TimeOutView> timeOutViewList = new ArrayList<>();
	private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

	public void register(Class<? extends AbstractFxmlView> from, Class<? extends AbstractFxmlView> to,
			Integer timeOutSec) {
		timeOutViewList.add(new TimeOutView(from, to, timeOutSec));
	}

	@PostConstruct
	public void init() {
		// executorService.scheduleWithFixedDelay(this, 1000, 1000,
		// TimeUnit.MILLISECONDS);
	}

	public void setCurrentView(Class<? extends AbstractFxmlView> currentView) {
		this.currentView = currentView;
		this.currentLeft = this.timeOutViewList.stream().filter(f -> f.getFrom() == currentView)
				.map(TimeOutView::getTimeOutSec).findFirst().orElse(-1);
	}

	public void setCurrentLeft(Integer currentLeft) {
		this.currentLeft = currentLeft;
	}

	@Override
	public void run() {
		log.debug("超时检查 当前{} 超时时间{}", currentView, currentLeft);
		for (TimeOutView timeOutView : timeOutViewList) {
			if (timeOutView.getFrom() == currentView) {
				if (currentLeft == 0) {
					log.debug("在 {} 己停留 {} 秒,将自动跳转到 {}", currentView, timeOutView.timeOutSec, timeOutView.getTo());
					MongoSyncApplication.switchView(timeOutView.getFrom(), timeOutView.getTo(), null);
					return;
				}
				if (currentLeft > 0) {
					currentLeft--;
					log.debug("在 {} 己停留 1 秒,还剩 {} 秒", currentView, currentLeft);
				}
			}
		}
	}

	@AllArgsConstructor
	@Getter
	public static class TimeOutView {
		private final Class<? extends AbstractFxmlView> from;
		private final Class<? extends AbstractFxmlView> to;
		private final Integer timeOutSec;
	}
}
