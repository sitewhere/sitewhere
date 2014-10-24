/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.SiteWhere;
import com.sitewhere.Tracer;
import com.sitewhere.rest.model.cache.CacheInformation;
import com.sitewhere.rest.model.cache.CacheSummary;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.cache.ICache;
import com.sitewhere.spi.device.IDeviceManagementCacheProvider;
import com.sitewhere.spi.server.debug.ITracer;
import com.sitewhere.spi.server.debug.TracerCategory;
import com.sitewhere.spi.system.IVersion;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for system operations.
 * 
 * @author Derek Adams
 */
@Controller
@RequestMapping(value = "/system")
@Api(value = "system", description = "Operations related to SiteWhere system management.")
public class SystemController extends SiteWhereController {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(SystemController.class);

	@RequestMapping(value = "/version", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get version information")
	public IVersion getVersion() throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "getVersion", LOGGER);
		try {
			return SiteWhere.getServer().getVersion();
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Get a summary of statistics for caches.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/cachesummary", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get a summary of cache statistics")
	public CacheSummary getCacheSummary() throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "getCacheSummary", LOGGER);
		try {
			CacheSummary summary = new CacheSummary();
			IDeviceManagementCacheProvider provider =
					SiteWhere.getServer().getDeviceManagementCacheProvider();
			if (provider != null) {
				summary.getCaches().add(getCacheInformation(provider.getSiteCache()));
				summary.getCaches().add(getCacheInformation(provider.getDeviceSpecificationCache()));
				summary.getCaches().add(getCacheInformation(provider.getDeviceCache()));
				summary.getCaches().add(getCacheInformation(provider.getDeviceAssignmentCache()));
			}
			return summary;
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Extract information from cache.
	 * 
	 * @param cache
	 * @return
	 * @throws SiteWhereException
	 */
	protected CacheInformation getCacheInformation(ICache<?, ?> cache) throws SiteWhereException {
		CacheInformation info = new CacheInformation();
		info.setCacheType(cache.getType());
		info.setRequestCount(cache.getRequestCount());
		info.setHitCount(cache.getHitCount());
		if (cache.getRequestCount() > 0) {
			info.setHitRatio((double) cache.getHitCount() / (double) cache.getRequestCount());
		} else {
			info.setHitRatio(0);
		}
		return info;
	}

	/**
	 * Set enablement of {@link ITracer} implementation.
	 * 
	 * @param enabled
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/tracer", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Set tracer enablement")
	public void setTracerEnablement(
			@ApiParam(value = "Enable tracer implementation", required = false) @RequestParam(defaultValue = "true") boolean enabled)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "setTracerEnablement", LOGGER);
		try {
			Tracer.setEnabled(enabled);
		} finally {
			Tracer.stop(LOGGER);
		}
	}
}