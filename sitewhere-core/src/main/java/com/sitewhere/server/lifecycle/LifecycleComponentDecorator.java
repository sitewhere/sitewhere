/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.lifecycle;

import java.util.List;

import org.apache.log4j.Logger;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Wraps instance of {@link ILifecycleComponent} to add functionality around
 * implementation.
 * 
 * @author Derek
 */
public class LifecycleComponentDecorator implements ILifecycleComponent {

	/** Delegate instance */
	private ILifecycleComponent delegate;

	public LifecycleComponentDecorator(ILifecycleComponent delegate) {
		this.delegate = delegate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getComponentId()
	 */
	@Override
	public String getComponentId() {
		return delegate.getComponentId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getComponentName()
	 */
	@Override
	public String getComponentName() {
		return delegate.getComponentName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getComponentType()
	 */
	@Override
	public LifecycleComponentType getComponentType() {
		return delegate.getComponentType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLifecycleStatus()
	 */
	@Override
	public LifecycleStatus getLifecycleStatus() {
		return delegate.getLifecycleStatus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLifecycleError()
	 */
	@Override
	public SiteWhereException getLifecycleError() {
		return delegate.getLifecycleError();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLifecycleComponents()
	 */
	@Override
	public List<ILifecycleComponent> getLifecycleComponents() {
		return delegate.getLifecycleComponents();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#lifecycleStart()
	 */
	@Override
	public void lifecycleStart() {
		delegate.lifecycleStart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		delegate.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#lifecyclePause()
	 */
	@Override
	public void lifecyclePause() {
		delegate.lifecyclePause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#canPause()
	 */
	@Override
	public boolean canPause() throws SiteWhereException {
		return delegate.canPause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#pause()
	 */
	@Override
	public void pause() throws SiteWhereException {
		delegate.pause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#lifecycleStop()
	 */
	@Override
	public void lifecycleStop() {
		delegate.lifecycleStop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		delegate.stop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#findComponentsOfType(com
	 * .sitewhere.spi.server.lifecycle.LifecycleComponentType)
	 */
	@Override
	public List<ILifecycleComponent> findComponentsOfType(LifecycleComponentType type)
			throws SiteWhereException {
		return delegate.findComponentsOfType(type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
		return delegate.getLogger();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#logState()
	 */
	@Override
	public void logState() {
		delegate.logState();
	}
}