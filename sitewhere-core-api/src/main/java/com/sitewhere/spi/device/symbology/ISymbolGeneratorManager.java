/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.symbology;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Manages a list of symbol generators.
 * 
 * @author Derek
 */
public interface ISymbolGeneratorManager extends ITenantLifecycleComponent {

	/**
	 * Get the list of available symbol generators.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public List<ISymbolGenerator> getSymbolGenerators() throws SiteWhereException;

	/**
	 * Get a symbol generator by id.
	 * 
	 * @param id
	 * @return
	 * @throws SiteWhereException
	 */
	public ISymbolGenerator getSymbolGenerator(String id) throws SiteWhereException;

	/**
	 * Get the default symbol generator.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public ISymbolGenerator getDefaultSymbolGenerator() throws SiteWhereException;
}