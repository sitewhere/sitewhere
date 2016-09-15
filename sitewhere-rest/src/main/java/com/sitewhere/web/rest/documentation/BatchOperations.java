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
import java.util.Date;
import java.util.List;

import com.sitewhere.rest.model.device.request.BatchCommandForCriteriaRequest;
import com.sitewhere.rest.model.device.request.BatchCommandInvocationRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.batch.IBatchElement;
import com.sitewhere.spi.device.batch.IBatchOperation;

/**
 * Example of REST requests for interacting with batch operations.
 * 
 * @author Derek
 */
public class BatchOperations {

    public static class GetBatchOperationResponse {

	public Object generate() throws SiteWhereException {
	    return ExampleData.BATCH_OPERATION1;
	}
    }

    public static class ListBatchOperationsResponse {

	public Object generate() throws SiteWhereException {
	    List<IBatchOperation> ops = new ArrayList<IBatchOperation>();
	    ops.add(ExampleData.BATCH_OPERATION1);
	    ops.add(ExampleData.BATCH_OPERATION2);
	    return new SearchResults<IBatchOperation>(ops, 2);
	}
    }

    public static class ListBatchOperationElementsResponse {

	public Object generate() throws SiteWhereException {
	    List<IBatchElement> list = new ArrayList<IBatchElement>();
	    list.add(ExampleData.BATCH_ELEMENT1);
	    list.add(ExampleData.BATCH_ELEMENT2);
	    return new SearchResults<IBatchElement>(list, 2);
	}
    }

    public static class BatchCommandInvocationCreateRequest {

	public Object generate() throws SiteWhereException {
	    BatchCommandInvocationRequest request = new BatchCommandInvocationRequest();
	    request.setToken("438e068c-0dcb-4d96-a35f-06a52b084373");
	    request.setCommandToken(ExampleData.COMMAND_SET_RPT_INTV.getToken());
	    request.setHardwareIds(Arrays.asList(
		    new String[] { ExampleData.TRACKER.getHardwareId(), ExampleData.HEART_MONITOR.getHardwareId() }));
	    request.getParameterValues().put("interval", "60");
	    request.getParameterValues().put("reboot", "true");
	    return request;
	}
    }

    public static class BatchCommandInvocationByCriteriaSpecRequest {

	public Object generate() throws SiteWhereException {
	    BatchCommandForCriteriaRequest request = new BatchCommandForCriteriaRequest();
	    request.setToken("438e068c-0dcb-4d96-a35f-06a52b084373");
	    request.setCommandToken(ExampleData.COMMAND_SET_RPT_INTV.getToken());
	    request.getParameterValues().put("interval", "60");
	    request.getParameterValues().put("reboot", "true");

	    request.setSpecificationToken(ExampleData.SPEC_HEART_MONITOR.getToken());
	    request.setStartDate(new Date());
	    request.setEndDate(new Date(System.currentTimeMillis() + 60 * 60 * 1000));
	    return request;
	}
    }

    public static class BatchCommandInvocationByCriteriaGroupRequest {

	public Object generate() throws SiteWhereException {
	    BatchCommandForCriteriaRequest request = new BatchCommandForCriteriaRequest();
	    request.setToken("438e068c-0dcb-4d96-a35f-06a52b084373");
	    request.setCommandToken(ExampleData.COMMAND_SET_RPT_INTV.getToken());
	    request.getParameterValues().put("interval", "60");
	    request.getParameterValues().put("reboot", "true");

	    request.setGroupToken(ExampleData.DEVICEGROUP_SOUTHEAST.getToken());
	    return request;
	}
    }

    public static class BatchCommandInvocationByCriteriaGroupRoleRequest {

	public Object generate() throws SiteWhereException {
	    BatchCommandForCriteriaRequest request = new BatchCommandForCriteriaRequest();
	    request.setToken("438e068c-0dcb-4d96-a35f-06a52b084373");
	    request.setCommandToken(ExampleData.COMMAND_SET_RPT_INTV.getToken());
	    request.getParameterValues().put("interval", "60");
	    request.getParameterValues().put("reboot", "true");

	    request.setGroupsWithRole("americas");
	    return request;
	}
    }
}