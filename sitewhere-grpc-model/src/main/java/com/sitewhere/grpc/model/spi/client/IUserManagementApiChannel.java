package com.sitewhere.grpc.model.spi.client;

import com.sitewhere.grpc.model.spi.IApiChannel;
import com.sitewhere.spi.user.IUserManagement;

/**
 * Provides an {@link IApiChannel} that supplies the {@link IUserManagement}
 * API.
 * 
 * @author Derek
 */
public interface IUserManagementApiChannel extends IUserManagement, IApiChannel {
}