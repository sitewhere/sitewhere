/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.schedule.jobs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.batch.BatchUtils;
import com.sitewhere.microservice.api.batch.IBatchManagement;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.schedule.BatchCommandInvocationJobParser;
import com.sitewhere.rest.model.batch.request.BatchCommandInvocationRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.batch.request.IInvocationByDeviceCriteriaRequest;

/**
 * Creates a batch command invocation (based on devices matching criteria) as
 * the result of a Quartz schedule.
 */
public class InvocationByDeviceCriteriaJob implements Job {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(InvocationByDeviceCriteriaJob.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
	Map<String, String> data = new HashMap<String, String>();
	JobDataMap jobData = context.getJobDetail().getJobDataMap();
	for (String key : jobData.keySet()) {
	    String value = jobData.getString(key);
	    data.put(key, value);
	}

	IInvocationByDeviceCriteriaRequest criteria = BatchCommandInvocationJobParser
		.parseInvocationByDeviceCriteria(data);
	if (criteria.getDeviceTypeToken() == null) {
	    throw new JobExecutionException("Device type token not provided.");
	}
	if (criteria.getCommandToken() == null) {
	    throw new JobExecutionException("Command token not provided.");
	}
	try {
	    // Resolve tokens for devices matching criteria.
	    List<String> deviceTokens = BatchUtils.resolveDeviceTokensForDeviceCriteria(criteria, getDeviceManagement(),
		    getAssetManagement());

	    // Create batch command invocation.
	    BatchCommandInvocationRequest invoke = new BatchCommandInvocationRequest();
	    invoke.setCommandToken(criteria.getCommandToken());
	    invoke.setParameterValues(criteria.getParameterValues());
	    invoke.setDeviceTokens(deviceTokens);

	    getBatchManagement().createBatchCommandInvocation(invoke);

	    LOGGER.info("Executed batch command invocation job.");
	} catch (SiteWhereException e) {
	    throw new JobExecutionException("Unable to create batch command invocation.", e);
	}
    }

    private IDeviceManagement getDeviceManagement() {
	return null;
    }

    private IAssetManagement getAssetManagement() {
	return null;
    }

    private IBatchManagement getBatchManagement() {
	return null;
    }
}