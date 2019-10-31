/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.marshaling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.rest.model.batch.BatchElement;
import com.sitewhere.rest.model.batch.MarshaledBatchElement;
import com.sitewhere.rest.model.common.PersistentEntity;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.batch.IBatchElement;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceManagement;

/**
 * Configurable helper class that allows {@link BatchElement} model objects to
 * be created from {@link IBatchElement} SPI objects.
 */
public class BatchElementMarshalHelper {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(BatchElementMarshalHelper.class);

    /** Include device information */
    private boolean includeDevice;

    /**
     * Convert the SPI into a model object based on marshaling parameters.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public MarshaledBatchElement convert(IBatchElement source, IDeviceManagement deviceManagement)
	    throws SiteWhereException {
	if (source == null) {
	    return null;
	}
	MarshaledBatchElement element = new MarshaledBatchElement();
	element.setId(source.getId());
	element.setBatchOperationId(source.getBatchOperationId());
	element.setDeviceId(source.getDeviceId());
	element.setProcessingStatus(source.getProcessingStatus());
	element.setProcessedDate(source.getProcessedDate());

	if (isIncludeDevice()) {
	    IDevice device = deviceManagement.getDevice(source.getDeviceId());
	    if (device != null) {
		element.setDevice(device);
	    } else {
		LOGGER.warn("Invalid device reference in batch element.");
	    }
	}

	PersistentEntity.copy(source, element);
	return element;
    }

    public boolean isIncludeDevice() {
	return includeDevice;
    }

    public void setIncludeDevice(boolean includeDevice) {
	this.includeDevice = includeDevice;
    }
}