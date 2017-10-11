package com.sitewhere.tenant.spi.kafka;

import com.sitewhere.microservice.spi.kafka.IMicroserviceKafkaProducer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Kafka producer that makes tenant model updates available to a well known
 * topic.
 * 
 * @author Derek
 */
public interface ITenantModelProducer extends IMicroserviceKafkaProducer {

    /**
     * Produce message indicating tenant was added.
     * 
     * @param tenant
     * @throws SiteWhereException
     */
    public void onTenantAdded(ITenant tenant) throws SiteWhereException;

    /**
     * Produce message indicating tenant was updated.
     * 
     * @param tenant
     * @throws SiteWhereException
     */
    public void onTenantUpdated(ITenant tenant) throws SiteWhereException;

    /**
     * Produce message indicating tenant was deleted.
     * 
     * @param tenant
     * @throws SiteWhereException
     */
    public void onTenantDeleted(ITenant tenant) throws SiteWhereException;
}