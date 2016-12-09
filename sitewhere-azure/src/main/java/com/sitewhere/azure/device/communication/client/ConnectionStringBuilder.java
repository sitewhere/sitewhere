/*******************************************************************************
 Copyright (c) Microsoft Open Technologies (Shanghai) Company Limited.  All rights reserved.

 The MIT License (MIT)

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

 *******************************************************************************/
package com.sitewhere.azure.device.communication.client;

import java.io.IOException;
import java.net.*;

public class ConnectionStringBuilder {

    private final String connectionString;

    private String host;
    private int port;
    private String userName;
    private String password;
    private boolean ssl;

    // amqps://[username]:[password]@[namespace].servicebus.windows.net/ or
    // amqps://[username]:[password]@[namespace].servicebus.chinacloudapi.cn in
    // China
    public ConnectionStringBuilder(String connectionString) throws EventHubException {
	this.connectionString = connectionString;
	this.initialize();
    }

    public String getHost() {
	return this.host;
    }

    public void setHost(String value) {
	this.host = value;
    }

    public int getPort() {
	return this.port;
    }

    public void setPort(int value) {
	this.port = value;
    }

    public String getUserName() {
	return this.userName;
    }

    public void setUserName(String value) {
	this.userName = value;
    }

    public String getPassword() {
	return this.password;
    }

    public void setPassword(String value) {
	this.password = value;
    }

    public boolean getSsl() {
	return this.ssl;
    }

    public void setSsl(boolean value) {
	this.ssl = value;
    }

    @SuppressWarnings("deprecation")
    private void initialize() throws EventHubException {

	URL url;
	try {
	    url = new URL(null, this.connectionString, new NullURLStreamHandler());
	} catch (MalformedURLException e) {
	    throw new EventHubException("connectionString is not valid.", e);
	}

	String protocol = url.getProtocol();
	this.ssl = protocol.equalsIgnoreCase(Constants.SslScheme);
	this.host = url.getHost();
	this.port = url.getPort();

	if (this.port == -1) {
	    this.port = this.ssl ? Constants.DefaultSslPort : Constants.DefaultPort;
	}

	String userInfo = url.getUserInfo();
	if (userInfo != null) {
	    String[] credentials = userInfo.split(":", 2);
	    this.userName = URLDecoder.decode(credentials[0]);
	    this.password = URLDecoder.decode(credentials[1]);
	}
    }

    class NullURLStreamHandler extends URLStreamHandler {

	@Override
	protected URLConnection openConnection(URL u) throws IOException {
	    throw new UnsupportedOperationException("Not supported yet.");
	}
    }
}
