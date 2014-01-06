/*
 * HbaseSites.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.common;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.regionserver.StoreFile.BloomType;
import org.apache.log4j.Logger;

import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.spi.SiteWhereException;

/**
 * Utility method for SiteWhere HBase tables.
 * 
 * @author Derek
 */
public class SiteWhereTables {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(SiteWhereTables.class);

	/**
	 * Assure that the given table exists and create it if not.
	 * 
	 * @param hbase
	 * @param tableName
	 * @throws SiteWhereException
	 */
	public static void assureTable(ISiteWhereHBaseClient hbase, byte[] tableName, BloomType bloom)
			throws SiteWhereException {
		try {
			String tnameStr = new String(tableName);
			if (!hbase.getAdmin().tableExists(tableName)) {
				LOGGER.info("Table '" + tnameStr + "' does not exist. Creating table...");
				HTableDescriptor table = new HTableDescriptor(tableName);
				HColumnDescriptor family = new HColumnDescriptor(ISiteWhereHBase.FAMILY_ID);
				family.setBloomFilterType(bloom);
				table.addFamily(family);
				hbase.getAdmin().createTable(table);
				LOGGER.info("Table '" + tnameStr + "' created successfully.");
			} else {
				LOGGER.info("Table '" + tnameStr + "' verfied.");
			}
		} catch (Throwable e) {
			throw new SiteWhereException(e);
		}
	}
}