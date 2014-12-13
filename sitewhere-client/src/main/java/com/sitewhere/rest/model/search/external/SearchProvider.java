/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.search.external;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.search.external.ISearchProvider;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Implementation of {@link ISearchProvider} used for marshaling.
 * 
 * @author Derek
 */
public class SearchProvider implements ISearchProvider, ILifecycleComponent {

	/** Provider id */
	private String id;

	/** Provider name */
	private String name;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getComponentName()
	 */
	@Override
	public String getComponentName() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.search.external.ISearchProvider#getId()
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.search.external.ISearchProvider#getName()
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Create copy of an {@link ISearchProvider} for marshaling.
	 * 
	 * @param source
	 * @return
	 */
	public static SearchProvider copy(ISearchProvider source) {
		SearchProvider provider = new SearchProvider();
		provider.setId(source.getId());
		provider.setName(source.getName());
		return provider;
	}
}