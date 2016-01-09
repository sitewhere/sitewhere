/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication.symbology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.symbology.ISymbolGenerator;
import com.sitewhere.spi.device.symbology.ISymbolGeneratorManager;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Manages a list of {@link ISymbolGenerator} implementations.
 * 
 * @author Derek
 */
public class SymbolGeneratorManager extends TenantLifecycleComponent implements ISymbolGeneratorManager {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(SymbolGeneratorManager.class);

	/** List of symbol generators */
	private List<ISymbolGenerator> symbolGenerators = new ArrayList<ISymbolGenerator>();

	/** Map of symbol generators by unique id */
	private Map<String, ISymbolGenerator> generatorsById = new HashMap<String, ISymbolGenerator>();

	public SymbolGeneratorManager() {
		super(LifecycleComponentType.SymbolGeneratorManager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		generatorsById.clear();

		// Start configured generators.
		if (getSymbolGenerators().size() > 0) {
			for (ISymbolGenerator generator : symbolGenerators) {
				startNestedComponent(generator, true);
				generatorsById.put(generator.getId(), generator);
			}
		}

		// Otherwise, add a fallback to support basic QR codes.
		else {
			QrCodeSymbolGenerator generator = new QrCodeSymbolGenerator();
			symbolGenerators.add(generator);
			startNestedComponent(generator, true);
			generatorsById.put(generator.getId(), generator);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		for (ISymbolGenerator generator : symbolGenerators) {
			generator.lifecycleStop();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.symbology.ISymbolGeneratorManager#getSymbolGenerators()
	 */
	public List<ISymbolGenerator> getSymbolGenerators() {
		return symbolGenerators;
	}

	public void setSymbolGenerators(List<ISymbolGenerator> symbolGenerators) {
		this.symbolGenerators = symbolGenerators;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.symbology.ISymbolGeneratorManager#getSymbolGenerator(java
	 * .lang.String)
	 */
	@Override
	public ISymbolGenerator getSymbolGenerator(String id) throws SiteWhereException {
		return generatorsById.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.symbology.ISymbolGeneratorManager#getDefaultSymbolGenerator
	 * ()
	 */
	@Override
	public ISymbolGenerator getDefaultSymbolGenerator() throws SiteWhereException {
		if (getSymbolGenerators().size() > 0) {
			return getSymbolGenerators().get(0);
		}
		return null;
	}
}