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
package com.sitewhere.instance.configuration;

import com.sitewhere.spi.microservice.multitenant.ITenantEngineConfiguration;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Maps instance management YAML configuration to objects.
 */
@RegisterForReflection
public class InstanceManagementTenantConfiguration implements ITenantEngineConfiguration {

    /** Number of pipeline history entries to keep in memory */
    private int eventPipelineHistoryLength;

    public int getEventPipelineHistoryLength() {
	return eventPipelineHistoryLength;
    }

    public void setEventPipelineHistoryLength(int eventPipelineHistoryLength) {
	this.eventPipelineHistoryLength = eventPipelineHistoryLength;
    }
}
