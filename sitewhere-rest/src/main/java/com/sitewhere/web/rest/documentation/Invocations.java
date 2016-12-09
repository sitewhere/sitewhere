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

import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.web.rest.view.DeviceInvocationSummaryBuilder;

/**
 * Example of REST requests for interacting with device command invocations.
 * 
 * @author Derek
 */
public class Invocations {

    public static class GetDeviceCommandInvocationSummary {

	public Object generate() throws SiteWhereException {
	    MockDeviceCommandInvocationMarshalHelper helper = new MockDeviceCommandInvocationMarshalHelper();
	    helper.setIncludeCommand(true);
	    DeviceCommandInvocation converted = helper.convert(ExampleData.INVOCATION_GET_FW_VER);
	    List<IDeviceCommandResponse> list = Arrays
		    .asList(new IDeviceCommandResponse[] { ExampleData.RESPONSE_GET_FW_VER });
	    ISearchResults<IDeviceCommandResponse> responses = new SearchResults<IDeviceCommandResponse>(list, 1);
	    return DeviceInvocationSummaryBuilder.build(converted, responses.getResults(), null);
	}
    }

    public static class GetDeviceCommandInvocationResponsesResponse {

	public Object generate() throws SiteWhereException {
	    List<IDeviceCommandResponse> list = new ArrayList<IDeviceCommandResponse>();
	    list.add(ExampleData.RESPONSE_GET_FW_VER);
	    return new SearchResults<IDeviceCommandResponse>(list, 1);
	}
    }
}