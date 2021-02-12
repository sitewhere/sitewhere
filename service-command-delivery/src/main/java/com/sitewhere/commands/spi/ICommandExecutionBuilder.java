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
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleComponent;

/**
 * Used to build an {@link IDeviceCommandExecution} from an
 * {@link IDeviceCommand} and a {@link IDeviceCommandInvocation}.
 */
public interface ICommandExecutionBuilder extends ILifecycleComponent {

    /**
     * Create an execution from a command and invocation details.
     * 
     * @param command
     * @param invocation
     * @return
     * @throws SiteWhereException
     */
    public IDeviceCommandExecution createExecution(IDeviceCommand command, IDeviceCommandInvocation invocation)
	    throws SiteWhereException;
}