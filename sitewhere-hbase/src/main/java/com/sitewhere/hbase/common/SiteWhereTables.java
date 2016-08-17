/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.common;

import java.nio.ByteBuffer;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.regionserver.BloomType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Utility method for SiteWhere HBase tables.
 * 
 * @author Derek
 */
public class SiteWhereTables {

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

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
			if (!hbase.getAdmin().tableExists(TableName.valueOf(tableName))) {
				LOGGER.info("Table '" + tnameStr + "' does not exist. Creating table...");
				HTableDescriptor table = new HTableDescriptor(TableName.valueOf(tableName));
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

	/**
	 * Assure a table exists for the given tenant.
	 * 
	 * @param context
	 * @param tableName
	 * @param bloom
	 * @throws SiteWhereException
	 */
	public static void assureTenantTable(IHBaseContext context, byte[] tableName, BloomType bloom)
			throws SiteWhereException {
		try {
			String tnameStr = new String(tableName);
			byte[] tenantTableName = getTenantTableName(context.getTenant(), tableName);
			if (!context.getClient().getAdmin().tableExists(TableName.valueOf(tenantTableName))) {
				LOGGER.info("Table '" + tnameStr + "' does not exist. Creating table...");
				HTableDescriptor table = new HTableDescriptor(TableName.valueOf(tenantTableName));
				HColumnDescriptor family = new HColumnDescriptor(ISiteWhereHBase.FAMILY_ID);
				family.setBloomFilterType(bloom);
				table.addFamily(family);
				context.getClient().getAdmin().createTable(table);
				LOGGER.info("Table '" + tnameStr + "' created successfully.");
			} else {
				LOGGER.info("Table '" + tnameStr + "' verfied.");
			}
		} catch (Throwable e) {
			throw new SiteWhereException(e);
		}
	}

	/**
	 * Get table name for a given tenant.
	 * 
	 * @param tenant
	 * @param tableName
	 * @return
	 * @throws SiteWhereException
	 */
	public static byte[] getTenantTableName(ITenant tenant, byte[] tableName) throws SiteWhereException {
		byte[] prefix = (tenant.getId() + "-").getBytes();
		ByteBuffer buffer = ByteBuffer.allocate(prefix.length + tableName.length);
		buffer.put(prefix);
		buffer.put(tableName);
		return buffer.array();
	}
}