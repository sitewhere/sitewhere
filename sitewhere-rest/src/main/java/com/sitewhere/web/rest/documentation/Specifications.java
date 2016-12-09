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

import com.sitewhere.rest.model.device.command.CommandParameter;
import com.sitewhere.rest.model.device.command.DeviceCommandNamespace;
import com.sitewhere.rest.model.device.request.DeviceCommandCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceSpecificationCreateRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.command.ICommandParameter;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.command.IDeviceCommandNamespace;
import com.sitewhere.web.rest.documentation.ExampleData.Specification_MeiTrack;

/**
 * Examples of REST payloads for various device specification methods.
 * 
 * @author Derek
 */
public class Specifications {

    public static class CreateSpecificationRequest {

	public Object generate() throws SiteWhereException {
	    DeviceSpecificationCreateRequest request = new DeviceSpecificationCreateRequest();
	    request.setToken(ExampleData.SPEC_MEITRACK.getToken());
	    request.setName(ExampleData.SPEC_MEITRACK.getName());
	    request.setContainerPolicy(ExampleData.SPEC_MEITRACK.getContainerPolicy());
	    request.setAssetModuleId(ExampleData.SPEC_MEITRACK.getAssetModuleId());
	    request.setAssetId(ExampleData.SPEC_MEITRACK.getAssetId());
	    return request;
	}
    }

    public static class CreateSpecificationResponse {

	public Object generate() throws SiteWhereException {
	    return ExampleData.SPEC_MEITRACK;
	}
    }

    public static class UpdateSpecificationRequest {

	public Object generate() throws SiteWhereException {
	    DeviceSpecificationCreateRequest request = new DeviceSpecificationCreateRequest();
	    request.setName(ExampleData.SPEC_MEITRACK.getName() + " Updated.");
	    return request;
	}
    }

    public static class UpdateSpecificationResponse {

	public Object generate() throws SiteWhereException {
	    Specification_MeiTrack spec = new Specification_MeiTrack();
	    spec.setName(ExampleData.SPEC_MEITRACK.getName() + " Updated.");
	    return spec;
	}
    }

    public static class ListSpecificationsResponse {

	public Object generate() throws SiteWhereException {
	    List<IDeviceSpecification> list = new ArrayList<IDeviceSpecification>();
	    list.add(ExampleData.SPEC_MEITRACK);
	    list.add(ExampleData.SPEC_HEART_MONITOR);
	    return new SearchResults<IDeviceSpecification>(list, 2);
	}
    }

    public static class CreateDeviceCommandRequest {

	public Object generate() throws SiteWhereException {
	    DeviceCommandCreateRequest request = new DeviceCommandCreateRequest();
	    request.setToken(ExampleData.COMMAND_SET_RPT_INTV.getToken());
	    request.setName(ExampleData.COMMAND_SET_RPT_INTV.getName());
	    request.setDescription(ExampleData.COMMAND_SET_RPT_INTV.getDescription());
	    request.setNamespace(ExampleData.COMMAND_SET_RPT_INTV.getNamespace());
	    List<CommandParameter> params = new ArrayList<CommandParameter>();
	    for (ICommandParameter iparam : ExampleData.COMMAND_SET_RPT_INTV.getParameters()) {
		params.add(new CommandParameter(iparam.getName(), iparam.getType(), iparam.isRequired()));
	    }
	    request.setParameters(params);
	    return request;
	}
    }

    public static class CreateDeviceCommandResponse {

	public Object generate() throws SiteWhereException {
	    return ExampleData.COMMAND_SET_RPT_INTV;
	}
    }

    public static class ListDeviceCommandsResponse {

	public Object generate() throws SiteWhereException {
	    List<IDeviceCommand> list = new ArrayList<IDeviceCommand>();
	    list.add(ExampleData.COMMAND_GET_FW_VER);
	    list.add(ExampleData.COMMAND_SET_RPT_INTV);
	    return new SearchResults<IDeviceCommand>(list, 2);
	}
    }

    public static class ListDeviceCommandsByNamespaceResponse {

	public Object generate() throws SiteWhereException {
	    List<IDeviceCommandNamespace> nss = new ArrayList<IDeviceCommandNamespace>();
	    DeviceCommandNamespace ns = new DeviceCommandNamespace();
	    ns.setValue(ExampleData.COMMAND_GET_FW_VER.getNamespace());
	    List<IDeviceCommand> list = new ArrayList<IDeviceCommand>();
	    list.add(ExampleData.COMMAND_GET_FW_VER);
	    list.add(ExampleData.COMMAND_SET_RPT_INTV);
	    ns.setCommands(list);
	    nss.add(ns);
	    return new SearchResults<IDeviceCommandNamespace>(nss, 1);
	}
    }
}