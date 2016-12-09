/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.streaming;

import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.streaming.IDeviceStream;

/**
 * Model object for a stream of binary data received from a device.
 * 
 * @author Derek
 */
public class DeviceStream extends MetadataProviderEntity implements IDeviceStream {

    /** Serial version UID */
    private static final long serialVersionUID = -5721420122887571143L;

    /** Parent assignment token */
    private String assignmentToken;

    /** Stream id */
    private String streamId;

    /** Stream content type */
    private String contentType;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.streaming.IDeviceStream#getAssignmentToken()
     */
    public String getAssignmentToken() {
	return assignmentToken;
    }

    public void setAssignmentToken(String assignmentToken) {
	this.assignmentToken = assignmentToken;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.streaming.IDeviceStream#getStreamId()
     */
    public String getStreamId() {
	return streamId;
    }

    public void setStreamId(String streamId) {
	this.streamId = streamId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.streaming.IDeviceStream#getContentType()
     */
    public String getContentType() {
	return contentType;
    }

    public void setContentType(String contentType) {
	this.contentType = contentType;
    }

    public static DeviceStream copy(IDeviceStream input) throws SiteWhereException {
	DeviceStream result = new DeviceStream();
	result.setAssignmentToken(input.getAssignmentToken());
	result.setStreamId(input.getStreamId());
	result.setContentType(input.getContentType());

	MetadataProviderEntity.copy(input, result);
	return result;
    }
}