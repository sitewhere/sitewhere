/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.socket;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.message.BasicHttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.sources.spi.IInboundEventReceiver;
import com.sitewhere.sources.spi.socket.ISocketInteractionHandler;
import com.sitewhere.sources.spi.socket.ISocketInteractionHandlerFactory;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Handles interactions where a remote client sends an HTTP request to be
 * processed by SiteWhere.
 * 
 * @author Derek
 */
public class HttpInteractionHandler implements ISocketInteractionHandler<byte[]> {

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.socket.ISocketInteractionHandler#
     * process( java.net.Socket,
     * com.sitewhere.spi.device.communication.IInboundEventReceiver)
     */
    @Override
    public void process(Socket socket, IInboundEventReceiver<byte[]> receiver) throws SiteWhereException {
	DefaultBHttpServerConnection conn = new DefaultBHttpServerConnection(8 * 1024);
	try {
	    conn.bind(socket);
	    HttpRequest request = conn.receiveRequestHeader();
	    if (request instanceof HttpEntityEnclosingRequest) {
		conn.receiveRequestEntity((HttpEntityEnclosingRequest) request);
		HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
		if (entity != null) {
		    int data;
		    BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
		    ByteArrayOutputStream out = new ByteArrayOutputStream();
		    while ((data = reader.read()) != -1) {
			out.write(data);
		    }
		    out.close();
		    receiver.onEventPayloadReceived(out.toByteArray(), null);
		}
	    }
	    HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
	    response.setEntity(new StringEntity("Information received by SiteWhere."));
	    conn.sendResponseHeader(response);
	    conn.sendResponseEntity(response);
	} catch (HttpException e) {
	    throw new SiteWhereException("HTTP error processing request in interaction handler.", e);
	} catch (IOException e) {
	    throw new SiteWhereException("I/O error processing HTTP interaction handler.", e);
	} finally {
	    try {
		conn.close();
	    } catch (IOException e) {
		throw new SiteWhereException("Error closing HTTP interaction handler.", e);
	    }
	}
    }

    /**
     * Factory class that produces {@link HttpInteractionHandler} instances.
     * 
     * @author Derek
     */
    public static class Factory extends LifecycleComponent implements ISocketInteractionHandlerFactory<byte[]> {

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

	public Factory() {
	    super(LifecycleComponentType.Other);
	}

	@Override
	public Logger getLogger() {
	    return LOGGER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.socket.
	 * ISocketInteractionHandlerFactory #newInstance()
	 */
	@Override
	public ISocketInteractionHandler<byte[]> newInstance() {
	    return new HttpInteractionHandler();
	}
    }
}