/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.labels.configuration;

import java.util.List;

import com.sitewhere.labels.configuration.manager.LabelGenerationManagerConfiguration;
import com.sitewhere.labels.configuration.manager.LabelGeneratorGenericConfiguration;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineConfiguration;

/**
 * Maps label generation tenant engine YAML configuration to objects.
 */
public class LabelGenerationTenantConfiguration implements ITenantEngineConfiguration {

    /** Manager configuration */
    private LabelGenerationManagerConfiguration manager;

    /** List of configured generators */
    private List<LabelGeneratorGenericConfiguration> generators;

    public LabelGenerationManagerConfiguration getManager() {
	return manager;
    }

    public void setManager(LabelGenerationManagerConfiguration manager) {
	this.manager = manager;
    }

    public List<LabelGeneratorGenericConfiguration> getGenerators() {
	return generators;
    }

    public void setGenerators(List<LabelGeneratorGenericConfiguration> generators) {
	this.generators = generators;
    }
}
