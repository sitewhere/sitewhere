/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.batch;

import com.sitewhere.spi.device.IDevice;

/**
 * Extends {@link BatchElement} to support fields that can be included on REST
 * calls.
 * 
 * @author Derek
 */
public class MarshaledBatchElement extends BatchElement {

    /** Serial version UID */
    private static final long serialVersionUID = -6204681263326181682L;

    /** Referenced device */
    private IDevice device;

    public IDevice getDevice() {
	return device;
    }

    public void setDevice(IDevice device) {
	this.device = device;
    }
}