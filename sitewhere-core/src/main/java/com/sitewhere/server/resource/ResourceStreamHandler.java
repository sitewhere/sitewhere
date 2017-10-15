/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
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