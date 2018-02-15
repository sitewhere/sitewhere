/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.marshaling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.rest.model.area.Area;
import com.sitewhere.rest.model.area.AreaMapData;
import com.sitewhere.rest.model.area.Zone;
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.marshaling.MarshaledArea;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.device.AssignmentSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.area.IZone;
import com.sitewhere.spi.asset.IAssetResolver;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Configurable helper class that allows {@link Area} model objects to be
 * created from {@link IArea} SPI objects.
 * 
 * @author Derek
 */
public class AreaMarshalHelper {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    /** Device management */
    private IDeviceManagement deviceManagement;

    /** Asset resolver */
    private IAssetResolver assetResolver;

    /** Include area type information */
    private boolean includeAreaType = false;

    /** Indicates whether assignments for site should be included */
    private boolean includeAssignments = false;

    /** Indicates whether zones are to be included */
    private boolean includeZones = false;

    /** Device assignment marshal helper */
    private DeviceAssignmentMarshalHelper assignmentHelper;

    public AreaMarshalHelper(IDeviceManagement deviceManagement, IAssetResolver assetResolver) {
	this.deviceManagement = deviceManagement;
	this.assetResolver = assetResolver;

	this.assignmentHelper = new DeviceAssignmentMarshalHelper(deviceManagement);
	assignmentHelper.setIncludeDevice(true);
	assignmentHelper.setIncludeAsset(true);
	assignmentHelper.setIncludeArea(false);
    }

    /**
     * Convert the SPI into a model object based on marshaling parameters.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public MarshaledArea convert(IArea source) throws SiteWhereException {
	MarshaledArea area = new MarshaledArea();
	area.setId(source.getId());
	area.setToken(source.getToken());
	area.setAreaTypeId(source.getAreaTypeId());
	area.setParentAreaId(source.getParentAreaId());
	area.setName(source.getName());
	area.setDescription(source.getDescription());
	area.setImageUrl(source.getImageUrl());
	area.setMap(AreaMapData.copy(source.getMap()));
	MetadataProviderEntity.copy(source, area);
	if (isIncludeAreaType()) {
	    area.setAreaType(getDeviceManagement().getAreaType(source.getAreaTypeId()));
	}
	if (isIncludeAssignments()) {
	    AssignmentSearchCriteria criteria = new AssignmentSearchCriteria(1, 0);
	    criteria.setStatus(DeviceAssignmentStatus.Active);
	    ISearchResults<IDeviceAssignment> matches = getDeviceManagement().getDeviceAssignmentsForArea(area.getId(),
		    criteria);
	    List<DeviceAssignment> assignments = new ArrayList<DeviceAssignment>();
	    for (IDeviceAssignment match : matches.getResults()) {
		assignments.add(assignmentHelper.convert(match, getAssetResolver()));
	    }
	    area.setDeviceAssignments(assignments);
	}
	if (isIncludeZones()) {
	    ISearchResults<IZone> matches = getDeviceManagement().listZones(area.getId(), SearchCriteria.ALL);
	    List<Zone> zones = new ArrayList<Zone>();
	    List<IZone> reordered = matches.getResults();
	    Collections.sort(reordered, new Comparator<IZone>() {

		@Override
		public int compare(IZone z0, IZone z1) {
		    return z0.getName().compareTo(z1.getName());
		}
	    });
	    for (IZone match : matches.getResults()) {
		zones.add(Zone.copy(match));
	    }
	    area.setZones(zones);
	}
	return area;
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    public IAssetResolver getAssetResolver() {
	return assetResolver;
    }

    public void setAssetResolver(IAssetResolver assetResolver) {
	this.assetResolver = assetResolver;
    }

    public boolean isIncludeAreaType() {
	return includeAreaType;
    }

    public void setIncludeAreaType(boolean includeAreaType) {
	this.includeAreaType = includeAreaType;
    }

    public boolean isIncludeAssignments() {
	return includeAssignments;
    }

    public void setIncludeAssignments(boolean includeAssignments) {
	this.includeAssignments = includeAssignments;
    }

    public boolean isIncludeZones() {
	return includeZones;
    }

    public void setIncludeZones(boolean includeZones) {
	this.includeZones = includeZones;
    }
}