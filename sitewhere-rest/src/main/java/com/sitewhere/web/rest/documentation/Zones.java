/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import com.sitewhere.rest.model.device.request.ZoneCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.web.rest.documentation.ExampleData.Zone_ConstructionSite;

/**
 * Examples of REST payloads for various zone methods.
 * 
 * @author Derek
 */
public class Zones {

    public static class UpdateZoneRequest {

	public Object generate() throws SiteWhereException {
	    ZoneCreateRequest request = new ZoneCreateRequest();
	    request.setName(ExampleData.ZONE_CONSTRUCTION_SITE.getName() + " Updated");
	    request.setCoordinates(null);
	    return request;
	}
    }

    public static class UpdateZoneResponse {

	public Object generate() throws SiteWhereException {
	    Zone_ConstructionSite zone = new Zone_ConstructionSite();
	    zone.setName(ExampleData.ZONE_CONSTRUCTION_SITE.getName() + " Updated");
	    return zone;
	}
    }
}