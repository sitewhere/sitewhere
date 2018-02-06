/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.kafka;

import com.sitewhere.spi.tenant.ITenant;

/**
 * Provides names for Kafka topics used in SiteWhere.
 * 
 * @author Derek
 */
public interface IKafkaTopicNaming {

    /**
     * Get prefix that uniquely identifies SiteWhere instance.
     * 
     * @return
     */
    public String getInstancePrefix();

    /**
     * Get prefix used for global topics.
     * 
     * @return
     */
    public String getGlobalPrefix();

    /**
     * Get prefix used for tenant-specific topics.
     * 
     * @param tenant
     * @return
     */
    public String getTenantPrefix(ITenant tenant);

    /**
     * Get topic name for tracking tenant model updates.
     * 
     * @return
     */
    public String getTenantUpdatesTopic();

    /**
     * Get topic name for tracking microservice state updates.
     * 
     * @return
     */
    public String getMicroserviceStateUpdatesTopic();

    /**
     * Get topic name for tracking instance topology updates.
     * 
     * @return
     */
    public String getInstanceTopologyUpdatesTopic();

    /**
     * Get name for topic that contains events that have been decoded from inbound
     * event sources.
     * 
     * @param tenant
     * @return
     */
    public String getEventSourceDecodedEventsTopic(ITenant tenant);

    /**
     * Get name for topic that contains events that could not be decoded from event
     * sources.
     * 
     * @param tenant
     * @return
     */
    public String getEventSourceFailedDecodeTopic(ITenant tenant);

    /**
     * Get topic for inbound events that were sent for out-of-band processing, then
     * returned to be reprocessed.
     * 
     * @param tenant
     * @return
     */
    public String getInboundReprocessEventsTopic(ITenant tenant);

    /**
     * Get topic for events that have been persisted to an event datastore.
     * 
     * @param tenant
     * @return
     */
    public String getInboundPersistedEventsTopic(ITenant tenant);

    /**
     * Get name fro topic that contains events for devices that were not registered
     * in the system.
     * 
     * @param tenant
     * @return
     */
    public String getUnregisteredDeviceEventsTopic(ITenant tenant);

    /**
     * Get topic for events that have been persisted and enriched with
     * device/assignment data.
     * 
     * @param tenant
     * @return
     */
    public String getInboundEnrichedEventsTopic(ITenant tenant);

    /**
     * Get topic for device command invocations that have been persisted and
     * enriched with device/assignment data.
     * 
     * @param tenant
     * @return
     */
    public String getInboundEnrichedCommandInvocationsTopic(ITenant tenant);
}