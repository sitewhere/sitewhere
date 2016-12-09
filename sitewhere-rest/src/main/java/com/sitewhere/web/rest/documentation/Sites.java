/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.rest.model.common.Location;
import com.sitewhere.rest.model.device.request.SiteCreateRequest;
import com.sitewhere.rest.model.device.request.ZoneCreateRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.common.ILocation;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.IZone;
import com.sitewhere.web.rest.documentation.ExampleData.Site_Construction;

/**
 * Examples of REST payloads for various site methods.
 * 
 * @author Derek
 */
public class Sites {

    public static class CreateSiteRequest {

	public Object generate() throws SiteWhereException {
	    SiteCreateRequest request = new SiteCreateRequest();
	    request.setToken(ExampleData.SITE_CONSTRUCTION.getToken());
	    request.setName(ExampleData.SITE_CONSTRUCTION.getName());
	    request.setDescription(ExampleData.SITE_CONSTRUCTION.getDescription());
	    request.setImageUrl(ExampleData.SITE_CONSTRUCTION.getImageUrl());
	    request.setMap(ExampleData.SITE_CONSTRUCTION.getMap());
	    request.setMetadata(ExampleData.SITE_CONSTRUCTION.getMetadata());
	    return request;
	}
    }

    public static class CreateSiteResponse {

	public Object generate() throws SiteWhereException {
	    return ExampleData.SITE_CONSTRUCTION;
	}
    }

    public static class UpdateSiteRequest {

	public Object generate() throws SiteWhereException {
	    SiteCreateRequest request = new SiteCreateRequest();
	    request.setName(ExampleData.SITE_CONSTRUCTION.getName() + " Updated.");
	    return request;
	}
    }

    public static class UpdateSiteResponse {

	public Object generate() throws SiteWhereException {
	    Site_Construction site = new Site_Construction();
	    site.setName(ExampleData.SITE_CONSTRUCTION.getName() + " Updated.");
	    return site;
	}
    }

    public static class ListSitesResponse {

	public Object generate() throws SiteWhereException {
	    List<ISite> list = new ArrayList<ISite>();
	    list.add(ExampleData.SITE_CONSTRUCTION);
	    list.add(ExampleData.SITE_VEHICLE_TRACKING);
	    return new SearchResults<ISite>(list, 2);
	}
    }

    public static class CreateZoneRequest {

	public Object generate() throws SiteWhereException {
	    ZoneCreateRequest request = new ZoneCreateRequest();
	    request.setName(ExampleData.ZONE_CONSTRUCTION_SITE.getName());
	    request.setBorderColor(ExampleData.ZONE_CONSTRUCTION_SITE.getBorderColor());
	    request.setFillColor(ExampleData.ZONE_CONSTRUCTION_SITE.getFillColor());
	    request.setOpacity(ExampleData.ZONE_CONSTRUCTION_SITE.getOpacity());
	    List<ILocation> coords = ExampleData.ZONE_CONSTRUCTION_SITE.getCoordinates();
	    List<Location> copy = new ArrayList<Location>();
	    for (ILocation location : coords) {
		copy.add(Location.copy(location));
	    }
	    request.setCoordinates(copy);
	    return request;
	}
    }

    public static class CreateZoneResponse {

	public Object generate() throws SiteWhereException {
	    return ExampleData.ZONE_CONSTRUCTION_SITE;
	}
    }

    public static class ListAssignmentsForSiteResponse {

	public Object generate() throws SiteWhereException {
	    List<IDeviceAssignment> list = new ArrayList<IDeviceAssignment>();
	    list.add(ExampleData.TRACKER_TO_DEREK);
	    list.add(ExampleData.TRACKER_TO_MARTIN);
	    return new SearchResults<IDeviceAssignment>(list, 2);
	}
    }

    public static class ListZonesForSiteResponse {

	public Object generate() throws SiteWhereException {
	    List<IZone> list = new ArrayList<IZone>();
	    list.add(ExampleData.ZONE_CONSTRUCTION_SITE);
	    list.add(ExampleData.ZONE_OFF_LIMITS);
	    return new SearchResults<IZone>(list, 2);
	}
    }
}