package com.mongodb.sync.data.mongo;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DynamicUtils {

	public static String getRandomUUID() {
		String var0 = UUID.randomUUID().toString();
		var0 = var0.replaceAll("-", "");
		return var0;
	}


	public static void main(String[] var0) {
		System.out.println(UUID.randomUUID().toString().replaceAll("-", ""));
		System.out.println(System.currentTimeMillis());
	}
}
