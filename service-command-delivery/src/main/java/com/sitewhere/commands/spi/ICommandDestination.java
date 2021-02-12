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
package com.sitewhere.commands.spi;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.command.ISystemCommand;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Delivers commands to devices by encoding the commands, finding the list of
 * target devices, then using a delivery provider to send the encoded commands.
 *
 * @param <T>
 * @param <P>
 */
public interface ICommandDestination<T, P> extends ITenantEngineLifecycleComponent {

    /**
     * Get unique identifier for destination.
     * 
     * @return
     */
    public String getDestinationId();

    /**
     * Gets the configured command execution encoder.
     * 
     * @return
     */
    public ICommandExecutionEncoder<T> getCommandExecutionEncoder();

    /**
     * Get the configured command delivery parameter extractor.
     * 
     * @return
     */
    public ICommandDeliveryParameterExtractor<P> getCommandDeliveryParameterExtractor();

    /**
     * Gets the configured command delivery provider.
     * 
     * @return
     */
    public ICommandDeliveryProvider<T, P> getCommandDeliveryProvider();

    /**
     * Deliver a command.
     * 
     * @param execution
     * @param nesting
     * @param assignments
     * @throws SiteWhereException
     */
    public void deliverCommand(IDeviceCommandExecution execution, IDeviceNestingContext nesting,
	    List<? extends IDeviceAssignment> assignments) throws SiteWhereException;

    /**
     * Deliver a system command.
     * 
     * @param command
     * @param nesting
     * @param assignments
     * @throws SiteWhereException
     */
    public void deliverSystemCommand(ISystemCommand command, IDeviceNestingContext nesting,
	    List<? extends IDeviceAssignment> assignments) throws SiteWhereException;
}