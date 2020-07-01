package com.mongodb.sync.module.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.mongodb.sync.config.MongoSyncConfig;
import com.mongodb.sync.data.service.RepoFileService;
import com.mongodb.sync.data.vo.Server;
import com.mongodb.sync.module.AlertManager;
import com.mongodb.sync.module.JSONFile;
import com.mongodb.sync.module.ScheduledRunner;
import com.mongodb.sync.module.dto.FormData;
import com.mongodb.sync.module.view.SqlPane;
import com.mongodb.sync.module.view.TabView;

import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Description: 主控制器
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
@FXMLController
@Slf4j
@ConfigurationProperties(prefix = "system")
public class MainController implements Initializable {
	@FXML
	public Label date;
	@FXML
	public BorderPane mainWindow;
	@FXML
	public HBox leftBox;
	@FXML
	public HBox tabPaneBox;
	@FXML
	public TabPane tabPane;
	@FXML
	public TreeView<String> treeBox;
	@FXML
	public Label version;
	@Setter
	private String deviceVersion;
	@FXML
	private HBox menuBar;
	@Resource
	private RepoFileService repoFileService;

	private List<FormData> formDatas;

	@Getter
	private final Map<String, FormData> formDataMap = new HashMap<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initWindowSize();
		initLeftTree();
		// 左下时间显示
		ScheduledRunner.runLater(
				() -> date.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss EEE"))));
		Platform.runLater(() -> {
			final Stage stage = getStage();
			stage.setOnCloseRequest(event -> {
				long workCount = 0;
				if (tabPane.getTabs().size() > 0) {
					workCount = tabPane.getTabs().stream().filter(tab -> ((TabView) tab).isDoWork()).count();
				}
				if (workCount > 0) {
					AlertManager.confirm("有任务正在执行，您确定终止任务并退出吗？", stage, confirm -> {
						if (confirm) {
							System.exit(2);
						} else {
							event.consume();
						}
					});
				} else {
					AlertManager.confirm("您确定退出吗？", stage, confirm -> {
						if (confirm) {
							System.exit(2);
						} else {
							event.consume();
						}
					});
				}
			});
		});

	}

	/**
	 * 初始化左侧菜单树信息
	 */
	private void initLeftTree() {
		// TreeItem名字和图标
		ImageView graphic = new ImageView("/image/search.png");
		graphic.setFitWidth(18);
		graphic.setFitHeight(18);
		TreeItem<String> rootItem = new TreeItem<>("数据库", graphic);
		rootItem.getGraphic().getStyleClass().add("tree-item");
		rootItem.setExpanded(true);
		// 每个Item下又可以添加新的Item
		List<FormData> formDatas = this.loadTreeData();
		formDatas.forEach(fd -> {
			ImageView imageView = new ImageView("/image/device.png");
			imageView.setFitWidth(18);
			imageView.setFitHeight(18);
			TreeItem<String> item = new TreeItem<>(fd.getTreeName(), imageView);
			item.getGraphic().getStyleClass().add("tree-item");
			rootItem.getChildren().add(item);
		});
		treeBox.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			final TreeItem<String> selectedItem = treeBox.getSelectionModel().getSelectedItem();
			if (selectedItem == null) {
				return;
			}
			final String name = selectedItem.getValue();
			if (name.equals(rootItem.getValue())) {
				return;
			}
			FormData formData = null;
			for (FormData data : this.formDatas) {
				if (name.equals(data.getTreeName())) {
					formData = data;
					break;
				}
			}
			addTab(formData);
		});
		treeBox.setRoot(rootItem);
	}

	/**
	 * 加载左侧树的配置数据
	 * @return
	 */
	private List<FormData> loadTreeData() {
		formDatas = JSONFile.readJson();
		formDataMap.clear();
		formDataMap.putAll(formDatas.stream().collect(Collectors.toMap(FormData::getUuid, FormData -> FormData)));
		return formDatas;
	}

	/**
	 * 初始化窗口内各个元素大小
	 */
	private void initWindowSize() {
		ReadOnlyDoubleProperty width = mainWindow.widthProperty();
		ReadOnlyDoubleProperty height = mainWindow.heightProperty();
		menuBar.prefWidthProperty().bind(width);
		mainWindow.prefWidthProperty().bind(width);
		tabPaneBox.prefWidthProperty().bind(width.divide(leftBox.getWidth()));
		tabPane.prefWidthProperty().bind(width);
		mainWindow.prefHeightProperty().bind(height);
		leftBox.prefHeightProperty().bind(height);
	}

	/**
	 * 打开新增的方法
	 */
	public void addConnect() {
		addTab(null);
	}

	/**
	 * 打开新的tab页面
	 * @param treeData
	 */
	private void addTab(FormData treeData) {
		String uuid = "null";
		if (treeData != null) {
			uuid = treeData.getUuid();
		}
		ObservableList<Tab> tabs = tabPane.getTabs();
		for (Tab t : tabs) {
			String text = t.getId();
			if (uuid.equals(text)) {
				tabPane.getSelectionModel().select(t);
				return;
			}
		}
		// TabView tab = new TabView(treeData, this);
		final TabView tab = new TabView(treeData, this);
		tabs.add(tab);
		tabPane.getSelectionModel().select(tab);
		tabPane.setTabMinWidth(100);
	}

	/**
	 * 执行同步操作
	 * @param formData
	 * @param myTab
	 */
	public void editConnect(FormData formData, TabView myTab) {
		final Server source = new Server(formData.getSourceHost(), Integer.parseInt(formData.getSourcePort()),
				formData.getSourceDb());
		source.setUserName(formData.getSourceUser());
		source.setPassword(formData.getSourcePwd());
		source.setTenant(formData.getSourceTenant());
		final Server target = new Server();
		target.setHost(formData.getTargetHost());
		MongoSyncConfig.exec(() -> repoFileService.sync(source, target, myTab, new Timestamp(formData.getStartTime()),
				new Timestamp(formData.getEndTime())));
	}

	/**
	 * 删除树数据
	 * @param formData
	 */
	public void delTreeData(FormData formData) {
		JSONFile.removeJson(this.formDatas, formData);
		initLeftTree();
	}

	/**
	 * 写入树状数据
	 * @param data
	 */
	public void writeTreeData(FormData data) {
		boolean writeFlag = true;
		for (FormData fd : this.formDatas) {
			if (!fd.getUuid().equals(data.getUuid()) && fd.getTreeName().equals(data.getTreeName())) {
				AlertManager.show("标签名称错误", "已经存在标签【" + fd.getTreeName() + "】", this.getStage(), Alert.AlertType.ERROR);
				writeFlag = false;
			}
		}
		if (writeFlag) {
			JSONFile.writeJson(this.formDatas, data);
			initLeftTree();
		}
	}

	/**
	 * 点击关于
	 */
	public void aboutAlert() {
		AlertManager.info("版本信息", this.deviceVersion, this.getStage());
	}

	/**
	 * 打开帮助文档
	 */
	public void helpAlert() {
		try {
			final String filePath = new File("readme.docx").getAbsolutePath();
			final String command = "rundll32 url.dll FileProtocolHandler " + filePath;
			Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 打开脚本弹窗
	 */
	public void sqlAlert() {
		AlertManager.showDialog("数据库脚本", "脚本中的租户库，要替换成实际的租户库", new SqlPane().getGridPane(), this.getStage());
	}

	/**
	 * 获取当前容器
	 * @return Current Stage
	 */
	public Stage getStage() {
		return (Stage) this.mainWindow.getScene().getWindow();
	}
}
