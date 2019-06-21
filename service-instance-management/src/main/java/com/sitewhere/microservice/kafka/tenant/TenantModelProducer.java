/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.kafka.tenant;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.RecordMetadata;

import com.sitewhere.grpc.client.tenant.TenantModelMarshaler;
import com.sitewhere.grpc.model.TenantModel.GTenantModelUpdateType;
import com.sitewhere.instance.spi.tenant.kafka.ITenantModelProducer;
import com.sitewhere.microservice.kafka.AckPolicy;
import com.sitewhere.microservice.kafka.MicroserviceKafkaProducer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Component that produces tenant model update message on a well-known Kafka
 * topic.
 * 
 * @author Derek
 */
public class TenantModelProducer extends MicroserviceKafkaProducer implements ITenantModelProducer {

    public TenantModelProducer() {
	super(AckPolicy.Leader);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.tenant.spi.kafka.ITenantModelProducer#onTenantAdded(com.
     * sitewhere.spi.tenant.ITenant)
     */
    @Override
    public void onTenantAdded(ITenant tenant) throws SiteWhereException {
	byte[] message = TenantModelMarshaler
		.buildTenantModelUpdateMessage(GTenantModelUpdateType.TENANTMODEL_TENANT_ADDED, tenant);
	deliver(tenant.getToken(), message);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.tenant.spi.kafka.ITenantModelProducer#onTenantUpdated(com.
     * sitewhere.spi.tenant.ITenant)
     */
    @Override
    public void onTenantUpdated(ITenant tenant) throws SiteWhereException {
	byte[] message = TenantModelMarshaler
		.buildTenantModelUpdateMessage(GTenantModelUpdateType.TENANTMODEL_TENANT_UPDATED, tenant);
	deliver(tenant.getToken(), message);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.tenant.spi.kafka.ITenantModelProducer#onTenantDeleted(com.
     * sitewhere.spi.tenant.ITenant)
     */
    @Override
    public void onTenantDeleted(ITenant tenant) throws SiteWhereException {
	byte[] message = TenantModelMarshaler
		.buildTenantModelUpdateMessage(GTenantModelUpdateType.TENANTMODEL_TENANT_DELETED, tenant);
	deliver(tenant.getToken(), message);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.kafka.IKafkaProducer#getTargetTopicName()
     */
    @Override
    public String getTargetTopicName() throws SiteWhereException {
	return getMicroservice().getKafkaTopicNaming().getTenantUpdatesTopic();
    }

    /**
     * Deliver a tenant update record to Kafka.
     * 
     * @param key
     * @param message
     * @throws SiteWhereException
     */
    protected void deliver(String key, byte[] message) throws SiteWhereException {
	Future<RecordMetadata> result = send(key, message);
	try {
	    RecordMetadata metadata = result.get();
	    getLogger().info(String.format("Metadata for delivered tenant record: %s", metadata.toString()));
	} catch (InterruptedException | ExecutionException e) {
	    throw new SiteWhereException(e);
	}
    }
}