/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.test;

import java.util.Arrays;

import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

/**
 * Tests related to MongoDB.
 * 
 * @author Derek
 */
public class MongoTests {

	/** Username for authentication */
	private static final String MONGO_USERNAME = "test";

	/** Password for authentication */
	private static final String MONGO_PASSWORD = "test123";

	/** Database to connect to */
	private static final String MONGO_DATABASE = "sitewhere";

	/** Host to connect to */
	private static final String MONGO_HOSTNAME = "localhost";

	/** Port to connect on */
	private static final int MONGO_PORT = 27017;

	@Test
	public void testAuthentication() throws Exception {
		MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
		builder.maxConnectionIdleTime(60 * 60 * 1000);

		MongoCredential credential =
				MongoCredential.createCredential(MONGO_USERNAME, MONGO_DATABASE, MONGO_PASSWORD.toCharArray());
		MongoClient client =
				new MongoClient(new ServerAddress(MONGO_HOSTNAME, MONGO_PORT), Arrays.asList(credential),
						builder.build());

		client.getDB(MONGO_DATABASE).getStats();
		client.close();
	}
}