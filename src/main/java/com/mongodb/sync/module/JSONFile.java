package com.mongodb.sync.module;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.sync.module.dto.FormData;

/**
 * Description: JSON数据文件操作
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
public final class JSONFile {

	public static List<FormData> readJson() {
		File dir = getDir();
		File jsonFile = new File(dir.getPath() + File.separator + "t.json");
		if (jsonFile.exists()) {
			try {
				String jsonStr = FileUtils.readFileToString(jsonFile, "utf-8");
				if (StringUtils.isNoneBlank(jsonStr)) {
					final JSONObject jo = JSONObject.parseObject(jsonStr);
					final Set<String> keySet = jo.keySet();
					List<FormData> formData = new ArrayList<>();
					keySet.forEach(key -> formData.add(jo.getObject(key, FormData.class)));
					return formData;
				}
				log.info("————读取" + jsonFile.getPath() + "文件结束!————");
			} catch (Exception e) {
				log.info("————读取" + jsonFile.getPath() + "文件出现异常，读取失败!————");
				log.error(e.getMessage(), e);
			}
		}
		return new ArrayList<>();
	}

	/**
	 * 新增数据
	 * @param list 原数据列表
	 * @param data 删除的数据
	 */
	public static void removeJson(List<FormData> list, FormData data) {
		final Map<String, Object> dataMap = list.stream().filter(formData -> !formData.getUuid().equals(data.getUuid()))
				.collect(Collectors.toMap(FormData::getUuid, FormData -> FormData));
		dataMap.remove(data.getUuid());
		write(dataMap);

	}

	/**
	 * 新增数据
	 * @param list 原数据列表
	 * @param saveData 新增或者修改的数据
	 */
	public static void writeJson(List<FormData> list, FormData saveData) {
		Map<String, Object> dataMap = list.stream().collect(Collectors.toMap(FormData::getUuid, FormData -> FormData));
		dataMap.put(saveData.getUuid(), saveData);
		write(dataMap);

	}

	/**
	 * 往json中写入数据
	 * @param dataMap
	 */
	private static void write(Map<String, Object> dataMap) {
		String str = JSON.toJSONString(dataMap);
		try {
			File dir = getDir();
			File jsonFile = new File(dir.getPath() + File.separator + "t.json");
			if (!jsonFile.exists()) {
				jsonFile.createNewFile();
			}
			FileUtils.write(jsonFile, str, StandardCharsets.UTF_8);
		} catch (IOException e) {
			log.info("写入文件出错！ {}", e.getMessage(), e);
		}
	}

	/**
	 * 获取json数据文件夹
	 * @return
	 */
	public static File getDir() {
		File file = new File("");
		String filePath = null;
		try {
			filePath = file.getCanonicalPath() + File.separator + "data";
			File fileData = new File(filePath);
			if (!fileData.exists()) {
				fileData.mkdir();
			}
			return fileData;
		} catch (IOException e) {
			log.info("读取文件夹出错！ {}", e.getMessage(), e);
		}
		return file;
	}

}
