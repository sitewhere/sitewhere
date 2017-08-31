/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.outbound.geospatial;

import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.geospatial.ZoneContainment;

/**
 * A single zone test condition that, if met, results in the firing of an alert
 * event.
 * 
 * @author Derek
 */
public class ZoneTest {

    /** Token for zone to test */
    private String zoneToken;

    /** Containment condition for test */
    private ZoneContainment condition;

    /** Alert type to use if test is met */
    private String alertType;

    /** Alert level to use if test is met */
    private AlertLevel alertLevel;

    /** Message to use if test is met */
    private String alertMessage;

    public String getZoneToken() {
	return zoneToken;
    }

    public void setZoneToken(String zoneToken) {
	this.zoneToken = zoneToken;
    }

    public ZoneContainment getCondition() {
	return condition;
    }

    public void setCondition(ZoneContainment condition) {
	this.condition = condition;
    }

    public String getAlertType() {
	return alertType;
    }

    public void setAlertType(String alertType) {
	this.alertType = alertType;
    }

    public AlertLevel getAlertLevel() {
	return alertLevel;
    }

    public void setAlertLevel(AlertLevel alertLevel) {
	this.alertLevel = alertLevel;
    }

    public String getAlertMessage() {
	return alertMessage;
    }

    public void setAlertMessage(String alertMessage) {
	this.alertMessage = alertMessage;
    }
}