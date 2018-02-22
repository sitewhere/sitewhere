/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.persistence.hbase;

import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.spi.SiteWhereException;

/**
 * HBase specifics for dealing with SiteWhere assets.
 * 
 * @author Derek
 */
public class HBaseAsset {

    /** Column qualifier for asset type indicator */
    public static final byte[] ASSET_TYPE_INDICATOR = Bytes.toBytes("t");

    /**
     * Get assets table based on context.
     * 
     * @param context
     * @return
     * @throws SiteWhereException
     */
    protected static Table getAssetsTableInterface(IHBaseContext context) throws SiteWhereException {
	return context.getClient().getTableInterface(context.getTenant(), ISiteWhereHBase.ASSETS_TABLE_NAME);
    }
}