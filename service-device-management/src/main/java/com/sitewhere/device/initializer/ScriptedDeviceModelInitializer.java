/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.initializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.device.spi.initializer.IDeviceModelInitializer;
import com.sitewhere.microservice.api.asset.AssetManagementRequestBuilder;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.device.DeviceManagementRequestBuilder;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.model.ScriptedModelInitializer;
import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.scripting.IScriptVariables;
import com.sitewhere.spi.microservice.scripting.ScriptScope;
import com.sitewhere.spi.microservice.scripting.ScriptType;

/**
 * Implementation of {@link IDeviceModelInitializer} that delegates creation
 * logic to a script.
 */
public class ScriptedDeviceModelInitializer extends ScriptedModelInitializer<Void> implements IDeviceModelInitializer {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(ScriptedDeviceModelInitializer.class);

    /*
     * @see
     * com.sitewhere.device.spi.initializer.IDeviceModelInitializer#initialize(com.
     * sitewhere.spi.device.IDeviceManagement,
     * com.sitewhere.spi.asset.IAssetManagement)
     */
    @Override
    public void initialize(IDeviceManagement deviceManagement, IAssetManagement assetManagement)
	    throws SiteWhereException {
	Binding binding = new Binding();
	binding.setVariable(IScriptVariables.VAR_LOGGER, LOGGER);
	binding.setVariable(IScriptVariables.VAR_DEVICE_MANAGEMENT_BUILDER,
		new DeviceManagementRequestBuilder(deviceManagement));
	binding.setVariable(IScriptVariables.VAR_ASSET_MANAGEMENT_BUILDER,
		new AssetManagementRequestBuilder(assetManagement));
	run(ScriptScope.TenantEngine, ScriptType.Bootstrap, binding);
    }
}