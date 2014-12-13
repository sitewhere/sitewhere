/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.geospatial;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.device.event.processor.OutboundEventProcessor;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IZone;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.processor.IOutboundEventProcessor;
import com.sitewhere.spi.geospatial.ZoneContainment;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Implementation of {@link IOutboundEventProcessor} that performs a series of tests for
 * whether a location is inside or outside of zones, firing alerts if the criteria is met.
 * 
 * @author Derek
 */
public class ZoneTestEventProcessor extends OutboundEventProcessor {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(ZoneTestEventProcessor.class);

	/** Map of polygons by zone token */
	private Map<String, Polygon> zoneMap = new HashMap<String, Polygon>();

	/** List of tests to perform */
	private List<ZoneTest> zoneTests = new ArrayList<ZoneTest>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		LOGGER.info("Starting zone test processor with " + zoneTests.size() + " tests.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.rest.model.device.event.processor.OutboundEventProcessor#onLocation
	 * (com.sitewhere.spi.device.event.IDeviceLocation)
	 */
	@Override
	public void onLocation(IDeviceLocation location) throws SiteWhereException {
		for (ZoneTest test : zoneTests) {
			Polygon poly = getZonePolygon(test.getZoneToken());
			ZoneContainment containment =
					(poly.contains(GeoUtils.createPointForLocation(location))) ? ZoneContainment.Inside
							: ZoneContainment.Outside;
			if (test.getCondition() == containment) {
				DeviceAlertCreateRequest alert = new DeviceAlertCreateRequest();
				alert.setType(test.getAlertType());
				alert.setLevel(test.getAlertLevel());
				alert.setMessage(test.getAlertMessage());
				alert.setUpdateState(false);
				alert.setEventDate(new Date());
				SiteWhere.getServer().getDeviceManagement().addDeviceAlert(
						location.getDeviceAssignmentToken(), alert);
			}
		}
	}

	/**
	 * Get cached zone polygon or try to load from datastore.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected Polygon getZonePolygon(String token) throws SiteWhereException {
		Polygon poly = zoneMap.get(token);
		if (poly != null) {
			return poly;
		}
		IZone zone = SiteWhere.getServer().getDeviceManagement().getZone(token);
		if (zone != null) {
			poly = GeoUtils.createPolygonForZone(zone);
			zoneMap.put(token, poly);
			return poly;
		}
		throw new SiteWhereException("Invalid zone token in " + ZoneTestEventProcessor.class.getName() + ": "
				+ token);
	}

	public List<ZoneTest> getZoneTests() {
		return zoneTests;
	}

	public void setZoneTests(List<ZoneTest> zoneTests) {
		this.zoneTests = zoneTests;
	}
}