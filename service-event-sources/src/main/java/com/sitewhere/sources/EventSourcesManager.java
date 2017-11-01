/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.server.lifecycle.TracerUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IDecodedDeviceRequest;
import com.sitewhere.spi.device.communication.IEventSourcesManager;
import com.sitewhere.spi.device.communication.IInboundEventSource;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

import io.opentracing.ActiveSpan;

/**
 * Manages lifecycle of the list of event sources configured for a tenant.
 * 
 * @author Derek
 */
public class EventSourcesManager extends TenantLifecycleComponent implements IEventSourcesManager {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** List of event sources */
    private List<IInboundEventSource<?>> eventSources;

    /** Kafka producer for decoded events form event sources */
    private DecodedEventsProducer decodedEventsProducer;

    /*
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Initialize Kafka producer for decoded events.
	initializeDecodedEventsProducer(monitor);

	ActiveSpan span = null;
	for (IInboundEventSource<?> source : getEventSources()) {
	    try {
		span = monitor.getMicroservice().getTracer().buildSpan("Initialize event source").startActive();
		span.log("Initializing '" + source.getComponentName() + "' event source.");
		source.setEventSourcesManager(this);
		initializeNestedComponent(source, monitor, true);
	    } catch (SiteWhereException e) {
		TracerUtils.handleErrorInTracerSpan(span, e);
		LOGGER.error("Error initializing event source.", e);
	    } catch (Throwable e) {
		TracerUtils.handleErrorInTracerSpan(span, e);
		LOGGER.error("Unhandled exception initializing event source.", e);
	    } finally {
		TracerUtils.finishTracerSpan(span);
	    }
	}
    }

    /**
     * Initialize Kafka producer for decoded events.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    protected void initializeDecodedEventsProducer(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	ActiveSpan span = null;
	try {
	    span = monitor.getMicroservice().getTracer().buildSpan("Initialize decoded events producer").startActive();
	    decodedEventsProducer = new DecodedEventsProducer(monitor.getMicroservice(), getTenant());
	    initializeNestedComponent(getDecodedEventsProducer(), monitor, true);
	} catch (SiteWhereException e) {
	    TracerUtils.handleErrorInTracerSpan(span, e);
	    LOGGER.error("Error initializing decoded events producer.", e);
	} catch (Throwable e) {
	    TracerUtils.handleErrorInTracerSpan(span, e);
	    LOGGER.error("Unhandled exception initializing decoded events producer.", e);
	} finally {
	    TracerUtils.finishTracerSpan(span);
	}
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Start Kafka producer for decoded events.
	startDecodedEventsProducer(monitor);

	ActiveSpan span = null;
	for (IInboundEventSource<?> source : getEventSources()) {
	    try {
		span = monitor.getMicroservice().getTracer().buildSpan("Start event source").startActive();
		span.log("Starting '" + source.getComponentName() + "' event source.");
		startNestedComponent(source, monitor, true);
	    } catch (SiteWhereException e) {
		TracerUtils.handleErrorInTracerSpan(span, e);
		LOGGER.error("Error starting event source.", e);
	    } catch (Throwable e) {
		TracerUtils.handleErrorInTracerSpan(span, e);
		LOGGER.error("Unhandled exception starting event source.", e);
	    } finally {
		TracerUtils.finishTracerSpan(span);
	    }
	}
    }

    /**
     * Start Kafka producer for decoded events.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    protected void startDecodedEventsProducer(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	ActiveSpan span = null;
	try {
	    span = monitor.getMicroservice().getTracer().buildSpan("Start decoded events producer").startActive();
	    startNestedComponent(getDecodedEventsProducer(), monitor, true);
	} catch (SiteWhereException e) {
	    TracerUtils.handleErrorInTracerSpan(span, e);
	    LOGGER.error("Error starting decoded events producer.", e);
	} catch (Throwable e) {
	    TracerUtils.handleErrorInTracerSpan(span, e);
	    LOGGER.error("Unhandled exception starting decoded events producer.", e);
	} finally {
	    TracerUtils.finishTracerSpan(span);
	}
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Stop Kafka producer for decoded events.
	stopDecodedEventsProducer(monitor);

	ActiveSpan span = null;
	for (IInboundEventSource<?> source : getEventSources()) {
	    try {
		span = monitor.getMicroservice().getTracer().buildSpan("Stop event source").startActive();
		span.log("Stopping '" + source.getComponentName() + "' event source.");
		source.stop(monitor);
	    } catch (SiteWhereException e) {
		TracerUtils.handleErrorInTracerSpan(span, e);
		LOGGER.error("Error stopping event source.", e);
	    } catch (Throwable e) {
		TracerUtils.handleErrorInTracerSpan(span, e);
		LOGGER.error("Unhandled exception stopping event source.", e);
	    } finally {
		TracerUtils.finishTracerSpan(span);
	    }
	}
    }

    /**
     * Stop Kafka producer for decoded events.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    protected void stopDecodedEventsProducer(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	ActiveSpan span = null;
	try {
	    span = monitor.getMicroservice().getTracer().buildSpan("Stop decoded events producer").startActive();
	    getDecodedEventsProducer().stop(monitor);
	} catch (SiteWhereException e) {
	    TracerUtils.handleErrorInTracerSpan(span, e);
	    LOGGER.error("Error stopping decoded events producer.", e);
	} catch (Throwable e) {
	    TracerUtils.handleErrorInTracerSpan(span, e);
	    LOGGER.error("Unhandled exception stopping decoded events producer.", e);
	} finally {
	    TracerUtils.finishTracerSpan(span);
	}
    }

    /*
     * @see
     * com.sitewhere.sources.spi.IEventSourcesManager#handleDecodedEvent(java.
     * lang.String, byte[], java.util.Map,
     * com.sitewhere.spi.device.communication.IDecodedDeviceRequest)
     */
    @Override
    public void handleDecodedEvent(String sourceId, byte[] encoded, Map<String, Object> metadata,
	    IDecodedDeviceRequest<?> decoded) throws SiteWhereException {
	if (getDecodedEventsProducer().getLifecycleStatus() == LifecycleStatus.Started) {
	    getDecodedEventsProducer().send(decoded.getHardwareId(), encoded);
	} else if (getLogger().isWarnEnabled()) {
	    getLogger().warn("Producer not started. Unable to add event to topic.");
	}
    }

    /*
     * @see
     * com.sitewhere.sources.spi.IEventSourcesManager#handleFailedDecode(java.
     * lang.String, byte[], java.util.Map, java.lang.Throwable)
     */
    @Override
    public void handleFailedDecode(String sourceId, byte[] encoded, Map<String, Object> metadata, Throwable t)
	    throws SiteWhereException {
	// Send to Kafka.
    }

    /*
     * @see com.sitewhere.sources.spi.IEventSourcesManager#getEventSources()
     */
    @Override
    public List<IInboundEventSource<?>> getEventSources() {
	return eventSources;
    }

    public void setEventSources(List<IInboundEventSource<?>> eventSources) {
	this.eventSources = eventSources;
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    public DecodedEventsProducer getDecodedEventsProducer() {
	return decodedEventsProducer;
    }

    public void setDecodedEventsProducer(DecodedEventsProducer decodedEventsProducer) {
	this.decodedEventsProducer = decodedEventsProducer;
    }
}