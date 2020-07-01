package com.mongodb.sync.module.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;

/**
 * Description: SQL脚本弹窗内容
 *
 * @author linzc
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本           修改人       修改日期         修改内容
 * 2020/5/29.1       linzc    2020/5/29           Create
 * </pre>
 * @date 2020/5/29
 */
@Slf4j
@Getter
public class SqlPane {
	private GridPane gridPane;

	public SqlPane() {
		Label label = new FormLabel("以XSZP_DEV1租户为例子");
		label.setPadding(new Insets(8, 5, 8, 5));
		TextArea textArea = new TextArea(loadContent());
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		gridPane = new GridPane();
		gridPane.setMaxWidth(Double.MAX_VALUE);
		gridPane.add(label, 0, 0);
		gridPane.add(textArea, 0, 1);
	}

	private String loadContent() {
		try {
			final File file = new File("REPO_FILE_SYNC_RESULT.sql");
			return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return "";
	}
}