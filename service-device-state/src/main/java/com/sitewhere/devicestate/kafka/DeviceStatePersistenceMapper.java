/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.devicestate.kafka;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Windowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.devicestate.configuration.DeviceStateTenantConfiguration;
import com.sitewhere.devicestate.spi.microservice.IDeviceStateMicroservice;
import com.sitewhere.devicestate.spi.microservice.IDeviceStateTenantEngine;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.state.IDeviceStateManagement;
import com.sitewhere.microservice.kafka.KeyValueMapperComponent;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.microservice.security.SystemUserCallable;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceEvent;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurement;
import com.sitewhere.rest.model.device.state.request.DeviceStateCreateRequest;
import com.sitewhere.rest.model.device.state.request.DeviceStateEventMergeRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.state.IDeviceState;

import io.prometheus.client.Histogram;

/**
 * Persists {@link AggregatedDeviceState} via the device state APIs.
 */
public class DeviceStatePersistenceMapper
	extends KeyValueMapperComponent<Windowed<UUID>, AggregatedDeviceState, KeyValue<UUID, AggregatedDeviceState>> {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(DeviceStatePersistenceMapper.class);

    /** Histogram for device state lookup */
    private static final Histogram DEVICE_STATE_LOOKUP_TIMER = TenantEngineLifecycleComponent
	    .createHistogramMetric("device_state_lookup_timer", "Timer for looking up device state");

    /** Histogram for device state persistence */
    private static final Histogram DEVICE_STATE_MERGE_TIMER = TenantEngineLifecycleComponent
	    .createHistogramMetric("device_state_merge_timer", "Timer for merging device state");

    /** Configuration */
    private DeviceStateTenantConfiguration configuration;

    public DeviceStatePersistenceMapper(DeviceStateTenantConfiguration configuration) {
	this.configuration = configuration;
    }

    /*
     * @see org.apache.kafka.streams.kstream.KeyValueMapper#apply(java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public KeyValue<UUID, AggregatedDeviceState> apply(Windowed<UUID> window, AggregatedDeviceState state) {
	try {
	    new PersistenceOperation(state).call();
	} catch (Exception e) {
	    LOGGER.error("Unable to execute device state persistence operation.", e);
	}

	KeyValue<UUID, AggregatedDeviceState> keyValue = new KeyValue<>(window.key(), state);
	return keyValue;
    }

    /**
     * Runs persistence operation in the context of a system user so that
     * device/assignment lookups may be authenticated.
     */
    protected class PersistenceOperation extends SystemUserCallable<List<IDeviceState>> {

	/** Aggregated device state to persist */
	private AggregatedDeviceState aggregated;

	public PersistenceOperation(AggregatedDeviceState aggregated) {
	    super(DeviceStatePersistenceMapper.this);
	    this.aggregated = aggregated;
	}

	/**
	 * Find and existing event merge request or create a new one.
	 * 
	 * @param event
	 * @param existing
	 * @return
	 */
	protected DeviceStateEventMergeRequest getOrCreateMergeRequestFor(DeviceEvent event,
		Map<UUID, DeviceStateEventMergeRequest> existing) {
	    if (existing.get(event.getDeviceAssignmentId()) != null) {
		return existing.get(event.getDeviceAssignmentId());
	    }
	    DeviceStateEventMergeRequest created = new DeviceStateEventMergeRequest();
	    existing.put(event.getDeviceAssignmentId(), created);
	    return created;
	}

	/**
	 * Get map of event merge requests by device assignment id.
	 * 
	 * @param state
	 * @return
	 */
	protected Map<UUID, DeviceStateEventMergeRequest> getMergeRequestsByDeviceAssignment(
		AggregatedDeviceState state) {
	    Map<UUID, DeviceStateEventMergeRequest> mergeByAssignmentId = new HashMap<>();
	    for (DeviceLocation location : state.getDeviceLocations()) {
		DeviceStateEventMergeRequest request = getOrCreateMergeRequestFor(location, mergeByAssignmentId);
		request.getLocations().add(location);
	    }
	    for (DeviceMeasurement mx : state.getDeviceMeasurements()) {
		DeviceStateEventMergeRequest request = getOrCreateMergeRequestFor(mx, mergeByAssignmentId);
		request.getMeasurements().add(mx);
	    }
	    for (DeviceAlert alert : state.getDeviceAlerts()) {
		DeviceStateEventMergeRequest request = getOrCreateMergeRequestFor(alert, mergeByAssignmentId);
		request.getAlerts().add(alert);
	    }
	    return mergeByAssignmentId;
	}

	/**
	 * Find the latest event and apply it to a state create request.
	 * 
	 * @param request
	 * @param merge
	 * @return
	 */
	protected DeviceStateCreateRequest applyLatestEvent(DeviceStateCreateRequest request,
		DeviceStateEventMergeRequest merge) {
	    List<IDeviceEvent> events = new ArrayList<>();
	    events.addAll(merge.getLocations());
	    events.addAll(merge.getMeasurements());
	    events.addAll(merge.getAlerts());
	    if (events.size() > 0) {
		events.sort(new Comparator<IDeviceEvent>() {

		    @Override
		    public int compare(IDeviceEvent o1, IDeviceEvent o2) {
			return -1 * o1.getEventDate().compareTo(o2.getEventDate());
		    }
		});
		IDeviceEvent event = events.get(0);
		request.setAreaId(event.getAreaId());
		request.setAssetId(event.getAssetId());
		request.setCustomerId(event.getCustomerId());
	    }
	    return request;
	}

	/**
	 * Create a new device state entry fo rthe given assignment.
	 * 
	 * @param device
	 * @param assignment
	 * @param merge
	 * @return
	 * @throws SiteWhereException
	 */
	protected IDeviceState createDeviceState(IDevice device, IDeviceAssignment assignment,
		DeviceStateEventMergeRequest merge) throws SiteWhereException {
	    DeviceStateCreateRequest create = new DeviceStateCreateRequest();
	    create.setDeviceId(assignment.getDeviceId());
	    create.setDeviceAssignmentId(assignment.getId());
	    create.setDeviceTypeId(device.getDeviceTypeId());
	    create.setLastInteractionDate(new Date());
	    create = applyLatestEvent(create, merge);
	    return getDeviceStateManagement().createDeviceState(create);
	}

	/*
	 * @see com.sitewhere.microservice.security.SystemUserCallable#runAsSystemUser()
	 */
	@Override
	public List<IDeviceState> runAsSystemUser() throws SiteWhereException {
	    List<IDeviceState> updated = new ArrayList<>();
	    Map<UUID, DeviceStateEventMergeRequest> mergeByAssignmentId = getMergeRequestsByDeviceAssignment(
		    aggregated);
	    for (UUID deviceAssignmentId : mergeByAssignmentId.keySet()) {
		try {
		    // Get merge request for device assignment and look up existing state.
		    DeviceStateEventMergeRequest merge = mergeByAssignmentId.get(deviceAssignmentId);

		    // Monitor time required to look up existing device state.
		    final Histogram.Timer lookupTime = DEVICE_STATE_LOOKUP_TIMER.labels(getTenantEngine().buildLabels())
			    .startTimer();
		    IDeviceState state = getDeviceStateManagement()
			    .getDeviceStateByDeviceAssignment(deviceAssignmentId);
		    lookupTime.close();

		    // Create new device state if not found for assignment.
		    if (state == null) {
			IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignment(deviceAssignmentId);
			if (assignment == null) {
			    throw new SiteWhereException(String.format("Device assignment not found for id %s.",
				    deviceAssignmentId.toString()));
			}
			IDevice device = getDeviceManagement().getDevice(assignment.getDeviceId());
			state = createDeviceState(device, assignment, merge);
		    }

		    // Monitor time required to merge events to state.
		    final Histogram.Timer mergeTime = DEVICE_STATE_MERGE_TIMER.labels(getTenantEngine().buildLabels())
			    .startTimer();
		    state = getDeviceStateManagement().merge(state.getId(), merge);
		    mergeTime.close();

		    updated.add(state);
		} catch (SiteWhereException e) {
		    LOGGER.error("Unable to persist device state.", e);
		}
	    }
	    return updated;
	}
    }

    /**
     * Get device state tenant configuration.
     * 
     * @return
     */
    protected DeviceStateTenantConfiguration getConfiguration() {
	return configuration;
    }

    /**
     * Get device management implementation.
     * 
     * @return
     */
    protected IDeviceStateManagement getDeviceStateManagement() {
	return ((IDeviceStateTenantEngine) getTenantEngine()).getDeviceStateManagement();
    }

    /**
     * Get device management implementation.
     * 
     * @return
     */
    protected IDeviceManagement getDeviceManagement() {
	return ((IDeviceStateMicroservice) getMicroservice()).getDeviceManagement();
    }
}
