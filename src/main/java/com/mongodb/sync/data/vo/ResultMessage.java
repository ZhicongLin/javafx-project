/*
 * @(#)ResultMessage.java 2012-10-15 1.0
 * 
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.mongodb.sync.data.vo;

import lombok.Data;

/**
 * Description: ResultMessage.java
 * 
 * @author zhulh
 * @date 2012-10-15
 * 
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-15.1	zhulh		2012-10-15		Create
 * </pre>
 * 
 */
@Data
public class ResultMessage {

    private StringBuilder msg;

    private Object data;

    private boolean success;

}
