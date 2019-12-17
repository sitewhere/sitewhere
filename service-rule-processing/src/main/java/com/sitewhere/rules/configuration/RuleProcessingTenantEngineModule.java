/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rules.configuration;

import com.sitewhere.microservice.multitenant.TenantEngineModule;

/**
 * Guice module used for configuring components associated with a rule
 * processing tenant engine.
 */
public class RuleProcessingTenantEngineModule extends TenantEngineModule<RuleProcessingTenantConfiguration> {

    public RuleProcessingTenantEngineModule(RuleProcessingTenantConfiguration configuration) {
	super(configuration);
    }
}
