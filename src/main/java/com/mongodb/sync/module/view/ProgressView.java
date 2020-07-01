package com.mongodb.sync.module.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;

import com.mongodb.sync.module.ScheduledRunner;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Description: 进度信息栏
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
@Setter
@Getter
public class ProgressView extends HBox implements Initializable {
    @FXML
    private ProgressBar progressBar;
    @FXML
    private ComboBox<Integer> rowCount;
    @FXML
    private Text textMsg;
    private Vector<String> doUuid = new Vector<>();

    private Vector<String> doSuccess = new Vector<>();

    private Vector<String> doFailed = new Vector<>();

    private long totalTime = 0;

    private int totalSize = 0;

    public ProgressView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ProgressBoxView.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void finishEvent() {

    }

    /**
     * 创建进度栏
     *
     * @param finishEvent
     * @return
     */
    public static ProgressView createProgressView(Runnable finishEvent) {
        return new ProgressView() {
            @Override
            public void finishEvent() {
                Platform.runLater(finishEvent);
            }
        };
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.rowCount.setItems(FXCollections.observableArrayList(500, 1000, 1500));
        this.rowCount.setValue(1000);
        this.rowCount.setTooltip(new Tooltip("消息栏记录行数"));
        this.getStyleClass().addAll("panel-footer");
        schedule();
    }

    private void schedule() {
        ScheduledRunner.runLater(() -> {
            final int doSize = doUuid.size();
            final int doSuccessSize = doSuccess.size();
            final int failedSize = doFailed.size();
            String timeStr = "";
            if (totalTime > 0) {
                timeStr = " | 总耗时【" + totalTime / 1000.0D + "s】";
            }
            if (doSize == 0) {
                textMsg.setText(textMsg.getText());
            } else if (doSize == totalSize) {
                String message = " 执行完毕！ | 执行数量【%s】 | 成功【%s】 | 失败【%s】";
                this.finishEvent();
                textMsg.setText(String.format(message, doSize, doSuccessSize, failedSize) + timeStr);
                this.getChildren().remove(progressBar);
            } else {
                String message = " %s/%s | 执行数量【%s】 | 成功【%s】 | 失败【%s】";
                textMsg.setText(String.format(message, doSize, totalSize, doSize, doSuccessSize, failedSize)
                        + timeStr);
            }
            if (totalSize == 0) {
                this.progressBar.setVisible(false);
                return;
            }
            this.progressBar.setVisible(true);
            this.progressBar.setProgress(((double) doSize) / totalSize);
        }, 500, 500);
    }
}