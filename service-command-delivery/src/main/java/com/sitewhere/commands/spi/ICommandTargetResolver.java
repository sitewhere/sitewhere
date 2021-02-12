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
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Allows an {@link IDeviceCommandInvocation} to be resolved to one or more
 * {@link IDeviceAssignment} records that should receive the command.
 */
public interface ICommandTargetResolver extends ITenantEngineLifecycleComponent {

    /**
     * Resolves a command invocation to a list of assignments that should receive
     * the command.
     * 
     * @param invocation
     * @return
     * @throws SiteWhereException
     */
    public List<IDeviceAssignment> resolveTargets(IDeviceCommandInvocation invocation) throws SiteWhereException;
}