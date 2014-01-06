/*
 * ZoneRelationship.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.geo;

/**
 * Specifies containment relationship between location and zone.
 * 
 * @author Derek
 */
public enum ZoneContainment {

	// If a location is inside a zone.
	Inside,

	// If a location is outside a zone.
	Outside;
}