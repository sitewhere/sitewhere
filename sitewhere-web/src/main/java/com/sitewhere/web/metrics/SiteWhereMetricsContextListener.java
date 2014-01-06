/*
 * SiteWhereMetricsContextListener.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.AdminServletContextListener;
import com.sitewhere.server.SiteWhereServer;

@SuppressWarnings("deprecation")
public class SiteWhereMetricsContextListener extends AdminServletContextListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.codahale.metrics.servlets.AdminServletContextListener#getMetricRegistry()
	 */
	protected MetricRegistry getMetricRegistry() {
		return SiteWhereServer.getInstance().getMetricRegistry();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.codahale.metrics.servlets.AdminServletContextListener#getHealthCheckRegistry()
	 */
	@Override
	protected HealthCheckRegistry getHealthCheckRegistry() {
		return SiteWhereServer.getInstance().getHealthCheckRegistry();
	}
}