/*
 * ProtobufRegistrationManager.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.provisioning.protobuf;

import org.apache.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.device.provisioning.RegistrationManager;
import com.sitewhere.device.provisioning.protobuf.proto.Sitewhere.Device.RegistrationAck;
import com.sitewhere.device.provisioning.protobuf.proto.Sitewhere.Device.RegistrationAckError;
import com.sitewhere.device.provisioning.protobuf.proto.Sitewhere.Device.RegistrationAckState;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.provisioning.IRegistrationManager;

/**
 * Google Protocol Buffer implementation of {@link IRegistrationManager}.
 * 
 * @author Derek
 */
public class ProtobufRegistrationManager extends RegistrationManager {

	/** Static logger instance */
	@SuppressWarnings("unused")
	private static Logger LOGGER = Logger.getLogger(ProtobufRegistrationManager.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.device.provisioning.RegistrationManager#sendRegistrationAck(java.
	 * lang.String, boolean)
	 */
	@Override
	protected void sendRegistrationAck(String hardwareId, boolean newRegistration) throws SiteWhereException {
		RegistrationAckState state =
				(newRegistration) ? RegistrationAckState.NEW_REGISTRATION
						: RegistrationAckState.ALREADY_REGISTERED;
		RegistrationAck ack = RegistrationAck.newBuilder().setState(state).build();
		SiteWhere.getServer().getDeviceProvisioning().deliverSystemCommand(hardwareId, ack);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.device.provisioning.RegistrationManager#sendInvalidSpecification(
	 * java.lang.String)
	 */
	@Override
	protected void sendInvalidSpecification(String hardwareId) throws SiteWhereException {
		RegistrationAck ack =
				RegistrationAck.newBuilder().setState(RegistrationAckState.REGISTRATION_ERROR).setErrorType(
						RegistrationAckError.INVALID_SPECIFICATION).build();
		SiteWhere.getServer().getDeviceProvisioning().deliverSystemCommand(hardwareId, ack);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.device.provisioning.RegistrationManager#sendSiteTokenRequired(java
	 * .lang.String)
	 */
	@Override
	protected void sendSiteTokenRequired(String hardwareId) throws SiteWhereException {
		RegistrationAck ack =
				RegistrationAck.newBuilder().setState(RegistrationAckState.REGISTRATION_ERROR).setErrorType(
						RegistrationAckError.SITE_TOKEN_REQUIRED).build();
		SiteWhere.getServer().getDeviceProvisioning().deliverSystemCommand(hardwareId, ack);
	}
}