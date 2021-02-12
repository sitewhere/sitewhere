/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

import com.sitewhere.microservice.lifecycle.LifecycleComponent;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.sources.spi.IInboundEventReceiver;
import com.sitewhere.sources.spi.socket.ISocketInteractionHandler;
import com.sitewhere.sources.spi.socket.ISocketInteractionHandlerFactory;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Handles interactions where a remote client sends an HTTP request to be
 * processed by SiteWhere.
 */
public class HttpInteractionHandler extends TenantEngineLifecycleComponent
	implements ISocketInteractionHandler<byte[]> {

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
     */
    public static class Factory extends LifecycleComponent implements ISocketInteractionHandlerFactory<byte[]> {

	public Factory() {
	    super(LifecycleComponentType.Other);
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