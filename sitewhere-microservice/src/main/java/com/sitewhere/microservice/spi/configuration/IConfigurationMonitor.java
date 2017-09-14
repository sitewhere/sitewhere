package com.sitewhere.microservice.spi.configuration;

import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Monitors configuration nodes in Zk and allows microservices to respond to
 * configuration changes.
 * 
 * @author Derek
 */
public interface IConfigurationMonitor extends ILifecycleComponent {
}