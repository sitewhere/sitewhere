/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.labels.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.sitewhere.labels.configuration.LabelGenerationTenantConfiguration;
import com.sitewhere.labels.spi.ILabelGenerator;
import com.sitewhere.labels.spi.manager.ILabelGeneratorManager;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Manages a list of {@link ILabelGenerator} implementations.
 */
public class LabelGeneratorManager extends TenantEngineLifecycleComponent implements ILabelGeneratorManager {

    /** Configuration */
    private LabelGenerationTenantConfiguration configuration;

    /** List of label generators */
    private List<ILabelGenerator> labelGenerators = new ArrayList<ILabelGenerator>();

    /** Map of label generators by unique id */
    private Map<String, ILabelGenerator> generatorsById = new HashMap<String, ILabelGenerator>();

    @Inject
    public LabelGeneratorManager(LabelGenerationTenantConfiguration configuration) {
	super(LifecycleComponentType.LabelGeneratorManager);
	this.configuration = configuration;
    }

    /*
     * @see com.sitewhere.microservice.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.labelGenerators = LabelGeneratorsParser.parse(this, configuration);

	for (ILabelGenerator generator : getLabelGenerators()) {
	    initializeNestedComponent(generator, monitor, true);
	}
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getGeneratorsById().clear();

	// Start configured generators.
	for (ILabelGenerator generator : getLabelGenerators()) {
	    startNestedComponent(generator, monitor, true);
	    getGeneratorsById().put(generator.getId(), generator);
	}
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	for (ILabelGenerator generator : getLabelGenerators()) {
	    stopNestedComponent(generator, monitor);
	}
    }

    /*
     * @see com.sitewhere.spi.label.ILabelGeneratorManager#getLabelGenerators()
     */
    @Override
    public List<ILabelGenerator> getLabelGenerators() {
	return labelGenerators;
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGeneratorManager#getLabelGenerator(java.lang.
     * String)
     */
    @Override
    public ILabelGenerator getLabelGenerator(String id) throws SiteWhereException {
	return getGeneratorsById().get(id);
    }

    protected Map<String, ILabelGenerator> getGeneratorsById() {
	return generatorsById;
    }
}