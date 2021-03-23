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

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.command.ISystemCommand;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Defines the flow executed for processing a command for delivery.
 */
public interface ICommandProcessingStrategy extends ITenantEngineLifecycleComponent {

    /**
     * Get the {@link ICommandTargetResolver} implementation.
     * 
     * @return
     */
    public ICommandTargetResolver getCommandTargetResolver();

    /**
     * Deliver a command invocation.
     * 
     * @param eventContext
     * @param invocation
     * @throws SiteWhereException
     */
    public void deliverCommand(IDeviceEventContext eventContext, IDeviceCommandInvocation invocation)
	    throws SiteWhereException;

    /**
     * Deliver a system command.
     * 
     * @param eventContext
     * @param command
     * @throws SiteWhereException
     */
    public void deliverSystemCommand(IDeviceEventContext eventContext, ISystemCommand command)
	    throws SiteWhereException;
}