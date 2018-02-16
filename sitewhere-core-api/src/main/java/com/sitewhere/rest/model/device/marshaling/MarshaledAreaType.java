/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.marshaling;

import java.util.List;

import com.sitewhere.rest.model.area.AreaType;
import com.sitewhere.spi.area.IAreaType;

/**
 * Extends {@link AreaType} to support fields that can be included on REST
 * calls.
 * 
 * @author Derek
 */
public class MarshaledAreaType extends AreaType {

    /** Serial version UID */
    private static final long serialVersionUID = 2440287915755224663L;

    /** List of contained area types */
    private List<IAreaType> containedAreaTypes;

    public List<IAreaType> getContainedAreaTypes() {
	return containedAreaTypes;
    }

    public void setContainedAreaTypes(List<IAreaType> containedAreaTypes) {
	this.containedAreaTypes = containedAreaTypes;
    }
}