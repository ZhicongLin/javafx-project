package com.mongodb.sync.module;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Description: 消息弹窗管理
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
public class AlertManager {
	/**
	 * 弹出一个通用的确定对话框
	 * @param msg 对话框的信息
	 * @return 用户点击了是或否
	 */
	public static void confirm(String msg, Stage stage, AlertCallback alertCallback) {
		// 按钮部分可以使用预设的也可以像这样自己 new 一个
		final ButtonType cancel = new ButtonType("取消", ButtonBar.ButtonData.NO);
		final ButtonType confirmBtn = new ButtonType("确定", ButtonBar.ButtonData.YES);
		final Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", cancel, confirmBtn);
		// 设置窗口的标题
		alert.setTitle("确认信息");
		alert.setHeaderText(msg);
		// 设置对话框的 icon 图标，参数是主窗口的 stage
		alert.initOwner(stage);
		// showAndWait() 将在对话框消失以前不会执行之后的代码
		final Optional<ButtonType> buttonType = alert.showAndWait();
		// 根据点击结果返回
		if (buttonType.isPresent()) {
			alertCallback.invoke(buttonType.get().getButtonData().equals(ButtonBar.ButtonData.YES));
		} else {
			alertCallback.invoke(false);
		}
	}

	/**
	 * 弹出一个通用的确定对话框
	 * @param header 对话框的信息标题
	 * @param msg 对话框的信息
	 * @param stage 显示区域
	 */
	public static void info(String header, String msg, Stage stage) {
		final Alert alt = new Alert(Alert.AlertType.INFORMATION);
		alt.setTitle("信息");
		alt.setHeaderText(header);
		alt.setContentText(msg);
		alt.initOwner(stage);
		alt.show();
	}

	/**
	 * 弹出一个通用的确定对话框
	 * @param header 对话框的信息标题
	 * @param msg 对话框的信息
	 * @param stage 显示区域
	 */
	public static void show(String header, String msg, Stage stage, Alert.AlertType alertType) {
		final Alert alt = new Alert(alertType);
		alt.setTitle("信息");
		alt.setHeaderText(header);
		alt.setContentText(msg);
		alt.initOwner(stage);
		alt.show();
	}

	/**
	 * 打开dialog弹窗
	 * @param title
	 * @param header
	 * @param pane
	 * @param stage
	 */
	public static void showDialog(String title, String header, Pane pane, Stage stage) {
		final Alert alert = new Alert(Alert.AlertType.INFORMATION);
		final Image image = new Image("/image/device.png");
		final ImageView graphic = new ImageView(image);
		graphic.setFitHeight(30);
		graphic.setFitWidth(30);
		alert.setGraphic(graphic);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.initOwner(stage);
		alert.getDialogPane().setContent(pane);
		alert.showAndWait();
	}
}
