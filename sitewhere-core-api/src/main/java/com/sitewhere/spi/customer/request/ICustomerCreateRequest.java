/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.customer.request;

import java.io.Serializable;
import java.util.Map;

/**
 * Interface for arguments needed to create a customer.
 * 
 * @author Derek
 */
public interface ICustomerCreateRequest extends Serializable {

    /**
     * Customer token. Can be set to null if token should be auto-generated.
     * 
     * @return
     */
    public String getToken();

    /**
     * Get token for corresponding customer type.
     * 
     * @return
     */
    public String getCustomerTypeToken();

    /**
     * Get token for parent customer (null if none).
     * 
     * @return
     */
    public String getParentCustomerToken();

    /**
     * Get name.
     * 
     * @return
     */
    public String getName();

    /**
     * Get description.
     * 
     * @return
     */
    public String getDescription();

    /**
     * Get URL for image.
     * 
     * @return
     */
    public String getImageUrl();

    /**
     * Get metadata values.
     * 
     * @return
     */
    public Map<String, String> getMetadata();
}