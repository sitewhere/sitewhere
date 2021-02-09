/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.media.persistence.cassandra;

import java.util.UUID;

import com.sitewhere.cassandra.CassandraClient;
import com.sitewhere.microservice.api.stream.IDeviceStreamDataManagement;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.streaming.IDeviceStreamData;
import com.sitewhere.spi.device.streaming.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Implementation of {@link IDeviceStreamDataManagement} that stores data in
 * Apache Cassandra.
 */
public class CassandraDeviceStreamManagement extends TenantEngineLifecycleComponent
	implements IDeviceStreamDataManagement {

    /** Configured Cassandra client */
    private CassandraClient client;

    public CassandraDeviceStreamManagement() {
	super(LifecycleComponentType.DataStore);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getClient() == null) {
	    throw new SiteWhereException("No Cassandra client configured.");
	}
	getClient().start(monitor);
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

    public CassandraClient getClient() {
	return client;
    }

    public void setClient(CassandraClient client) {
	this.client = client;
    }
}
