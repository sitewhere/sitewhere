/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server;

import java.util.List;
import java.util.Map;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.sitewhere.server.lifecycle.LifecycleComponentDecorator;
import com.sitewhere.spi.ServerStartupException;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.configuration.IGlobalConfigurationResolver;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.communication.IDeviceCommunication;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IEventProcessing;
import com.sitewhere.spi.resource.IResourceManager;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.scheduling.IScheduleManager;
import com.sitewhere.spi.search.external.ISearchProviderManager;
import com.sitewhere.spi.server.ISiteWhereServer;
import com.sitewhere.spi.server.ISiteWhereServerRuntime;
import com.sitewhere.spi.server.ISiteWhereServerState;
import com.sitewhere.spi.server.groovy.IGroovyConfiguration;
import com.sitewhere.spi.server.groovy.ITenantGroovyConfiguration;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.tenant.ISiteWhereTenantEngine;
import com.sitewhere.spi.system.IVersion;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.spi.user.IUserManagement;

/**
 * Wraps an {@link ISiteWhereServer} implementation so new functionality can be
 * triggered by API calls withhout changing core server logic.
 * 
 * @author Derek
 */
public class SiteWhereServerDecorator extends LifecycleComponentDecorator implements ISiteWhereServer {

    /** Delegate instance being wrapped */
    private ISiteWhereServer server;

    public SiteWhereServerDecorator(ISiteWhereServer server) {
	super(server);
	this.server = server;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getVersion()
     */
    @Override
    public IVersion getVersion() {
	return server.getVersion();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereServer#getConfigurationParserClassname
     * ()
     */
    @Override
    public String getConfigurationParserClassname() {
	return server.getConfigurationParserClassname();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#
     * getTenantConfigurationParserClassname()
     */
    @Override
    public String getTenantConfigurationParserClassname() {
	return server.getTenantConfigurationParserClassname();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getServerState()
     */
    @Override
    public ISiteWhereServerState getServerState() {
	return server.getServerState();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getServerState(boolean)
     */
    @Override
    public ISiteWhereServerRuntime getServerRuntimeInformation(boolean includeHistorical) throws SiteWhereException {
	return server.getServerRuntimeInformation(includeHistorical);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponentDecorator#initialize(com
     * .sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	server.initialize(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getServerStartupError()
     */
    @Override
    public ServerStartupException getServerStartupError() {
	return server.getServerStartupError();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#setServerStartupError(com.
     * sitewhere.spi .ServerStartupException)
     */
    @Override
    public void setServerStartupError(ServerStartupException e) {
	server.setServerStartupError(e);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getGroovyConfiguration()
     */
    @Override
    public IGroovyConfiguration getGroovyConfiguration() {
	return server.getGroovyConfiguration();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereServer#getBootstrapResourceManager()
     */
    @Override
    public IResourceManager getBootstrapResourceManager() {
	return server.getBootstrapResourceManager();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereServer#getRuntimeResourceManager()
     */
    @Override
    public IResourceManager getRuntimeResourceManager() {
	return server.getRuntimeResourceManager();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getConfigurationResolver()
     */
    @Override
    public IGlobalConfigurationResolver getConfigurationResolver() {
	return server.getConfigurationResolver();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereServer#getTenantByAuthToken(java.lang.
     * String)
     */
    @Override
    public ITenant getTenantByAuthToken(String authToken) throws SiteWhereException {
	return server.getTenantByAuthToken(authToken);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereServer#getAuthorizedTenants(java.lang.
     * String, boolean)
     */
    @Override
    public List<ITenant> getAuthorizedTenants(String userId, boolean requireStarted) throws SiteWhereException {
	return server.getAuthorizedTenants(userId, requireStarted);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getTenantEnginesById()
     */
    @Override
    public Map<String, ISiteWhereTenantEngine> getTenantEnginesById() {
	return server.getTenantEnginesById();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getTenantEngine(java.lang.
     * String)
     */
    @Override
    public ISiteWhereTenantEngine getTenantEngine(String tenantId) throws SiteWhereException {
	return server.getTenantEngine(tenantId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereServer#onTenantUpdated(com.sitewhere.
     * spi.tenant.ITenant)
     */
    @Override
    public void onTenantUpdated(ITenant tenant) throws SiteWhereException {
	server.onTenantUpdated(tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereServer#onTenantDeleted(com.sitewhere.
     * spi.tenant.ITenant)
     */
    @Override
    public void onTenantDeleted(ITenant tenant) throws SiteWhereException {
	server.onTenantDeleted(tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getUserManagement()
     */
    @Override
    public IUserManagement getUserManagement() {
	return server.getUserManagement();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getTenantManagement()
     */
    @Override
    public ITenantManagement getTenantManagement() {
	return server.getTenantManagement();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getDeviceManagement(com.
     * sitewhere.spi .user.ITenant)
     */
    @Override
    public IDeviceManagement getDeviceManagement(ITenant tenant) throws SiteWhereException {
	return server.getDeviceManagement(tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereServer#getDeviceEventManagement(com.
     * sitewhere .spi.user.ITenant)
     */
    @Override
    public IDeviceEventManagement getDeviceEventManagement(ITenant tenant) throws SiteWhereException {
	return server.getDeviceEventManagement(tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getAssetManagement(com.
     * sitewhere.spi. user.ITenant)
     */
    @Override
    public IAssetManagement getAssetManagement(ITenant tenant) throws SiteWhereException {
	return server.getAssetManagement(tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getScheduleManagement(com.
     * sitewhere.spi .user.ITenant)
     */
    @Override
    public IScheduleManagement getScheduleManagement(ITenant tenant) throws SiteWhereException {
	return server.getScheduleManagement(tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereServer#getDeviceCommunication(com.
     * sitewhere. spi.user.ITenant)
     */
    @Override
    public IDeviceCommunication getDeviceCommunication(ITenant tenant) throws SiteWhereException {
	return server.getDeviceCommunication(tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getEventProcessing(com.
     * sitewhere.spi. user.ITenant)
     */
    @Override
    public IEventProcessing getEventProcessing(ITenant tenant) throws SiteWhereException {
	return server.getEventProcessing(tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getAssetModuleManager(com.
     * sitewhere.spi .user.ITenant)
     */
    @Override
    public IAssetModuleManager getAssetModuleManager(ITenant tenant) throws SiteWhereException {
	return server.getAssetModuleManager(tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereServer#getSearchProviderManager(com.
     * sitewhere .spi.user.ITenant)
     */
    @Override
    public ISearchProviderManager getSearchProviderManager(ITenant tenant) throws SiteWhereException {
	return server.getSearchProviderManager(tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getScheduleManager(com.
     * sitewhere.spi. user.ITenant)
     */
    @Override
    public IScheduleManager getScheduleManager(ITenant tenant) throws SiteWhereException {
	return server.getScheduleManager(tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereServer#getTenantGroovyConfiguration(
     * com.sitewhere.spi.tenant.ITenant)
     */
    @Override
    public ITenantGroovyConfiguration getTenantGroovyConfiguration(ITenant tenant) throws SiteWhereException {
	return server.getTenantGroovyConfiguration(tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#
     * getRegisteredLifecycleComponents()
     */
    @Override
    public List<ILifecycleComponent> getRegisteredLifecycleComponents() {
	return server.getRegisteredLifecycleComponents();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ISiteWhereServer#getLifecycleComponentById(java.
     * lang. String )
     */
    @Override
    public ILifecycleComponent getLifecycleComponentById(String id) {
	return server.getLifecycleComponentById(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getMetricRegistry()
     */
    @Override
    public MetricRegistry getMetricRegistry() {
	return server.getMetricRegistry();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereServer#getHealthCheckRegistry()
     */
    @Override
    public HealthCheckRegistry getHealthCheckRegistry() {
	return server.getHealthCheckRegistry();
    }
}