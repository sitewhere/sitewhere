package com.sitewhere.kafka;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.kafka.common.utils.SystemTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

import kafka.metrics.KafkaMetricsReporter;
import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import scala.Option;
import scala.collection.JavaConversions;
import scala.collection.Seq;

/**
 * Embedded Kafka single-node instance used in cases where an external Kafka
 * cluster is not configured. This is intended for demonstration purposes only
 * and not for real deployments!
 * 
 * @author Derek
 */
public class EmbeddedKafkaCluster extends LifecycleComponent {

    /** Default Kafka broker port */
    public static final String DEFAULT_KAFKA_BROKER_HOST = "localhost";

    /** Default Kafka broker port */
    public static final int DEFAULT_KAFKA_BROKER_PORT = 9092;

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Kafka host */
    private String hostname = DEFAULT_KAFKA_BROKER_HOST;

    /** Kafka port */
    private int port = DEFAULT_KAFKA_BROKER_PORT;

    /** Embedded Kafka server */
    private KafkaServer kafka;

    /** Log directory */
    private File logFolder;

    /** Delete log folder on shutdown? */
    private boolean deleteOnShutdown = false;

    public EmbeddedKafkaCluster() {
	this(DEFAULT_KAFKA_BROKER_HOST, DEFAULT_KAFKA_BROKER_PORT);
    }

    public EmbeddedKafkaCluster(String hostname, int port) {
	super(LifecycleComponentType.Other);
	this.hostname = hostname;
	this.port = port;
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
	this.logFolder = KafkaUtils.createRelativeFolder("embedded-kafka");

	// Connection information for Zookeeper. Make configurable!
	String zk = EmbeddedZookeeper.DEFAULT_ZOOKEEPER_HOST + ":" + EmbeddedZookeeper.DEFAULT_ZOOKEEPER_PORT;

	Properties properties = new Properties();
	properties.setProperty("zookeeper.connect", zk);
	properties.setProperty("broker.id", String.valueOf(1));
	properties.setProperty("host.name", getHostname());
	properties.setProperty("port", Integer.toString(getPort()));
	properties.setProperty("log.dir", getLogFolder().getAbsolutePath());
	properties.setProperty("log.flush.interval.messages", String.valueOf(1));

	// Set up prefix for threads.
	Option<String> threadPrefix = Option.apply("sitewhere-kafka");

	// Set up metrics reporters.
	Seq<KafkaMetricsReporter> metricsReporters = JavaConversions
		.asScalaBuffer(new ArrayList<KafkaMetricsReporter>());

	this.kafka = new KafkaServer(new KafkaConfig(properties), new SystemTime(), threadPrefix, metricsReporters);
	getKafka().startup();
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
	getKafka().shutdown();
	if (isDeleteOnShutdown()) {
	    try {
		FileUtils.forceDelete(getLogFolder());
	    } catch (IOException e) {
		LOGGER.warn("Unable to delete log folder.", e);
	    }
	}
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

    public KafkaServer getKafka() {
	return kafka;
    }

    public void setKafka(KafkaServer kafka) {
	this.kafka = kafka;
    }

    public File getLogFolder() {
	return logFolder;
    }

    public void setLogFolder(File logFolder) {
	this.logFolder = logFolder;
    }

    public boolean isDeleteOnShutdown() {
	return deleteOnShutdown;
    }

    public void setDeleteOnShutdown(boolean deleteOnShutdown) {
	this.deleteOnShutdown = deleteOnShutdown;
    }
}