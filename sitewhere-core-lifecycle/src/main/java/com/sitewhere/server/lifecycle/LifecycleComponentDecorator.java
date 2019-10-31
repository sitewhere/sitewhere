/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.lifecycle;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.cal10n.LocLogger;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.state.ILifecycleComponentState;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponentParameter;
import com.sitewhere.spi.server.lifecycle.ILifecycleConstraints;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Wraps instance of {@link ILifecycleComponent} to add functionality around
 * implementation.
 */
public class LifecycleComponentDecorator<T extends ILifecycleComponent> implements ILifecycleComponent {

    /** Delegate instance */
    private T delegate;

    public LifecycleComponentDecorator(T delegate) {
	this.delegate = delegate;
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getComponentId()
     */
    @Override
    public UUID getComponentId() {
	return getDelegate().getComponentId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getComponentName()
     */
    @Override
    public String getComponentName() {
	return getDelegate().getComponentName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getComponentType()
     */
    @Override
    public LifecycleComponentType getComponentType() {
	return getDelegate().getComponentType();
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getMicroservice()
     */
    @Override
    public IMicroservice<? extends IFunctionIdentifier> getMicroservice() {
	return getDelegate().getMicroservice();
    }

    /*
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#setMicroservice(com.
     * sitewhere.spi.microservice.IMicroservice)
     */
    @Override
    public void setMicroservice(IMicroservice<? extends IFunctionIdentifier> microservice) {
	getDelegate().setMicroservice(microservice);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLifecycleStatus ()
     */
    @Override
    public LifecycleStatus getLifecycleStatus() {
	return getDelegate().getLifecycleStatus();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLifecycleError( )
     */
    @Override
    public SiteWhereException getLifecycleError() {
	return getDelegate().getLifecycleError();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getCreatedDate()
     */
    @Override
    public Date getCreatedDate() {
	return getDelegate().getCreatedDate();
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getParameters()
     */
    @Override
    public List<ILifecycleComponentParameter<?>> getParameters() {
	return getDelegate().getParameters();
    }

    /*
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#initializeParameters()
     */
    @Override
    public void initializeParameters() throws SiteWhereException {
	getDelegate().initializeParameters();
    }

    /*
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLifecycleComponents
     * ()
     */
    @Override
    public Map<UUID, ILifecycleComponent> getLifecycleComponents() {
	return getDelegate().getLifecycleComponents();
    }

    /*
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#lifecycleProvision(com
     * .sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void lifecycleProvision(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getDelegate().lifecycleProvision(monitor);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#provision(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void provision(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getDelegate().provision(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#
     * lifecycleInitialize(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void lifecycleInitialize(ILifecycleProgressMonitor monitor) {
	getDelegate().lifecycleInitialize(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#canInitialize()
     */
    @Override
    public boolean canInitialize() throws SiteWhereException {
	return getDelegate().canInitialize();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getDelegate().initialize(monitor);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#
     * initializeNestedComponent(com.sitewhere.spi.server.lifecycle.
     * ILifecycleComponent,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor, boolean)
     */
    @Override
    public void initializeNestedComponent(ILifecycleComponent component, ILifecycleProgressMonitor monitor,
	    boolean require) throws SiteWhereException {
	getDelegate().initializeNestedComponent(component, monitor, require);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#lifecycleStart(com
     * .sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void lifecycleStart(ILifecycleProgressMonitor monitor) {
	getDelegate().lifecycleStart(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#canStart()
     */
    @Override
    public boolean canStart() throws SiteWhereException {
	return getDelegate().canStart();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getDelegate().start(monitor);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#
     * startNestedComponent(com.sitewhere.spi.server.lifecycle. ILifecycleComponent,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor, boolean)
     */
    @Override
    public void startNestedComponent(ILifecycleComponent component, ILifecycleProgressMonitor monitor, boolean require)
	    throws SiteWhereException {
	getDelegate().startNestedComponent(component, monitor, require);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#lifecyclePause(com
     * .sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void lifecyclePause(ILifecycleProgressMonitor monitor) {
	getDelegate().lifecyclePause(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#canPause()
     */
    @Override
    public boolean canPause() throws SiteWhereException {
	return getDelegate().canPause();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#pause(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void pause(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getDelegate().pause(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#lifecycleStop(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void lifecycleStop(ILifecycleProgressMonitor monitor) {
	getDelegate().lifecycleStop(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#lifecycleStop(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor,
     * com.sitewhere.spi.server.lifecycle.ILifecycleConstraints)
     */
    @Override
    public void lifecycleStop(ILifecycleProgressMonitor monitor, ILifecycleConstraints constraints) {
	getDelegate().lifecycleStop(monitor, constraints);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#canStop()
     */
    @Override
    public boolean canStop() throws SiteWhereException {
	return getDelegate().canStop();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop(com.sitewhere
     * .spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getDelegate().stop(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop(com.sitewhere
     * .spi.server.lifecycle.ILifecycleProgressMonitor,
     * com.sitewhere.spi.server.lifecycle.ILifecycleConstraints)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor, ILifecycleConstraints constraints) throws SiteWhereException {
	getDelegate().stop(monitor, constraints);
    }

    /*
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stopNestedComponent(
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stopNestedComponent(ILifecycleComponent component, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
	getDelegate().stopNestedComponent(component, monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#lifecycleTerminate
     * (com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void lifecycleTerminate(ILifecycleProgressMonitor monitor) {
	getDelegate().lifecycleTerminate(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#terminate(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void terminate(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getDelegate().terminate(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#
     * lifecycleStatusChanged(com.sitewhere.spi.server.lifecycle. LifecycleStatus,
     * com.sitewhere.spi.server.lifecycle.LifecycleStatus)
     */
    @Override
    public void lifecycleStatusChanged(LifecycleStatus before, LifecycleStatus after) {
	getDelegate().lifecycleStatusChanged(before, after);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#
     * findComponentsOfType(com
     * .sitewhere.spi.server.lifecycle.LifecycleComponentType)
     */
    @Override
    public List<ILifecycleComponent> findComponentsOfType(LifecycleComponentType type) throws SiteWhereException {
	return getDelegate().findComponentsOfType(type);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public LocLogger getLogger() {
	return getDelegate().getLogger();
    }

    /*
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getComponentState()
     */
    @Override
    public ILifecycleComponentState getComponentState() {
	return getDelegate().getComponentState();
    }

    /**
     * Access the delegate instance.
     * 
     * @return
     */
    public T getDelegate() {
	return delegate;
    }

    public void setDelegate(T delegate) {
	this.delegate = delegate;
    }
}