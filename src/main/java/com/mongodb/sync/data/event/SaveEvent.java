package com.mongodb.sync.data.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import com.mongodb.sync.module.view.TabView;

/**
 * Description: 保存事件
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
@Setter
@Getter
@AllArgsConstructor
public class SaveEvent {

	private TabView myTab;

	private Long time;
}
