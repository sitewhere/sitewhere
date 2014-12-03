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
	private static final String HARDWARE_ID = "26ac91a5-aa59-44ef-af77-85e470ab0c16";

	/** Port that server socket listens on */
	public static final int SERVER_SOCKET_PORT = 8585;

	@Test
	public void doSocketTest() throws Exception {
		Socket socket = new Socket("localhost", SERVER_SOCKET_PORT);
		byte[] encoded = EventsHelper.generateEncodedMeasurementsMessage(HARDWARE_ID);
		socket.getOutputStream().write(encoded);
		socket.getOutputStream().flush();
		socket.getOutputStream().close();
	}
}