package com.mongodb.sync.module.view;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.browniebytes.javafx.control.DateTimePicker;
import com.mongodb.sync.data.mongo.DynamicUtils;
import com.mongodb.sync.module.AlertManager;
import com.mongodb.sync.module.controller.MainController;
import com.mongodb.sync.module.dto.FormData;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Description: Tab视图主内容
 *
 * @author linzc
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本           修改人       修改日期         修改内容
 * 2020/6/3.1       linzc    2020/6/3           Create
 * </pre>
 * @date 2020/6/3
 */
@Slf4j
@Getter
public class TabView extends Tab implements Initializable {
	private static final String FORMAT = LocalDateTime.now()
			.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS:  "));
	private static final String pattern = "yyyy-MM-dd HH:mm:ss";
	private static final DateTimeFormatter FORMAT_SS = DateTimeFormatter.ofPattern(pattern);

	@FXML
	public SplitPane splitPane;
	@FXML
	public VBox form;
	@FXML
	public ScrollPane message;

	public ProgressView progress;

	private final FormData formData;

	private final MainController controller;

	private final FormManager formManager;
	// 开始事件
	public DateTimePicker startTimePicker = new DateTimePicker();
	// 结束事件
	public DateTimePicker endTimePicker = new DateTimePicker();

	private Button execBtn;
	@Setter
	private boolean doWork = false;

	public TabView(FormData formData, MainController mainController) {
		super(formData == null || formData.getTreeName() == null ? "新增" : formData.getTreeName());
		String id = formData == null || StringUtils.isBlank(formData.getUuid()) ? DynamicUtils.getRandomUUID()
				: formData.getUuid();
		super.setId(id);
		this.formData = formData;
		this.controller = mainController;
		this.formManager = new FormManager(formData);
		final URL location = getClass().getResource("/fxml/MainTabView.fxml");
		final FXMLLoader fxmlLoader = new FXMLLoader(location);
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initFormBox();
		this.addContextMenu(this, controller.tabPane.getTabs());
		this.progress = ProgressView.createProgressView(() -> {
			this.doWork = false;
			execBtn.setDisable(false);
		});
		this.splitPane.getItems().add(this.progress);
		this.splitPane.getStyleClass().add("vertical-grabber");
		this.setOnCloseRequest(event -> {
			if (doWork) {
				AlertManager.confirm("关闭将终止正在同步的任务", controller.getStage(), (confirm) -> {
					if (confirm) {
						doWork = false;
					} else {
						event.consume();
					}
				});
			}
		});
	}

	/**
	 * 初始化保存按纽
	 *
	 * @param formGridPane formGridPane
	 */
	public void initSaveBtn(FormGridPane formGridPane) {
		final FormButton save = new FormButton("保存");
		formGridPane.getGrid().add(save, 3, 0);
		save.setOnAction(event -> {
			final FormData formData = formManager.getFormData(FormData.class);
			formData.setUuid(this.getId());
			String treeName = formData.getTreeName();
			if (StringUtils.isBlank(treeName)) {
				AlertManager.show("标签名称错误", "标签不能为空", controller.getStage(), Alert.AlertType.ERROR);
				return;
			}
			controller.writeTreeData(formData);
			this.setText(formData.getTreeName());
		});
	}

	/**
	 * 初始化保存按纽
	 *
	 * @param formGridPane formGridPane
	 */
	public void initSearchBtn(FormGridPane formGridPane) {
		execBtn = new FormButton("开始同步");
		formGridPane.getGrid().add(execBtn, 5, 0);
		execBtn.setOnAction(event -> {
			final FormData formData = this.getFormData();
			boolean validate = StringUtils.isBlank(formData.getSourceHost())
					|| StringUtils.isBlank(formData.getSourcePort()) || StringUtils.isBlank(formData.getSourceDb())
					|| StringUtils.isBlank(formData.getTargetHost());
			if (validate) {
				AlertManager.info("表单未填写完整", "Host,port,db均为必填项,请仔细填写", controller.getStage());
				return;
			}
			this.doWork = true;
			execBtn.setDisable(true);
			controller.editConnect(formData, this);
		});
	}

	private void initFormBox() {
		// 标签栏信息
		FormGridPane tagNameGrid = new FormGridPane();
		FormTextField treeNameField = new FormTextField("treeName");
		tagNameGrid.addFieldLabel("标签", treeNameField);
		initSaveBtn(tagNameGrid);
		this.formManager.add(treeNameField);
		// 源数据库信息配置
		FormGridPane sourceGrid = new FormGridPane("源数据库", this.formManager);
		final PasswordField sourcePwd = new PasswordField();
		sourcePwd.setId("sourcePwd");
		sourceGrid.addFieldLabel("Host", new FormTextField("sourceHost"))
				.addFieldLabel("Port", new FormTextField("sourcePort"))
				.addFieldLabel("Db", new FormTextField("sourceDb"))
				.addFieldLabel("Tenant", new FormTextField("sourceTenant"))
				.addFieldLabel("User", new FormTextField("sourceUser")).addFieldLabel("Pwd", sourcePwd);
		// 目标数据库信息配置
		FormGridPane targetGrid = new FormGridPane("目标服务", this.formManager);
		final PasswordField targetPwd = new PasswordField();
		targetPwd.setId("targetPwd");
		final FormTextField targetHost = new FormTextField("targetHost");
		targetHost.setPromptText("http://localhost:8080");
		targetGrid.addFieldLabel("服务器接口", targetHost);
				targetGrid.getGrid().add(new Text("/repository/file/mongo/upgrade"), 3, 0);
		// 搜索栏信息配置
		FormGridPane searchGrid = new FormGridPane();
		startTimePicker = searchGrid.createFieldLabel("开始时间", new DateTimePicker());
		endTimePicker = searchGrid.createFieldLabel("结束时间", new DateTimePicker());
		this.initSearchBtn(searchGrid);
		// 表单区域大小配置
		form.getChildren().addAll(tagNameGrid.build(), sourceGrid.build(), targetGrid.build(), searchGrid.build());
	}

	/**
	 * 设置进度栏的提示消息
	 */
	public void setProcessorLabelText(String text) {
		this.progress.getTextMsg().setText(text);
	}

	/**
	 * 添加一条消息
	 * @param msg
	 * @param success
	 */
	public void addMessage(String msg, boolean success) {
		Platform.runLater(() -> {
			final String ft = FORMAT + msg;
			final TextField text = new TextField(ft);
			text.setStyle("-fx-border-color: #ffffff;-fx-border-radius: 0;-fx-background-radius: 0;");
			text.setPadding(new Insets(2));
			text.setEditable(false);
			if (!success) {
				text.getStyleClass().add("text-danger");
			} else {
				text.getStyleClass().add("text-success");
			}
			final ObservableList<Node> children = ((VBox) message.getContent()).getChildren();
			children.add(0, text);
			final int index = progress.getRowCount().getSelectionModel().getSelectedItem();
			if (children.size() > index) {
				children.remove(index);
			}
		});
	}

	/**
	 * 清空消息栏
	 */
	public void clearMessage() {
		Platform.runLater(() -> {
			((VBox) message.getContent()).getChildren().clear();
			this.progress.getDoUuid().clear();
			this.progress.getDoSuccess().clear();
			this.progress.getDoFailed().clear();
			this.progress.setTotalTime(0);
			this.progress.getChildren().clear();
			this.progress.getChildren().addAll(this.progress.getRowCount(), this.progress.getProgressBar(),
					this.progress.getTextMsg());
		});
	}

	/**
	 * 获取Tab右键菜单
	 * @param tab
	 * @param tabs
	 */
	public void addContextMenu(TabView tab, ObservableList<Tab> tabs) {
		// 创建右键菜单
		final ContextMenu contextMenu = new ContextMenu();
		contextMenu.getStyleClass().add("context-menu");
		// 菜单项
		final MenuItem saveBtn = new MenuItem(" 保 存 ");
		saveBtn.setOnAction(event -> {
			final FormData formData = tab.getFormData();
			controller.writeTreeData(formData);
			tab.setText(formData.getTreeName());
		});
		// 菜单项
		final MenuItem delBtn = new MenuItem(" 删 除 ");
		delBtn.setOnAction(event -> {
			final FormData formData = controller.getFormDataMap().get(tab.getId());
			if (formData == null) {
				tabs.remove(tab);
				return;
			}

			if (doWork) {
				AlertManager.confirm("删除后将终止正在同步的任务", controller.getStage(), (confirm) -> {
					if (confirm) {
						doWork = false;
						tabs.remove(tab);
						controller.delTreeData(tab.getFormData());
					} else {
						event.consume();
					}
				});
			} else {
				AlertManager.confirm("确认删除【" + tab.getText() + "】吗？", controller.getStage(), confirm -> {
					if (confirm) {
						tabs.remove(tab);
						controller.delTreeData(tab.getFormData());
					}
				});
			}
		});

		contextMenu.getItems().addAll(saveBtn, delBtn);
		this.setContextMenu(contextMenu);
	}

	public FormData getFormData() {
		final FormData formData = this.formManager.getFormData(FormData.class);
		final LocalDateTime start = startTimePicker.dateTimeProperty().get();
		final LocalDateTime end = endTimePicker.dateTimeProperty().get();
		final String startStr = start.format(FORMAT_SS);
		final String endStr = end.format(FORMAT_SS);
		try {
			formData.setStartTime(DateUtils.parseDate(startStr, pattern).getTime());
			formData.setEndTime(DateUtils.parseDate(endStr, pattern).getTime());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		}
		formData.setUuid(this.getId());
		return formData;
	}

	/**
	 * 开始同步消息提示
	 */
	public void startSynData() {
		this.clearMessage();
		this.addMessage("开始执行...", true);
		this.addMessage("查询数据同步...", true);
		Platform.runLater(() -> this.setProcessorLabelText("同步数据准备中..."));
	}
	/**
	 * 不用同步数据提示
	 */
	public void emptySynData() {
		Platform.runLater(() -> {
			this.setDoWork(false);
			this.getExecBtn().setDisable(false);
			this.setProcessorLabelText("没有需要同步的数据");
			this.addMessage("没有需要同步的数据", true);
		});
	}
}