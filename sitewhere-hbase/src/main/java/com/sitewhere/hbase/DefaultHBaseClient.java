/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.BufferedMutator;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.springframework.beans.factory.InitializingBean;

import com.sitewhere.hbase.common.SiteWhereTables;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Default SiteWhere HBase client implementation.
 * 
 * @author Derek
 */
public class DefaultHBaseClient implements InitializingBean, ISiteWhereHBaseClient {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(DefaultHBaseClient.class);

    /** Zookeeper quorum */
    private String quorum;

    /** Zookeeper client port */
    private int zookeeperClientPort = 2181;

    /** Zookeeper znode parent */
    private String zookeeperZnodeParent = "/hbase";

    /** Zookeeper znode root server */
    private String zookeeperZnodeRootServer = "root-region-server";

    /** HBase configuration */
    private Configuration configuration;

    /** HBase connection */
    private Connection connection;

    /** Standard admin interface */
    private Admin admin;

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
	try {
	    configuration = HBaseConfiguration.create();
	    configuration.set("hbase.zookeeper.quorum", quorum);
	    configuration.set("hbase.zookeeper.property.clientPort", String.valueOf(getZookeeperClientPort()));
	    configuration.set("zookeeper.znode.parent", getZookeeperZnodeParent());
	    configuration.set("zookeeper.znode.rootserver", getZookeeperZnodeRootServer());
	    this.connection = ConnectionFactory.createConnection(configuration);
	    this.admin = connection.getAdmin();
	} catch (Exception e) {
	    throw new SiteWhereException(e);
	}
    }

    /**
     * Stop all connectivity. TODO: Where does this eventually get called?
     */
    public void stop() {
	if (getAdmin() != null) {
	    try {
		getAdmin().shutdown();
	    } catch (IOException e) {
		LOGGER.error("HBaseAdmin did not shut down cleanly.", e);
	    }
	}
	try {
	    getConnection().close();
	} catch (IOException e) {
	    LOGGER.error("HConnection did not close cleanly.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.ISiteWhereHBaseClient#getAdmin()
     */
    @Override
    public Admin getAdmin() {
	return admin;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.ISiteWhereHBaseClient#getConfiguration()
     */
    @Override
    public Configuration getConfiguration() {
	return configuration;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.ISiteWhereHBaseClient#getTableInterface(byte[])
     */
    @Override
    public Table getTableInterface(byte[] tableName) throws SiteWhereException {
	try {
	    return getConnection().getTable(TableName.valueOf(tableName));
	} catch (IOException e) {
	    throw new SiteWhereException("IOException getting HBase table interface.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.hbase.ISiteWhereHBaseClient#getTableInterface(com.sitewhere
     * .spi.tenant.ITenant, byte[])
     */
    @Override
    public Table getTableInterface(ITenant tenant, byte[] tableName) throws SiteWhereException {
	return getTableInterface(tenant, tableName, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.hbase.ISiteWhereHBaseClient#getTableInterface(com.sitewhere
     * .spi.user .ITenant, byte[], boolean)
     */
    @Override
    public Table getTableInterface(ITenant tenant, byte[] tableName, boolean autoFlush) throws SiteWhereException {
	try {
	    byte[] tablename = SiteWhereTables.getTenantTableName(tenant, tableName);
	    return getConnection().getTable(TableName.valueOf(tablename));
	} catch (IOException e) {
	    throw new SiteWhereException("IOException getting HBase table interface.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.ISiteWhereHBaseClient#getBufferedMutator(com.
     * sitewhere.spi.tenant.ITenant, byte[])
     */
    @Override
    public BufferedMutator getBufferedMutator(ITenant tenant, byte[] tableName) throws SiteWhereException {
	try {
	    byte[] tablename = SiteWhereTables.getTenantTableName(tenant, tableName);
	    return getConnection().getBufferedMutator(TableName.valueOf(tablename));
	} catch (IOException e) {
	    throw new SiteWhereException("IOException getting HBase buffered mutator.", e);
	}
    }

    public Connection getConnection() {
	return connection;
    }

    public String getQuorum() {
	return quorum;
    }

    public void setQuorum(String quorum) {
	this.quorum = quorum;
    }

    public int getZookeeperClientPort() {
	return zookeeperClientPort;
    }

    public void setZookeeperClientPort(int zookeeperClientPort) {
	this.zookeeperClientPort = zookeeperClientPort;
    }

    public String getZookeeperZnodeParent() {
	return zookeeperZnodeParent;
    }

    public void setZookeeperZnodeParent(String zookeeperZnodeParent) {
	this.zookeeperZnodeParent = zookeeperZnodeParent;
    }

    public String getZookeeperZnodeRootServer() {
	return zookeeperZnodeRootServer;
    }

    public void setZookeeperZnodeRootServer(String zookeeperZnodeRootServer) {
	this.zookeeperZnodeRootServer = zookeeperZnodeRootServer;
    }
}