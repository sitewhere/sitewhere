/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.microservice.state;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sitewhere.spi.microservice.state.IInstanceTopologyEntry;
import com.sitewhere.spi.microservice.state.IInstanceTopologySnapshot;

/**
 * Snapshot of instance topology at a given point in time.
 */
public class InstanceTopologySnapshot implements IInstanceTopologySnapshot {

    /** Topology entries by module identifier */
    private Map<String, IInstanceTopologyEntry> topologyEntriesByIdentifier = new ConcurrentHashMap<>();

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologySnapshot#
     * getTopologyEntriesByIdentifier()
     */
    @Override
    public Map<String, IInstanceTopologyEntry> getTopologyEntriesByIdentifier() {
	return topologyEntriesByIdentifier;
    }

    public void setTopologyEntriesByIdentifier(Map<String, IInstanceTopologyEntry> topologyEntriesByIdentifier) {
	this.topologyEntriesByIdentifier = topologyEntriesByIdentifier;
    }
}