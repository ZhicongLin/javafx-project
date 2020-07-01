package com.mongodb.sync.module.view;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.sync.module.dto.FormData;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Description: 表单内容操作
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
@NoArgsConstructor
public class FormManager {

	private final Map<String, TextField> textFieldMap = new HashMap<>();
	private final Map<String, String> dataValues = new HashMap<>();

	private FormData formData;

	public FormManager(FormData formData) {
		this.formData = formData;
	}

	public void add(TextField textField) {
		final String id = textField.getId();
		if (formData != null) {
			final Field field = ReflectionUtils.findField(FormData.class, id);
			try {
				if (field != null) {
					field.setAccessible(true);
					textField.setText(field.get(formData) != null ? field.get(formData).toString() : "");
				}
			} catch (IllegalAccessException e) {
				log.error(e.getMessage(), e);
			}
		}
		textFieldMap.put(id, textField);
	}

	public void addAll(TextField... textField) {
		for (TextField field : textField) {
			add(field);
		}
	}

	public void addValues(String key, String value) {
		this.dataValues.put(key, value);
	}

	public <T> T getFormData(Class<T> clazz) {
		final Set<String> keySet = textFieldMap.keySet();
		for (String key : keySet) {
			String text = textFieldMap.get(key).getText();
			if (StringUtils.isBlank(text)) {
				text = textFieldMap.get(key).getPromptText();
			}
			this.dataValues.put(key, text);
		}

		final String dataStr = JSON.toJSONString(dataValues);
		return JSON.parseObject(dataStr, clazz);
	}

	public void initData(FormData formData) {
		if (formData == null) {
			return;
		}
		final String json = JSON.toJSONString(formData);
		final JSONObject jsonObject = JSONObject.parseObject(json);
		final Set<String> keySet = jsonObject.keySet();
		Platform.runLater(() -> keySet.forEach(key -> {
			final TextField textField = textFieldMap.get(key);
			if (textField == null) {
				return;
			}
			textField.setText(jsonObject.getString(key));
		}));

	}
}
