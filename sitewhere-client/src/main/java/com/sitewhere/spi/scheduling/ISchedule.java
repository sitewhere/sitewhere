/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.scheduling;

import java.io.Serializable;
import java.util.Map;

import com.sitewhere.spi.common.IMetadataProviderEntity;

/**
 * Represents a named schedule that can be applied to system actions.
 * 
 * @author Derek
 */
public interface ISchedule extends IMetadataProviderEntity, Serializable {

	/**
	 * Unique token for schedule.
	 * 
	 * @return
	 */
	public String getToken();

	/**
	 * Schedule name.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Get type of trigger for schedule.
	 * 
	 * @return
	 */
	public TriggerType getTriggerType();

	/**
	 * Get trigger configuration values.
	 * 
	 * @return
	 */
	public Map<String, String> getTriggerConfiguration();
}