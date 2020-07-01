package com.mongodb.sync.module;

public interface AlertCallback {

	void invoke(boolean confirm);
}
