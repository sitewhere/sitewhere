/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.tenant.kafka;

import com.sitewhere.grpc.kafka.model.KafkaModel.GTenantModelUpdateType;
import com.sitewhere.grpc.model.marshaler.KafkaModelMarshaler;
import com.sitewhere.microservice.kafka.MicroserviceKafkaProducer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.tenant.spi.kafka.ITenantModelProducer;

/**
 * Component that produces tenant model update message on a well-known Kafka
 * topic.
 * 
 * @author Derek
 */
public class TenantModelProducer extends MicroserviceKafkaProducer implements ITenantModelProducer {

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.tenant.spi.kafka.ITenantModelProducer#onTenantAdded(com.
     * sitewhere.spi.tenant.ITenant)
     */
    @Override
    public void onTenantAdded(ITenant tenant) throws SiteWhereException {
	byte[] message = KafkaModelMarshaler
		.buildTenantModelUpdateMessage(GTenantModelUpdateType.TENANTMODEL_TENANT_ADDED, tenant);
	send(tenant.getToken(), message);
	getLogger().info("Sent Kafka tenant model update for added tenant.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.tenant.spi.kafka.ITenantModelProducer#onTenantUpdated(com.
     * sitewhere.spi.tenant.ITenant)
     */
    @Override
    public void onTenantUpdated(ITenant tenant) throws SiteWhereException {
	byte[] message = KafkaModelMarshaler
		.buildTenantModelUpdateMessage(GTenantModelUpdateType.TENANTMODEL_TENANT_UPDATED, tenant);
	send(tenant.getToken(), message);
	getLogger().info("Sent Kafka tenant model update for updated tenant.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.tenant.spi.kafka.ITenantModelProducer#onTenantDeleted(com.
     * sitewhere.spi.tenant.ITenant)
     */
    @Override
    public void onTenantDeleted(ITenant tenant) throws SiteWhereException {
	byte[] message = KafkaModelMarshaler
		.buildTenantModelUpdateMessage(GTenantModelUpdateType.TENANTMODEL_TENANT_DELETED, tenant);
	send(tenant.getToken(), message);
	getLogger().info("Sent Kafka tenant model update for deleted tenant.");
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
}