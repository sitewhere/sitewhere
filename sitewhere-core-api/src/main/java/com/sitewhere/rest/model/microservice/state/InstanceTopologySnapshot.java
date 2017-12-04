/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.microservice.state;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.spi.microservice.state.IInstanceTopologyEntry;
import com.sitewhere.spi.microservice.state.IInstanceTopologySnapshot;

/**
 * Snapshot of instance topology at a given point in time.
 * 
 * @author Derek
 */
public class InstanceTopologySnapshot implements IInstanceTopologySnapshot {

    /** Topology entries */
    private List<IInstanceTopologyEntry> topologyEntries = new ArrayList<>();

    @Override
    public List<IInstanceTopologyEntry> getTopologyEntries() {
	return topologyEntries;
    }

    public void setTopologyEntries(List<IInstanceTopologyEntry> topologyEntries) {
	this.topologyEntries = topologyEntries;
    }
}