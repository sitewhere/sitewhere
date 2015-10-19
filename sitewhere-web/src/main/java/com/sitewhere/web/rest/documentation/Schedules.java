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

import com.sitewhere.rest.model.scheduling.request.ScheduleCreateRequest;
import com.sitewhere.rest.model.scheduling.request.ScheduledJobCreateRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.web.rest.documentation.ExampleData.Schedule_Simple1;
import com.sitewhere.web.rest.documentation.ExampleData.ScheduledJob_Command1;

/**
 * Examples of REST payloads for various schedule methods.
 * 
 * @author Derek
 */
public class Schedules {

	public static class CreateScheduleRequest {

		public Object generate() throws SiteWhereException {
			ScheduleCreateRequest request = new ScheduleCreateRequest();
			request.setToken(ExampleData.SCHEDULE_SIMPLE1.getToken());
			request.setName(ExampleData.SCHEDULE_SIMPLE1.getName());
			request.setTriggerType(ExampleData.SCHEDULE_SIMPLE1.getTriggerType());
			request.setTriggerConfiguration(ExampleData.SCHEDULE_SIMPLE1.getTriggerConfiguration());
			request.setStartDate(ExampleData.SCHEDULE_SIMPLE1.getStartDate());
			request.setEndDate(ExampleData.SCHEDULE_SIMPLE1.getEndDate());
			return request;
		}
	}

	public static class CreateScheduleResponse {

		public Object generate() throws SiteWhereException {
			return ExampleData.SCHEDULE_SIMPLE1;
		}
	}

	public static class UpdateScheduleRequest {

		public Object generate() throws SiteWhereException {
			ScheduleCreateRequest request = new ScheduleCreateRequest();
			request.setName(ExampleData.SCHEDULE_SIMPLE1.getName() + " Updated");
			return request;
		}
	}

	public static class UpdateScheduleResponse {

		public Object generate() throws SiteWhereException {
			Schedule_Simple1 schedule = new Schedule_Simple1();
			schedule.setName(ExampleData.SCHEDULE_SIMPLE1.getName() + " Updated");
			return schedule;
		}
	}

	public static class ListSchedulesResponse {

		public Object generate() throws SiteWhereException {
			List<ISchedule> list = new ArrayList<ISchedule>();
			list.add(ExampleData.SCHEDULE_SIMPLE1);
			list.add(ExampleData.SCHEDULE_CRON1);
			return new SearchResults<ISchedule>(list, 2);
		}
	}

	public static class CreateScheduledJobRequest {

		public Object generate() throws SiteWhereException {
			ScheduledJobCreateRequest request = new ScheduledJobCreateRequest();
			request.setToken(ExampleData.JOB_COMMAND1.getToken());
			request.setJobType(ExampleData.JOB_COMMAND1.getJobType());
			request.setJobConfiguration(ExampleData.JOB_COMMAND1.getJobConfiguration());
			request.setScheduleToken(ExampleData.JOB_COMMAND1.getScheduleToken());
			return request;
		}
	}

	public static class CreateScheduledJobResponse {

		public Object generate() throws SiteWhereException {
			return ExampleData.JOB_COMMAND1;
		}
	}

	public static class UpdateScheduledJobRequest {

		public Object generate() throws SiteWhereException {
			ScheduledJobCreateRequest request = new ScheduledJobCreateRequest();
			request.setScheduleToken(ExampleData.SCHEDULE_SIMPLE1.getToken());
			return request;
		}
	}

	public static class UpdateScheduledJobResponse {

		public Object generate() throws SiteWhereException {
			ScheduledJob_Command1 job = new ScheduledJob_Command1();
			job.setScheduleToken(ExampleData.SCHEDULE_SIMPLE1.getToken());
			return job;
		}
	}

	public static class ListScheduledjobsResponse {

		public Object generate() throws SiteWhereException {
			List<IScheduledJob> list = new ArrayList<IScheduledJob>();
			list.add(ExampleData.JOB_COMMAND1);
			list.add(ExampleData.JOB_COMMAND2);
			return new SearchResults<IScheduledJob>(list, 2);
		}
	}
}