/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import java.util.HashMap;
import java.util.Map;

import com.sitewhere.device.DeviceManagementDecorator;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceSpecification;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.ISite;

/**
 * Device management facade using static sample data. This is used to generate JSON for
 * REST documentation.
 * 
 * @author Derek
 */
public class MockDeviceManagement extends DeviceManagementDecorator {

	/** Sites by token */
	private Map<String, Site> sites = new HashMap<String, Site>();

	/** Specifications by token */
	private Map<String, DeviceSpecification> specifications = new HashMap<String, DeviceSpecification>();

	/** Devices by hardware id */
	private Map<String, Device> devices = new HashMap<String, Device>();

	/** Assignments by token */
	private Map<String, DeviceAssignment> assignments = new HashMap<String, DeviceAssignment>();

	public MockDeviceManagement() {
		super(null);

		sites.put(ExampleData.SITE_CONSTRUCTION.getToken(), ExampleData.SITE_CONSTRUCTION);

		specifications.put(ExampleData.SPEC_MEITRACK.getToken(), ExampleData.SPEC_MEITRACK);
		specifications.put(ExampleData.SPEC_HEART_MONITOR.getToken(), ExampleData.SPEC_HEART_MONITOR);

		devices.put(ExampleData.TRACKER.getHardwareId(), ExampleData.TRACKER);
		devices.put(ExampleData.HEART_MONITOR.getHardwareId(), ExampleData.HEART_MONITOR);

		assignments.put(ExampleData.TRACKER_TO_DEREK.getToken(), ExampleData.TRACKER_TO_DEREK);
		assignments.put(ExampleData.HEART_MONITOR_TO_DEREK.getToken(), ExampleData.HEART_MONITOR_TO_DEREK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.device.DeviceManagementDecorator#getSiteByToken(java.lang.String)
	 */
	@Override
	public ISite getSiteByToken(String token) throws SiteWhereException {
		return sites.get(token);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.device.DeviceManagementDecorator#getDeviceSpecificationByToken(java
	 * .lang.String)
	 */
	@Override
	public IDeviceSpecification getDeviceSpecificationByToken(String token) throws SiteWhereException {
		return specifications.get(token);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.device.DeviceManagementDecorator#getDeviceByHardwareId(java.lang.
	 * String)
	 */
	@Override
	public IDevice getDeviceByHardwareId(String hardwareId) throws SiteWhereException {
		return devices.get(hardwareId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.device.DeviceManagementDecorator#getDeviceAssignmentByToken(java.
	 * lang.String)
	 */
	@Override
	public IDeviceAssignment getDeviceAssignmentByToken(String token) throws SiteWhereException {
		return assignments.get(token);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.device.DeviceManagementDecorator#getDeviceForAssignment(com.sitewhere
	 * .spi.device.IDeviceAssignment)
	 */
	@Override
	public IDevice getDeviceForAssignment(IDeviceAssignment assignment) throws SiteWhereException {
		return getDeviceByHardwareId(assignment.getDeviceHardwareId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.device.DeviceManagementDecorator#getSiteForAssignment(com.sitewhere
	 * .spi.device.IDeviceAssignment)
	 */
	@Override
	public ISite getSiteForAssignment(IDeviceAssignment assignment) throws SiteWhereException {
		return getSiteByToken(assignment.getSiteToken());
	}
}