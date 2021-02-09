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
package com.sitewhere.schedule.spi;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduledJob;

/**
 * Manages a list of schedules that are applied to system actions.
 */
public interface IScheduleManager extends ITenantEngineLifecycleComponent {

    /**
     * Called when a new schedule has been added.
     * 
     * @param schedule
     * @throws SiteWhereException
     */
    public void scheduleAdded(ISchedule schedule) throws SiteWhereException;

    /**
     * Called when a schedule is removed.
     * 
     * @param schedule
     * @throws SiteWhereException
     */
    public void scheduleRemoved(ISchedule schedule) throws SiteWhereException;

    /**
     * Adds a job to the scheduler.
     * 
     * @param job
     * @throws SiteWhereException
     */
    public void scheduleJob(IScheduledJob job) throws SiteWhereException;

    /**
     * Unschedules the given job if scheduled.
     * 
     * @param job
     * @throws SiteWhereException
     */
    public void unscheduleJob(IScheduledJob job) throws SiteWhereException;
}