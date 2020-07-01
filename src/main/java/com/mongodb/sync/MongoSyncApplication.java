package com.mongodb.sync;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.mongodb.sync.module.view.MainStageView;
import com.mongodb.sync.module.view.TimeOutViewManager;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import de.felixroske.jfxsupport.FXMLView;
import de.felixroske.jfxsupport.GUIState;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Description: 启动类
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
@Slf4j
@SpringBootApplication
public class MongoSyncApplication extends AbstractJavaFxApplicationSupport {

	public static ConfigurableApplicationContext context;
	private static final EventBus bus = EventBus.builder().build();

	public static void main(String[] args) {
		log.info("系统启动");
		launch(MongoSyncApplication.class, MainStageView.class, new CustomSplash(), args);
	}

	public static void showView(final Class<? extends AbstractFxmlView> window, final Modality mode, Object object) {
		final AbstractFxmlView view = context.getBean(window);
		Stage newStage = new Stage();

		Scene newScene;
		if (view.getView().getScene() != null) {
			newScene = view.getView().getScene();
		} else {
			newScene = new Scene(view.getView());
			newScene.setFill(null);
		}

		if (GUIState.getScene().getRoot().getEffect() == null) {
			Lighting value = new Lighting();
			value.setDiffuseConstant(1.0);
			value.setSpecularConstant(2.0);
			value.setSpecularExponent(40.0);
			value.setSurfaceScale(0);
			GUIState.getScene().getRoot().setEffect(value);
		} else {
			Lighting effect = (Lighting) GUIState.getScene().getRoot().getEffect();
			effect.setDiffuseConstant(1.0);
		}

		if (!bus.isRegistered(view.getPresenter()) && hasSubscribe(view.getPresenter())) {
			bus.register(view.getPresenter());
			log.info("registered:{}", view.getPresenter().getClass());
		}

		FXMLView annotation = window.getAnnotation(FXMLView.class);

		newStage.setScene(newScene);
		newStage.initModality(mode);
		newStage.initOwner(getStage());
		newStage.setTitle(annotation.title());
		newStage.initStyle(StageStyle.valueOf(annotation.stageStyle()));

		newStage.setOnShown(event -> {
			if (object != null) {
				log.debug("跳转参数:{}", object);
				bus.post(object);
			}
		});

		newStage.showAndWait();
	}

	public static void switchView(Class<? extends AbstractFxmlView> from, Class<? extends AbstractFxmlView> to,
			Object object) {
		try {
			log.debug("从 {} 跳转到 {}", from, to);
			StopWatch started = StopWatch.createStarted();
			AbstractFxmlView fromViewer = context.getBean(from);
			AbstractFxmlView toViewer = context.getBean(to);

			context.getBean(TimeOutViewManager.class).setCurrentView(to);

			if (!bus.isRegistered(fromViewer.getPresenter()) && hasSubscribe(fromViewer.getPresenter())) {
				bus.register(fromViewer.getPresenter());
				log.info("registered:{}", fromViewer.getPresenter().getClass());
			}

			if (!bus.isRegistered(toViewer.getPresenter()) && hasSubscribe(toViewer.getPresenter())) {
				bus.register(toViewer.getPresenter());
				log.info("registered:{}", toViewer.getPresenter().getClass());
			}

			if (bus.isRegistered(fromViewer.getPresenter())) {
				log.debug("发布隐藏事件");
				bus.post(new ViewEvent(ViewEvent.ViewEvenType.hide, fromViewer, fromViewer.getPresenter()));
			}

			Platform.runLater(() -> {
				AbstractJavaFxApplicationSupport.showView(to);
				if (bus.isRegistered(toViewer.getPresenter())) {
					log.debug("发布显示事件");
					bus.post(new ViewEvent(ViewEvent.ViewEvenType.show, toViewer, toViewer.getPresenter()));
				}

				if (object != null) {
					log.debug("跳转参数:{}", object);
					bus.post(object);
				}

				log.debug("跳转页面耗时:{}", started.getTime());
			});

		} catch (Exception e) {
			log.error("跳转页面异常", e);
		}
	}

	public static boolean hasSubscribe(Object object) {
		Method[] subscribes = MethodUtils.getMethodsWithAnnotation(object.getClass(), Subscribe.class);
		return subscribes != null && subscribes.length > 0;
	}

	@SneakyThrows
	@Override
	public void beforeInitialView(Stage stage, ConfigurableApplicationContext ctx) {
		MongoSyncApplication.context = ctx;
		stage.setTitle("MongoDB Synchronous Client");
		// stage.setMaximized(true);
		final SystemTray tray = SystemTray.getSystemTray();
		final BufferedImage image = ImageIO.read(getClass().getResource("/image/logo.png"));
		final PopupMenu pm = new PopupMenu();
		final MenuItem open = new MenuItem("open");
		final MenuItem exit = new MenuItem("exit");
		open.addActionListener(e -> openWindow(stage));
		exit.addActionListener(e -> Platform.runLater(() -> System.exit(2)));
		pm.add(open);
		pm.add(exit);

		final TrayIcon trayIcon = new TrayIcon(image, "MongoDB同步客户端", pm);
		trayIcon.setToolTip("MongoDB同步客户端");
		trayIcon.setImageAutoSize(true);
		// 添加鼠标点击事件
		trayIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 双击左键
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					openWindow(stage);
				}
			}
		});
		tray.add(trayIcon);
	}

	private void openWindow(Stage stage) {
		Platform.runLater(() -> {
			if (stage.isIconified()) {
				stage.setIconified(false);
			}
			if (!stage.isShowing()) {
				stage.show();
			}
			stage.toFront();
		});
	}

	@Override
	public Collection<Image> loadDefaultIcons() {
		return Collections.singletonList(new Image(this.getClass().getResourceAsStream("/image/logo.png")));
	}

}
