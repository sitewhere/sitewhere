/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.kafka;

import org.springframework.beans.factory.annotation.Autowired;

import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Class for locating SiteWhere Kafka topics.
 * 
 * @author Derek
 */
public class KafkaTopicNaming implements IKafkaTopicNaming {

    /** Separator used to partition topic name */
    protected static final String SEPARATOR = ".";

    /** Base name prepended to all SiteWhere topics */
    protected static final String BASE_NAME = "sitewhere";

    /** Global topic indicator */
    protected static final String GLOBAL_INDICATOR = "global";

    /** Tenant topic indicator */
    protected static final String TENANT_INDICATOR = "tenant";

    /** Topic suffix for tenant model updates */
    protected static final String TENANT_MODEL_UPDATES_SUFFIX = "tenant-model-updates";

    /** Topic suffix for events decoded by event sources for a tenant */
    protected static final String TENANT_TOPIC_EVENT_SOURCE_DECODED_EVENTS = "event-source-decoded-events";

    /** Topic suffix for events that could not be decoded for a tenant */
    protected static final String TENANT_TOPIC_EVENT_SOURCE_FAILED_DECODE_EVENTS = "event-source-failed-decode-events";

    /** Topic suffix for events that should be reprocessed */
    protected static final String TENANT_TOPIC_INBOUND_REPROCESS_EVENTS = "inbound-reprocess-events";

    /** Topic suffix for events that have been persisted */
    protected static final String TENANT_TOPIC_INBOUND_PERSISTED_EVENTS = "inbound-persisted-events";

    /** Topic suffix for tenant events sent to unregistered devices */
    protected static final String TENANT_TOPIC_INBOUND_UNREGISTERED_DEVICE_EVENTS = "inbound-unregistered-device-events";

    /** Topic suffix for events that have been persisted and enriched */
    protected static final String TENANT_TOPIC_INBOUND_ENRICHED_EVENTS = "inbound-enriched-events";

    @Autowired
    private IInstanceSettings instanceSettings;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.kafka.IKafkaTopicNaming#getInstancePrefix( )
     */
    @Override
    public String getInstancePrefix() {
	return BASE_NAME + SEPARATOR + getInstanceSettings().getInstanceId();
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming#getGlobalPrefix()
     */
    @Override
    public String getGlobalPrefix() {
	return getInstancePrefix() + SEPARATOR + GLOBAL_INDICATOR + SEPARATOR;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming#getTenantPrefix(
     * com.sitewhere.spi.tenant.ITenant)
     */
    @Override
    public String getTenantPrefix(ITenant tenant) {
	return getInstancePrefix() + SEPARATOR + TENANT_INDICATOR + SEPARATOR + tenant.getId() + SEPARATOR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.kafka.IKafkaTopicNaming#
     * getTenantUpdatesTopic()
     */
    @Override
    public String getTenantUpdatesTopic() {
	return getGlobalPrefix() + TENANT_MODEL_UPDATES_SUFFIX;
    }

    /*
     * @see com.sitewhere.microservice.spi.kafka.IKafkaTopicNaming#
     * getEventSourceDecodedEventsTopic(com.sitewhere.spi.tenant.ITenant)
     */
    @Override
    public String getEventSourceDecodedEventsTopic(ITenant tenant) {
	return getTenantPrefix(tenant) + TENANT_TOPIC_EVENT_SOURCE_DECODED_EVENTS;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming#
     * getEventSourceFailedDecodeTopic(com.sitewhere.spi.tenant.ITenant)
     */
    @Override
    public String getEventSourceFailedDecodeTopic(ITenant tenant) {
	return getTenantPrefix(tenant) + TENANT_TOPIC_EVENT_SOURCE_FAILED_DECODE_EVENTS;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming#
     * getInboundReprocessEventsTopic(com.sitewhere.spi.tenant.ITenant)
     */
    @Override
    public String getInboundReprocessEventsTopic(ITenant tenant) {
	return getTenantPrefix(tenant) + TENANT_TOPIC_INBOUND_REPROCESS_EVENTS;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming#
     * getInboundPersistedEventsTopic(com.sitewhere.spi.tenant.ITenant)
     */
    @Override
    public String getInboundPersistedEventsTopic(ITenant tenant) {
	return getTenantPrefix(tenant) + TENANT_TOPIC_INBOUND_PERSISTED_EVENTS;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming#
     * getUnregisteredDeviceEventsTopic(com.sitewhere.spi.tenant.ITenant)
     */
    @Override
    public String getUnregisteredDeviceEventsTopic(ITenant tenant) {
	return getTenantPrefix(tenant) + TENANT_TOPIC_INBOUND_UNREGISTERED_DEVICE_EVENTS;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming#
     * getInboundEnrichedEventsTopic(com.sitewhere.spi.tenant.ITenant)
     */
    @Override
    public String getInboundEnrichedEventsTopic(ITenant tenant) {
	return getTenantPrefix(tenant) + TENANT_TOPIC_INBOUND_ENRICHED_EVENTS;
    }

    protected IInstanceSettings getInstanceSettings() {
	return instanceSettings;
    }

    protected void setInstanceSettings(IInstanceSettings instanceSettings) {
	this.instanceSettings = instanceSettings;
    }
}