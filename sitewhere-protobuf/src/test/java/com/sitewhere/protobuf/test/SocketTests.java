/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.protobuf.test;

import java.net.Socket;

import org.junit.Test;

public class SocketTests {

	/** Hardware id for test message */
	private static final String HARDWARE_ID = "46497751-fdbd-46c0-a6db-3f0007b0fd00";

	/** Port that server socket listens on */
	public static final int SERVER_SOCKET_PORT = 8585;

	@Test
	public void doSocketTest() throws Exception {
		Socket socket = new Socket("localhost", SERVER_SOCKET_PORT);
		byte[] encoded = EventsHelper.generateEncodedMeasurementsMessage(HARDWARE_ID);
		socket.getOutputStream().write(encoded);
		socket.getOutputStream().flush();
		socket.getOutputStream().close();
		socket.close();
	}
}