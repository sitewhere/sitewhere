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
 * Extracts delivery parameters from
 * 
 * @param <T>
 */
public interface ICommandDeliveryParameterExtractor<T> extends ITenantEngineLifecycleComponent {

    /**
     * Extract required delivery parameters from the given sources.
     * 
     * @param destination
     * @param nesting
     * @param assignments
     * @param execution
     * @return
     * @throws SiteWhereException
     */
    public T extractDeliveryParameters(ICommandDestination<?, ?> destination, IDeviceNestingContext nesting,
	    List<? extends IDeviceAssignment> assignments, IDeviceCommandExecution execution) throws SiteWhereException;
}