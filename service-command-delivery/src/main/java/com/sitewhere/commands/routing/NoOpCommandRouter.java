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
package com.sitewhere.commands.routing;

import java.util.Collections;
import java.util.List;

import com.sitewhere.commands.spi.ICommandDestination;
import com.sitewhere.commands.spi.IOutboundCommandRouter;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.command.ISystemCommand;

/**
 * Implementation of {@link IOutboundCommandRouter} that does nothing with
 * commands. Used as default for situations where command routing is not needed.
 */
public class NoOpCommandRouter extends OutboundCommandRouter {

    /*
     * @see
     * com.sitewhere.commands.spi.IOutboundCommandRouter#getDestinationsFor(com.
     * sitewhere.spi.device.command.IDeviceCommandExecution,
     * com.sitewhere.spi.device.IDeviceNestingContext, java.util.List)
     */
    @Override
    public List<ICommandDestination<?, ?>> getDestinationsFor(IDeviceCommandExecution execution,
	    IDeviceNestingContext nesting, List<? extends IDeviceAssignment> assignments) throws SiteWhereException {
	return Collections.emptyList();
    }

    /*
     * @see
     * com.sitewhere.commands.spi.IOutboundCommandRouter#getDestinationsFor(com.
     * sitewhere.spi.device.command.ISystemCommand,
     * com.sitewhere.spi.device.IDeviceNestingContext, java.util.List)
     */
    @Override
    public List<ICommandDestination<?, ?>> getDestinationsFor(ISystemCommand command, IDeviceNestingContext nesting,
	    List<? extends IDeviceAssignment> assignments) throws SiteWhereException {
	return Collections.emptyList();
    }
}