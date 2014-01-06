/*
 * DeviceManagementMetricsFacade.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.metrics;

import java.util.List;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.DeviceManagementAdapter;
import com.sitewhere.spi.device.IDeviceAlert;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceEventBatch;
import com.sitewhere.spi.device.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.IDeviceLocation;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceMeasurements;
import com.sitewhere.spi.device.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Wraps a device management implementation in a facade that gathers metrics about each
 * API call.
 * 
 * @author Derek
 */
public class DeviceManagementMetricsFacade extends DeviceManagementAdapter {

	/** Times invocations of addDeviceEventBatch() */
	private final Timer addDeviceEventBatchTimer = getMetrics().timer(
			MetricRegistry.name(IDeviceManagement.class, "addDeviceEventBatch", "timer"));

	/** Times invocations of getDeviceAssignmentsForSite() */
	private final Timer getDeviceAssignmentsForSiteTimer = getMetrics().timer(
			MetricRegistry.name(IDeviceManagement.class, "getDeviceAssignmentsForSite", "timer"));

	/** Times invocations of addDeviceMeasurements() */
	private final Timer addDeviceMeasurementsTimer = getMetrics().timer(
			MetricRegistry.name(IDeviceManagement.class, "addDeviceMeasurements", "timer"));

	/** Times invocations of listDeviceMeasurements() */
	private final Timer listDeviceMeasurementsTimer = getMetrics().timer(
			MetricRegistry.name(IDeviceManagement.class, "listDeviceMeasurements", "timer"));

	/** Times invocations of listDeviceMeasurementsForSite() */
	private final Timer listDeviceMeasurementsForSiteTimer = getMetrics().timer(
			MetricRegistry.name(IDeviceManagement.class, "listDeviceMeasurementsForSite", "timer"));

	/** Times invocations of addDeviceLocation() */
	private final Timer addDeviceLocationTimer = getMetrics().timer(
			MetricRegistry.name(IDeviceManagement.class, "addDeviceLocation", "timer"));

	/** Times invocations of listDeviceLocations() */
	private final Timer listDeviceLocationsTimer = getMetrics().timer(
			MetricRegistry.name(IDeviceManagement.class, "listDeviceLocations", "timer"));

	/** Times invocations of listDeviceLocationsForSite() */
	private final Timer listDeviceLocationsForSiteTimer = getMetrics().timer(
			MetricRegistry.name(IDeviceManagement.class, "listDeviceLocationsForSite", "timer"));

	/** Times invocations of addDeviceAlert() */
	private final Timer addDeviceAlertTimer = getMetrics().timer(
			MetricRegistry.name(IDeviceManagement.class, "addDeviceAlert", "timer"));

	/** Times invocations of listDeviceAlerts() */
	private final Timer listDeviceAlertsTimer = getMetrics().timer(
			MetricRegistry.name(IDeviceManagement.class, "listDeviceAlerts", "timer"));

	/** Times invocations of listDeviceAlertsForSite() */
	private final Timer listDeviceAlertsForSiteTimer = getMetrics().timer(
			MetricRegistry.name(IDeviceManagement.class, "listDeviceAlertsForSite", "timer"));

	/**
	 * Get the central metrics registry.
	 * 
	 * @return
	 */
	public MetricRegistry getMetrics() {
		return SiteWhereServer.getInstance().getMetricRegistry();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#addDeviceEventBatch(java.lang.String,
	 * com.sitewhere.spi.device.IDeviceEventBatch)
	 */
	public IDeviceEventBatchResponse addDeviceEventBatch(String assignmentToken, IDeviceEventBatch batch)
			throws SiteWhereException {
		final Timer.Context context = addDeviceEventBatchTimer.time();
		try {
			return super.addDeviceEventBatch(assignmentToken, batch);
		} finally {
			context.stop();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentsForSite(java.lang
	 * .String, com.sitewhere.spi.common.ISearchCriteria)
	 */
	public ISearchResults<IDeviceAssignment> getDeviceAssignmentsForSite(String siteToken,
			ISearchCriteria criteria) throws SiteWhereException {
		final Timer.Context context = getDeviceAssignmentsForSiteTimer.time();
		try {
			return super.getDeviceAssignmentsForSite(siteToken, criteria);
		} finally {
			context.stop();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#addDeviceMeasurements(com.sitewhere.
	 * spi.device.IDeviceAssignment,
	 * com.sitewhere.spi.device.request.IDeviceMeasurementsCreateRequest)
	 */
	public IDeviceMeasurements addDeviceMeasurements(IDeviceAssignment assignment,
			IDeviceMeasurementsCreateRequest measurements) throws SiteWhereException {
		final Timer.Context context = addDeviceMeasurementsTimer.time();
		try {
			return super.addDeviceMeasurements(assignment, measurements);
		} finally {
			context.stop();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceMeasurements(java.lang.String,
	 * com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	public ISearchResults<IDeviceMeasurements> listDeviceMeasurements(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		final Timer.Context context = listDeviceMeasurementsTimer.time();
		try {
			return super.listDeviceMeasurements(siteToken, criteria);
		} finally {
			context.stop();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceMeasurementsForSite(java.lang
	 * .String, com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	public ISearchResults<IDeviceMeasurements> listDeviceMeasurementsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		final Timer.Context context = listDeviceMeasurementsForSiteTimer.time();
		try {
			return super.listDeviceMeasurementsForSite(siteToken, criteria);
		} finally {
			context.stop();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#addDeviceLocation(com.sitewhere.spi.
	 * device.IDeviceAssignment,
	 * com.sitewhere.spi.device.request.IDeviceLocationCreateRequest)
	 */
	public IDeviceLocation addDeviceLocation(IDeviceAssignment assignment,
			IDeviceLocationCreateRequest request) throws SiteWhereException {
		final Timer.Context context = addDeviceLocationTimer.time();
		try {
			return super.addDeviceLocation(assignment, request);
		} finally {
			context.stop();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceLocations(java.lang.String,
	 * com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	public ISearchResults<IDeviceLocation> listDeviceLocations(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		final Timer.Context context = listDeviceLocationsTimer.time();
		try {
			return super.listDeviceLocations(assignmentToken, criteria);
		} finally {
			context.stop();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceLocationsForSite(java.lang
	 * .String, com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	public ISearchResults<IDeviceLocation> listDeviceLocationsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		final Timer.Context context = listDeviceLocationsForSiteTimer.time();
		try {
			return super.listDeviceLocationsForSite(siteToken, criteria);
		} finally {
			context.stop();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceLocations(java.util.List,
	 * com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	public ISearchResults<IDeviceLocation> listDeviceLocations(List<String> assignmentTokens,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return super.listDeviceLocations(assignmentTokens, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#addDeviceAlert(com.sitewhere.spi.device
	 * .IDeviceAssignment, com.sitewhere.spi.device.request.IDeviceAlertCreateRequest)
	 */
	public IDeviceAlert addDeviceAlert(IDeviceAssignment assignment, IDeviceAlertCreateRequest request)
			throws SiteWhereException {
		final Timer.Context context = addDeviceAlertTimer.time();
		try {
			return super.addDeviceAlert(assignment, request);
		} finally {
			context.stop();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceAlerts(java.lang.String,
	 * com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	public ISearchResults<IDeviceAlert> listDeviceAlerts(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		final Timer.Context context = listDeviceAlertsTimer.time();
		try {
			return super.listDeviceAlerts(assignmentToken, criteria);
		} finally {
			context.stop();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceAlertsForSite(java.lang.String
	 * , com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	public ISearchResults<IDeviceAlert> listDeviceAlertsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		final Timer.Context context = listDeviceAlertsForSiteTimer.time();
		try {
			return super.listDeviceAlertsForSite(siteToken, criteria);
		} finally {
			context.stop();
		}
	}
}