package com.mongodb.sync;

import de.felixroske.jfxsupport.SplashScreen;

/**
 * Description: 启动时中间页配置
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
public class CustomSplash extends SplashScreen {
	@Override
	public boolean visible() {
		return super.visible();
	}

	@Override
	public String getImagePath() {
		return "/image/banner1.jpg";
	}
}
