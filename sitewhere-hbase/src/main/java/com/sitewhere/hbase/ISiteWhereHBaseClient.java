/*
 * ISiteWhereHBaseClient.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTableInterface;

import com.sitewhere.spi.SiteWhereException;

/**
 * Interface expected for HBase client implementations.
 * 
 * @author Derek
 */
public interface ISiteWhereHBaseClient {

	/**
	 * Get client configuration.
	 * 
	 * @return
	 */
	public Configuration getConfiguration();

	/**
	 * Get HBase admin interface.
	 * 
	 * @return
	 */
	public HBaseAdmin getAdmin();

	/**
	 * Get the named table interface.
	 * 
	 * @param tableName
	 * @return
	 * @throws SiteWhereException
	 */
	public HTableInterface getTableInterface(byte[] tableName) throws SiteWhereException;
}