/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.configuration.model;

/**
 * Used to indicate role of an element.
 * 
 * @author Derek
 */
public enum ElementRole {

	/** Top level. Persistence configuration. */
	Top_Persistence,

	/** Persistence container. Datastore configuration. */
	Persistence_Datastore,

	/** Persistence container. Cache provider configuration. */
	Persistence_CacheProvider;
}