/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.configuration.model;

/**
 * Type indicator for attribute.
 */
public enum AttributeType {

    /** String value */
    String,

    /** Whole number value */
    Integer,

    /** Decimal number value */
    Decimal,

    /** Boolean value */
    Boolean,

    /** Script */
    Script,

    /** Reference to a device type by id */
    DeviceTypeReference,

    /** Reference to a customer by id */
    CustomerReference,

    /** Reference to an area by id */
    AreaReference,

    /** Reference to an asset by id */
    AssetReference;
}
