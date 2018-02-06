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

import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.SiteMapData;
import com.sitewhere.rest.model.device.Zone;
import com.sitewhere.rest.model.device.marshaling.MarshaledSite;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.device.AssignmentSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetResolver;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.IZone;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Configurable helper class that allows {@link Site} model objects to be
 * created from {@link ISite} SPI objects.
 * 
 * @author Derek
 */
public class SiteMarshalHelper {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    /** Device management */
    private IDeviceManagement deviceManagement;

    /** Asset resolver */
    private IAssetResolver assetResolver;

    /** Indicates whether assignments for site should be included */
    private boolean includeAssignements = false;

    /** Indicates whether zones are to be included */
    private boolean includeZones = false;

    /** Device assignment marshal helper */
    private DeviceAssignmentMarshalHelper assignmentHelper;

    public SiteMarshalHelper(IDeviceManagement deviceManagement, IAssetResolver assetResolver) {
	this.deviceManagement = deviceManagement;
	this.assetResolver = assetResolver;

	this.assignmentHelper = new DeviceAssignmentMarshalHelper(deviceManagement);
	assignmentHelper.setIncludeDevice(true);
	assignmentHelper.setIncludeAsset(true);
	assignmentHelper.setIncludeSite(false);
    }

    /**
     * Convert the SPI into a model object based on marshaling parameters.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public MarshaledSite convert(ISite source) throws SiteWhereException {
	MarshaledSite site = new MarshaledSite();
	site.setId(source.getId());
	site.setToken(source.getToken());
	site.setName(source.getName());
	site.setDescription(source.getDescription());
	site.setImageUrl(source.getImageUrl());
	site.setMap(SiteMapData.copy(source.getMap()));
	MetadataProviderEntity.copy(source, site);
	if (isIncludeAssignements()) {
	    AssignmentSearchCriteria criteria = new AssignmentSearchCriteria(1, 0);
	    criteria.setStatus(DeviceAssignmentStatus.Active);
	    ISearchResults<IDeviceAssignment> matches = getDeviceManagement().getDeviceAssignmentsForSite(site.getId(),
		    criteria);
	    List<DeviceAssignment> assignments = new ArrayList<DeviceAssignment>();
	    for (IDeviceAssignment match : matches.getResults()) {
		assignments.add(assignmentHelper.convert(match, getAssetResolver()));
	    }
	    site.setDeviceAssignments(assignments);
	}
	if (isIncludeZones()) {
	    ISearchResults<IZone> matches = getDeviceManagement().listZones(site.getId(), SearchCriteria.ALL);
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
	    site.setZones(zones);
	}
	return site;
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

    public boolean isIncludeAssignements() {
	return includeAssignements;
    }

    public void setIncludeAssignements(boolean includeAssignements) {
	this.includeAssignements = includeAssignements;
    }

    public boolean isIncludeZones() {
	return includeZones;
    }

    public void setIncludeZones(boolean includeZones) {
	this.includeZones = includeZones;
    }
}