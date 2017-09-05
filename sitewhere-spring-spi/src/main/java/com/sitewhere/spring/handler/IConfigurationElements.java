/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spring.handler;

/**
 * Constants related to Spring configuration elements.
 * 
 * @author Derek
 */
public interface IConfigurationElements {

    /** SiteWhere community edition namespace */
    public static final String SITEWHERE_COMMUNITY_NS = "http://www.sitewhere.com/schema/sitewhere/ce";

    /** SiteWhere enterprise edition namespace */
    public static final String SITEWHERE_ENTERPRISE_NS = "http://www.sitewhere.com/schema/sitewhere/ee";

    /** SiteWhere community edition tenant namespace */
    public static final String SITEWHERE_CE_TENANT_NS = "http://www.sitewhere.com/schema/sitewhere/ce/tenant";

    /** Constant for top-level configuration element */
    public static final String CONFIGURATION = "configuration";

    /** Constant for top-level tenant configuration element */
    public static final String TENANT_CONFIGURATION = "tenant-configuration";
}