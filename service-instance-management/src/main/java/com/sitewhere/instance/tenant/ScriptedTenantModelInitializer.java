/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.tenant;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.instance.spi.tenant.ITenantModelInitializer;
import com.sitewhere.microservice.model.ScriptedModelInitializer;
import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.microservice.tenant.TenantManagementRequestBuilder;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.scripting.IScriptVariables;
import com.sitewhere.spi.microservice.scripting.ScriptScope;
import com.sitewhere.spi.microservice.scripting.ScriptType;
import com.sitewhere.spi.microservice.tenant.ITenantManagement;

/**
 * Implementation of {@link ITenantModelInitializer} that delegates creation
 * logic to a script.
 */
public class ScriptedTenantModelInitializer extends ScriptedModelInitializer<Void> implements ITenantModelInitializer {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(ScriptedTenantModelInitializer.class);

    @Override
    public void initialize(ITenantManagement tenantManagement) throws SiteWhereException {
	Binding binding = new Binding();
	binding.setVariable(IScriptVariables.VAR_LOGGER, LOGGER);
	binding.setVariable(IScriptVariables.VAR_TENANT_MANAGEMENT_BUILDER,
		new TenantManagementRequestBuilder(tenantManagement));
	run(ScriptScope.Microservice, ScriptType.Bootstrap, binding);
    }
}