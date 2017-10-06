package com.sitewhere.grpc.model.spi.client;

import com.sitewhere.grpc.model.spi.IApiChannel;
import com.sitewhere.spi.device.IDeviceManagement;

/**
 * Provides an {@link IApiChannel} that supplies the {@link IDeviceManagement}.
 * API.
 * 
 * @author Derek
 */
public interface IDeviceManagementApiChannel extends IDeviceManagement, IApiChannel {
}