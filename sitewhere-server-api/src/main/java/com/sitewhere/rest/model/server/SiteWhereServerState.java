/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.server;

import com.sitewhere.spi.server.ISiteWhereServerState;

/**
 * Model implementation of {@link ISiteWhereServerState}.
 * 
 * @author Derek
 */
public class SiteWhereServerState implements ISiteWhereServerState {

    /** Unique node id */
    private String nodeId;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServerState#getNodeId()
     */
    public String getNodeId() {
	return nodeId;
    }

    public void setNodeId(String nodeId) {
	this.nodeId = nodeId;
    }
}