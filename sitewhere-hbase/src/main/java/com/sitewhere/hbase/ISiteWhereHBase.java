/*
 * SiteWhereColumnFamily.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase;

import org.apache.hadoop.hbase.util.Bytes;

/**
 * Constants associated with the SiteWhere column family.
 * 
 * @author Derek
 */
public interface ISiteWhereHBase {

	/***************
	 * TABLE NAMES *
	 ***************/

	/** Sites table name */
	public static final byte[] UID_TABLE_NAME = Bytes.toBytes("sw-uids");

	/** Sites table name */
	public static final byte[] SITES_TABLE_NAME = Bytes.toBytes("sw-sites");

	/** Events table name */
	public static final byte[] EVENTS_TABLE_NAME = Bytes.toBytes("sw-events");

	/** Devices table name */
	public static final byte[] DEVICES_TABLE_NAME = Bytes.toBytes("sw-devices");

	/** Users table name */
	public static final byte[] USERS_TABLE_NAME = Bytes.toBytes("sw-users");

	/*******************
	 * COLUMN FAMILIES *
	 *******************/

	/** SiteWhere family id */
	public static final byte[] FAMILY_ID = Bytes.toBytes("s");

	/*********************
	 * COMMON QUALIFIERS *
	 *********************/

	/** Column qualifier for site JSON content */
	public static final byte[] JSON_CONTENT = Bytes.toBytes("json");

	/** Column qualifier that indicates a deleted record */
	public static final byte[] DELETED = Bytes.toBytes("deleted");
}