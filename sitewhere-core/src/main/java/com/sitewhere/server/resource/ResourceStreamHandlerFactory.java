package com.sitewhere.server.resource;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * Factory that allows the custom SiteWhere resource protocol to be plugged in
 * for URL resolution.
 * 
 * @author Derek
 */
public class ResourceStreamHandlerFactory implements URLStreamHandlerFactory {

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.net.URLStreamHandlerFactory#createURLStreamHandler(java.lang.String)
     */
    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
	if (ResourceUrlConnection.PROTO_SITEWHERE.equals(protocol)) {
	    return new ResourceStreamHandler();
	}
	return null;
    }
}