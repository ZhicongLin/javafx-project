package com.mongodb.sync.module.view;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.Glow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;

/**
 * Description: 表单栏视图
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
@Getter
@Setter
public class FormGridPane {

	private final List<Label> labels = new ArrayList<>();
	private final List<Region> controls = new ArrayList<>();
	private final GridPane grid = new GridPane();
	private final StackPane pane = new StackPane();
	private FormManager formManager;
	private String title;

	public FormGridPane() {
		this("");
	}

	public FormGridPane(String titleTag) {
		this.title = titleTag;
		grid.setStyle("-fx-content-display: top");
		grid.setStyle("-fx-border-insets: 20 10 10 10");
		grid.setStyle("-fx-border-color: #f0f0f0");
		grid.setHgap(5);
		grid.setVgap(5);
		grid.setPadding(new Insets(StringUtils.isBlank(titleTag) ? 8 : 25, 5, 8, 5));

		pane.getChildren().add(grid);
		if (StringUtils.isNoneBlank(titleTag)) {
			Label title = new FormLabel(titleTag);
			title.setAlignment(Pos.CENTER);
			title.getStyleClass().add("title-label");
			Glow glow = new Glow(0.2);
			title.setEffect(glow);
			StackPane.setAlignment(title, Pos.TOP_LEFT);
			pane.getChildren().add(title);
		}
	}

	public FormGridPane(String titleTag, FormManager formManager) {
		this(titleTag);
		this.formManager = formManager;
	}

	public FormGridPane addFieldLabel(String name, TextField control) {
		Label nameLabel = new FormLabel(name);
		this.labels.add(nameLabel);
		this.controls.add(control);
		if (this.formManager != null) {
			this.formManager.add(control);
		}
		return this;
	}

	public <T extends Region> T createFieldLabel(String name, T control) {
		Label nameLabel = new FormLabel(name);
		this.labels.add(nameLabel);
		this.controls.add(control);
		return control;
	}

	public StackPane build() {
		for (int i = 0; i < labels.size(); i++) {
			int rowIndex = (int) Math.floor(i / 3.0d);
			int columnIndex = (i % 3 + 1) * 2 - 1;
			grid.add(labels.get(i), columnIndex, rowIndex);
		}
		for (int i = 0; i < controls.size(); i++) {
			int rowIndex = (int) Math.floor(i / 3.0d);
			int columnIndex = (i % 3 + 1) * 2;
			grid.add(controls.get(i), columnIndex, rowIndex);
		}
		return this.pane;
	}

}
