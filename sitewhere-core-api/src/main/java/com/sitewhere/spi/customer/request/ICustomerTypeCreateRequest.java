/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.customer.request;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Provides information needed to create a customer type.
 * 
 * @author Derek
 */
public interface ICustomerTypeCreateRequest extends Serializable {

    /**
     * Get token that acts as an alias for customer type id.
     * 
     * @return
     */
    public String getToken();

    /**
     * Get the customer type name.
     * 
     * @return
     */
    public String getName();

    /**
     * Get the customer type description.
     * 
     * @return
     */
    public String getDescription();

    /**
     * Get icon shown for customer type.
     * 
     * @return
     */
    public String getIcon();

    /**
     * Get metadata values.
     * 
     * @return
     */
    public Map<String, String> getMetadata();

    /**
     * Get list of customer type tokens which may be contained.
     * 
     * @return
     */
    public List<String> getContainedCustomerTypeTokens();
}
