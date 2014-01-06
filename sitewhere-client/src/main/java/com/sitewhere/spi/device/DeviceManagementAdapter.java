/*
 * DeviceManagementAdapter.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.common.IMetadataProvider;
import com.sitewhere.spi.device.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCreateRequest;
import com.sitewhere.spi.device.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.request.ISiteCreateRequest;
import com.sitewhere.spi.device.request.IZoneCreateRequest;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Allows classes to inject themselves as a facade around an existing device management
 * implementation. By default all methods just pass calls to the underlying delegate.
 * 
 * @author Derek
 */
public class DeviceManagementAdapter implements IDeviceManagement {

	/** Delegate instance */
	private IDeviceManagement delegate;

	@Override
	public void start() throws SiteWhereException {
		delegate.start();
	}

	@Override
	public void stop() throws SiteWhereException {
		delegate.stop();
	}

	@Override
	public IDevice createDevice(IDeviceCreateRequest device) throws SiteWhereException {
		return delegate.createDevice(device);
	}

	@Override
	public IDevice getDeviceByHardwareId(String hardwareId) throws SiteWhereException {
		return delegate.getDeviceByHardwareId(hardwareId);
	}

	@Override
	public IDevice updateDevice(String hardwareId, IDeviceCreateRequest request) throws SiteWhereException {
		return delegate.updateDevice(hardwareId, request);
	}

	@Override
	public IDeviceAssignment getCurrentDeviceAssignment(IDevice device) throws SiteWhereException {
		return delegate.getCurrentDeviceAssignment(device);
	}

	@Override
	public ISearchResults<IDevice> listDevices(boolean includeDeleted, ISearchCriteria criteria)
			throws SiteWhereException {
		return delegate.listDevices(includeDeleted, criteria);
	}

	@Override
	public ISearchResults<IDevice> listUnassignedDevices(ISearchCriteria criteria) throws SiteWhereException {
		return delegate.listUnassignedDevices(criteria);
	}

	@Override
	public IDevice deleteDevice(String hardwareId, boolean force) throws SiteWhereException {
		return delegate.deleteDevice(hardwareId, force);
	}

	@Override
	public IDeviceAssignment createDeviceAssignment(IDeviceAssignmentCreateRequest request)
			throws SiteWhereException {
		return delegate.createDeviceAssignment(request);
	}

	@Override
	public IDeviceAssignment getDeviceAssignmentByToken(String token) throws SiteWhereException {
		return delegate.getDeviceAssignmentByToken(token);
	}

	@Override
	public IDeviceAssignment deleteDeviceAssignment(String token, boolean force) throws SiteWhereException {
		return delegate.deleteDeviceAssignment(token, force);
	}

	@Override
	public IDevice getDeviceForAssignment(IDeviceAssignment assignment) throws SiteWhereException {
		return delegate.getDeviceForAssignment(assignment);
	}

	@Override
	public ISite getSiteForAssignment(IDeviceAssignment assignment) throws SiteWhereException {
		return delegate.getSiteForAssignment(assignment);
	}

	@Override
	public IDeviceAssignment updateDeviceAssignmentMetadata(String token, IMetadataProvider metadata)
			throws SiteWhereException {
		return delegate.updateDeviceAssignmentMetadata(token, metadata);
	}

	@Override
	public IDeviceAssignment updateDeviceAssignmentStatus(String token, DeviceAssignmentStatus status)
			throws SiteWhereException {
		return delegate.updateDeviceAssignmentStatus(token, status);
	}

	@Override
	public IDeviceAssignment updateDeviceAssignmentState(String token, IDeviceEventBatch batch)
			throws SiteWhereException {
		return delegate.updateDeviceAssignmentState(token, batch);
	}

	@Override
	public IDeviceEventBatchResponse addDeviceEventBatch(String assignmentToken, IDeviceEventBatch batch)
			throws SiteWhereException {
		return delegate.addDeviceEventBatch(assignmentToken, batch);
	}

	@Override
	public IDeviceAssignment endDeviceAssignment(String token) throws SiteWhereException {
		return delegate.endDeviceAssignment(token);
	}

	@Override
	public ISearchResults<IDeviceAssignment> getDeviceAssignmentHistory(String hardwareId,
			ISearchCriteria criteria) throws SiteWhereException {
		return delegate.getDeviceAssignmentHistory(hardwareId, criteria);
	}

	@Override
	public ISearchResults<IDeviceAssignment> getDeviceAssignmentsForSite(String siteToken,
			ISearchCriteria criteria) throws SiteWhereException {
		return delegate.getDeviceAssignmentsForSite(siteToken, criteria);
	}

	@Override
	public ISearchResults<IDeviceAssignment> getDeviceAssignmentsNear(double latitude, double longitude,
			double maxDistance, ISearchCriteria criteria) throws SiteWhereException {
		return delegate.getDeviceAssignmentsNear(latitude, longitude, maxDistance, criteria);
	}

	@Override
	public IDeviceMeasurements addDeviceMeasurements(IDeviceAssignment assignment,
			IDeviceMeasurementsCreateRequest measurements) throws SiteWhereException {
		return delegate.addDeviceMeasurements(assignment, measurements);
	}

	@Override
	public ISearchResults<IDeviceMeasurements> listDeviceMeasurements(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceMeasurements(assignmentToken, criteria);
	}

	@Override
	public ISearchResults<IDeviceMeasurements> listDeviceMeasurementsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceMeasurementsForSite(siteToken, criteria);
	}

	@Override
	public IDeviceLocation addDeviceLocation(IDeviceAssignment assignment,
			IDeviceLocationCreateRequest request) throws SiteWhereException {
		return delegate.addDeviceLocation(assignment, request);
	}

	@Override
	public ISearchResults<IDeviceLocation> listDeviceLocations(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceLocations(assignmentToken, criteria);
	}

	@Override
	public ISearchResults<IDeviceLocation> listDeviceLocationsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceLocationsForSite(siteToken, criteria);
	}

	@Override
	public ISearchResults<IDeviceLocation> listDeviceLocations(List<String> assignmentTokens,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceLocations(assignmentTokens, criteria);
	}

	@Override
	public IDeviceAlert addDeviceAlert(IDeviceAssignment assignment, IDeviceAlertCreateRequest request)
			throws SiteWhereException {
		return delegate.addDeviceAlert(assignment, request);
	}

	@Override
	public ISearchResults<IDeviceAlert> listDeviceAlerts(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceAlerts(assignmentToken, criteria);
	}

	@Override
	public ISearchResults<IDeviceAlert> listDeviceAlertsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceAlertsForSite(siteToken, criteria);
	}

	@Override
	public ISite createSite(ISiteCreateRequest request) throws SiteWhereException {
		return delegate.createSite(request);
	}

	@Override
	public ISite deleteSite(String siteToken, boolean force) throws SiteWhereException {
		return delegate.deleteSite(siteToken, force);
	}

	@Override
	public ISite updateSite(String siteToken, ISiteCreateRequest request) throws SiteWhereException {
		return delegate.updateSite(siteToken, request);
	}

	@Override
	public ISite getSiteByToken(String token) throws SiteWhereException {
		return delegate.getSiteByToken(token);
	}

	@Override
	public ISearchResults<ISite> listSites(ISearchCriteria criteria) throws SiteWhereException {
		return delegate.listSites(criteria);
	}

	@Override
	public IZone createZone(ISite site, IZoneCreateRequest request) throws SiteWhereException {
		return delegate.createZone(site, request);
	}

	@Override
	public IZone updateZone(String token, IZoneCreateRequest request) throws SiteWhereException {
		return delegate.updateZone(token, request);
	}

	@Override
	public IZone getZone(String zoneToken) throws SiteWhereException {
		return delegate.getZone(zoneToken);
	}

	@Override
	public ISearchResults<IZone> listZones(String siteToken, ISearchCriteria criteria)
			throws SiteWhereException {
		return delegate.listZones(siteToken, criteria);
	}

	@Override
	public IZone deleteZone(String zoneToken, boolean force) throws SiteWhereException {
		return delegate.deleteZone(zoneToken, force);
	}

	public IDeviceManagement getDelegate() {
		return delegate;
	}

	public void setDelegate(IDeviceManagement delegate) {
		this.delegate = delegate;
	}
}