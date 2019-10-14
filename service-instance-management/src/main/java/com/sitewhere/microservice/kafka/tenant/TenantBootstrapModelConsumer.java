/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.kafka.tenant;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;

import com.sitewhere.grpc.client.tenant.TenantModelConverter;
import com.sitewhere.grpc.client.tenant.TenantModelMarshaler;
import com.sitewhere.grpc.model.TenantModel.GTenantModelUpdate;
import com.sitewhere.grpc.model.TenantModel.GTenantModelUpdateType;
import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.instance.spi.tenant.kafka.ITenantBootstrapModelConsumer;
import com.sitewhere.microservice.kafka.DirectKafkaConsumer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Kafka consumer that listens to the tenant model updates topic and bootstraps
 * newly added tenants.
 * 
 * @author Derek
 */
public class TenantBootstrapModelConsumer extends DirectKafkaConsumer implements ITenantBootstrapModelConsumer {

    /** Consumer id */
    private static String CONSUMER_ID = UUID.randomUUID().toString();

    /** Suffix for group id */
    private static String GROUP_ID_SUFFIX = ".tenant-bootstrap-consumers";

    /** Number of threads bootstrapping tenants before queued */
    private static final int CONCURRENT_TENANT_BOOTSTRAP_THREADS = 3;

    /** Executor */
    private ExecutorService executor;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.kafka.IMicroserviceKafkaConsumer#
     * getConsumerId()
     */
    @Override
    public String getConsumerId() throws SiteWhereException {
	return CONSUMER_ID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.kafka.IMicroserviceKafkaConsumer#
     * getConsumerGroupId()
     */
    @Override
    public String getConsumerGroupId() throws SiteWhereException {
	return getMicroservice().getKafkaTopicNaming().getInstancePrefix() + GROUP_ID_SUFFIX;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer#
     * getSourceTopicNames()
     */
    @Override
    public List<String> getSourceTopicNames() throws SiteWhereException {
	return Collections.singletonList(getMicroservice().getKafkaTopicNaming().getTenantUpdatesTopic());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.kafka.MicroserviceKafkaConsumer#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);
	executor = Executors.newFixedThreadPool(CONCURRENT_TENANT_BOOTSTRAP_THREADS,
		new TenantBootstrapperThreadFactory());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.kafka.MicroserviceKafkaConsumer#stop(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.stop(monitor);
	if (executor != null) {
	    executor.shutdown();
	}
    }

    /*
     * @see
     * com.sitewhere.microservice.kafka.DirectKafkaConsumer#attemptToProcess(org.
     * apache.kafka.common.TopicPartition, java.util.List)
     */
    @Override
    public void attemptToProcess(TopicPartition topicPartition, List<ConsumerRecord<String, byte[]>> records)
	    throws SiteWhereException {
	for (ConsumerRecord<String, byte[]> record : records) {
	    received(record.key(), record.value());
	}
    }

    public void received(String key, byte[] message) throws SiteWhereException {
	GTenantModelUpdate update = TenantModelMarshaler.parseTenantModelUpdateMessage(message);

	// If a tenant was added, bootstrap it.
	getLogger().info("Received tenant model update message.");
	if (update.getType() == GTenantModelUpdateType.TENANTMODEL_TENANT_ADDED) {
	    ITenant tenant = TenantModelConverter.asApiTenant(update.getTenant());
	    executor.execute(new TenantBootstrapper(tenant));
	} else {
	    getLogger()
		    .info(String.format("Unknown tenant model update message type %s.", update.getType().toString()));
	}
    }

    /**
     * Thread that takes care of bootstrapping a tenant based on its template.
     */
    private class TenantBootstrapper implements Runnable {

	/** Tenant to bootstrap */
	@SuppressWarnings("unused")
	private ITenant tenant;

	public TenantBootstrapper(ITenant tenant) {
	    this.tenant = tenant;
	}

	@Override
	public void run() {
	    // try {
	    // getLogger().info("About to bootstrap new tenant.");
	    // CuratorFramework curator =
	    // getMicroservice().getZookeeperManager().getCurator();
	    // createTenantConfigurationIfNotFound(curator);
	    // } catch (SiteWhereException e) {
	    // getLogger().error("Unable to bootstrap tenant.", e);
	    // } catch (Throwable e) {
	    // getLogger().error("Unhandled exception while bootstrapping tenant.", e);
	    // }
	}
    }

    /**
     * Get instance management microservice.
     * 
     * @return
     */
    protected IInstanceManagementMicroservice<?> getInstanceManagementMicroservice() {
	return (IInstanceManagementMicroservice<?>) getMicroservice();
    }

    /** Used for naming tenant bootstrapper threads */
    private class TenantBootstrapperThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Tenant Bootstrapper " + counter.incrementAndGet());
	}
    }
}