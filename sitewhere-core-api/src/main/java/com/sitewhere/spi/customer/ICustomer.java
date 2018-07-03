/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.customer;

import java.util.UUID;

import com.sitewhere.spi.common.ISiteWhereEntity;

/**
 * An entity that represents a customer (device owner) based on a previously
 * defined customer type.
 * 
 * @author Derek
 */
public interface ICustomer extends ISiteWhereEntity {

    /**
     * Get id of corresponding customer type.
     * 
     * @return
     */
    public UUID getCustomerTypeId();

    /**
     * Get id of parent customer (null if none).
     * 
     * @return
     */
    public UUID getParentCustomerId();

    /**
     * Get the customer name.
     * 
     * @return
     */
    public String getName();

    /**
     * Get the description.
     * 
     * @return
     */
    public String getDescription();

    /**
     * Get the image URL.
     * 
     * @return
     */
    public String getImageUrl();
}