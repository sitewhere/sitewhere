/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.spi.tenant.kafka;

import com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer;

/**
 * Kafka consumer that reacts to updates in the tenant model.
 * 
 * @author Derek
 */
public interface ITenantBootstrapModelConsumer extends IMicroserviceKafkaConsumer {
}