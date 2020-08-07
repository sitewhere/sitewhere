/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.labels.configuration.qrcode;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.labels.configuration.generator.LabelGeneratorConfiguration;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Configuration for QR code label generator.
 */
public class QrCodeGeneratorConfiguration extends LabelGeneratorConfiguration {

    /** Image width in pixels */
    private int width = 200;

    /** Image height in pixels */
    private int height = 200;

    /** Foreground color (RGBA) */
    private String foregroundColor = "333333ff";

    /** Background color (RGBA) */
    private String backgroundColor = "ffffffff";

    public QrCodeGeneratorConfiguration(ITenantEngineLifecycleComponent component) {
	super(component);
    }

    /*
     * @see
     * com.sitewhere.labels.configuration.generator.LabelGeneratorConfiguration#
     * loadFrom(com.fasterxml.jackson.databind.JsonNode)
     */
    @Override
    public void loadFrom(JsonNode json) throws SiteWhereException {
	this.width = configurableInt("width", json, 200);
	this.height = configurableInt("height", json, 200);
	this.foregroundColor = configurableString("foregroundColor", json, "333333ff");
	this.backgroundColor = configurableString("backgroundColor", json, "ffffffff");
    }

    public int getWidth() {
	return width;
    }

    public int getHeight() {
	return height;
    }

    public String getForegroundColor() {
	return foregroundColor;
    }

    public String getBackgroundColor() {
	return backgroundColor;
    }
}
