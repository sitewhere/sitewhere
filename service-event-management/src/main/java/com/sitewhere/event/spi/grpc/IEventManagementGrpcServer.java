package com.sitewhere.event.spi.grpc;

import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Binds to a port and listens for event management GRPC requests.
 * 
 * @author Derek
 */
public interface IEventManagementGrpcServer extends ITenantLifecycleComponent {
}