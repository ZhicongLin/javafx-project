package com.mongodb.sync.data.mongo;

import java.util.zip.Checksum;

public interface DigestChecksum extends Checksum {

	byte[] digest();

}
