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
package com.sitewhere.commands;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.commands.spi.ICommandTargetResolver;
import com.sitewhere.commands.spi.microservice.ICommandDeliveryMicroservice;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Uses information in an {@link IDeviceCommandInvocation} to determine a list
 * of target {@link IDeviceAssignment} objects. This implementation returns the
 * {@link IDeviceAssignment} associated with the invocation.
 */
public class DefaultCommandTargetResolver extends TenantEngineLifecycleComponent implements ICommandTargetResolver {

    public DefaultCommandTargetResolver() {
	super(LifecycleComponentType.CommandTargetResolver);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.ICommandTargetResolver#
     * resolveTargets(com .sitewhere.spi.device.event.IDeviceCommandInvocation)
     */
    @Override
    public List<IDeviceAssignment> resolveTargets(IDeviceCommandInvocation invocation) throws SiteWhereException {
	IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignment(invocation.getDeviceAssignmentId());
	List<IDeviceAssignment> results = new ArrayList<IDeviceAssignment>();
	results.add(assignment);
	return results;
    }

    private IDeviceManagement getDeviceManagement() {
	return ((ICommandDeliveryMicroservice) getMicroservice()).getDeviceManagement();
    }
}