/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sitewhere.rest.model.device.request.DeviceGroupCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupElementCreateRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.group.GroupElementType;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.web.rest.documentation.ExampleData.DeviceGroup_SouthEast;

/**
 * Example of REST requests for interacting with device groups.
 * 
 * @author Derek
 */
public class DeviceGroups {

	public static class CreateDeviceGroupRequest {

		public Object generate() throws SiteWhereException {
			DeviceGroupCreateRequest request = new DeviceGroupCreateRequest();
			request.setToken(ExampleData.DEVICEGROUP_SOUTHEAST.getToken());
			request.setName(ExampleData.DEVICEGROUP_SOUTHEAST.getName());
			request.setDescription(ExampleData.DEVICEGROUP_SOUTHEAST.getDescription());
			request.setRoles(ExampleData.DEVICEGROUP_SOUTHEAST.getRoles());
			return request;
		}
	}

	public static class CreateDeviceGroupResponse {

		public Object generate() throws SiteWhereException {
			return ExampleData.DEVICEGROUP_SOUTHEAST;
		}
	}

	public static class UpdateDeviceGroupRequest {

		public Object generate() throws SiteWhereException {
			DeviceGroupCreateRequest request = new DeviceGroupCreateRequest();
			request.setName(ExampleData.DEVICEGROUP_SOUTHEAST.getName() + " Updated");
			request.setDescription(ExampleData.DEVICEGROUP_SOUTHEAST.getDescription() + " Updated.");
			return request;
		}
	}

	public static class UpdateDeviceGroupResponse {

		public Object generate() throws SiteWhereException {
			DeviceGroup_SouthEast southeast = new DeviceGroup_SouthEast();
			southeast.setName(ExampleData.DEVICEGROUP_SOUTHEAST.getName() + " Updated");
			southeast.setDescription(ExampleData.DEVICEGROUP_SOUTHEAST.getDescription() + " Updated.");
			return southeast;
		}
	}

	public static class ListDeviceGroupResponse {

		public Object generate() throws SiteWhereException {
			List<IDeviceGroup> list = new ArrayList<IDeviceGroup>();
			list.add(ExampleData.DEVICEGROUP_SOUTHEAST);
			list.add(ExampleData.DEVICEGROUP_NORTHEAST);
			return new SearchResults<IDeviceGroup>(list, 2);
		}
	}

	public static class ListDeviceGroupElementsResponse {

		public Object generate() throws SiteWhereException {
			List<IDeviceGroupElement> list = new ArrayList<IDeviceGroupElement>();
			list.add(ExampleData.GROUPELEMENT_SOUTHEAST);
			list.add(ExampleData.GROUPELEMENT_NORTHEAST);
			list.add(ExampleData.GROUPELEMENT_TRACKER);
			return new SearchResults<IDeviceGroupElement>(list, 3);
		}
	}

	public static class AddGroupElementsRequest {

		public Object generate() throws SiteWhereException {
			List<DeviceGroupElementCreateRequest> list = new ArrayList<DeviceGroupElementCreateRequest>();

			DeviceGroupElementCreateRequest r1 = new DeviceGroupElementCreateRequest();
			r1.setType(GroupElementType.Group);
			r1.setElementId(ExampleData.DEVICEGROUP_SOUTHEAST.getToken());
			r1.setRoles(Arrays.asList(new String[] { "region" }));
			list.add(r1);

			DeviceGroupElementCreateRequest r2 = new DeviceGroupElementCreateRequest();
			r2.setType(GroupElementType.Group);
			r2.setElementId(ExampleData.DEVICEGROUP_NORTHEAST.getToken());
			r2.setRoles(Arrays.asList(new String[] { "region" }));
			list.add(r2);

			DeviceGroupElementCreateRequest r3 = new DeviceGroupElementCreateRequest();
			r3.setType(GroupElementType.Device);
			r3.setElementId(ExampleData.TRACKER.getHardwareId());
			r3.setRoles(Arrays.asList(new String[] { "standalone" }));
			list.add(r3);

			return list;
		}
	}

	public static class DeleteGroupElementsRequest {

		public Object generate() throws SiteWhereException {
			List<DeviceGroupElementCreateRequest> list = new ArrayList<DeviceGroupElementCreateRequest>();

			DeviceGroupElementCreateRequest r1 = new DeviceGroupElementCreateRequest();
			r1.setType(GroupElementType.Group);
			r1.setElementId(ExampleData.DEVICEGROUP_NORTHEAST.getToken());
			list.add(r1);

			return list;
		}
	}

	public static class DeleteGroupElementsResponse {

		public Object generate() throws SiteWhereException {
			List<IDeviceGroupElement> list = new ArrayList<IDeviceGroupElement>();
			list.add(ExampleData.GROUPELEMENT_SOUTHEAST);
			list.add(ExampleData.GROUPELEMENT_TRACKER);
			return new SearchResults<IDeviceGroupElement>(list, 2);
		}
	}
}