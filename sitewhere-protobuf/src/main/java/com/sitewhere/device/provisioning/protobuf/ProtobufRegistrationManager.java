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

import com.sitewhere.device.provisioning.protobuf.proto.Sitewhere.Device.RegistrationAck;
import com.sitewhere.device.provisioning.protobuf.proto.Sitewhere.Device.RegistrationAckError;
import com.sitewhere.device.provisioning.protobuf.proto.Sitewhere.Device.RegistrationAckState;
import com.sitewhere.rest.model.device.request.DeviceAssignmentCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;
import com.sitewhere.spi.device.provisioning.IRegistrationManager;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Google Protocol Buffer implementation of {@link IRegistrationManager}.
 * 
 * @author Derek
 */
public class ProtobufRegistrationManager implements IRegistrationManager {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(ProtobufRegistrationManager.class);

	/** Indicates if new devices can register with the system */
	private boolean allowNewDevices;

	/** Indicates if devices can be auto-assigned if no site token is passed */
	private boolean autoAssignSite;

	/** Token used if autoAssignSite is enabled */
	private String autoAssignSiteToken;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.IRegistrationManager#handleDeviceRegistration
	 * (com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest)
	 */
	@Override
	public void handleDeviceRegistration(IDeviceRegistrationRequest request) throws SiteWhereException {
		LOGGER.debug("Handling device registration request.");
		IDevice device =
				SiteWhereServer.getInstance().getDeviceManagement().getDeviceByHardwareId(
						request.getHardwareId());
		RegistrationAckState state =
				(device == null) ? RegistrationAckState.NEW_REGISTRATION
						: RegistrationAckState.ALREADY_REGISTERED;
		IDeviceSpecification specification =
				SiteWhereServer.getInstance().getDeviceManagement().getDeviceSpecificationByToken(
						request.getSpecificationToken());
		// Create device if it does not already exist.
		if (device == null) {
			LOGGER.debug("Creating new device as part of registration.");
			if (specification == null) {
				RegistrationAck ack =
						RegistrationAck.newBuilder().setState(RegistrationAckState.REGISTRATION_ERROR).setErrorType(
								RegistrationAckError.INVALID_SPECIFICATION).build();
				SiteWhereServer.getInstance().getDeviceProvisioning().deliverSystemCommand(
						request.getHardwareId(), ack);
				return;
			}
			DeviceCreateRequest deviceCreate = new DeviceCreateRequest();
			deviceCreate.setHardwareId(request.getHardwareId());
			deviceCreate.setSpecificationToken(request.getSpecificationToken());
			deviceCreate.setComments("Device created by on-demand registration.");
			device = SiteWhereServer.getInstance().getDeviceManagement().createDevice(deviceCreate);
		} else if (!device.getSpecificationToken().equals(request.getSpecificationToken())) {
			// TODO: Is this an error or a valid use case?
			RegistrationAck ack =
					RegistrationAck.newBuilder().setState(RegistrationAckState.REGISTRATION_ERROR).setErrorType(
							RegistrationAckError.INVALID_SPECIFICATION).build();
			SiteWhereServer.getInstance().getDeviceProvisioning().deliverSystemCommand(
					request.getHardwareId(), ack);
			return;
		}
		// Make sure device is assigned.
		if (device.getAssignmentToken() == null) {
			if (!isAutoAssignSite()) {
				RegistrationAck ack =
						RegistrationAck.newBuilder().setState(RegistrationAckState.REGISTRATION_ERROR).setErrorType(
								RegistrationAckError.SITE_TOKEN_REQUIRED).build();
				SiteWhereServer.getInstance().getDeviceProvisioning().deliverSystemCommand(
						request.getHardwareId(), ack);
				return;
			}
			LOGGER.debug("Handling unassigned device for registration.");
			DeviceAssignmentCreateRequest assnCreate = new DeviceAssignmentCreateRequest();
			assnCreate.setSiteToken(getAutoAssignSiteToken());
			assnCreate.setDeviceHardwareId(device.getHardwareId());
			assnCreate.setAssignmentType(DeviceAssignmentType.Unassociated);
			SiteWhereServer.getInstance().getDeviceManagement().createDeviceAssignment(assnCreate);
		}
		RegistrationAck ack = RegistrationAck.newBuilder().setState(state).build();
		SiteWhereServer.getInstance().getDeviceProvisioning().deliverSystemCommand(request.getHardwareId(),
				ack);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		LOGGER.info("Device registration manager starting.");
		if (isAutoAssignSite()) {
			if (getAutoAssignSiteToken() == null) {
				ISearchResults<ISite> sites =
						SiteWhereServer.getInstance().getDeviceManagement().listSites(
								new SearchCriteria(1, 1));
				if (sites.getResults().isEmpty()) {
					throw new SiteWhereException(
							"Registration manager configured for auto-assign site, but no sites were found.");
				}
				setAutoAssignSiteToken(sites.getResults().get(0).getToken());
			} else {
				ISite site =
						SiteWhereServer.getInstance().getDeviceManagement().getSiteByToken(
								getAutoAssignSiteToken());
				if (site == null) {
					throw new SiteWhereException(
							"Registration manager auto assignment site token is invalid.");
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		LOGGER.info("Device registration manager stopping.");
	}

	public boolean isAllowNewDevices() {
		return allowNewDevices;
	}

	public void setAllowNewDevices(boolean allowNewDevices) {
		this.allowNewDevices = allowNewDevices;
	}

	public boolean isAutoAssignSite() {
		return autoAssignSite;
	}

	public void setAutoAssignSite(boolean autoAssignSite) {
		this.autoAssignSite = autoAssignSite;
	}

	public String getAutoAssignSiteToken() {
		return autoAssignSiteToken;
	}

	public void setAutoAssignSiteToken(String autoAssignSiteToken) {
		this.autoAssignSiteToken = autoAssignSiteToken;
	}
}