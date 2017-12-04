/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.state;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.kafka.model.KafkaModel.GInstanceTopologySnapshot;
import com.sitewhere.grpc.model.converter.KafkaModelConverter;
import com.sitewhere.grpc.model.marshaling.KafkaModelMarshaler;
import com.sitewhere.microservice.kafka.MicroserviceKafkaProducer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.state.IInstanceTopologySnapshot;
import com.sitewhere.spi.microservice.state.IInstanceTopologySnapshotsKafkaProducer;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Kafka producer that reports instance topology updates.
 * 
 * @author Derek
 */
public class InstanceTopologySnapshotsKafkaProducer extends MicroserviceKafkaProducer
	implements IInstanceTopologySnapshotsKafkaProducer {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    public InstanceTopologySnapshotsKafkaProducer(IMicroservice microservice) {
	super(microservice);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.state.IInstanceTopologySnapshotsKafkaProducer#
     * send(com.sitewhere.spi.microservice.state.IInstanceTopologySnapshot)
     */
    @Override
    public void send(IInstanceTopologySnapshot snapshot) throws SiteWhereException {
	GInstanceTopologySnapshot grpc = KafkaModelConverter.asGrpcInstanceTopologySnapshot(snapshot);
	byte[] payload = KafkaModelMarshaler.buildInstanceTopologySnapshotMessage(grpc);
	if (getLifecycleStatus() == LifecycleStatus.Started) {
	    send(getMicroservice().getInstanceSettings().getInstanceId(), payload);
	} else {
	    getLogger().debug("Skipping topology snapshot. Kafka producer not started.");
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaProducer#
     * getTargetTopicName()
     */
    @Override
    public String getTargetTopicName() throws SiteWhereException {
	return getMicroservice().getKafkaTopicNaming().getInstanceTopologyUpdatesTopic();
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}