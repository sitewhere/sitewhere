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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.rest.model.area.Area;
import com.sitewhere.rest.model.area.Zone;
import com.sitewhere.rest.model.common.BrandedEntity;
import com.sitewhere.rest.model.common.Location;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.marshaling.MarshaledArea;
import com.sitewhere.rest.model.search.device.DeviceAssignmentSearchCriteria;
import com.sitewhere.rest.model.search.device.ZoneSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.area.IAreaType;
import com.sitewhere.spi.area.IZone;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Configurable helper class that allows {@link Area} model objects to be
 * created from {@link IArea} SPI objects.
 */
public class AreaMarshalHelper {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LoggerFactory.getLogger(AreaMarshalHelper.class);

    /** Device management */
    private IDeviceManagement deviceManagement;

    /** Asset management */
    private IAssetManagement assetManagement;

    /** Include area type information */
    private boolean includeAreaType = false;

    /** Include parent area information */
    private boolean includeParentArea = false;

    /** Indicates whether assignments for area should be included */
    private boolean includeAssignments = false;

    /** Indicates whether zones are to be included */
    private boolean includeZones = false;

    /** Device assignment marshal helper */
    private DeviceAssignmentMarshalHelper assignmentHelper;

    public AreaMarshalHelper(IDeviceManagement deviceManagement, IAssetManagement assetManagement) {
	this.deviceManagement = deviceManagement;
	this.assetManagement = assetManagement;

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
	if (source == null) {
	    return null;
	}
	MarshaledArea area = new MarshaledArea();
	area.setAreaTypeId(source.getAreaTypeId());
	area.setParentId(source.getParentId());
	area.setName(source.getName());
	area.setDescription(source.getDescription());
	area.setBounds(Location.copy(source.getBounds()));
	BrandedEntity.copy(source, area);
	if (isIncludeAreaType()) {
	    IAreaType type = getDeviceManagement().getAreaType(source.getAreaTypeId());
	    area.setAreaType(new AreaTypeMarshalHelper(deviceManagement).convert(type));
	}
	if (isIncludeParentArea()) {
	    if (source.getParentId() != null) {
		IArea parent = getDeviceManagement().getArea(source.getParentId());
		area.setParentArea(new AreaMarshalHelper(deviceManagement, assetManagement).convert(parent));
	    }
	}
	if (isIncludeAssignments()) {
	    DeviceAssignmentSearchCriteria criteria = new DeviceAssignmentSearchCriteria(1, 0);
	    criteria.setAssignmentStatuses(Collections.singletonList(DeviceAssignmentStatus.Active));
	    criteria.setAreaTokens(Collections.singletonList(area.getToken()));
	    ISearchResults<IDeviceAssignment> matches = getDeviceManagement().listDeviceAssignments(criteria);
	    List<DeviceAssignment> assignments = new ArrayList<DeviceAssignment>();
	    for (IDeviceAssignment match : matches.getResults()) {
		assignments.add(assignmentHelper.convert(match, getAssetManagement()));
	    }
	    area.setDeviceAssignments(assignments);
	}
	if (isIncludeZones()) {
	    ZoneSearchCriteria criteria = new ZoneSearchCriteria(1, 0);
	    criteria.setAreaToken(area.getToken());
	    ISearchResults<IZone> matches = getDeviceManagement().listZones(criteria);
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

    public IAssetManagement getAssetManagement() {
	return assetManagement;
    }

    public void setAssetManagement(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
    }

    public boolean isIncludeAreaType() {
	return includeAreaType;
    }

    public void setIncludeAreaType(boolean includeAreaType) {
	this.includeAreaType = includeAreaType;
    }

    public boolean isIncludeParentArea() {
	return includeParentArea;
    }

    public void setIncludeParentArea(boolean includeParentArea) {
	this.includeParentArea = includeParentArea;
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