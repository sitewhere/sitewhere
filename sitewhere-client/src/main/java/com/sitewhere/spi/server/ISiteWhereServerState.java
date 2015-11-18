/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server;

/**
 * Holds persistent state information for a SiteWhere Server node.
 * 
 * @author Derek
 */
public interface ISiteWhereServerState {

	/**
	 * Get the unique node id.
	 * 
	 * @return
	 */
	public String getNodeId();
}