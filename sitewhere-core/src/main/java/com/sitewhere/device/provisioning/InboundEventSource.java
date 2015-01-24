/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.provisioning;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;
import com.sitewhere.spi.device.provisioning.IDecodedDeviceEventRequest;
import com.sitewhere.spi.device.provisioning.IDeviceEventDecoder;
import com.sitewhere.spi.device.provisioning.IInboundEventReceiver;
import com.sitewhere.spi.device.provisioning.IInboundEventSource;
import com.sitewhere.spi.device.provisioning.IInboundProcessingStrategy;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Default implementation of {@link IInboundEventSource}.
 * 
 * @author Derek
 * 
 * @param <T>
 */
public class InboundEventSource<T> extends LifecycleComponent implements IInboundEventSource<T> {

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
			setInboundProcessingStrategy(SiteWhere.getServer().getDeviceProvisioning().getInboundProcessingStrategy());
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
	 * @see
	 * com.sitewhere.spi.device.provisioning.IInboundEventSource#onEncodedEventReceived
	 * (com.sitewhere.spi.device.provisioning.IInboundEventReceiver, java.lang.Object)
	 */
	@Override
	public void onEncodedEventReceived(IInboundEventReceiver<T> receiver, T encodedPayload) {
		try {
			LOGGER.debug("Device event receiver thread picked up event.");
			List<IDecodedDeviceEventRequest> requests = decodePayload(encodedPayload);
			if (requests != null) {
				for (IDecodedDeviceEventRequest decoded : requests) {
					if (decoded.getRequest() instanceof IDeviceRegistrationRequest) {
						getInboundProcessingStrategy().processRegistration(decoded);
					} else if (decoded.getRequest() instanceof IDeviceCommandResponseCreateRequest) {
						getInboundProcessingStrategy().processDeviceCommandResponse(decoded);
					} else if (decoded.getRequest() instanceof IDeviceMeasurementsCreateRequest) {
						getInboundProcessingStrategy().processDeviceMeasurements(decoded);
					} else if (decoded.getRequest() instanceof IDeviceLocationCreateRequest) {
						getInboundProcessingStrategy().processDeviceLocation(decoded);
					} else if (decoded.getRequest() instanceof IDeviceAlertCreateRequest) {
						getInboundProcessingStrategy().processDeviceAlert(decoded);
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
	 * @return
	 * @throws SiteWhereException
	 */
	protected List<IDecodedDeviceEventRequest> decodePayload(T encodedPayload) throws SiteWhereException {
		return getDeviceEventDecoder().decode(encodedPayload);
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
	 * @see com.sitewhere.spi.device.provisioning.IInboundEventSource#getSourceId()
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
	 * @see
	 * com.sitewhere.spi.device.provisioning.IInboundEventSource#setDeviceEventDecoder
	 * (com.sitewhere.spi.device.provisioning.IDeviceEventDecoder)
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
	 * @see
	 * com.sitewhere.spi.device.provisioning.IInboundEventSource#setInboundProcessingStrategy
	 * (com.sitewhere.spi.device.provisioning.IInboundProcessingStrategy)
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
	 * @see
	 * com.sitewhere.spi.device.provisioning.IInboundEventSource#setInboundEventReceivers
	 * (java.util.List)
	 */
	public void setInboundEventReceivers(List<IInboundEventReceiver<T>> inboundEventReceivers) {
		this.inboundEventReceivers = inboundEventReceivers;
	}

	public List<IInboundEventReceiver<T>> getInboundEventReceivers() {
		return inboundEventReceivers;
	}
}