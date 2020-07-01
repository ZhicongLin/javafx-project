package com.mongodb.sync;

import de.felixroske.jfxsupport.AbstractFxmlView;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Description: FX事件
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
@AllArgsConstructor
public class ViewEvent {
	private ViewEvenType viewEvenType;
	private AbstractFxmlView abstractFxmlView;
	private Object object;

	public boolean isPresent(ViewEvenType viewEvenType, Object object) {
		return this.viewEvenType == viewEvenType && this.object == object;
	}

	public enum ViewEvenType {
		show, hide
	}
}
