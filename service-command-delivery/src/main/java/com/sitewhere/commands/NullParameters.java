/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.commands.spi.ICommandDeliveryParameterExtractor;
import com.sitewhere.commands.spi.ICommandDeliveryProvider;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Placeholder object for {@link ICommandDeliveryProvider} that do not require
 * parameters.
 * 
 * @author Derek
 */
public class NullParameters {

    /**
     * Implementation of {@link ICommandDeliveryParameterExtractor} that returns
     * {@link NullParameters}.
     * 
     * @author Derek
     */
    public static class Extractor extends TenantEngineLifecycleComponent
	    implements ICommandDeliveryParameterExtractor<NullParameters> {

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

	/** Value to be returned */
	private NullParameters parameters = new NullParameters();

	public Extractor() {
	    super(LifecycleComponentType.CommandParameterExtractor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.
	 * ICommandDeliveryParameterExtractor#
	 * extractDeliveryParameters(com.sitewhere.spi.device.
	 * IDeviceNestingContext, com.sitewhere.spi.device.IDeviceAssignment,
	 * com.sitewhere.spi.device.command.IDeviceCommandExecution)
	 */
	@Override
	public NullParameters extractDeliveryParameters(IDeviceNestingContext nesting, IDeviceAssignment assignment,
		IDeviceCommandExecution execution) throws SiteWhereException {
	    return parameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
	    return LOGGER;
	}
    }
}