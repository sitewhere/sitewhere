/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.lifecycle;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.sitewhere.spi.ServerStartupException;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Base class for implementing {@link ILifecycleComponent}.
 * 
 * @author Derek
 */
public abstract class LifecycleComponent implements ILifecycleComponent {

	/** Unique component id */
	private String componentId = UUID.randomUUID().toString();

	/** Component type */
	private LifecycleComponentType componentType;

	/** Lifecycle status indicator */
	private LifecycleStatus lifecycleStatus = LifecycleStatus.Stopped;

	/** Last error encountered in lifecycle operations */
	private SiteWhereException lifecycleError;

	/** List of contained lifecycle components */
	private List<ILifecycleComponent> lifecycleComponents = new ArrayList<ILifecycleComponent>();

	public LifecycleComponent(LifecycleComponentType type) {
		this.componentType = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getComponentId()
	 */
	public String getComponentId() {
		return componentId;
	}

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
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getComponentType()
	 */
	public LifecycleComponentType getComponentType() {
		return componentType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#lifecycleStart()
	 */
	public void lifecycleStart() {
		LifecycleStatus old = getLifecycleStatus();
		setLifecycleStatus(LifecycleStatus.Starting);
		getLogger().info(getComponentName() + " state transitioned to STARTING.");
		try {
			if (old != LifecycleStatus.Paused) {
				start();
			}
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
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#lifecyclePause()
	 */
	@Override
	public void lifecyclePause() {
		setLifecycleStatus(LifecycleStatus.Pausing);
		getLogger().info(getComponentName() + " state transitioned to PAUSING.");
		try {
			pause();
			setLifecycleStatus(LifecycleStatus.Paused);
			getLogger().info(getComponentName() + " state transitioned to PAUSED.");
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
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#pause()
	 */
	@Override
	public void pause() throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#canPause()
	 */
	public boolean canPause() throws SiteWhereException {
		return false;
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
	 * @see
	 * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#findComponentsOfType(com
	 * .sitewhere.spi.server.lifecycle.LifecycleComponentType)
	 */
	@Override
	public List<ILifecycleComponent> findComponentsOfType(LifecycleComponentType type)
			throws SiteWhereException {
		List<ILifecycleComponent> matches = new ArrayList<ILifecycleComponent>();
		findComponentsOfType(this, matches, type);
		return matches;
	}

	/**
	 * Recursive matching of nested components to find those of the given type.
	 * 
	 * @param matches
	 * @param type
	 * @throws SiteWhereException
	 */
	public void findComponentsOfType(ILifecycleComponent current, List<ILifecycleComponent> matches,
			LifecycleComponentType type) throws SiteWhereException {
		if (current.getComponentType() == type) {
			matches.add(current);
		}
		for (ILifecycleComponent child : current.getLifecycleComponents()) {
			findComponentsOfType(child, matches, type);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#logState()
	 */
	public void logState() {
		getLogger().info("\n\nSiteWhere Server State:\n" + logState("", this) + "\n");
	}

	/**
	 * Recursively log state for a component.
	 * 
	 * @param pad
	 * @param component
	 */
	protected String logState(String pad, ILifecycleComponent component) {
		String entry =
				"\n" + pad + "+ " + component.getComponentName() + " " + component.getLifecycleStatus();
		for (ILifecycleComponent nested : component.getLifecycleComponents()) {
			entry = entry + logState("  " + pad, nested);
		}
		return entry;
	}

	/**
	 * Starts a nested {@link ILifecycleComponent}. Uses default message.
	 * 
	 * @param component
	 * @param require
	 * @throws SiteWhereException
	 */
	public void startNestedComponent(ILifecycleComponent component, boolean require)
			throws SiteWhereException {
		startNestedComponent(component, getComponentName() + " failed to start.", require);
	}

	/**
	 * Starts a nested {@link ILifecycleComponent}.
	 * 
	 * @param component
	 * @param errorMessage
	 * @param require
	 * @throws SiteWhereException
	 */
	public void startNestedComponent(ILifecycleComponent component, String errorMessage, boolean require)
			throws SiteWhereException {
		component.lifecycleStart();
		if (require) {
			if (component.getLifecycleStatus() == LifecycleStatus.Error) {
				throw new ServerStartupException(component, errorMessage);
			}
		}
		getLifecycleComponents().add(component);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLifecycleComponents()
	 */
	public List<ILifecycleComponent> getLifecycleComponents() {
		return lifecycleComponents;
	}

	public void setLifecycleComponents(List<ILifecycleComponent> lifecycleComponents) {
		this.lifecycleComponents = lifecycleComponents;
	}
}