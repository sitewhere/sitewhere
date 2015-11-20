/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.search.device;

import com.sitewhere.spi.device.batch.ElementProcessingStatus;
import com.sitewhere.spi.search.ISearchCriteria;

/**
 * Extends search criteria to add batch element fields.
 * 
 * @author Derek
 */
public interface IBatchElementSearchCriteria extends ISearchCriteria {

	/**
	 * Gets processing status to match. Null matches all.
	 * 
	 * @return
	 */
	public ElementProcessingStatus getProcessingStatus();
}