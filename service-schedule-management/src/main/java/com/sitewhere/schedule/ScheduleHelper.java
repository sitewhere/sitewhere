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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.sitewhere.rest.model.scheduling.request.ScheduleCreateRequest;
import com.sitewhere.spi.scheduling.TriggerConstants;
import com.sitewhere.spi.scheduling.TriggerType;
import com.sitewhere.spi.scheduling.request.IScheduleCreateRequest;

/**
 * Helper class for building {@link IScheduleCreateRequest} instances based on
 * schedule types.
 */
public class ScheduleHelper {

    /**
     * Build request for a simple schedule.
     * 
     * @param token
     * @param name
     * @param start
     * @param end
     * @param interval
     * @param count
     * @return
     */
    public static IScheduleCreateRequest createSimpleSchedule(String token, String name, Date start, Date end,
	    Long interval, Integer count) {
	ScheduleCreateRequest schedule = new ScheduleCreateRequest();
	schedule.setToken(token);
	schedule.setName(name);
	schedule.setTriggerType(TriggerType.SimpleTrigger);
	schedule.setStartDate(start);
	schedule.setEndDate(end);

	Map<String, String> config = new HashMap<String, String>();
	if (interval != null) {
	    config.put(TriggerConstants.SimpleTrigger.REPEAT_INTERVAL, String.valueOf(interval));
	}
	if (count != null) {
	    config.put(TriggerConstants.SimpleTrigger.REPEAT_COUNT, String.valueOf(count));
	}
	schedule.setTriggerConfiguration(config);

	return schedule;
    }

    /**
     * Build request for a cron schedule.
     * 
     * @param token
     * @param name
     * @param start
     * @param end
     * @param expression
     * @return
     */
    public static IScheduleCreateRequest createCronSchedule(String token, String name, Date start, Date end,
	    String expression) {
	ScheduleCreateRequest schedule = new ScheduleCreateRequest();
	schedule.setToken(token);
	schedule.setName(name);
	schedule.setTriggerType(TriggerType.CronTrigger);
	schedule.setStartDate(start);
	schedule.setEndDate(end);

	Map<String, String> config = new HashMap<String, String>();
	config.put(TriggerConstants.CronTrigger.CRON_EXPRESSION, expression);
	schedule.setTriggerConfiguration(config);

	return schedule;
    }
}