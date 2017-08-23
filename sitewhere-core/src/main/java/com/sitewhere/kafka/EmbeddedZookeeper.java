package com.sitewhere.kafka;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.server.NIOServerCnxnFactory;
import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ZooKeeperServer;

import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Embedded Zookeeper instance used to manage {@link EmbeddedKafkaCluster}.
 * 
 * @author Derek
 */
public class EmbeddedZookeeper extends LifecycleComponent {

    /** Default Zookeeper host */
    public static final String DEFAULT_ZOOKEEPER_HOST = "localhost";

    /** Default Zookeeper port */
    public static final int DEFAULT_ZOOKEEPER_PORT = 2181;

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Zookeeper host */
    private String hostname = DEFAULT_ZOOKEEPER_HOST;

    /** Zookeeper port */
    private int port = DEFAULT_ZOOKEEPER_PORT;

    /** Used by Zookeeper constructor */
    private int tickTime = 500;

    /** Factory for creating Zookeeper connection */
    private ServerCnxnFactory factory;

    /** Snapshot directory on file system */
    private File snapshotDir;

    /** Log directory on file system */
    private File logDir;

    /** Delete log folder on shutdown? */
    private boolean deleteOnShutdown = false;

    public EmbeddedZookeeper() {
	this(DEFAULT_ZOOKEEPER_HOST, DEFAULT_ZOOKEEPER_PORT);
    }

    public EmbeddedZookeeper(String hostname, int port) {
	this(hostname, port, 500);
    }

    public EmbeddedZookeeper(String hostname, int port, int tickTime) {
	super(LifecycleComponentType.Other);
	this.hostname = hostname;
	this.port = port;
	this.tickTime = tickTime;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	try {
	    this.factory = NIOServerCnxnFactory.createFactory(new InetSocketAddress(getHostname(), getPort()), 1024);
	    this.snapshotDir = KafkaUtils.createRelativeFolder("embedded-zk/snapshot");
	    this.logDir = KafkaUtils.createRelativeFolder("embedded-zk/log");

	    try {
		factory.startup(new ZooKeeperServer(snapshotDir, logDir, tickTime));
	    } catch (InterruptedException e) {
		throw new IOException(e);
	    }
	} catch (IOException e) {
	    throw new SiteWhereException(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	factory.shutdown();
	if (isDeleteOnShutdown()) {
	    try {
		FileUtils.forceDelete(snapshotDir);
	    } catch (IOException e) {
		LOGGER.warn("Unable to delete snapshot folder.", e);
	    }
	    try {
		FileUtils.forceDelete(logDir);
	    } catch (IOException e) {
		LOGGER.warn("Unable to delete log folder.", e);
	    }
	}
    }

    public String getHostname() {
	return hostname;
    }

    public void setHostname(String hostname) {
	this.hostname = hostname;
    }

    public int getPort() {
	return port;
    }

    public void setPort(int port) {
	this.port = port;
    }

    public boolean isDeleteOnShutdown() {
	return deleteOnShutdown;
    }

    public void setDeleteOnShutdown(boolean deleteOnShutdown) {
	this.deleteOnShutdown = deleteOnShutdown;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}