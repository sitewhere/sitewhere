/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.test;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.sitewhere.rest.test.SiteWhereClientTester;

public class MultithreadedRestTest {

	/** Number of threads to launch */
	private int numThreads = 100;

	/** Indicates whether to update assignment state from events */
	private boolean updateState = true;

	@Test
	public void doRestTest() throws Exception {
		java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.FINEST);
		java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.FINEST);
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
		System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "ERROR");
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "ERROR");
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "ERROR");

		ExecutorService executor = Executors.newFixedThreadPool(numThreads);
		CompletionService<SiteWhereClientTester.TestResults> completionService =
				new ExecutorCompletionService<SiteWhereClientTester.TestResults>(executor);

		for (int i = 0; i < numThreads; i++) {
			completionService.submit(new SiteWhereClientTester("90389b40-7c25-401b-bf72-98673913d59e", 100,
					updateState));
		}
		for (int i = 0; i < numThreads; ++i) {
			completionService.take().get();
		}
	}
}