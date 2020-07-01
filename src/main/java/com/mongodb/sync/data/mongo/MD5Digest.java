package com.mongodb.sync.data.mongo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Description: 如何描述该类
 *  
 * @author zhongzh
 * @date 2016年7月7日
 * @version 1.0
 * 
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年7月7日.1	zhongzh		2016年7月7日		Create
 * </pre>
 *
 */
public class MD5Digest implements DigestChecksum {

	/**
	 * The message digest associated with this stream.
	 */
	protected MessageDigest digest;

	public MD5Digest() {
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("NO MD5!", e);
		}
		digest.reset();
	}

	/** 
	 * 如何描述该方法
	 * 
	 * (non-Javadoc)
	 * @see java.util.zip.Checksum#update(int)
	 */
	@Override
	public void update(int b) {
		digest.update((byte) b);
	}

	/** 
	 * 如何描述该方法
	 * 
	 * (non-Javadoc)
	 * @see java.util.zip.Checksum#update(byte[], int, int)
	 */
	@Override
	public void update(byte[] b, int off, int len) {
		if (b == null) {
			throw new NullPointerException();
		}
		if (off < 0 || len < 0 || off > b.length - len) {
			throw new ArrayIndexOutOfBoundsException();
		}
		digest.update(b, off, len);
	}

	/** 
	 * 如何描述该方法
	 * 
	 * (non-Javadoc)
	 * @see java.util.zip.Checksum#getValue()
	 */
	@Override
	public long getValue() {
		throw new UnsupportedOperationException("");
	}

	/** 
	 * 如何描述该方法
	 * 
	 * (non-Javadoc)
	 * @see java.util.zip.Checksum#reset()
	 */
	@Override
	public void reset() {
		digest.reset();
	}

	@Override
	public byte[] digest() {
		return digest.digest();
	}

}
