/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.user;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.instance.spi.user.IUserModelInitializer;
import com.sitewhere.microservice.api.user.IUserManagement;
import com.sitewhere.microservice.api.user.UserManagementRequestBuilder;
import com.sitewhere.microservice.model.ScriptedModelInitializer;
import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.scripting.IScriptVariables;
import com.sitewhere.spi.microservice.scripting.ScriptScope;
import com.sitewhere.spi.microservice.scripting.ScriptType;

/**
 * Implementation of {@link IUserModelInitializer} that delegates creation logic
 * to a script.
 */
public class ScriptedUserModelInitializer extends ScriptedModelInitializer<Void> implements IUserModelInitializer {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(ScriptedUserModelInitializer.class);

    /*
     * @see com.sitewhere.instance.spi.user.IUserModelInitializer#initialize(com.
     * sitewhere.spi.user.IUserManagement)
     */
    @Override
    public void initialize(IUserManagement userManagement) throws SiteWhereException {
	Binding binding = new Binding();
	binding.setVariable(IScriptVariables.VAR_LOGGER, LOGGER);
	binding.setVariable("userBuilder", new UserManagementRequestBuilder(userManagement));
	run(ScriptScope.Microservice, ScriptType.Bootstrap, binding);
    }
}