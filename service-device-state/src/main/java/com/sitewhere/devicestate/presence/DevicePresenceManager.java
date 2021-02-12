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
package com.sitewhere.devicestate.presence;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import com.sitewhere.devicestate.spi.IDevicePresenceManager;
import com.sitewhere.devicestate.spi.IPresenceNotificationStrategy;
import com.sitewhere.devicestate.spi.microservice.IDeviceStateMicroservice;
import com.sitewhere.devicestate.spi.microservice.IDeviceStateTenantEngine;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.event.DeviceEventRequestBuilder;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.microservice.api.state.IDeviceStateManagement;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.microservice.security.SystemUserRunnable;
import com.sitewhere.rest.model.device.event.request.DeviceStateChangeCreateRequest;
import com.sitewhere.rest.model.device.state.request.DeviceStateCreateRequest;
import com.sitewhere.rest.model.search.device.DeviceStateSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.event.state.PresenceState;
import com.sitewhere.spi.device.state.IDeviceState;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Monitors assignment state to detect device presence information.
 */
public class DevicePresenceManager extends TenantEngineLifecycleComponent implements IDevicePresenceManager {

    /** Default presence check interval (10 min) */
    private static final String DEFAULT_PRESENCE_CHECK_INTERVAL = "10m";

    /** Default presence missing interval (1 hour) */
    private static final String DEFAULT_PRESENCE_MISSING_INTERVAL = "8h";

    /** Used to format durations for output */
    private static final PeriodFormatter PERIOD_FORMATTER = new PeriodFormatterBuilder().appendWeeks().appendSuffix("w")
	    .appendSeparator(" ").appendDays().appendSuffix("d").appendSeparator(" ").appendHours().appendSuffix("h")
	    .appendSeparator(" ").appendMinutes().appendSuffix("m").appendSeparator(" ").appendSeconds()
	    .appendSuffix("s").toFormatter();

    /** Presence check interval */
    private String presenceCheckInterval = DEFAULT_PRESENCE_CHECK_INTERVAL;

    /** Presence missing interval */
    private String presenceMissingInterval = DEFAULT_PRESENCE_MISSING_INTERVAL;

    /**
     * Chooses how presence state is stored and how often notifications are sent
     */
    private IPresenceNotificationStrategy presenceNotificationStrategy = new PresenceNotificationStrategies.SendOnceNotificationStrategy();

    /** Executor service for threading */
    private ExecutorService executor;

    public DevicePresenceManager() {
	super(LifecycleComponentType.DevicePresenceManager);
    }

    /*
     * @see
     * com.sitewhere.microservice.lifecycle.LifecycleComponent#start(com.sitewhere.
     * spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.executor = Executors.newSingleThreadExecutor();
	executor.execute(new PresenceChecker());
    }

    /*
     * @see
     * com.sitewhere.microservice.lifecycle.LifecycleComponent#stop(com.sitewhere.
     * spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (executor != null) {
	    executor.shutdownNow();
	}
    }

    /**
     * Thread that checks for device presence.
     */
    private class PresenceChecker extends SystemUserRunnable {

	public PresenceChecker() {
	    super(DevicePresenceManager.this);
	}

	@Override
	public void runAsSystemUser() throws SiteWhereException {
	    Period missingInterval;
	    try {
		missingInterval = Period.parse(getPresenceMissingInterval(), ISOPeriodFormat.standard());
	    } catch (IllegalArgumentException e) {
		missingInterval = PERIOD_FORMATTER.parsePeriod(getPresenceMissingInterval());
	    }
	    int missingIntervalSecs = missingInterval.toStandardSeconds().getSeconds();

	    Period checkInterval;
	    try {
		checkInterval = Period.parse(getPresenceCheckInterval(), ISOPeriodFormat.standard());
	    } catch (IllegalArgumentException e) {
		checkInterval = PERIOD_FORMATTER.parsePeriod(getPresenceCheckInterval());
	    }
	    int checkIntervalSecs = checkInterval.toStandardSeconds().getSeconds();

	    getLogger().info("Presence manager checking every " + PERIOD_FORMATTER.print(checkInterval) + " ("
		    + checkIntervalSecs + " seconds) " + "for devices with last interaction date of more than "
		    + PERIOD_FORMATTER.print(missingInterval) + " (" + missingIntervalSecs + " seconds) " + ".");

	    while (true) {
		try {
		    // TODO: For performance/memory consumption, this should be done in batches.
		    Date endDate = new Date(System.currentTimeMillis() - (missingIntervalSecs * 1000));
		    DeviceStateSearchCriteria criteria = new DeviceStateSearchCriteria(1, 0);
		    criteria.setLastInteractionDateBefore(endDate);
		    ISearchResults<? extends IDeviceState> missing = getDeviceStateManagement()
			    .searchDeviceStates(criteria);

		    if (missing.getNumResults() > 0) {
			getLogger()
				.info("Presence manager detected " + missing.getNumResults() + " non-present devices.");
		    } else {
			getLogger().info("No non-present devices detected.");
		    }
		    for (IDeviceState deviceState : missing.getResults()) {
			if (sendPresenceMissing(deviceState)) {
			    try {
				DeviceStateCreateRequest update = new DeviceStateCreateRequest();
				update.setDeviceId(deviceState.getDeviceId());
				update.setDeviceAssignmentId(deviceState.getDeviceAssignmentId());
				update.setPresenceMissingDate(new Date());
				update.setLastInteractionDate(deviceState.getLastInteractionDate());
				getDeviceStateManagement().updateDeviceState(deviceState.getId(), update);
			    } catch (SiteWhereException e) {
				getLogger().warn("Unable to update presence missing date.", e);
			    }
			}
		    }
		} catch (SiteWhereException e) {
		    getLogger().error("Error processing presence query.", e);
		}

		try {
		    Thread.sleep(checkIntervalSecs * 1000);
		} catch (InterruptedException e) {
		    getLogger().info("Presence check thread shut down.");
		}
	    }
	}

	/**
	 * Create state change event to indicate device not present.
	 * 
	 * @param deviceState
	 * @throws SiteWhereException
	 */
	protected boolean sendPresenceMissing(IDeviceState deviceState) {
	    DeviceStateChangeCreateRequest create = new DeviceStateChangeCreateRequest();
	    create.setAttribute(IDeviceStateChangeCreateRequest.ATTRIBUTE_PRESENCE);
	    create.setType("automated");
	    create.setPreviousState(PresenceState.PRESENT.name());
	    create.setNewState(PresenceState.NOT_PRESENT.name());

	    try {
		// Only send an event if the strategy permits it.
		if (getPresenceNotificationStrategy().shouldGenerateEvent(deviceState, create)) {
		    IDeviceAssignment assignment = getDeviceManagement()
			    .getDeviceAssignment(deviceState.getDeviceAssignmentId());
		    IDeviceEventContext context = DeviceEventRequestBuilder
			    .getContextForAssignment(getDeviceManagement(), assignment);
		    getDeviceEventManagement().addDeviceStateChanges(context, create);
		    return true;
		}
	    } catch (SiteWhereException e) {
		getLogger().error("Unable to create state change event for presence missing.", e);
	    }
	    return false;
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.presence.IDevicePresenceManager#
     * getPresenceNotificationStrategy()
     */
    public IPresenceNotificationStrategy getPresenceNotificationStrategy() {
	return presenceNotificationStrategy;
    }

    public void setPresenceNotificationStrategy(IPresenceNotificationStrategy presenceNotificationStrategy) {
	this.presenceNotificationStrategy = presenceNotificationStrategy;
    }

    public String getPresenceCheckInterval() {
	return presenceCheckInterval;
    }

    public void setPresenceCheckInterval(String presenceCheckInterval) {
	this.presenceCheckInterval = presenceCheckInterval;
    }

    public String getPresenceMissingInterval() {
	return presenceMissingInterval;
    }

    public void setPresenceMissingInterval(String presenceMissingInterval) {
	this.presenceMissingInterval = presenceMissingInterval;
    }

    private IDeviceStateManagement getDeviceStateManagement() {
	return ((IDeviceStateTenantEngine) getTenantEngine()).getDeviceStateManagement();
    }

    private IDeviceManagement getDeviceManagement() {
	return ((IDeviceStateMicroservice) getMicroservice()).getDeviceManagement();
    }

    private IDeviceEventManagement getDeviceEventManagement() {
	return ((IDeviceStateMicroservice) getMicroservice()).getDeviceEventManagementApiChannel();
    }
}