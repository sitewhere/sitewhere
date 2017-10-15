/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.microservice;

import com.sitewhere.device.grpc.DeviceManagementImpl;
import com.sitewhere.device.spi.microservice.IDeviceManagementTenantEngine;
import com.sitewhere.grpc.service.DeviceManagementGrpc;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.microservice.spi.multitenant.IMultitenantMicroservice;
import com.sitewhere.microservice.spi.spring.DeviceManagementBeans;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements device
 * management functionality.
 * 
 * @author Derek
 */
public class DeviceManagementTenantEngine extends MicroserviceTenantEngine implements IDeviceManagementTenantEngine {

    /** Device management persistence API */
    private IDeviceManagement deviceManagement;

    /** Responds to device management GRPC requests */
    private DeviceManagementGrpc.DeviceManagementImplBase deviceManagementImpl;

    public DeviceManagementTenantEngine(IMultitenantMicroservice<IDeviceManagementTenantEngine> microservice,
	    ITenant tenant) {
	super(microservice, tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.deviceManagement = (IDeviceManagement) getModuleContext()
		.getBean(DeviceManagementBeans.BEAN_DEVICE_MANAGEMENT);
	this.deviceManagementImpl = new DeviceManagementImpl(getDeviceManagement());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * tenantStart(com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStart(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * tenantStop(com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    public DeviceManagementGrpc.DeviceManagementImplBase getDeviceManagementImpl() {
	return deviceManagementImpl;
    }

    public void setDeviceManagementImpl(DeviceManagementGrpc.DeviceManagementImplBase deviceManagementImpl) {
	this.deviceManagementImpl = deviceManagementImpl;
    }
}