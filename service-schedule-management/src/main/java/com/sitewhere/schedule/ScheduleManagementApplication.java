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
package com.sitewhere.schedule;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.sitewhere.microservice.MicroserviceApplication;
import com.sitewhere.schedule.spi.microservice.IScheduleManagementMicroservice;

/**
 * Main application which runs the schedule management microservice.
 */
@ApplicationScoped
public class ScheduleManagementApplication extends MicroserviceApplication<IScheduleManagementMicroservice> {

    @Inject
    private IScheduleManagementMicroservice microservice;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroserviceApplication#getMicroservice()
     */
    @Override
    public IScheduleManagementMicroservice getMicroservice() {
	return microservice;
    }
}