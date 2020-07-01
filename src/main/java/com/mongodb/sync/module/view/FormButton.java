package com.mongodb.sync.module.view;

import javafx.scene.control.Button;

/**
 * Description: 自定义按纽
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
public class FormButton extends Button {
	public FormButton(String text) {
		super(text);
		getStyleClass().add("btn");
		getStyleClass().add("btn-primary");
	}

}
