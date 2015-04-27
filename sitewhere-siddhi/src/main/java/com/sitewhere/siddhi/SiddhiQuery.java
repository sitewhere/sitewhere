/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.siddhi;

import java.util.HashMap;
import java.util.Map;

import org.wso2.siddhi.core.stream.output.StreamCallback;

/**
 * Holds information about a Siddhi query including callbacks that should be registered
 * for it.
 * 
 * @author Derek
 */
public class SiddhiQuery {

	/** Selector expression */
	private String selector;

	/** Map of callbacks by stream name */
	private Map<String, StreamCallback> callbacks = new HashMap<String, StreamCallback>();

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	public Map<String, StreamCallback> getCallbacks() {
		return callbacks;
	}

	public void setCallbacks(Map<String, StreamCallback> callbacks) {
		this.callbacks = callbacks;
	}
}