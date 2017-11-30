/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.kafka;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.instance.spi.kafka.IStateAggregatorKafkaConsumer;
import com.sitewhere.microservice.state.MicroserviceStateUpdatesKafkaConsumer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.state.IMicroserviceState;
import com.sitewhere.spi.microservice.state.ITenantEngineState;

/**
 * Kafka consumer that listens for state updates and aggregates them to produce
 * an estimated topology of the SiteWhere instance.
 * 
 * @author Derek
 */
public class StateAggregatorKafkaConsumer extends MicroserviceStateUpdatesKafkaConsumer
	implements IStateAggregatorKafkaConsumer {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    public StateAggregatorKafkaConsumer(IMicroservice microservice) {
	super(microservice);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.state.IMicroserviceStateUpdatesKafkaConsumer#
     * onMicroserviceStateUpdate(com.sitewhere.spi.microservice.state.
     * IMicroserviceState)
     */
    @Override
    public void onMicroserviceStateUpdate(IMicroserviceState state) {
	try {
	    getLogger().info(
		    "Instance received microservice state update:\n\n" + MarshalUtils.marshalJsonAsPrettyString(state));
	} catch (SiteWhereException e) {
	    getLogger().info("Instance unable to parse microservice state update.");
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.state.IMicroserviceStateUpdatesKafkaConsumer#
     * onTenantEngineStateUpdate(com.sitewhere.spi.microservice.state.
     * ITenantEngineState)
     */
    @Override
    public void onTenantEngineStateUpdate(ITenantEngineState state) {
	try {
	    getLogger().info("Instance received tenant engine state update:\n\n"
		    + MarshalUtils.marshalJsonAsPrettyString(state));
	} catch (SiteWhereException e) {
	    getLogger().info("Instance unable to parse tenant engine state update.");
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}