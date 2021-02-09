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
 * Routes commands to one or more {@link ICommandDestination} implementations.
 */
public interface IOutboundCommandRouter extends ITenantEngineLifecycleComponent {

    /**
     * Compute list of destinations for the given command invocation.
     * 
     * @param execution
     * @param nesting
     * @param assignments
     * @return
     * @throws SiteWhereException
     */
    public List<ICommandDestination<?, ?>> getDestinationsFor(IDeviceCommandExecution execution,
	    IDeviceNestingContext nesting, List<? extends IDeviceAssignment> assignments) throws SiteWhereException;

    /**
     * Compute list of destinations for the given system command.
     * 
     * @param command
     * @param nesting
     * @param assignments
     * @return
     * @throws SiteWhereException
     */
    public List<ICommandDestination<?, ?>> getDestinationsFor(ISystemCommand command, IDeviceNestingContext nesting,
	    List<? extends IDeviceAssignment> assignments) throws SiteWhereException;
}