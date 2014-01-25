/*
 * DefaultDeviceProvisioning.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.provisioning;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.provisioning.ICommandDeliveryProvider;
import com.sitewhere.spi.device.provisioning.ICommandExecutionBuilder;
import com.sitewhere.spi.device.provisioning.ICommandExecutionEncoder;
import com.sitewhere.spi.device.provisioning.ICommandProcessingStrategy;
import com.sitewhere.spi.device.provisioning.IDeviceProvisioning;

/**
 * Default implementation of the {@link IDeviceProvisioning} interface.
 * 
 * @author Derek
 */
public class DefaultDeviceProvisioning implements IDeviceProvisioning, InitializingBean {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DefaultDeviceProvisioning.class);

	/** Configured command execution builder */
	private ICommandExecutionBuilder commandExecutionBuilder = new DefaultCommandExecutionBuilder();

	/** Configured command execution encoder */
	private ICommandExecutionEncoder commandExecutionEncoder;

	/** Configured command delivery provider */
	private ICommandDeliveryProvider commandDeliveryProvider = new DefaultCommandDeliveryProvider();

	/** Configured command processing strategy */
	private ICommandProcessingStrategy commandProcessingStrategy = new DefaultCommandProcessingStrategy();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		LOGGER.info("Starting device provisioning support ...");
		if (getCommandExecutionBuilder() == null) {
			throw new SiteWhereException("No execution builder configured for provisioning.");
		}
		if (getCommandExecutionEncoder() == null) {
			throw new SiteWhereException("No execution encoder configured for provisioning.");
		}
		if (getCommandDeliveryProvider() == null) {
			throw new SiteWhereException("No delivery provider configured for provisioning.");
		}
		if (getCommandProcessingStrategy() == null) {
			throw new SiteWhereException("No command processing strategy configured for provisioning.");
		}
		LOGGER.info("Started device provisioning support.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.IDeviceProvisioning#deliver(com.sitewhere
	 * .spi.device.event.IDeviceCommandInvocation)
	 */
	@Override
	public void deliver(IDeviceCommandInvocation invocation) throws SiteWhereException {
		getCommandProcessingStrategy().deliver(this, invocation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.IDeviceProvisioning#getCommandExecutionBuilder
	 * ()
	 */
	public ICommandExecutionBuilder getCommandExecutionBuilder() {
		return commandExecutionBuilder;
	}

	public void setCommandExecutionBuilder(ICommandExecutionBuilder commandExecutionBuilder) {
		this.commandExecutionBuilder = commandExecutionBuilder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.IDeviceProvisioning#getCommandExecutionEncoder
	 * ()
	 */
	public ICommandExecutionEncoder getCommandExecutionEncoder() {
		return commandExecutionEncoder;
	}

	public void setCommandExecutionEncoder(ICommandExecutionEncoder commandExecutionEncoder) {
		this.commandExecutionEncoder = commandExecutionEncoder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.IDeviceProvisioning#getCommandDeliveryProvider
	 * ()
	 */
	public ICommandDeliveryProvider getCommandDeliveryProvider() {
		return commandDeliveryProvider;
	}

	public void setCommandDeliveryProvider(ICommandDeliveryProvider commandDeliveryProvider) {
		this.commandDeliveryProvider = commandDeliveryProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.IDeviceProvisioning#getCommandProcessingStrategy
	 * ()
	 */
	public ICommandProcessingStrategy getCommandProcessingStrategy() {
		return commandProcessingStrategy;
	}

	public void setCommandProcessingStrategy(ICommandProcessingStrategy commandProcessingStrategy) {
		this.commandProcessingStrategy = commandProcessingStrategy;
	}
}