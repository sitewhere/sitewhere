/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rules;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.microservice.kafka.KafkaRuleProcessorHost;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.rules.spi.IRuleProcessor;
import com.sitewhere.rules.spi.IRuleProcessorsManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Manages the list of rule processors configured for a tenant.
 */
public class RuleProcessorsManager extends TenantEngineLifecycleComponent implements IRuleProcessorsManager {

    /** List of rule processors */
    private List<IRuleProcessor> ruleProcessors;

    /** List of host wrappers for rule processors */
    private List<KafkaRuleProcessorHost> ruleProcessorHosts = new ArrayList<KafkaRuleProcessorHost>();

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getRuleProcessorHosts().clear();
	for (IRuleProcessor processor : getRuleProcessors()) {
	    // Create host for managing rule processor.
	    KafkaRuleProcessorHost host = new KafkaRuleProcessorHost(processor);
	    initializeNestedComponent(host, monitor, true);
	    getRuleProcessorHosts().add(host);
	}
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	for (KafkaRuleProcessorHost host : getRuleProcessorHosts()) {
	    startNestedComponent(host, monitor, true);
	}
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	for (KafkaRuleProcessorHost host : getRuleProcessorHosts()) {
	    stopNestedComponent(host, monitor);
	}
    }

    /*
     * @see com.sitewhere.rules.spi.IRuleProcessorsManager#getRuleProcessors()
     */
    @Override
    public List<IRuleProcessor> getRuleProcessors() {
	return ruleProcessors;
    }

    public void setRuleProcessors(List<IRuleProcessor> ruleProcessors) {
	this.ruleProcessors = ruleProcessors;
    }

    public List<KafkaRuleProcessorHost> getRuleProcessorHosts() {
	return ruleProcessorHosts;
    }

    public void setRuleProcessorHosts(List<KafkaRuleProcessorHost> ruleProcessorHosts) {
	this.ruleProcessorHosts = ruleProcessorHosts;
    }
}