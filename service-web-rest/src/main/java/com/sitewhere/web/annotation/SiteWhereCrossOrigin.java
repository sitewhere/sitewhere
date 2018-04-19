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
