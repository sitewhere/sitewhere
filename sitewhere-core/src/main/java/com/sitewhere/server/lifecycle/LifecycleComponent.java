/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.lifecycle;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Base class for implementing {@link ILifecycleComponent}.
 * 
 * @author Derek
 */
public abstract class LifecycleComponent implements ILifecycleComponent {

	/** Lifecycle status indicator */
	private LifecycleStatus lifecycleStatus = LifecycleStatus.Stopped;

	/** Last error encountered in lifecycle operations */
	private SiteWhereException lifecycleError;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getComponentName()
	 */
	@Override
	public String getComponentName() {
		return getClass().getSimpleName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#lifecycleStart()
	 */
	public void lifecycleStart() {
		setLifecycleStatus(LifecycleStatus.Starting);
		getLogger().info(getComponentName() + " state transitioned to STARTING.");
		try {
			start();
			setLifecycleStatus(LifecycleStatus.Started);
			getLogger().info(getComponentName() + " state transitioned to STARTED.");
		} catch (SiteWhereException e) {
			setLifecycleStatus(LifecycleStatus.Error);
			setLifecycleError(e);
			getLogger().error(getComponentName() + " state transitioned to ERROR.", e);
		} catch (Throwable t) {
			setLifecycleStatus(LifecycleStatus.Error);
			setLifecycleError(new SiteWhereException(t));
			getLogger().error(getComponentName() + " state transitioned to ERROR.", t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#lifecycleStop()
	 */
	public void lifecycleStop() {
		setLifecycleStatus(LifecycleStatus.Stopping);
		getLogger().info(getComponentName() + " state transitioned to STOPPING.");
		try {
			stop();
			setLifecycleStatus(LifecycleStatus.Stopped);
			getLogger().info(getComponentName() + " state transitioned to STOPPED.");
		} catch (SiteWhereException e) {
			setLifecycleStatus(LifecycleStatus.Error);
			setLifecycleError(e);
			getLogger().error(getComponentName() + " state transitioned to ERROR.", e);
		} catch (Throwable t) {
			setLifecycleStatus(LifecycleStatus.Error);
			setLifecycleError(new SiteWhereException(t));
			getLogger().error(getComponentName() + " state transitioned to ERROR.", t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLifecycleStatus()
	 */
	public LifecycleStatus getLifecycleStatus() {
		return lifecycleStatus;
	}

	public void setLifecycleStatus(LifecycleStatus lifecycleStatus) {
		this.lifecycleStatus = lifecycleStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLifecycleError()
	 */
	public SiteWhereException getLifecycleError() {
		return lifecycleError;
	}

	public void setLifecycleError(SiteWhereException lifecycleError) {
		this.lifecycleError = lifecycleError;
	}
}