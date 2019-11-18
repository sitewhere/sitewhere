/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.labels.microservice;

import com.sitewhere.labels.spi.microservice.ILabelGenerationMicroservice;
import com.sitewhere.microservice.instance.InstanceSettings;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;

/**
 * Spring bean configuration for microservice.
 */
public class LabelGenerationMicroserviceConfiguration {

    public ILabelGenerationMicroservice labelGenerationMicroservice() {
	return new LabelGenerationMicroservice();
    }

    public IInstanceSettings instanceSettings() {
	return new InstanceSettings();
    }
}