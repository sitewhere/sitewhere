/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.EventDecodeException;
import com.sitewhere.spi.device.communication.IDecodedDeviceRequest;
import com.sitewhere.spi.device.communication.IDeviceEventDecoder;
import com.sitewhere.spi.device.communication.IInboundEventReceiver;
import com.sitewhere.spi.device.communication.IInboundEventSource;
import com.sitewhere.spi.device.communication.IInboundProcessingStrategy;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMappingCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.device.event.request.ISendDeviceStreamDataRequest;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.server.tenant.ITenantHazelcastAware;
import com.sitewhere.spi.server.tenant.ITenantHazelcastConfiguration;

/**
 * Default implementation of {@link IInboundEventSource}.
 * 
 * @author Derek
 * 
 * @param <T>
 */
public class InboundEventSource<T> extends TenantLifecycleComponent
		implements IInboundEventSource<T>, ITenantHazelcastAware {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(InboundEventSource.class);

	/** Unique id for referencing source */
	private String sourceId;

	/** Device event decoder */
	private IDeviceEventDecoder<T> deviceEventDecoder;

	/** Inbound event processing strategy */
	private IInboundProcessingStrategy inboundProcessingStrategy;

	/** List of {@link IInboundEventReceiver} that supply this processor */
	private List<IInboundEventReceiver<T>> inboundEventReceivers = new ArrayList<IInboundEventReceiver<T>>();

	public InboundEventSource() {
		super(LifecycleComponentType.InboundEventSource);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		getLifecycleComponents().clear();

		LOGGER.debug("Starting event source '" + getSourceId() + "'.");
		if (getInboundProcessingStrategy() == null) {
			setInboundProcessingStrategy(
					SiteWhere.getServer().getEventProcessing(getTenant()).getInboundProcessingStrategy());
		}
		if ((getInboundEventReceivers() == null) || (getInboundEventReceivers().size() == 0)) {
			throw new SiteWhereException("No inbound event receivers registered for event source.");
		}
		startEventReceivers();
		LOGGER.debug("Started event source '" + getSourceId() + "'.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.server.lifecycle.LifecycleComponent#getComponentName()
	 */
	@Override
	public String getComponentName() {
		return "Event Source (" + getSourceId() + ")";
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

	/**
	 * Start event receivers for this event source.
	 * 
	 * @throws SiteWhereException
	 */
	protected void startEventReceivers() throws SiteWhereException {
		if (getInboundEventReceivers().size() > 0) {
			for (IInboundEventReceiver<T> receiver : getInboundEventReceivers()) {
				receiver.setEventSource(this);
				startNestedComponent(receiver, true);
			}
		} else {
			LOGGER.warn("No device event receivers configured for event source!");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.IInboundEventSource#
	 * onEncodedEventReceived(
	 * com.sitewhere.spi.device.communication.IInboundEventReceiver,
	 * java.lang.Object, java.util.Map)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void onEncodedEventReceived(IInboundEventReceiver<T> receiver, T encodedPayload,
			Map<String, String> metadata) throws EventDecodeException {
		LOGGER.debug("Device event receiver thread picked up event.");
		List<IDecodedDeviceRequest<?>> requests = decodePayload(encodedPayload, metadata);
		try {
			if (requests != null) {
				for (IDecodedDeviceRequest<?> decoded : requests) {
					if (decoded.getRequest() instanceof IDeviceRegistrationRequest) {
						getInboundProcessingStrategy()
								.processRegistration((IDecodedDeviceRequest<IDeviceRegistrationRequest>) decoded);
					} else if (decoded.getRequest() instanceof IDeviceCommandResponseCreateRequest) {
						getInboundProcessingStrategy().processDeviceCommandResponse(
								(IDecodedDeviceRequest<IDeviceCommandResponseCreateRequest>) decoded);
					} else if (decoded.getRequest() instanceof IDeviceMeasurementsCreateRequest) {
						getInboundProcessingStrategy().processDeviceMeasurements(
								(IDecodedDeviceRequest<IDeviceMeasurementsCreateRequest>) decoded);
					} else if (decoded.getRequest() instanceof IDeviceLocationCreateRequest) {
						getInboundProcessingStrategy()
								.processDeviceLocation((IDecodedDeviceRequest<IDeviceLocationCreateRequest>) decoded);
					} else if (decoded.getRequest() instanceof IDeviceAlertCreateRequest) {
						getInboundProcessingStrategy()
								.processDeviceAlert((IDecodedDeviceRequest<IDeviceAlertCreateRequest>) decoded);
					} else if (decoded.getRequest() instanceof IDeviceStateChangeCreateRequest) {
						getInboundProcessingStrategy().processDeviceStateChange(
								(IDecodedDeviceRequest<IDeviceStateChangeCreateRequest>) decoded);
					} else if (decoded.getRequest() instanceof IDeviceStreamCreateRequest) {
						getInboundProcessingStrategy()
								.processDeviceStream((IDecodedDeviceRequest<IDeviceStreamCreateRequest>) decoded);
					} else if (decoded.getRequest() instanceof IDeviceStreamDataCreateRequest) {
						getInboundProcessingStrategy().processDeviceStreamData(
								(IDecodedDeviceRequest<IDeviceStreamDataCreateRequest>) decoded);
					} else if (decoded.getRequest() instanceof ISendDeviceStreamDataRequest) {
						getInboundProcessingStrategy().processSendDeviceStreamData(
								(IDecodedDeviceRequest<ISendDeviceStreamDataRequest>) decoded);
					} else if (decoded.getRequest() instanceof IDeviceMappingCreateRequest) {
						getInboundProcessingStrategy().processCreateDeviceMapping(
								(IDecodedDeviceRequest<IDeviceMappingCreateRequest>) decoded);
					} else {
						LOGGER.error("Decoded device event request could not be routed: "
								+ decoded.getRequest().getClass().getName());
					}
				}
			}
		} catch (SiteWhereException e) {
			onEventDecodeFailed(encodedPayload, e);
		} catch (Throwable e) {
			onEventDecodeFailed(encodedPayload, e);
		}
	}

	/**
	 * Decode a payload into individual events.
	 * 
	 * @param encodedPayload
	 * @param metadata
	 * @return
	 * @throws EventDecodeException
	 */
	protected List<IDecodedDeviceRequest<?>> decodePayload(T encodedPayload, Map<String, String> metadata)
			throws EventDecodeException {
		return getDeviceEventDecoder().decode(encodedPayload, metadata);
	}

	/**
	 * Handler for case where decoder throws an exception.
	 * 
	 * @param encodedEvent
	 * @param t
	 */
	protected void onEventDecodeFailed(T encodedEvent, Throwable t) {
		LOGGER.error("Event receiver thread unable to decode event request.", t);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		LOGGER.info("Stopping inbound event source '" + getSourceId() + "'.");
		if (getInboundEventReceivers().size() > 0) {
			for (IInboundEventReceiver<T> receiver : getInboundEventReceivers()) {
				receiver.lifecycleStop();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.tenant.ITenantHazelcastAware#
	 * setHazelcastConfiguration(com
	 * .sitewhere.spi.server.tenant.ITenantHazelcastConfiguration)
	 */
	@Override
	public void setHazelcastConfiguration(ITenantHazelcastConfiguration configuration) {
		for (IInboundEventReceiver<T> receiver : getInboundEventReceivers()) {
			if (receiver instanceof ITenantHazelcastAware) {
				((ITenantHazelcastAware) receiver).setHazelcastConfiguration(configuration);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.IInboundEventSource#getSourceId()
	 */
	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.IInboundEventSource#
	 * setDeviceEventDecoder
	 * (com.sitewhere.spi.device.communication.IDeviceEventDecoder)
	 */
	public void setDeviceEventDecoder(IDeviceEventDecoder<T> deviceEventDecoder) {
		this.deviceEventDecoder = deviceEventDecoder;
	}

	public IDeviceEventDecoder<T> getDeviceEventDecoder() {
		return deviceEventDecoder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.IInboundEventSource#
	 * setInboundProcessingStrategy
	 * (com.sitewhere.spi.device.communication.IInboundProcessingStrategy)
	 */
	public void setInboundProcessingStrategy(IInboundProcessingStrategy inboundProcessingStrategy) {
		this.inboundProcessingStrategy = inboundProcessingStrategy;
	}

	public IInboundProcessingStrategy getInboundProcessingStrategy() {
		return inboundProcessingStrategy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.IInboundEventSource#
	 * setInboundEventReceivers (java.util.List)
	 */
	public void setInboundEventReceivers(List<IInboundEventReceiver<T>> inboundEventReceivers) {
		this.inboundEventReceivers = inboundEventReceivers;
	}

	public List<IInboundEventReceiver<T>> getInboundEventReceivers() {
		return inboundEventReceivers;
	}
}