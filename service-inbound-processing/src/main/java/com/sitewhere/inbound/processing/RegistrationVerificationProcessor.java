/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.processing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.kafka.model.KafkaModel.GInboundEventPayload;
import com.sitewhere.grpc.model.marshaling.KafkaModelMarshaler;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingMicroservice;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine;
import com.sitewhere.inbound.spi.processing.IRegistrationVerificationProcessor;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;

/**
 * Processing node which verifies that an incoming event belongs to a registered
 * device. If the event does not belong to a registered device, it is added to a
 * Kafka topic that can be processed by other services.
 * 
 * @author Derek
 */
public class RegistrationVerificationProcessor extends TenantLifecycleComponent
	implements IRegistrationVerificationProcessor {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Handle to inbound processing tenant engine */
    private IInboundProcessingTenantEngine tenantEngine;

    public RegistrationVerificationProcessor(IInboundProcessingTenantEngine tenantEngine) {
	this.tenantEngine = tenantEngine;
    }

    /*
     * @see
     * com.sitewhere.inbound.spi.processing.IRegistrationVerificationProcessor#
     * process(com.sitewhere.grpc.kafka.model.KafkaModel.GInboundEventPayload)
     */
    @Override
    public void process(GInboundEventPayload payload) throws SiteWhereException {
	// Verify that device is registered.
	IDevice device = getDeviceManagement().getDeviceByHardwareId(payload.getHardwareId());
	if (device == null) {
	    handleUnregisteredDevice(payload);
	    return;
	}

	// Verify that device is assigned.
	if (device.getAssignmentToken() == null) {
	    handleUnassignedDevice(payload);
	    return;
	}

	IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignmentByToken(device.getAssignmentToken());
	if (assignment == null) {
	    getLogger().info("Assignment information for " + payload.getHardwareId() + " is invalid.");
	    handleUnassignedDevice(payload);
	    return;
	}
    }

    /**
     * Handle case where event is processed for an unregistered device. Forwards
     * information to an out-of-band topic to be processed later.
     * 
     * @param payload
     * @throws SiteWhereException
     */
    protected void handleUnregisteredDevice(GInboundEventPayload payload) throws SiteWhereException {
	getLogger().info(
		"Device " + payload.getHardwareId() + " is not registered. Forwarding to unregistered devices topic.");
	byte[] marshaled = KafkaModelMarshaler.buildInboundEventPayloadMessage(payload);
	getTenantEngine().getUnregisteredDeviceEventsProducer().send(payload.getHardwareId(), marshaled);
	return;
    }

    /**
     * Handle case where event is sent for an unassigned device. Forwards
     * information to an out-of-band topic to be processed later.
     * 
     * @param payload
     * @throws SiteWhereException
     */
    protected void handleUnassignedDevice(GInboundEventPayload payload) throws SiteWhereException {
	getLogger().info("Device " + payload.getHardwareId()
		+ " is not currently assigned. Forwarding to unassigned devices topic.");
	byte[] marshaled = KafkaModelMarshaler.buildInboundEventPayloadMessage(payload);
	getTenantEngine().getUnregisteredDeviceEventsProducer().send(payload.getHardwareId(), marshaled);
	return;
    }

    /**
     * Get device management implementation.
     * 
     * @return
     */
    protected IDeviceManagement getDeviceManagement() {
	return ((IInboundProcessingMicroservice) getTenantEngine().getMicroservice()).getDeviceManagementApiChannel();
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    public IInboundProcessingTenantEngine getTenantEngine() {
	return tenantEngine;
    }

    public void setTenantEngine(IInboundProcessingTenantEngine tenantEngine) {
	this.tenantEngine = tenantEngine;
    }
}