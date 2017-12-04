/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.state;

/**
 * Listener interested in snapshots of the instance topology as microservices
 * are added or removed.
 * 
 * @author Derek
 */
public interface IInstanceTopologySnapshotsListener {

    /**
     * Handle instance topology snapshot.
     * 
     * @param update
     */
    public void onInstanceTopologySnapshot(IInstanceTopologySnapshot snapshot);
}