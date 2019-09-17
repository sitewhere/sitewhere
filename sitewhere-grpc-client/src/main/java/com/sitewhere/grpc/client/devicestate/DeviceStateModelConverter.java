/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.devicestate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sitewhere.grpc.client.common.converter.CommonModelConverter;
import com.sitewhere.grpc.model.CommonModel.GUUID;
import com.sitewhere.grpc.model.DeviceStateModel.GDeviceState;
import com.sitewhere.grpc.model.DeviceStateModel.GDeviceStateCreateRequest;
import com.sitewhere.grpc.model.DeviceStateModel.GDeviceStateSearchCriteria;
import com.sitewhere.grpc.model.DeviceStateModel.GDeviceStateSearchResults;
import com.sitewhere.rest.model.device.state.DeviceState;
import com.sitewhere.rest.model.device.state.request.DeviceStateCreateRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.device.DeviceStateSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.state.IDeviceState;
import com.sitewhere.spi.device.state.request.IDeviceStateCreateRequest;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IDeviceStateSearchCriteria;

/**
 * Convert device state entities between SiteWhere API model and GRPC model.
 * 
 * @author Derek
 */
public class DeviceStateModelConverter {

    /**
     * Convert device state create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceStateCreateRequest asApiDeviceStateCreateRequest(GDeviceStateCreateRequest grpc)
	    throws SiteWhereException {
	DeviceStateCreateRequest api = new DeviceStateCreateRequest();
	api.setDeviceId(CommonModelConverter.asApiUuid(grpc.getDeviceId()));
	api.setDeviceTypeId(CommonModelConverter.asApiUuid(grpc.getDeviceTypeId()));
	api.setDeviceAssignmentId(CommonModelConverter.asApiUuid(grpc.getDeviceAssignmentId()));
	api.setCustomerId(CommonModelConverter.asApiUuid(grpc.getCustomerId()));
	api.setAreaId(CommonModelConverter.asApiUuid(grpc.getAreaId()));
	api.setAssetId(CommonModelConverter.asApiUuid(grpc.getAssetId()));
	api.setLastInteractionDate(CommonModelConverter.asApiDate(grpc.getLastInteractionDate()));
	api.setPresenceMissingDate(CommonModelConverter.asApiDate(grpc.getPresenceMissingDate()));
	api.setLastLocationEventId(
		grpc.hasLastLocationEventId() ? CommonModelConverter.asApiUuid(grpc.getLastLocationEventId()) : null);

	Map<String, GUUID> lastMeasurementIds = grpc.getLastMeasurementEventIdsMap();
	for (String key : lastMeasurementIds.keySet()) {
	    api.getLastMeasurementEventIds().put(key, CommonModelConverter.asApiUuid(lastMeasurementIds.get(key)));
	}

	Map<String, GUUID> lastAlertIds = grpc.getLastAlertEventIdsMap();
	for (String key : lastAlertIds.keySet()) {
	    api.getLastAlertEventIds().put(key, CommonModelConverter.asApiUuid(lastAlertIds.get(key)));
	}

	return api;
    }

    /**
     * Convert device state create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceStateCreateRequest asGrpcDeviceStateCreateRequest(IDeviceStateCreateRequest api)
	    throws SiteWhereException {
	GDeviceStateCreateRequest.Builder grpc = GDeviceStateCreateRequest.newBuilder();
	if (api.getDeviceId() != null) {
	    grpc.setDeviceId(CommonModelConverter.asGrpcUuid(api.getDeviceId()));
	}
	if (api.getDeviceTypeId() != null) {
	    grpc.setDeviceTypeId(CommonModelConverter.asGrpcUuid(api.getDeviceTypeId()));
	}
	if (api.getDeviceAssignmentId() != null) {
	    grpc.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(api.getDeviceAssignmentId()));
	}
	if (api.getCustomerId() != null) {
	    grpc.setCustomerId(CommonModelConverter.asGrpcUuid(api.getCustomerId()));
	}
	if (api.getAreaId() != null) {
	    grpc.setAreaId(CommonModelConverter.asGrpcUuid(api.getAreaId()));
	}
	if (api.getAssetId() != null) {
	    grpc.setAssetId(CommonModelConverter.asGrpcUuid(api.getAssetId()));
	}
	if (api.getLastInteractionDate() != null) {
	    grpc.setLastInteractionDate(CommonModelConverter.asGrpcDate(api.getLastInteractionDate()));
	}
	if (api.getPresenceMissingDate() != null) {
	    grpc.setPresenceMissingDate(CommonModelConverter.asGrpcDate(api.getPresenceMissingDate()));
	}
	if (api.getLastLocationEventId() != null) {
	    grpc.setLastLocationEventId(CommonModelConverter.asGrpcUuid(api.getLastLocationEventId()));
	}
	if (api.getLastMeasurementEventIds() != null) {
	    for (String key : api.getLastMeasurementEventIds().keySet()) {
		grpc.putLastMeasurementEventIds(key,
			CommonModelConverter.asGrpcUuid(api.getLastMeasurementEventIds().get(key)));
	    }
	}
	if (api.getLastAlertEventIds() != null) {
	    for (String key : api.getLastAlertEventIds().keySet()) {
		grpc.putLastAlertEventIds(key, CommonModelConverter.asGrpcUuid(api.getLastAlertEventIds().get(key)));
	    }
	}
	return grpc.build();
    }

    /**
     * Convert device state search criteria from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceStateSearchCriteria asApiDeviceStateSearchCriteria(GDeviceStateSearchCriteria grpc)
	    throws SiteWhereException {
	DeviceStateSearchCriteria api = new DeviceStateSearchCriteria();
	api.setLastInteractionDateBefore(CommonModelConverter.asApiDate(grpc.getLastInteractionDateBefore()));
	if (grpc.getDeviceTypeTokenCount() > 0) {
	    api.setDeviceTypeTokens(grpc.getDeviceTypeTokenList());
	}
	if (grpc.getCustomerTokenCount() > 0) {
	    api.setCustomerTokens(grpc.getCustomerTokenList());
	}
	if (grpc.getAreaTokenCount() > 0) {
	    api.setAreaTokens(grpc.getAreaTokenList());
	}
	if (grpc.getAssetTokenCount() > 0) {
	    api.setAssetTokens(grpc.getAssetTokenList());
	}
	api.setPageNumber(grpc.getPaging().getPageNumber());
	api.setPageSize(grpc.getPaging().getPageSize());
	return api;
    }

    /**
     * Convert device state search criteria from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceStateSearchCriteria asGrpcDeviceStateSearchCriteria(IDeviceStateSearchCriteria api)
	    throws SiteWhereException {
	GDeviceStateSearchCriteria.Builder grpc = GDeviceStateSearchCriteria.newBuilder();
	grpc.setLastInteractionDateBefore(CommonModelConverter.asGrpcDate(api.getLastInteractionDateBefore()));
	if (api.getDeviceTypeTokens() != null) {
	    grpc.addAllDeviceTypeToken(api.getDeviceTypeTokens());
	}
	if (api.getCustomerTokens() != null) {
	    grpc.addAllCustomerToken(api.getCustomerTokens());
	}
	if (api.getAreaTokens() != null) {
	    grpc.addAllAreaToken(api.getAreaTokens());
	}
	if (api.getAssetTokens() != null) {
	    grpc.addAllAssetToken(api.getAssetTokens());
	}
	grpc.setPaging(CommonModelConverter.asGrpcPaging(api));
	return grpc.build();
    }

    /**
     * Convert device state search results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IDeviceState> asApiDeviceStateSearchResults(GDeviceStateSearchResults response)
	    throws SiteWhereException {
	List<IDeviceState> results = new ArrayList<IDeviceState>();
	for (GDeviceState grpc : response.getDeviceStatesList()) {
	    results.add(DeviceStateModelConverter.asApiDeviceState(grpc));
	}
	return new SearchResults<IDeviceState>(results, response.getCount());
    }

    /**
     * Convert device state from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceState asApiDeviceState(GDeviceState grpc) throws SiteWhereException {
	DeviceState api = new DeviceState();
	api.setId(CommonModelConverter.asApiUuid(grpc.getId()));
	api.setDeviceId(CommonModelConverter.asApiUuid(grpc.getDeviceId()));
	api.setDeviceTypeId(CommonModelConverter.asApiUuid(grpc.getDeviceTypeId()));
	api.setDeviceAssignmentId(CommonModelConverter.asApiUuid(grpc.getDeviceAssignmentId()));
	api.setCustomerId(CommonModelConverter.asApiUuid(grpc.getCustomerId()));
	api.setAreaId(CommonModelConverter.asApiUuid(grpc.getAreaId()));
	api.setAssetId(CommonModelConverter.asApiUuid(grpc.getAssetId()));
	api.setLastInteractionDate(CommonModelConverter.asApiDate(grpc.getLastInteractionDate()));
	api.setPresenceMissingDate(CommonModelConverter.asApiDate(grpc.getPresenceMissingDate()));
	api.setLastLocationEventId(CommonModelConverter.asApiUuid(grpc.getLastLocationEventId()));

	Map<String, GUUID> lastMeasurementIds = grpc.getLastMeasurementEventIdsMap();
	for (String key : lastMeasurementIds.keySet()) {
	    api.getLastMeasurementEventIds().put(key, CommonModelConverter.asApiUuid(lastMeasurementIds.get(key)));
	}

	Map<String, GUUID> lastAlertIds = grpc.getLastAlertEventIdsMap();
	for (String key : lastAlertIds.keySet()) {
	    api.getLastAlertEventIds().put(key, CommonModelConverter.asApiUuid(lastAlertIds.get(key)));
	}

	return api;
    }

    /**
     * Convert device state from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceState asGrpcDeviceState(IDeviceState api) throws SiteWhereException {
	GDeviceState.Builder grpc = GDeviceState.newBuilder();
	if (api.getId() != null) {
	    grpc.setId(CommonModelConverter.asGrpcUuid(api.getId()));
	}
	if (api.getDeviceId() != null) {
	    grpc.setDeviceId(CommonModelConverter.asGrpcUuid(api.getDeviceId()));
	}
	if (api.getDeviceTypeId() != null) {
	    grpc.setDeviceTypeId(CommonModelConverter.asGrpcUuid(api.getDeviceTypeId()));
	}
	if (api.getDeviceAssignmentId() != null) {
	    grpc.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(api.getDeviceAssignmentId()));
	}
	if (api.getCustomerId() != null) {
	    grpc.setCustomerId(CommonModelConverter.asGrpcUuid(api.getCustomerId()));
	}
	if (api.getAreaId() != null) {
	    grpc.setAreaId(CommonModelConverter.asGrpcUuid(api.getAreaId()));
	}
	if (api.getAssetId() != null) {
	    grpc.setAssetId(CommonModelConverter.asGrpcUuid(api.getAssetId()));
	}
	if (api.getLastInteractionDate() != null) {
	    grpc.setLastInteractionDate(CommonModelConverter.asGrpcDate(api.getLastInteractionDate()));
	}
	if (api.getPresenceMissingDate() != null) {
	    grpc.setPresenceMissingDate(CommonModelConverter.asGrpcDate(api.getPresenceMissingDate()));
	}
	if (api.getLastLocationEventId() != null) {
	    grpc.setLastLocationEventId(CommonModelConverter.asGrpcUuid(api.getLastLocationEventId()));
	}
	if (api.getLastMeasurementEventIds() != null) {
	    for (String key : api.getLastMeasurementEventIds().keySet()) {
		grpc.putLastMeasurementEventIds(key,
			CommonModelConverter.asGrpcUuid(api.getLastMeasurementEventIds().get(key)));
	    }
	}
	if (api.getLastAlertEventIds() != null) {
	    for (String key : api.getLastAlertEventIds().keySet()) {
		grpc.putLastAlertEventIds(key, CommonModelConverter.asGrpcUuid(api.getLastAlertEventIds().get(key)));
	    }
	}
	return grpc.build();
    }
}
