/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.sitewhere.spi.mule.delegate;

import java.util.List;

import com.sitewhere.spi.ISiteWhereContext;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.geo.IZoneMatcher;

/**
 * Delegate that receives callbacks for zone processing so that developers can specify
 * responses to locations inside/outside of a given zone.
 * 
 * @author Derek Adams
 */
public interface IZoneProcessingDelegate {

	/**
	 * Called by zone check code to delegate handling of client-specific zone logic.
	 * 
	 * @param context
	 * @param matcher
	 * @return
	 * @throws SiteWhereException
	 */
	public List<IDeviceAlertCreateRequest> handleZoneResults(ISiteWhereContext context, IZoneMatcher matcher)
			throws SiteWhereException;
}