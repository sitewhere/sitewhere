/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.CrossOrigin;

import com.sitewhere.rest.ISiteWhereWebConstants;

/**
 * Adds SiteWhere headers to the standard list.
 * 
 * @author Derek
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
@CrossOrigin(exposedHeaders = { ISiteWhereWebConstants.HEADER_SITEWHERE_ERROR,
	ISiteWhereWebConstants.HEADER_SITEWHERE_ERROR_CODE })
public @interface SiteWhereCrossOrigin {
}
