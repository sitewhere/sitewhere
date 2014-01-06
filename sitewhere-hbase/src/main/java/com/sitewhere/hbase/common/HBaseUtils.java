/*
 * HBaseUtils.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.common;

import java.io.IOException;

import org.apache.hadoop.hbase.client.HTableInterface;

import com.sitewhere.spi.SiteWhereException;

/**
 * Handle common HBase functionality.
 * 
 * @author Derek
 */
public class HBaseUtils {

	/**
	 * Prevent having to add custom {@link IOException} handling.
	 * 
	 * @param table
	 * @throws SiteWhereException
	 */
	public static void closeCleanly(HTableInterface table) throws SiteWhereException {
		try {
			if (table != null) {
				table.close();
			}
		} catch (IOException e) {
			throw new SiteWhereException("Exception closing table.", e);
		}
	}
}