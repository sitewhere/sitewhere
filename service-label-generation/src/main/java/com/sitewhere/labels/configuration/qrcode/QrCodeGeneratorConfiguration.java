/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
