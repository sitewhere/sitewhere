/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.outbound.siddhi;

import java.util.ArrayList;
import java.util.List;

import org.wso2.siddhi.core.stream.output.StreamCallback;

/**
 * Holds information about a Siddhi query including callbacks that should be
 * registered for it.
 * 
 * @author Derek
 */
public class SiddhiQuery {

    /** Selector expression */
    private String selector;

    /** Map of callbacks by stream name */
    private List<StreamCallback> callbacks = new ArrayList<StreamCallback>();

    public String getSelector() {
	return selector;
    }

    public void setSelector(String selector) {
	this.selector = selector;
    }

    public List<StreamCallback> getCallbacks() {
	return callbacks;
    }

    public void setCallbacks(List<StreamCallback> callbacks) {
	this.callbacks = callbacks;
    }
}