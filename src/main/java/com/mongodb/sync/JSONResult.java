package com.mongodb.sync;

import com.alibaba.fastjson.JSONObject;

import lombok.Data;

/**
 * Description: 请求通用格式配置
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
@Data
public class JSONResult {
	private Integer resultCode;
	private String resultMsg;
	private JSONObject data;
}
