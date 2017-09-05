/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.groovy.device.event.processor.multicast;

import com.sitewhere.spi.SiteWhereException;

/**
 * {@link AllWithSpecificationMulticaster} that uses String routes.
 * 
 * @author Derek
 */
public class AllWithSpecificationStringMulticaster extends AllWithSpecificationMulticaster<String> {

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.groovy.device.communication.multicaster.
     * AllWithSpecificationMulticaster #convertRoute(java.lang.Object)
     */
    @Override
    public String convertRoute(Object scriptResult) throws SiteWhereException {
	if (!(scriptResult instanceof String)) {
	    throw new SiteWhereException("Multicaster script result expected to be of type String.");
	}
	return (String) scriptResult;
    }
}