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
package com.sitewhere.commands.encoding.json;

import java.util.List;

import com.sitewhere.commands.encoding.EncodedCommandExecution;
import com.sitewhere.commands.spi.ICommandExecutionEncoder;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.command.ISystemCommand;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link ICommandExecutionEncoder} that sends commands in
 * JSON format.
 */
public class JsonCommandExecutionEncoder extends TenantEngineLifecycleComponent
	implements ICommandExecutionEncoder<byte[]> {

    public JsonCommandExecutionEncoder() {
	super(LifecycleComponentType.CommandExecutionEncoder);
    }

    /*
     * @see
     * com.sitewhere.commands.spi.ICommandExecutionEncoder#encode(com.sitewhere.spi.
     * device.command.IDeviceCommandExecution,
     * com.sitewhere.spi.device.IDeviceNestingContext, java.util.List)
     */
    @Override
    public byte[] encode(IDeviceCommandExecution command, IDeviceNestingContext nested,
	    List<? extends IDeviceAssignment> assignments) throws SiteWhereException {
	EncodedCommandExecution encoded = new EncodedCommandExecution(command, nested, assignments);
	getLogger().debug("Custom command being encoded:\n\n" + MarshalUtils.marshalJsonAsPrettyString(encoded));
	return MarshalUtils.marshalJson(encoded);
    }

    /*
     * @see
     * com.sitewhere.commands.spi.ICommandExecutionEncoder#encodeSystemCommand(com.
     * sitewhere.spi.device.command.ISystemCommand,
     * com.sitewhere.spi.device.IDeviceNestingContext, java.util.List)
     */
    @Override
    public byte[] encodeSystemCommand(ISystemCommand command, IDeviceNestingContext nested,
	    List<? extends IDeviceAssignment> assignments) throws SiteWhereException {
	EncodedCommandExecution encoded = new EncodedCommandExecution(command, nested, assignments);
	getLogger().debug("System command being encoded:\n\n" + MarshalUtils.marshalJsonAsPrettyString(encoded));
	return MarshalUtils.marshalJson(encoded);
    }
}