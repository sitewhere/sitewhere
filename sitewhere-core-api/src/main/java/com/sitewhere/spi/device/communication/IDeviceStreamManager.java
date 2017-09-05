/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.communication;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceStreamData;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.device.event.request.ISendDeviceStreamDataRequest;
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Manages creation of {@link IDeviceStream} entities based on requests from
 * devices.
 * 
 * @author Derek
 */
public interface IDeviceStreamManager extends ITenantLifecycleComponent {

    /**
     * Handle request for creating a new {@link IDeviceStream}.
     * 
     * @param hardwareId
     * @param request
     * @throws SiteWhereException
     */
    public void handleDeviceStreamRequest(String hardwareId, IDeviceStreamCreateRequest request)
	    throws SiteWhereException;

    /**
     * Handle request for creating new {@link IDeviceStreamData}.
     * 
     * @param hardwareId
     * @param request
     * @throws SiteWhereException
     */
    public void handleDeviceStreamDataRequest(String hardwareId, IDeviceStreamDataCreateRequest request)
	    throws SiteWhereException;

    /**
     * Handle request for sending data from an {@link IDeviceStream} to a remote
     * device.
     * 
     * @param hardwareId
     * @param request
     * @throws SiteWhereException
     */
    public void handleSendDeviceStreamDataRequest(String hardwareId, ISendDeviceStreamDataRequest request)
	    throws SiteWhereException;
}