/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands;

import java.util.List;

import com.sitewhere.commands.spi.ICommandDeliveryParameterExtractor;
import com.sitewhere.commands.spi.ICommandDeliveryProvider;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Placeholder object for {@link ICommandDeliveryProvider} that do not require
 * parameters.
 */
public class NullParameters {

    /**
     * Implementation of {@link ICommandDeliveryParameterExtractor} that returns
     * {@link NullParameters}.
     */
    public static class Extractor extends TenantEngineLifecycleComponent
	    implements ICommandDeliveryParameterExtractor<NullParameters> {

	/** Value to be returned */
	private NullParameters parameters = new NullParameters();

	public Extractor() {
	    super(LifecycleComponentType.CommandParameterExtractor);
	}

	/*
	 * @see com.sitewhere.commands.spi.ICommandDeliveryParameterExtractor#
	 * extractDeliveryParameters(com.sitewhere.spi.device.IDeviceNestingContext,
	 * java.util.List, com.sitewhere.spi.device.command.IDeviceCommandExecution)
	 */
	@Override
	public NullParameters extractDeliveryParameters(IDeviceNestingContext nesting,
		List<IDeviceAssignment> assignments, IDeviceCommandExecution execution) throws SiteWhereException {
	    return parameters;
	}
    }
}