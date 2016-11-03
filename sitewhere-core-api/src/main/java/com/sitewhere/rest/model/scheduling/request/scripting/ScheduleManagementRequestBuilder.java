package com.sitewhere.rest.model.scheduling.request.scripting;

import com.sitewhere.rest.model.scheduling.request.ScheduleCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduleManagement;

/**
 * Builder that supports creating schedule management entities.
 * 
 * @author Derek
 */
public class ScheduleManagementRequestBuilder {

    /** Asset management implementation */
    private IScheduleManagement scheduleManagement;

    public ScheduleManagementRequestBuilder(IScheduleManagement scheduleManagement) {
	this.scheduleManagement = scheduleManagement;
    }

    public ScheduleCreateRequest.Builder newSchedule(String name) {
	return new ScheduleCreateRequest.Builder(name);
    }

    public ScheduleCreateRequest.Builder newSchedule(String token, String name) {
	return new ScheduleCreateRequest.Builder(token, name);
    }

    public ISchedule persist(ScheduleCreateRequest.Builder builder) throws SiteWhereException {
	return getScheduleManagement().createSchedule(builder.build());
    }

    public IScheduleManagement getScheduleManagement() {
	return scheduleManagement;
    }

    public void setScheduleManagement(IScheduleManagement scheduleManagement) {
	this.scheduleManagement = scheduleManagement;
    }
}