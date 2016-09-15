/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.BufferedMutator;
import org.apache.hadoop.hbase.client.Table;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;

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
    public Admin getAdmin();

    /**
     * Get a table that has global scope.
     * 
     * @param tableName
     * @return
     * @throws SiteWhereException
     */
    public Table getTableInterface(byte[] tableName) throws SiteWhereException;

    /**
     * Get a table with tenant scope. Auto flush is disabled.
     * 
     * @param tenant
     * @param tableName
     * @return
     * @throws SiteWhereException
     */
    public Table getTableInterface(ITenant tenant, byte[] tableName) throws SiteWhereException;

    /**
     * Get a table with tenant scope.
     * 
     * @param tenant
     * @param tableName
     * @param autoFlush
     * @return
     * @throws SiteWhereException
     */
    @Deprecated
    public Table getTableInterface(ITenant tenant, byte[] tableName, boolean autoFlush) throws SiteWhereException;

    /**
     * Get buffered mutator with tenant scope.
     * 
     * @param tenant
     * @param tableName
     * @return
     * @throws SiteWhereException
     */
    public BufferedMutator getBufferedMutator(ITenant tenant, byte[] tableName) throws SiteWhereException;
}