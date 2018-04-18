/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.docker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.IOUtils;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;

/**
 * Helper methods for managing marker file used by Docker to determine whether
 * microservice has been initialized.
 * 
 * @author Derek
 */
public class InitializationMarkerUtils {

    /** Path to marker file indicating microservice is initialized */
    public static final String MARKER_PATH = "/opt/initialized";

    /**
     * Remove existing initialization marker file.
     * 
     * @param microservice
     * @throws SiteWhereException
     */
    public static void removeExistingInitializationMarker(IMicroservice<?> microservice) throws SiteWhereException {
	File file = new File(MARKER_PATH);
	if (file.exists()) {
	    file.delete();
	}
    }

    /**
     * Create initialization marker file.
     * 
     * @param microservice
     * @throws SiteWhereException
     */
    public static void createInitializationMarker(IMicroservice<?> microservice) throws SiteWhereException {
	try {
	    FileOutputStream out = new FileOutputStream(MARKER_PATH);
	    out.write(new String("Initialized " + microservice.getComponentName() + " at " + new Date().toString())
		    .getBytes());
	    IOUtils.closeQuietly(out);
	} catch (FileNotFoundException e) {
	    throw new SiteWhereException("Unable to create initialization marker.", e);
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to write initialization marker.", e);
	}
    }
}