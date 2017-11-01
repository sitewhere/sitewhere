/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.EventDecodeException;
import com.sitewhere.spi.device.communication.IDecodedDeviceRequest;
import com.sitewhere.spi.device.communication.IDeviceEventDecoder;
import com.sitewhere.spi.device.communication.IDeviceEventDeduplicator;
import com.sitewhere.spi.device.communication.IEventSourcesManager;
import com.sitewhere.spi.device.communication.IInboundEventReceiver;
import com.sitewhere.spi.device.communication.IInboundEventSource;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Default implementation of {@link IInboundEventSource}.
 * 
 * @author Derek
 * 
 * @param <T>
 */
public abstract class InboundEventSource<T> extends TenantLifecycleComponent implements IInboundEventSource<T> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Manager for all event sources in a tenant */
    private IEventSourcesManager eventSourcesManager;

    /** Unique id for referencing source */
    private String sourceId;

    /** Device event decoder */
    private IDeviceEventDecoder<T> deviceEventDecoder;

    /** Device event deduplicator (optional) */
    private IDeviceEventDeduplicator deviceEventDeduplicator;

    /** List of {@link IInboundEventReceiver} that supply this processor */
    private List<IInboundEventReceiver<T>> inboundEventReceivers = new ArrayList<IInboundEventReceiver<T>>();

    public InboundEventSource() {
	super(LifecycleComponentType.InboundEventSource);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getLifecycleComponents().clear();

	if ((getInboundEventReceivers() == null) || (getInboundEventReceivers().size() == 0)) {
	    throw new SiteWhereException("No inbound event receivers registered for event source.");
	}
	if (getDeviceEventDecoder() == null) {
	    throw new SiteWhereException("No device event decoder assigned.");
	}

	// Start device event decoder.
	startNestedComponent(getDeviceEventDecoder(), monitor, true);

	// Start device event deduplicator if provided.
	if (getDeviceEventDeduplicator() != null) {
	    startNestedComponent(getDeviceEventDeduplicator(), monitor, true);
	}

	startEventReceivers(monitor);
    }

    /**
     * Start event receivers for this event source.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    protected void startEventReceivers(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getInboundEventReceivers().size() > 0) {
	    for (IInboundEventReceiver<T> receiver : getInboundEventReceivers()) {
		receiver.setEventSource(this);
		startNestedComponent(receiver, monitor, true);
	    }
	} else {
	    LOGGER.warn("No device event receivers configured for event source!");
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Stop all inbound event receivers.
	if (getInboundEventReceivers().size() > 0) {
	    for (IInboundEventReceiver<T> receiver : getInboundEventReceivers()) {
		receiver.lifecycleStop(monitor);
	    }
	}

	// Stop device event decoder.
	getDeviceEventDecoder().lifecycleStop(monitor);

	// Stop device event deduplicator if present.
	if (getDeviceEventDeduplicator() != null) {
	    getDeviceEventDeduplicator().lifecycleStop(monitor);
	}
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

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundEventSource#
     * onEncodedEventReceived(
     * com.sitewhere.spi.device.communication.IInboundEventReceiver,
     * java.lang.Object, java.util.Map)
     */
    @Override
    public void onEncodedEventReceived(IInboundEventReceiver<T> receiver, T encoded, Map<String, Object> metadata)
	    throws EventDecodeException {
	LOGGER.debug("Device event receiver picked up event.");
	List<IDecodedDeviceRequest<?>> requests = decodePayload(encoded, metadata);
	try {
	    if (requests != null) {
		for (IDecodedDeviceRequest<?> decoded : requests) {
		    boolean isDuplicate = ((getDeviceEventDeduplicator() != null)
			    && (getDeviceEventDeduplicator().isDuplicate(decoded)));
		    if (!isDuplicate) {
			handleDecodedRequest(encoded, metadata, decoded);
		    } else {
			LOGGER.info("Event not processed due to duplicate detected.");
		    }
		}
	    }
	} catch (Throwable e) {
	    try {
		onEventDecodeFailed(encoded, metadata, e);
	    } catch (SiteWhereException e1) {
		LOGGER.error("Unable to report failed decode to event source manager.", e1);
	    }
	}
    }

    /**
     * Pass decoded events to the {@link IEventSourcesManager} for further
     * processing.
     * 
     * @param encoded
     * @param metadata
     * @param decoded
     * @throws SiteWhereException
     */
    protected void handleDecodedRequest(T encoded, Map<String, Object> metadata, IDecodedDeviceRequest<?> decoded)
	    throws SiteWhereException {
	getEventSourcesManager().handleDecodedEvent(getSourceId(), getRawPayload(encoded), metadata, decoded);
    }

    /**
     * Pass failed decoded to the {@link IEventSourcesManager} for further
     * processing.
     * 
     * @param encoded
     * @param metadata
     * @param t
     * @throws SiteWhereException
     */
    protected void onEventDecodeFailed(T encoded, Map<String, Object> metadata, Throwable t) throws SiteWhereException {
	getEventSourcesManager().handleFailedDecode(getSourceId(), getRawPayload(encoded), metadata, t);
    }

    /**
     * Decode a payload into individual events.
     * 
     * @param encodedPayload
     * @param metadata
     * @return
     * @throws EventDecodeException
     */
    protected List<IDecodedDeviceRequest<?>> decodePayload(T encodedPayload, Map<String, Object> metadata)
	    throws EventDecodeException {
	return getDeviceEventDecoder().decode(encodedPayload, metadata);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.communication.IInboundEventSource#getSourceId()
     */
    @Override
    public String getSourceId() {
	return sourceId;
    }

    public void setSourceId(String sourceId) {
	this.sourceId = sourceId;
    }

    /*
     * @see com.sitewhere.spi.device.communication.IInboundEventSource#
     * setEventSourcesManager(com.sitewhere.spi.device.communication.
     * IEventSourcesManager)
     */
    @Override
    public void setEventSourcesManager(IEventSourcesManager eventSourcesManager) {
	this.eventSourcesManager = eventSourcesManager;
    }

    public IEventSourcesManager getEventSourcesManager() {
	return eventSourcesManager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundEventSource#
     * setDeviceEventDecoder
     * (com.sitewhere.spi.device.communication.IDeviceEventDecoder)
     */
    @Override
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
     * setDeviceEventDeduplicator(com.sitewhere.spi.device.communication.
     * IDeviceEventDeduplicator)
     */
    @Override
    public void setDeviceEventDeduplicator(IDeviceEventDeduplicator deviceEventDeduplicator) {
	this.deviceEventDeduplicator = deviceEventDeduplicator;
    }

    public IDeviceEventDeduplicator getDeviceEventDeduplicator() {
	return deviceEventDeduplicator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundEventSource#
     * setInboundEventReceivers (java.util.List)
     */
    @Override
    public void setInboundEventReceivers(List<IInboundEventReceiver<T>> inboundEventReceivers) {
	this.inboundEventReceivers = inboundEventReceivers;
    }

    public List<IInboundEventReceiver<T>> getInboundEventReceivers() {
	return inboundEventReceivers;
    }
}