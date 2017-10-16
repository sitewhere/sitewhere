/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.kafka;

import org.springframework.beans.factory.annotation.Autowired;

import com.sitewhere.microservice.spi.instance.IInstanceSettings;
import com.sitewhere.microservice.spi.kafka.IKafkaTopicNaming;

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

    @Autowired
    private IInstanceSettings instanceSettings;

    /**
     * Get prefix used for global topics.
     * 
     * @return
     */
    protected String getGlobalPrefix() {
	return getInstancePrefix() + SEPARATOR + GLOBAL_INDICATOR + SEPARATOR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.kafka.IKafkaTopicNaming#getInstancePrefix(
     * )
     */
    @Override
    public String getInstancePrefix() {
	return BASE_NAME + SEPARATOR + getInstanceSettings().getInstanceId();
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

    protected IInstanceSettings getInstanceSettings() {
	return instanceSettings;
    }

    protected void setInstanceSettings(IInstanceSettings instanceSettings) {
	this.instanceSettings = instanceSettings;
    }
}