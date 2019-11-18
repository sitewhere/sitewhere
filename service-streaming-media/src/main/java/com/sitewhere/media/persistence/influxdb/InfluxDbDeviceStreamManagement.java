/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.media.persistence.influxdb;

import java.util.UUID;

import com.sitewhere.influxdb.InfluxDbClient;
import com.sitewhere.microservice.api.stream.IDeviceStreamDataManagement;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.streaming.IDeviceStreamData;
import com.sitewhere.spi.device.streaming.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Implementation of {@link IDeviceStreamDataManagement} that stores data in
 * InfluxDB.
 */
public class InfluxDbDeviceStreamManagement extends TenantEngineLifecycleComponent
	implements IDeviceStreamDataManagement {

    /** Client */
    private InfluxDbClient client;

    public InfluxDbDeviceStreamManagement() {
	super(LifecycleComponentType.DataStore);
    }

    /*
     * @see com.sitewhere.spi.device.streaming.IDeviceStreamDataManagement#
     * addDeviceStreamData(java.util.UUID,
     * com.sitewhere.spi.device.streaming.request.IDeviceStreamDataCreateRequest)
     */
    @Override
    public IDeviceStreamData addDeviceStreamData(UUID streamId, IDeviceStreamDataCreateRequest request)
	    throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see com.sitewhere.spi.device.streaming.IDeviceStreamDataManagement#
     * getDeviceStreamData(java.util.UUID, long)
     */
    @Override
    public IDeviceStreamData getDeviceStreamData(UUID streamId, long sequenceNumber) throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see com.sitewhere.spi.device.streaming.IDeviceStreamDataManagement#
     * listDeviceStreamDataForAssignment(java.util.UUID,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStreamData> listDeviceStreamDataForAssignment(UUID streamId,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    public InfluxDbClient getClient() {
	return client;
    }

    public void setClient(InfluxDbClient client) {
	this.client = client;
    }
}
