/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.spi.kafka;

import com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer;

/**
 * Consumer for events that have been persisted via the event management APIs.
 * 
 * @author Derek
 */
public interface IUnprocessedBatchOperationsConsumer extends IMicroserviceKafkaConsumer {
}