/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.service;

/**
 * Interface for constants used in web operations.
 * 
 * @author dadams
 */
public interface ISiteWhereWebConstants {

	/** Header that holds sitewhere error string on error response */
	public static final String HEADER_SITEWHERE_ERROR = "X-SiteWhere-Error";

	/** Header that holds sitewhere error code on error response */
	public static final String HEADER_SITEWHERE_ERROR_CODE = "X-SiteWhere-Error-Code";
}