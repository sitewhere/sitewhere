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
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Handles delivery of encoded command information on an underlying transport.
 * 
 * @param <T>
 *                type of data that was encoded by the
 *                {@link ICommandExecutionEncoder}/
 * @param <P>
 *                parameters specific to the delivery provider
 */
public interface ICommandDeliveryProvider<T, P> extends ITenantEngineLifecycleComponent {

    /**
     * Deliver the given encoded invocation. The device, active assignments and
     * invocation details are included since they may contain metadata important to
     * the delivery mechanism.
     * 
     * @param nested
     * @param assignments
     * @param execution
     * @param encoded
     * @param parameters
     * @throws SiteWhereException
     */
    public void deliver(IDeviceNestingContext nested, List<? extends IDeviceAssignment> assignments,
	    IDeviceCommandExecution execution, T encoded, P parameters) throws SiteWhereException;

    /**
     * Delivers a system command.
     * 
     * @param nested
     * @param assignments
     * @param encoded
     * @param parameters
     * @throws SiteWhereException
     */
    public void deliverSystemCommand(IDeviceNestingContext nested, List<? extends IDeviceAssignment> assignments,
	    T encoded, P parameters) throws SiteWhereException;
}