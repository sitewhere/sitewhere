/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Interface for device event management operations.
 * 
 * @author Derek
 */
public interface IDeviceEventManagement extends ITenantLifecycleComponent {

	/**
	 * Set the device management implementation.
	 * 
	 * @param deviceManagement
	 * @throws SiteWhereException
	 */
	public void setDeviceManagement(IDeviceManagement deviceManagement) throws SiteWhereException;

	/**
	 * Get the device management implementation.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceManagement getDeviceManagement() throws SiteWhereException;

	/**
	 * Add a batch of events for the given assignment.
	 * 
	 * @param assignmentToken
	 * @param batch
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceEventBatchResponse addDeviceEventBatch(String assignmentToken, IDeviceEventBatch batch)
			throws SiteWhereException;

	/**
	 * Get a device event by unique id.
	 * 
	 * @param id
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceEvent getDeviceEventById(String id) throws SiteWhereException;

	/**
	 * List all events for the given assignment that meet the search criteria.
	 * 
	 * @param assignmentToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceEvent> listDeviceEvents(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * Add measurements for a given device assignment.
	 * 
	 * @param assignmentToken
	 * @param measurements
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceMeasurements addDeviceMeasurements(String assignmentToken,
			IDeviceMeasurementsCreateRequest measurements) throws SiteWhereException;

	/**
	 * Gets device measurement entries for an assignment based on criteria.
	 * 
	 * @param assignmentToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceMeasurements> listDeviceMeasurements(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * List device measurements for a site.
	 * 
	 * @param siteToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceMeasurements> listDeviceMeasurementsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * Add location for a given device assignment.
	 * 
	 * @param assignmentToken
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceLocation addDeviceLocation(String assignmentToken, IDeviceLocationCreateRequest request)
			throws SiteWhereException;

	/**
	 * Gets device location entries for an assignment.
	 * 
	 * @param assignmentToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceLocation> listDeviceLocations(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * List device locations for a site.
	 * 
	 * @param siteToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceLocation> listDeviceLocationsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * List device locations for the given tokens within the given time range.
	 * 
	 * @param assignmentTokens
	 * @param start
	 * @param end
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceLocation> listDeviceLocations(List<String> assignmentTokens,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * Add alert for a given device assignment.
	 * 
	 * @param assignmentToken
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceAlert addDeviceAlert(String assignmentToken, IDeviceAlertCreateRequest request)
			throws SiteWhereException;

	/**
	 * Gets the most recent device alert entries for an assignment.
	 * 
	 * @param assignmentToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceAlert> listDeviceAlerts(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * List device alerts for a site.
	 * 
	 * @param siteToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceAlert> listDeviceAlertsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * Add a chunk of stream data for a given device assignment.
	 * 
	 * @param assignmentToken
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceStreamData addDeviceStreamData(String assignmentToken,
			IDeviceStreamDataCreateRequest request) throws SiteWhereException;

	/**
	 * Get a single chunk of data from a device stream.
	 * 
	 * @param assignmentToken
	 * @param streamId
	 * @param sequenceNumber
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceStreamData getDeviceStreamData(String assignmentToken, String streamId, long sequenceNumber)
			throws SiteWhereException;

	/**
	 * List all chunks of data in a device assignment that belong to a given stream and
	 * meet the criteria.
	 * 
	 * @param assignmentToken
	 * @param streamId
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceStreamData> listDeviceStreamData(String assignmentToken, String streamId,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * Add a device command invocation event for the given assignment.
	 * 
	 * @param assignmentToken
	 * @param command
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceCommandInvocation addDeviceCommandInvocation(String assignmentToken,
			IDeviceCommand command, IDeviceCommandInvocationCreateRequest request) throws SiteWhereException;

	/**
	 * Gets device command invocations for an assignment based on criteria.
	 * 
	 * @param assignmentToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocations(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * List device command invocations for a site.
	 * 
	 * @param siteToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * List responses associated with a command invocation.
	 * 
	 * @param invocationId
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceCommandResponse> listDeviceCommandInvocationResponses(String invocationId)
			throws SiteWhereException;

	/**
	 * Adds a new device command response event.
	 * 
	 * @param assignmentToken
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceCommandResponse addDeviceCommandResponse(String assignmentToken,
			IDeviceCommandResponseCreateRequest request) throws SiteWhereException;

	/**
	 * Gets the most recent device command response entries for an assignment.
	 * 
	 * @param assignmentToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponses(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * List device command responses for a site.
	 * 
	 * @param siteToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * Adds a new device state change event.
	 * 
	 * @param assignmentToken
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceStateChange addDeviceStateChange(String assignmentToken,
			IDeviceStateChangeCreateRequest request) throws SiteWhereException;

	/**
	 * Gets the most recent device state change entries for an assignment.
	 * 
	 * @param assignmentToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceStateChange> listDeviceStateChanges(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * List device state changes for a site.
	 * 
	 * @param siteToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceStateChange> listDeviceStateChangesForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;
}