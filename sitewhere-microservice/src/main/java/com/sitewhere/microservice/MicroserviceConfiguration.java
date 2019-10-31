/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice;

import com.sitewhere.microservice.kafka.KafkaTopicNaming;
import com.sitewhere.microservice.metrics.MetricsServer;
import com.sitewhere.microservice.security.SystemUser;
import com.sitewhere.microservice.security.TokenManagement;
import com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming;
import com.sitewhere.spi.microservice.metrics.IMetricsServer;
import com.sitewhere.spi.microservice.security.ISystemUser;
import com.sitewhere.spi.microservice.security.ITokenManagement;

public class MicroserviceConfiguration {

    public IMetricsServer metricsServer() {
	return new MetricsServer();
    }

    IKafkaTopicNaming kafkaTopicNaming() {
	return new KafkaTopicNaming();
    }

    public ITokenManagement tokenManagement() {
	return new TokenManagement();
    }

    public ISystemUser systemUser() {
	return new SystemUser();
    }
}