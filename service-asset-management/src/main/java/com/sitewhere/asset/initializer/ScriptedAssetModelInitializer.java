/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.initializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.asset.spi.IAssetModelInitializer;
import com.sitewhere.microservice.api.asset.AssetManagementRequestBuilder;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.model.ScriptedModelInitializer;
import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.scripting.ScriptScope;
import com.sitewhere.spi.microservice.scripting.ScriptType;

/**
 * Implementation of {@link IAssetModelInitializer} that delegates creation
 * logic to a script.
 */
public class ScriptedAssetModelInitializer extends ScriptedModelInitializer<Void> implements IAssetModelInitializer {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(ScriptedAssetModelInitializer.class);

    /*
     * @see com.sitewhere.asset.spi.IAssetModelInitializer#initialize(com.sitewhere.
     * microservice.api.asset.IAssetManagement)
     */
    @Override
    public void initialize(IAssetManagement assetManagement) throws SiteWhereException {
	Binding binding = new Binding();
	binding.setVariable("logger", LOGGER);
	binding.setVariable("assetBuilder", new AssetManagementRequestBuilder(assetManagement));
	run(ScriptScope.TenantEngine, ScriptType.Bootstrap, binding);
    }
}