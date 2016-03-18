/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device;

import java.util.List;

import com.sitewhere.server.lifecycle.LifecycleComponentDecorator;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.IDeviceStreamData;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Allows classes to inject themselves as a facade around an existing device event
 * management implementation. By default all methods just pass calls to the underlying
 * delegate.
 * 
 * @author Derek
 */
public class DeviceEventManagementDecorator extends LifecycleComponentDecorator implements
		IDeviceEventManagement {

	/** Delegate instance */
	private IDeviceEventManagement delegate;

	public DeviceEventManagementDecorator(IDeviceEventManagement delegate) {
		super(delegate);
		this.delegate = delegate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent#setTenant(com.sitewhere
	 * .spi.user.ITenant)
	 */
	@Override
	public void setTenant(ITenant tenant) {
		delegate.setTenant(tenant);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent#getTenant()
	 */
	@Override
	public ITenant getTenant() {
		return delegate.getTenant();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#setDeviceManagement(com.sitewhere
	 * .spi.device.IDeviceManagement)
	 */
	@Override
	public void setDeviceManagement(IDeviceManagement deviceManagement) throws SiteWhereException {
		delegate.setDeviceManagement(deviceManagement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceEventManagement#getDeviceManagement()
	 */
	@Override
	public IDeviceManagement getDeviceManagement() throws SiteWhereException {
		return delegate.getDeviceManagement();
	}

	@Override
	public IDeviceEventBatchResponse addDeviceEventBatch(String assignmentToken, IDeviceEventBatch batch)
			throws SiteWhereException {
		return delegate.addDeviceEventBatch(assignmentToken, batch);
	}

	@Override
	public IDeviceEvent getDeviceEventById(String id) throws SiteWhereException {
		return delegate.getDeviceEventById(id);
	}

	@Override
	public ISearchResults<IDeviceEvent> listDeviceEvents(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceEvents(assignmentToken, criteria);
	}

	@Override
	public IDeviceMeasurements addDeviceMeasurements(String assignmentToken,
			IDeviceMeasurementsCreateRequest measurements) throws SiteWhereException {
		return delegate.addDeviceMeasurements(assignmentToken, measurements);
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
	public IDeviceLocation addDeviceLocation(String assignmentToken, IDeviceLocationCreateRequest request)
			throws SiteWhereException {
		return delegate.addDeviceLocation(assignmentToken, request);
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
	public IDeviceAlert addDeviceAlert(String assignmentToken, IDeviceAlertCreateRequest request)
			throws SiteWhereException {
		return delegate.addDeviceAlert(assignmentToken, request);
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
	public IDeviceStreamData addDeviceStreamData(String assignmentToken,
			IDeviceStreamDataCreateRequest request) throws SiteWhereException {
		return delegate.addDeviceStreamData(assignmentToken, request);
	}

	@Override
	public IDeviceStreamData getDeviceStreamData(String assignmentToken, String streamId, long sequenceNumber)
			throws SiteWhereException {
		return delegate.getDeviceStreamData(assignmentToken, streamId, sequenceNumber);
	}

	@Override
	public ISearchResults<IDeviceStreamData> listDeviceStreamData(String assignmentToken, String streamId,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceStreamData(assignmentToken, streamId, criteria);
	}

	@Override
	public IDeviceCommandInvocation addDeviceCommandInvocation(String assignmentToken,
			IDeviceCommand command, IDeviceCommandInvocationCreateRequest request) throws SiteWhereException {
		return delegate.addDeviceCommandInvocation(assignmentToken, command, request);
	}

	@Override
	public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocations(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceCommandInvocations(assignmentToken, criteria);
	}

	@Override
	public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceCommandInvocationsForSite(siteToken, criteria);
	}

	@Override
	public ISearchResults<IDeviceCommandResponse> listDeviceCommandInvocationResponses(String invocationId)
			throws SiteWhereException {
		return delegate.listDeviceCommandInvocationResponses(invocationId);
	}

	@Override
	public IDeviceCommandResponse addDeviceCommandResponse(String assignmentToken,
			IDeviceCommandResponseCreateRequest request) throws SiteWhereException {
		return delegate.addDeviceCommandResponse(assignmentToken, request);
	}

	@Override
	public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponses(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceCommandResponses(assignmentToken, criteria);
	}

	@Override
	public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceCommandResponsesForSite(siteToken, criteria);
	}

	@Override
	public IDeviceStateChange addDeviceStateChange(String assignmentToken,
			IDeviceStateChangeCreateRequest request) throws SiteWhereException {
		return delegate.addDeviceStateChange(assignmentToken, request);
	}

	@Override
	public ISearchResults<IDeviceStateChange> listDeviceStateChanges(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceStateChanges(assignmentToken, criteria);
	}

	@Override
	public ISearchResults<IDeviceStateChange> listDeviceStateChangesForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceStateChangesForSite(siteToken, criteria);
	}
}
