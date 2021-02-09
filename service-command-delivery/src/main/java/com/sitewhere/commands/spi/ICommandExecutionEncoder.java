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
 * Encodes an {@link IDeviceCommandExecution} into a format that can be
 * transmitted.
 * 
 * @param <T>
 *                format for encoded command. Must be compatible with the
 *                {@link ICommandDeliveryProvider} that will deliver the
 *                command.
 */
public interface ICommandExecutionEncoder<T> extends ITenantEngineLifecycleComponent {

    /**
     * Encodes a command execution.
     * 
     * @param command
     * @param nested
     * @param assignments
     * @return
     * @throws SiteWhereException
     */
    public T encode(IDeviceCommandExecution command, IDeviceNestingContext nested,
	    List<? extends IDeviceAssignment> assignments) throws SiteWhereException;

    /**
     * Encodes a SiteWhere system command.
     * 
     * @param command
     * @param nested
     * @param assignments
     * @return
     * @throws SiteWhereException
     */
    public T encodeSystemCommand(ISystemCommand command, IDeviceNestingContext nested,
	    List<? extends IDeviceAssignment> assignments) throws SiteWhereException;
}