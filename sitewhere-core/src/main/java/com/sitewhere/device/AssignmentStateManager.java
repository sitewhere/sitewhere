/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.sitewhere.rest.model.device.DeviceAssignmentState;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceEvent;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurement;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IAssignmentStateManager;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.state.PresenceState;
import com.sitewhere.spi.device.event.state.StateChangeCategory;
import com.sitewhere.spi.device.event.state.StateChangeType;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Manages concurrent updates to assignment state to prevent thrashing as the result of
 * high event throughput with state updates.
 * 
 * @author Derek
 */
public class AssignmentStateManager extends TenantLifecycleComponent implements IAssignmentStateManager {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(AssignmentStateManager.class);

	/** Number of milliseconds between flushes to persistence */
	private static final int FLUSH_INTERVAL = 250;

	/** Device management implementation */
	private IDeviceManagement deviceManagement;

	/** Map of assignment token to queued events */
	private Map<String, List<IDeviceEvent>> eventsByAssignment = new HashMap<String, List<IDeviceEvent>>();

	/** Used for executing persistence thread */
	private ExecutorService executor;

	public AssignmentStateManager(IDeviceManagement deviceManagement) {
		super(LifecycleComponentType.DataStore);
		this.deviceManagement = deviceManagement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		this.executor = Executors.newSingleThreadExecutor();
		executor.execute(new Persistence());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		if (executor != null) {
			executor.shutdownNow();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IAssignmentStateManager#addLocation(java.lang.String,
	 * com.sitewhere.spi.device.event.IDeviceLocation)
	 */
	public void addLocation(String token, IDeviceLocation location) throws SiteWhereException {
		List<IDeviceEvent> events = getEventsFor(token);
		events.add(location);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IAssignmentStateManager#addMeasurements(java.lang.String,
	 * com.sitewhere.spi.device.event.IDeviceMeasurements)
	 */
	public void addMeasurements(String token, IDeviceMeasurements measurements) throws SiteWhereException {
		List<IDeviceEvent> events = getEventsFor(token);
		events.add(measurements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IAssignmentStateManager#addAlert(java.lang.String,
	 * com.sitewhere.spi.device.event.IDeviceAlert)
	 */
	public void addAlert(String token, IDeviceAlert alert) throws SiteWhereException {
		List<IDeviceEvent> events = getEventsFor(token);
		events.add(alert);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IAssignmentStateManager#addStateChange(java.lang.String,
	 * com.sitewhere.spi.device.event.IDeviceStateChange)
	 */
	@Override
	public void addStateChange(String token, IDeviceStateChange state) throws SiteWhereException {
		List<IDeviceEvent> events = getEventsFor(token);
		events.add(state);
	}

	/**
	 * Get state for a given assignment. Create if necessary.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected List<IDeviceEvent> getEventsFor(String token) throws SiteWhereException {
		List<IDeviceEvent> events = getEventsByAssignment().get(token);
		if (events == null) {
			events = new ArrayList<IDeviceEvent>();
			synchronized (eventsByAssignment) {
				eventsByAssignment.put(token, events);
			}
		}
		return events;
	}

	/**
	 * Periodically persists state values.
	 * 
	 * @author Derek
	 */
	private class Persistence implements Runnable {

		@Override
		public void run() {
			while (true) {
				long start = System.currentTimeMillis();

				Map<String, List<IDeviceEvent>> working = new HashMap<String, List<IDeviceEvent>>();
				synchronized (eventsByAssignment) {
					working.putAll(eventsByAssignment);
					eventsByAssignment.clear();
				}

				Set<String> keys = working.keySet();
				for (String key : keys) {
					try {
						IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignmentByToken(key);
						DeviceAssignmentState state =
								(assignment.getState() != null)
										? DeviceAssignmentState.copy(assignment.getState())
										: new DeviceAssignmentState();
						List<IDeviceEvent> events = working.get(key);
						mergeEvents(state, events);
						getDeviceManagement().updateDeviceAssignmentState(key, state);
					} catch (SiteWhereException e) {
						LOGGER.warn("Unable to update device assignment state.", e);
					}
				}

				long diff = System.currentTimeMillis() - start;
				if (diff < FLUSH_INTERVAL) {
					try {
						Thread.sleep(FLUSH_INTERVAL - diff);
					} catch (InterruptedException e) {
						LOGGER.info("State manager persistence thread shut down.", e);
						return;
					}
				}
			}
		}

		/**
		 * Merge events into the assignment state.
		 * 
		 * @param state
		 * @param events
		 * @throws SiteWhereException
		 */
		protected void mergeEvents(DeviceAssignmentState state, List<IDeviceEvent> events)
				throws SiteWhereException {
			for (IDeviceEvent event : events) {
				switch (event.getEventType()) {
				case Location: {
					updateWithLocation(state, (IDeviceLocation) event);
					state.setPresenceMissingDate(null);
					break;
				}
				case Measurements: {
					updateWithMeasurements(state, (IDeviceMeasurements) event);
					state.setPresenceMissingDate(null);
					break;
				}
				case Alert: {
					updateWithAlert(state, (IDeviceAlert) event);
					state.setPresenceMissingDate(null);
					break;
				}
				case StateChange: {
					updateWithStateChange(state, (IDeviceStateChange) event);
					break;
				}
				default: {
					LOGGER.warn("Unhandle event type for state: " + event.getEventType().name());
				}
				}
			}
		}

		/**
		 * Update state with new location.
		 * 
		 * @param state
		 * @param location
		 * @throws SiteWhereException
		 */
		private void updateWithLocation(DeviceAssignmentState state, IDeviceLocation location)
				throws SiteWhereException {
			state.setLastInteractionDate(new Date());

			if ((state.getLastLocation() == null)
					|| (location.getEventDate().after(state.getLastLocation().getEventDate()))) {
				state.setLastLocation(DeviceLocation.copy(location));
			}
		}

		/**
		 * Update state with new measurements.
		 * 
		 * @param state
		 * @param measurements
		 * @throws SiteWhereException
		 */
		private void updateWithMeasurements(DeviceAssignmentState state, IDeviceMeasurements measurements)
				throws SiteWhereException {
			state.setLastInteractionDate(new Date());

			Map<String, IDeviceMeasurement> measurementsById = new HashMap<String, IDeviceMeasurement>();
			if (state.getLatestMeasurements() != null) {
				for (IDeviceMeasurement m : state.getLatestMeasurements()) {
					measurementsById.put(m.getName(), m);
				}
			}
			for (String key : measurements.getMeasurements().keySet()) {
				IDeviceMeasurement em = measurementsById.get(key);
				if ((em == null) || (em.getEventDate().before(measurements.getEventDate()))) {
					Double value = measurements.getMeasurement(key);
					DeviceMeasurement newMeasurement = new DeviceMeasurement();
					DeviceEvent.copy(measurements, newMeasurement);
					newMeasurement.setName(key);
					newMeasurement.setValue(value);
					measurementsById.put(key, newMeasurement);
				}
			}
			state.getLatestMeasurements().clear();
			for (IDeviceMeasurement m : measurementsById.values()) {
				state.getLatestMeasurements().add(m);
			}
		}

		/**
		 * Update state with new alert.
		 * 
		 * @param state
		 * @param alert
		 * @throws SiteWhereException
		 */
		private void updateWithAlert(DeviceAssignmentState state, IDeviceAlert alert)
				throws SiteWhereException {
			state.setLastInteractionDate(new Date());

			Map<String, IDeviceAlert> alertsById = new HashMap<String, IDeviceAlert>();
			for (IDeviceAlert a : state.getLatestAlerts()) {
				alertsById.put(a.getType(), a);
			}
			IDeviceAlert ea = alertsById.get(alert.getType());
			if ((ea == null) || (ea.getEventDate().before(alert.getEventDate()))) {
				DeviceAlert newAlert = DeviceAlert.copy(alert);
				alertsById.put(newAlert.getType(), newAlert);
			}
			state.getLatestAlerts().clear();
			for (IDeviceAlert a : alertsById.values()) {
				state.getLatestAlerts().add(a);
			}
		}

		/**
		 * Update state with state change.
		 * 
		 * @param state
		 * @param change
		 * @throws SiteWhereException
		 */
		private void updateWithStateChange(DeviceAssignmentState state, IDeviceStateChange change)
				throws SiteWhereException {
			// Handle case where state should reflect non-present device.
			if ((change.getCategory() == StateChangeCategory.Presence)
					&& (change.getType() == StateChangeType.Presence_Updated)
					&& (PresenceState.NOT_PRESENT.name().equals(change.getNewState()))) {
				state.setPresenceMissingDate(new Date());
			}
		}
	}

	protected IDeviceManagement getDeviceManagement() {
		return deviceManagement;
	}

	protected void setDeviceManagement(IDeviceManagement deviceManagement) {
		this.deviceManagement = deviceManagement;
	}

	public Map<String, List<IDeviceEvent>> getEventsByAssignment() {
		return eventsByAssignment;
	}

	public void setEventsByAssignment(Map<String, List<IDeviceEvent>> eventsByAssignment) {
		this.eventsByAssignment = eventsByAssignment;
	}
}