package com.sitewhere.server.resource;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * Invoked as hook for URL resolution when URL protocol is 'sitewhere'.
 * 
 * NOTE: Classname *must* be Handler for this to be recognized correctly by the
 * JVM.
 * 
 * @author Derek
 *
 */
public class ResourceStreamHandler extends URLStreamHandler {

    /*
     * (non-Javadoc)
     * 
     * @see java.net.URLStreamHandler#openConnection(java.net.URL)
     */
    @Override
    protected URLConnection openConnection(URL url) throws IOException {
	return new ResourceUrlConnection(url);
    }
}