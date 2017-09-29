package com.sitewhere.grpc.model.spi.client;

import com.sitewhere.grpc.model.spi.IApiChannel;
import com.sitewhere.spi.tenant.ITenantManagement;

/**
 * Provides an {@link IApiChannel} that supplies the {@link ITenantManagement}.
 * API.
 * 
 * @author Derek
 */
public interface ITenantManagementApiChannel extends ITenantManagement, IApiChannel {
}