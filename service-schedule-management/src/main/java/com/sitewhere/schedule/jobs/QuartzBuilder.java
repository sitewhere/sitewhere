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

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.scheduling.TriggerConstants;

/**
 * Builds Quartz jobs based on an {@link IScheduledJob}.
 */
public class QuartzBuilder {

    /**
     * Build Quartz {@link JobDetail} information for an {@link IScheduledJob}.
     * 
     * @param job
     * @return
     * @throws SiteWhereException
     */
    public static JobDetail buildJobDetail(IScheduledJob job) throws SiteWhereException {
	switch (job.getJobType()) {
	case CommandInvocation: {
	    JobBuilder builder = JobBuilder.newJob(CommandInvocationJob.class).withIdentity(job.getToken());
	    for (String key : job.getJobConfiguration().keySet()) {
		builder.usingJobData(key, job.getJobConfiguration().get(key));
	    }
	    return builder.build();
	}
	case BatchCommandInvocation: {
	    JobBuilder builder = JobBuilder.newJob(InvocationByDeviceCriteriaJob.class).withIdentity(job.getToken());
	    for (String key : job.getJobConfiguration().keySet()) {
		builder.usingJobData(key, job.getJobConfiguration().get(key));
	    }
	    return builder.build();
	}
	default: {
	    throw new SiteWhereException("Unhandled job type: " + job.getJobType());
	}
	}
    }

    /**
     * Build Quartz {@link Trigger} information.
     * 
     * @param job
     * @param schedule
     * @return
     * @throws SiteWhereException
     */
    public static Trigger buildTrigger(IScheduledJob job, ISchedule schedule) throws SiteWhereException {
	switch (schedule.getTriggerType()) {
	case SimpleTrigger: {
	    return buildSimpleTrigger(job, schedule);
	}
	case CronTrigger: {
	    return buildCronTrigger(job, schedule);
	}
	default: {
	    throw new SiteWhereException("Unhandled trigger type: " + schedule.getTriggerType());
	}
	}
    }

    /**
     * Build a Quartz {@link SimpleTrigger} from a SiteWhere {@link ISchedule}.
     * 
     * @param job
     * @param schedule
     * @return
     * @throws SiteWhereException
     */
    protected static Trigger buildSimpleTrigger(IScheduledJob job, ISchedule schedule) throws SiteWhereException {
	SimpleScheduleBuilder simple = SimpleScheduleBuilder.simpleSchedule();
	String repeat = schedule.getTriggerConfiguration().get(TriggerConstants.SimpleTrigger.REPEAT_COUNT);
	if (repeat != null) {
	    try {
		int count = Integer.parseInt(repeat);
		if (count > 0) {
		    simple.withRepeatCount(count);
		} else {
		    simple.repeatForever();
		}
	    } catch (NumberFormatException e) {
		throw new SiteWhereException("Non-numeric value used for repeat count.", e);
	    }
	}
	String interval = schedule.getTriggerConfiguration().get(TriggerConstants.SimpleTrigger.REPEAT_INTERVAL);
	if (interval != null) {
	    try {
		simple.withIntervalInMilliseconds(Long.parseLong(interval));
	    } catch (NumberFormatException e) {
		throw new SiteWhereException("Non-numeric value used for repeat interval.", e);
	    }
	}
	TriggerBuilder<?> builder = TriggerBuilder.newTrigger().withIdentity(job.getToken()).withSchedule(simple);
	addCommonFields(job, schedule, builder);
	return builder.build();
    }

    /**
     * Build Quartz {@link CronTrigger} based on SiteWhere {@link ISchedule}.
     * 
     * @param job
     * @param schedule
     * @return
     * @throws SiteWhereException
     */
    protected static Trigger buildCronTrigger(IScheduledJob job, ISchedule schedule) throws SiteWhereException {
	String expression = schedule.getTriggerConfiguration().get(TriggerConstants.CronTrigger.CRON_EXPRESSION);
	if (expression == null) {
	    throw new SiteWhereException("Cron trigger did not specify expression.");
	}
	if (!CronExpression.isValidExpression(expression)) {
	    throw new SiteWhereException("Cron expression is invalid.");
	}
	CronScheduleBuilder cron = CronScheduleBuilder.cronSchedule(expression);
	TriggerBuilder<?> builder = TriggerBuilder.newTrigger().withIdentity(job.getToken()).withSchedule(cron);
	addCommonFields(job, schedule, builder);
	return builder.build();
    }

    /**
     * Add fields common to all schedules.
     * 
     * @param job
     * @param schedule
     * @param builder
     * @throws SiteWhereException
     */
    protected static void addCommonFields(IScheduledJob job, ISchedule schedule, TriggerBuilder<?> builder)
	    throws SiteWhereException {
	if (schedule.getStartDate() != null) {
	    builder.startAt(schedule.getStartDate());
	}
	if (schedule.getEndDate() != null) {
	    builder.endAt(schedule.getEndDate());
	}
    }
}