/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rules;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.rules.spi.IRuleProcessor;
import com.sitewhere.rules.spi.IRuleProcessorsManager;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;

/**
 * Manages the list of rule processors configured for a tenant.
 * 
 * @author Derek
 */
public class RuleProcessorsManager extends TenantEngineLifecycleComponent implements IRuleProcessorsManager {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** List of rule processors */
    private List<IRuleProcessor> ruleProcessors;

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    public List<IRuleProcessor> getRuleProcessors() {
	return ruleProcessors;
    }

    public void setRuleProcessors(List<IRuleProcessor> ruleProcessors) {
	this.ruleProcessors = ruleProcessors;
    }
}