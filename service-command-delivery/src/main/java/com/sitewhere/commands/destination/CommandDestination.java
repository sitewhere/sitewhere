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
package com.sitewhere.commands.destination;

import java.util.List;

import com.sitewhere.commands.spi.ICommandDeliveryParameterExtractor;
import com.sitewhere.commands.spi.ICommandDeliveryProvider;
import com.sitewhere.commands.spi.ICommandDestination;
import com.sitewhere.commands.spi.ICommandExecutionEncoder;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.command.ISystemCommand;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Default implementation of {@link ICommandDestination}.
 * 
 * @param <T>
 */
public class CommandDestination<T, P> extends TenantEngineLifecycleComponent implements ICommandDestination<T, P> {

    /** Unique destination id */
    private String destinationId;

    /** Configured command execution encoder */
    private ICommandExecutionEncoder<T> commandExecutionEncoder;

    /** Configured command delivery parameter extractor */
    private ICommandDeliveryParameterExtractor<P> commandDeliveryParameterExtractor;

    /** Configured command delivery provider */
    private ICommandDeliveryProvider<T, P> commandDeliveryProvider;

    public CommandDestination() {
	super(LifecycleComponentType.CommandDestination);
    }

    /*
     * @see
     * com.sitewhere.commands.spi.ICommandDestination#deliverCommand(com.sitewhere.
     * spi.device.command.IDeviceCommandExecution,
     * com.sitewhere.spi.device.IDeviceNestingContext, java.util.List)
     */
    @Override
    public void deliverCommand(IDeviceCommandExecution execution, IDeviceNestingContext nesting,
	    List<? extends IDeviceAssignment> assignments) throws SiteWhereException {
	T encoded = getCommandExecutionEncoder().encode(execution, nesting, assignments);
	if (encoded != null) {
	    P params = getCommandDeliveryParameterExtractor().extractDeliveryParameters(this, nesting, assignments,
		    execution);
	    getCommandDeliveryProvider().deliver(nesting, assignments, execution, encoded, params);
	} else {
	    getLogger().info("Skipping command delivery. Encoder returned null.");
	}
    }

    /*
     * @see com.sitewhere.commands.spi.ICommandDestination#deliverSystemCommand(com.
     * sitewhere.spi.device.command.ISystemCommand,
     * com.sitewhere.spi.device.IDeviceNestingContext, java.util.List)
     */
    @Override
    public void deliverSystemCommand(ISystemCommand command, IDeviceNestingContext nesting,
	    List<? extends IDeviceAssignment> assignments) throws SiteWhereException {
	T encoded = getCommandExecutionEncoder().encodeSystemCommand(command, nesting, assignments);
	if (encoded != null) {
	    P params = getCommandDeliveryParameterExtractor().extractDeliveryParameters(this, nesting, assignments,
		    null);
	    getCommandDeliveryProvider().deliverSystemCommand(nesting, assignments, encoded, params);
	} else {
	    getLogger().info("Skipping system command delivery. Encoder returned null.");
	}
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Composite step for initializing processing strategy.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize execution encoder.
	init.addInitializeStep(this, getCommandExecutionEncoder(), true);

	// Initialize parameter extractor.
	init.addInitializeStep(this, getCommandDeliveryParameterExtractor(), true);

	// Initialize delivery provider.
	init.addInitializeStep(this, getCommandDeliveryProvider(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Composite step for starting processing strategy.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getComponentName());

	// Start execution encoder.
	start.addStartStep(this, getCommandExecutionEncoder(), true);

	// Start parameter extractor.
	start.addStartStep(this, getCommandDeliveryParameterExtractor(), true);

	// Start delivery provider.
	start.addStartStep(this, getCommandDeliveryProvider(), true);

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#getComponentName()
     */
    @Override
    public String getComponentName() {
	return getClass().getSimpleName() + " (" + getDestinationId() + ")";
    }

    /*
     * @see
     * com.sitewhere.microservice.lifecycle.LifecycleComponent#stop(com.sitewhere.
     * spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Composite step for stopping processing strategy.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getComponentName());

	// Stop delivery provider.
	stop.addStopStep(this, getCommandDeliveryProvider());

	// Stop parameter extractor.
	stop.addStopStep(this, getCommandDeliveryParameterExtractor());

	// Stop execution encoder.
	stop.addStopStep(this, getCommandExecutionEncoder());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.ICommandDestination#
     * getDestinationId()
     */
    public String getDestinationId() {
	return destinationId;
    }

    public void setDestinationId(String destinationId) {
	this.destinationId = destinationId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.ICommandDestination#
     * getCommandExecutionEncoder ()
     */
    public ICommandExecutionEncoder<T> getCommandExecutionEncoder() {
	return commandExecutionEncoder;
    }

    public void setCommandExecutionEncoder(ICommandExecutionEncoder<T> commandExecutionEncoder) {
	this.commandExecutionEncoder = commandExecutionEncoder;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.ICommandDestination#
     * getCommandDeliveryParameterExtractor()
     */
    public ICommandDeliveryParameterExtractor<P> getCommandDeliveryParameterExtractor() {
	return commandDeliveryParameterExtractor;
    }

    public void setCommandDeliveryParameterExtractor(
	    ICommandDeliveryParameterExtractor<P> commandDeliveryParameterExtractor) {
	this.commandDeliveryParameterExtractor = commandDeliveryParameterExtractor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.ICommandDestination#
     * getCommandDeliveryProvider ()
     */
    public ICommandDeliveryProvider<T, P> getCommandDeliveryProvider() {
	return commandDeliveryProvider;
    }

    public void setCommandDeliveryProvider(ICommandDeliveryProvider<T, P> commandDeliveryProvider) {
	this.commandDeliveryProvider = commandDeliveryProvider;
    }
}