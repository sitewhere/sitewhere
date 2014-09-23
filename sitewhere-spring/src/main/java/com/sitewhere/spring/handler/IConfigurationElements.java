/*
 * IConfigurationElements.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
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

	/** SiteWhere namespace */
	public static final String SITEWHERE_NS = "http://www.sitewhere.com/schema/sitewhere";

	/** Constant for top-level configuration element */
	public static final String CONFIGURATION = "configuration";

	/** Constant for section providing datastore information */
	public static final String DATASTORE = "datastore";

	/** Constant for section providing inbound processing chain information */
	public static final String INBOUND_PROCESSING_CHAIN = "inbound-processing-chain";
}