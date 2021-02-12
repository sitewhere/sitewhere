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
package com.sitewhere.devicestate.persistence.rdb;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.sitewhere.devicestate.persistence.rdb.entity.RdbDeviceState;
import com.sitewhere.devicestate.persistence.rdb.entity.RdbRecentAlertEvent;
import com.sitewhere.devicestate.persistence.rdb.entity.RdbRecentLocationEvent;
import com.sitewhere.devicestate.persistence.rdb.entity.RdbRecentMeasurementEvent;
import com.sitewhere.devicestate.spi.IDeviceStateMergeStrategy;
import com.sitewhere.devicestate.spi.microservice.IDeviceStateTenantEngine;
import com.sitewhere.microservice.api.state.IDeviceStateManagement;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.rdb.spi.IRdbEntityManagerProvider;
import com.sitewhere.rdb.spi.ITransactionCallback;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.state.request.IDeviceStateEventMergeRequest;

/**
 * Default device state merge strategy.
 */
public class RdbDeviceStateMergeStrategy implements IDeviceStateMergeStrategy<RdbDeviceState> {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(RdbDeviceStateMergeStrategy.class);

    /** Number of recent locations to keep */
    private static final int NUM_RECENT_LOCATIONS = 3;

    /** Device state management implementation */
    private IDeviceStateTenantEngine tenantEngine;

    /** Device state management implementation */
    private IDeviceStateManagement deviceStateManagement;

    @Inject
    public RdbDeviceStateMergeStrategy(IDeviceStateTenantEngine tenantEngine,
	    IDeviceStateManagement deviceStateManagement) {
	this.tenantEngine = tenantEngine;
	this.deviceStateManagement = deviceStateManagement;
    }

    /*
     * @see
     * com.sitewhere.devicestate.spi.IDeviceStateMergeStrategy#merge(java.util.UUID,
     * com.sitewhere.spi.device.state.request.IDeviceStateEventMergeRequest)
     */
    @Override
    public RdbDeviceState merge(UUID deviceStateId, IDeviceStateEventMergeRequest request) throws SiteWhereException {
	LOGGER.info(String.format("Merging device state for %s", deviceStateId));

	return getRdbEntityManagerProvider().runInTransaction(new ITransactionCallback<RdbDeviceState>() {

	    /*
	     * @see com.sitewhere.rdb.spi.ITransactionCallback#process()
	     */
	    @Override
	    public RdbDeviceState process() {
		try {
		    RdbDeviceState state = getRdbEntityManagerProvider().findById(deviceStateId, RdbDeviceState.class);
		    if (state != null) {
			// Merge various event types.
			mergeLocations(state, request);
			mergeMeasurements(state, request);
			mergeAlerts(state, request);
		    }

		    return getRdbEntityManagerProvider().merge(state);
		} catch (SiteWhereException e) {
		    LOGGER.error("Unable to merge device state.", e);
		    return null;
		}
	    }
	});
    }

    /**
     * Merge device locations into existing state.
     * 
     * @param original
     * @param request
     * @throws SiteWhereException
     */
    protected void mergeLocations(RdbDeviceState original, IDeviceStateEventMergeRequest request)
	    throws SiteWhereException {
	if (request.getLocations().size() > 0) {
	    // Combine existing with new locations.
	    List<IDeviceLocation> all = new ArrayList<>();
	    List<RdbRecentLocationEvent> recent = original.getRecentLocations();
	    if (recent != null) {
		for (RdbRecentLocationEvent current : recent) {
		    all.add(RdbRecentLocationEvent.createApiFrom(current));
		}
	    }
	    all.addAll(request.getLocations());

	    // Inverse sort of locations by event date.
	    all.sort(new Comparator<IDeviceLocation>() {

		@Override
		public int compare(IDeviceLocation o1, IDeviceLocation o2) {
		    return -1 * o1.getEventDate().compareTo(o2.getEventDate());
		}
	    });

	    int remaining = NUM_RECENT_LOCATIONS;
	    List<RdbRecentLocationEvent> updated = new ArrayList<>();
	    while (remaining > 0) {
		if (all.size() > 0) {
		    IDeviceLocation location = all.remove(0);
		    RdbRecentLocationEvent rdb = RdbRecentLocationEvent.createFrom(original, location);
		    rdb = getRdbEntityManagerProvider().persist(rdb);
		    updated.add(rdb);
		}
		remaining--;
	    }

	    original.setRecentLocations(updated);
	}
    }

    /**
     * Merge device measurements into existing state.
     * 
     * @param original
     * @param request
     * @throws SiteWhereException
     */
    protected void mergeMeasurements(RdbDeviceState original, IDeviceStateEventMergeRequest request)
	    throws SiteWhereException {
	if (request.getMeasurements().size() > 0) {
	    // Create map of existing measurements by name.
	    Map<String, RdbRecentMeasurementEvent> eventsByMxName = new HashMap<>();
	    List<RdbRecentMeasurementEvent> recent = original.getRecentMeasurements();
	    if (recent != null) {
		for (RdbRecentMeasurementEvent current : recent) {
		    eventsByMxName.put(current.getName(), current);
		}
	    }

	    // Sort measurements by event date.
	    request.getMeasurements().sort(new Comparator<IDeviceMeasurement>() {

		@Override
		public int compare(IDeviceMeasurement o1, IDeviceMeasurement o2) {
		    return o1.getEventDate().compareTo(o2.getEventDate());
		}
	    });

	    // Add or update measurements based on new events.
	    for (IDeviceMeasurement current : request.getMeasurements()) {
		RdbRecentMeasurementEvent rdb = eventsByMxName.get(current.getName());
		if (rdb == null) {
		    rdb = RdbRecentMeasurementEvent.createFrom(original, current);
		    rdb = getRdbEntityManagerProvider().persist(rdb);
		    eventsByMxName.put(rdb.getName(), rdb);
		}
		if ((rdb.getMaxValue() == null) || (current.getValue().compareTo(rdb.getValue()) > 0)) {
		    rdb.setMaxValue(current.getValue());
		    rdb.setMaxValueDate(new Date());
		}
		if ((rdb.getMinValue() == null) || (current.getValue().compareTo(rdb.getValue()) < 0)) {
		    rdb.setMinValue(current.getValue());
		    rdb.setMinValueDate(new Date());
		}
	    }

	    // Set list of updated measurements.
	    List<RdbRecentMeasurementEvent> updated = new ArrayList<>();
	    for (RdbRecentMeasurementEvent current : eventsByMxName.values()) {
		updated.add(current);
	    }
	    original.setRecentMeasurements(updated);
	}
    }

    /**
     * Merge device alerts into existing state.
     * 
     * @param original
     * @param request
     * @throws SiteWhereException
     */
    protected void mergeAlerts(RdbDeviceState original, IDeviceStateEventMergeRequest request)
	    throws SiteWhereException {
	LOGGER.info(String.format("About to merge %s alerts.", request.getAlerts().size()));
	if (request.getAlerts().size() > 0) {
	    // Create map of existing measurements by name.
	    Map<String, RdbRecentAlertEvent> eventsByAlertType = new HashMap<>();
	    List<RdbRecentAlertEvent> recent = original.getRecentAlerts();
	    if (recent != null) {
		for (RdbRecentAlertEvent current : recent) {
		    eventsByAlertType.put(current.getType(), current);
		}
	    }
	    LOGGER.info(
		    String.format("Alerts before:\n%s\n\n", MarshalUtils.marshalJsonAsPrettyString(eventsByAlertType)));

	    // Sort alerts event date.
	    request.getAlerts().sort(new Comparator<IDeviceAlert>() {

		@Override
		public int compare(IDeviceAlert o1, IDeviceAlert o2) {
		    return o1.getEventDate().compareTo(o2.getEventDate());
		}
	    });

	    // Add or update alerts based on new events.
	    for (IDeviceAlert current : request.getAlerts()) {
		RdbRecentAlertEvent rdb = eventsByAlertType.get(current.getType());
		if (rdb == null) {
		    rdb = RdbRecentAlertEvent.createFrom(original, current);
		    rdb = getRdbEntityManagerProvider().persist(rdb);
		    eventsByAlertType.put(rdb.getType(), rdb);
		}
	    }
	    LOGGER.info(String.format("Alerts updated:\n%s\n\n",
		    MarshalUtils.marshalJsonAsPrettyString(eventsByAlertType)));

	    // Set list of updated measurements.
	    List<RdbRecentAlertEvent> updated = new ArrayList<>();
	    for (RdbRecentAlertEvent current : eventsByAlertType.values()) {
		updated.add(current);
	    }
	    original.setRecentAlerts(updated);
	}
    }

    protected IDeviceStateTenantEngine getTenantEngine() {
	return tenantEngine;
    }

    protected IDeviceStateManagement getDeviceStateManagement() {
	return deviceStateManagement;
    }

    protected IRdbEntityManagerProvider getRdbEntityManagerProvider() {
	return getTenantEngine().getRdbEntityManagerProvider();
    }
}
