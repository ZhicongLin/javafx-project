package com.mongodb.sync.module.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;

/**
 * Description: Label显示框组件
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
public class FormLabel extends Label {

	public FormLabel(String text) {
		super(text);
		this.setAlignment(Pos.CENTER_RIGHT);
		this.getStyleClass().add("text-label");
	}
}
