/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule.spi.initializer;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.server.IModelInitializer;

/**
 * Class that initializes the schedule model with data needed to bootstrap the
 * system.
 * 
 * @author Derek
 */
public interface IScheduleModelInitializer extends IModelInitializer {

    /**
     * Intialize schedule management.
     * 
     * @param scheduleManagement
     * @throws SiteWhereException
     */
    public void initialize(IScheduleManagement scheduleManagement) throws SiteWhereException;
}