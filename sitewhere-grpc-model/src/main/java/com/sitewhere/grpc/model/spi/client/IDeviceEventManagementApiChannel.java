package com.sitewhere.grpc.model.spi.client;

import com.sitewhere.grpc.model.spi.IApiChannel;
import com.sitewhere.spi.device.event.IDeviceEventManagement;

/**
 * Provides an {@link IApiChannel} that supplies the
 * {@link IDeviceEventManagement}. API.
 * 
 * @author Derek
 */
public interface IDeviceEventManagementApiChannel extends IDeviceEventManagement, IApiChannel {
}