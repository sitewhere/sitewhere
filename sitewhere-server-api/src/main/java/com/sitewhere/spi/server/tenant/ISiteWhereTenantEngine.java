/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server.tenant;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.command.ICommandResponse;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.communication.IDeviceCommunication;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.search.external.ISearchProviderManager;
import com.sitewhere.spi.server.ITenantRuntimeState;
import com.sitewhere.spi.server.lifecycle.ILifecycleHierarchyRoot;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * A SiteWhere tenant engine wraps up the processing pipeline and data storage
 * for a single SiteWhere tenant.
 * 
 * @author Derek
 */
public interface ISiteWhereTenantEngine extends ITenantLifecycleComponent, ILifecycleHierarchyRoot {

    /**
     * Get the device management implementation.
     * 
     * @return
     */
    public IDeviceManagement getDeviceManagement();

    /**
     * Get the device event management implementation.
     * 
     * @return
     */
    public IDeviceEventManagement getDeviceEventManagement();

    /**
     * Get the asset management implementation.
     * 
     * @return
     */
    public IAssetManagement getAssetManagement();

    /**
     * Get the schedule management implementation.
     * 
     * @return
     * @throws SiteWhereException
     */
    public IScheduleManagement getScheduleManagement() throws SiteWhereException;

    /**
     * Get the device communication subsystem implementation.
     * 
     * @return
     */
    public IDeviceCommunication getDeviceCommunication();

    /**
     * Get the asset modules manager instance.
     * 
     * @return
     */
    public IAssetModuleManager getAssetModuleManager();

    /**
     * Get the search provider manager implementation.
     * 
     * @return
     */
    public ISearchProviderManager getSearchProviderManager();

    /**
     * Get current runtime state of engine.
     * 
     * @return
     */
    public ITenantRuntimeState getEngineState();

    /**
     * Issue a command to the tenant engine.
     * 
     * @param command
     * @param monitor
     * @return
     * @throws SiteWhereException
     */
    public ICommandResponse issueCommand(String command, ILifecycleProgressMonitor monitor) throws SiteWhereException;
}