/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.sitewhere.mongodb;

/**
 * Constants for default MongoDB collection names.
 * 
 * @author dadams
 */
public interface IMongoCollectionNames {

	/** Default collection name for SiteWhere sites */
	public static final String DEFAULT_SITES_COLLECTION_NAME = "sites";

	/** Default collection name for SiteWhere zones */
	public static final String DEFAULT_ZONES_COLLECTION_NAME = "zones";

	/** Default collection name for SiteWhere device specifications */
	public static final String DEFAULT_DEVICE_SPECIFICATIONS_COLLECTION_NAME = "specifications";

	/** Default collection name for SiteWhere device commands */
	public static final String DEFAULT_DEVICE_COMMANDS_COLLECTION_NAME = "commands";

	/** Default collection name for SiteWhere devices */
	public static final String DEFAULT_DEVICES_COLLECTION_NAME = "devices";

	/** Default collection name for SiteWhere device groups */
	public static final String DEFAULT_DEVICE_GROUPS_COLLECTION_NAME = "devicegroups";

	/** Default collection name for SiteWhere device group elements */
	public static final String DEFAULT_DEVICE_GROUP_ELEMENTS_COLLECTION_NAME = "groupelements";

	/** Default collection name for SiteWhere device assignments */
	public static final String DEFAULT_DEVICE_ASSIGNMENTS_COLLECTION_NAME = "assignments";

	/** Default collection name for SiteWhere events */
	public static final String DEFAULT_EVENTS_COLLECTION_NAME = "events";

	/** Default collection name for SiteWhere users */
	public static final String DEFAULT_USERS_COLLECTION_NAME = "users";

	/** Default collection name for SiteWhere granted authorities */
	public static final String DEFAULT_AUTHORITIES_COLLECTION_NAME = "authorities";
}