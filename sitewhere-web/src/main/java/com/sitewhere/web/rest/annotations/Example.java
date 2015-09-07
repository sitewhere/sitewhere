/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates and example that illustrates calling a REST method.
 * 
 * @author Derek
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Example {

	/** Stage example applies to */
	public enum Stage {
		Request, Response;
	}

	/** Example description markdown relative path */
	String description() default "";

	/** Stage example applies to */
	Stage stage() default Stage.Request;

	/** Object example for marshaled JSON */
	Class<?> json();
}