/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.commands;

import java.util.List;

import com.sitewhere.commands.spi.ICommandDeliveryParameterExtractor;
import com.sitewhere.commands.spi.ICommandDeliveryProvider;
import com.sitewhere.commands.spi.ICommandDestination;
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
	 * extractDeliveryParameters(com.sitewhere.commands.spi.ICommandDestination,
	 * com.sitewhere.spi.device.IDeviceNestingContext, java.util.List,
	 * com.sitewhere.spi.device.command.IDeviceCommandExecution)
	 */
	@Override
	public NullParameters extractDeliveryParameters(ICommandDestination<?, ?> destination,
		IDeviceNestingContext nesting, List<? extends IDeviceAssignment> assignments,
		IDeviceCommandExecution execution) throws SiteWhereException {
	    return parameters;
	}
    }
}