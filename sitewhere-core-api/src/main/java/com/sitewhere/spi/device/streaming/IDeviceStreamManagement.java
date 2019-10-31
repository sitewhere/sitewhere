/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.streaming;

import java.util.UUID;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Interface for device stream management operations.
 */
public interface IDeviceStreamManagement {

    /**
     * Create a new {@link IDeviceStream} associated with an assignment.
     * 
     * @param assignmentId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStream createDeviceStream(UUID assignmentId, IDeviceStreamCreateRequest request)
	    throws SiteWhereException;

    /**
     * Get an existing device stream by id.
     * 
     * @param streamId
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStream getDeviceStream(UUID streamId) throws SiteWhereException;

    /**
     * List device streams for the assignment that meet the given criteria.
     * 
     * @param assignmentId
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceStream> listDeviceStreams(UUID assignmentId, ISearchCriteria criteria)
	    throws SiteWhereException;
}
