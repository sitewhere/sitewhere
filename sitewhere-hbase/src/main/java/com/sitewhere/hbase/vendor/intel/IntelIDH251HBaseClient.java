/*
 * IntelIDH251HBaseClient.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.vendor.intel;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.spi.SiteWhereException;

/**
 * SiteWhere HBase client configured to work with Intel IDH 2.5.1.
 * 
 * @author Derek
 */
@SuppressWarnings("deprecation")
public class IntelIDH251HBaseClient implements InitializingBean, ISiteWhereHBaseClient {

	/** Static logger instance */
	private static final Logger LOGGER = Logger.getLogger(IntelIDH251HBaseClient.class);

	/** Zookeeper quorum */
	private String quorum;

	/** HBase configuration */
	private Configuration configuration;

	/** Standard admin interface */
	private HBaseAdmin admin;

	/** Allow connection to be reused */
	private HTablePool pool;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		try {
			LOGGER.info("Starting SiteWhere HBase client for Intel IDH (2.5.1)");
			configuration = HBaseConfiguration.create();
			configuration.set("hbase.zookeeper.quorum", "centos");
			configuration.set("hbase.rootdir", "hdfs://centos:8020/hbase");
			configuration.set("dfs.support.append", "true");
			configuration.set("hbase.rpc.engine", "org.apache.hadoop.hbase.ipc.SecureRpcEngine");
			configuration.set("dfs.client.read.shortcircuit", "true");
			configuration.set("hbase.abort.disconected.batchmutate", "true");
			configuration.set("hbase.cluster.distributed", "true");
			configuration.set("hbase.coprocessor.master.classes",
					"org.apache.hadoop.hbase.search.LuceneMasterCoprocessor");
			configuration.set(
					"hbase.coprocessor.region.classes",
					"org.apache.hadoop.hbase.coprocessor.AggregateImplementation,org.apache.hadoop.hbase.coprocessor.GroupByImplementation,org.apache.hadoop.hbase.coprocessor.search.IndexSearcherEndpoint,org.apache.hadoop.hbase.blobstore.compactions.BlobStoreCompactionCoprocessor");
			configuration.set("hbase.hregion.majorcompaction.cron", "0 0 1 * * ?");
			configuration.set("hbase.hregion.max.filesize", "3758096384");
			configuration.set("hbase.hregion.memstore.flush.size", "67108864");
			configuration.set("hbase.hregion.memstore.mslab.enabled", "true");
			configuration.set("hbase.partition.ignore.unavailable.clusters", "false");
			configuration.set("hbase.regions.slop", "0");
			configuration.set("hbase.regionserver.coprocessorhandler.count", "100");
			configuration.set("hbase.regionserver.handler.count", "100");
			configuration.set("hbase.regionserver.lease.period", "600000");
			configuration.set("hbase.rpc.timeout", "120000");
			configuration.set("hbase.use.partition.table", "false");
			configuration.set("hbase.zookeeper.property.maxClientCnxns", "2000");
			configuration.set("hfile.block.cache.size", "0.1");
			configuration.set("hregion.index.path", "hdfs:////regionsIndex");
			configuration.set("zookeeper.session.timeout", "180000");
			this.admin = new HBaseAdmin(configuration);
			this.pool = new HTablePool(configuration, 5);
		} catch (Exception e) {
			throw new SiteWhereException(e);
		}
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
	 * @see com.sitewhere.hbase.ISiteWhereHBaseClient#getAdmin()
	 */
	@Override
	public HBaseAdmin getAdmin() {
		return admin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.hbase.ISiteWhereHBaseClient#getTableInterface(byte[])
	 */
	@Override
	public HTableInterface getTableInterface(byte[] tableName) throws SiteWhereException {
		return pool.getTable(tableName);
	}

	public String getQuorum() {
		return quorum;
	}

	public void setQuorum(String quorum) {
		this.quorum = quorum;
	}
}