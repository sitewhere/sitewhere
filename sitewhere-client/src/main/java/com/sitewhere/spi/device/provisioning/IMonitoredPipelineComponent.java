/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.provisioning;

/**
 * Interface for elements in the processing pipeline that can be monitored for flow
 * characteristics.
 * 
 * @author Derek
 */
public interface IMonitoredPipelineComponent {

	/**
	 * Get number of events processed by component.
	 * 
	 * @return
	 */
	public long getEventCount();

	/**
	 * Get number of errors from component (local or downstream).
	 * 
	 * @return
	 */
	public long getErrorCount();

	/**
	 * Get number of events wait to be processed.
	 * 
	 * @return
	 */
	public long getBacklog();

	/**
	 * Get average amount of time (in ms) between event arrival and invocation of next
	 * component in pipeline.
	 * 
	 * @return
	 */
	public long getAverageProcessingWaitTime();

	/**
	 * Get average amount of time (in ms) component spent processing an event.
	 * 
	 * @return
	 */
	public long getAverageProcessingTime();

	/**
	 * Get average amount of time (in ms) compoennts downstream took to finish processing
	 * event.
	 * 
	 * @return
	 */
	public long getAverageDownstreamProcessingTime();
}