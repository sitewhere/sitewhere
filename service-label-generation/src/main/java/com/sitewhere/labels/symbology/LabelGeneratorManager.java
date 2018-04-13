/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.labels.symbology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.label.ILabelGenerator;
import com.sitewhere.spi.label.ILabelGeneratorManager;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Manages a list of {@link ILabelGenerator} implementations.
 * 
 * @author Derek
 */
public class LabelGeneratorManager extends TenantEngineLifecycleComponent implements ILabelGeneratorManager {

    /** List of label generators */
    private List<ILabelGenerator> labelGenerators = new ArrayList<ILabelGenerator>();

    /** Map of label generators by unique id */
    private Map<String, ILabelGenerator> generatorsById = new HashMap<String, ILabelGenerator>();

    public LabelGeneratorManager() {
	super(LifecycleComponentType.LabelGeneratorManager);
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
	    generator.lifecycleStop(monitor);
	}
    }

    /*
     * @see com.sitewhere.spi.label.ILabelGeneratorManager#getLabelGenerators()
     */
    @Override
    public List<ILabelGenerator> getLabelGenerators() {
	return labelGenerators;
    }

    public void setLabelGenerators(List<ILabelGenerator> labelGenerators) {
	this.labelGenerators = labelGenerators;
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

    public Map<String, ILabelGenerator> getGeneratorsById() {
	return generatorsById;
    }

    public void setGeneratorsById(Map<String, ILabelGenerator> generatorsById) {
	this.generatorsById = generatorsById;
    }
}