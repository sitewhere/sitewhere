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
package com.sitewhere.commands.destination.coap;

import com.sitewhere.commands.configuration.destinations.coap.CoapConfiguration;
import com.sitewhere.commands.configuration.extractors.coap.MetadataCoapParameterExtractorConfiguration;
import com.sitewhere.commands.destination.CommandDestination;
import com.sitewhere.commands.encoding.json.JsonCommandExecutionEncoder;

/**
 * Command destination that makes a CoAP client request to send command data.
 */
public class CoapCommandDestination extends CommandDestination<byte[], CoapParameters> {

    /** Configuration */
    private CoapConfiguration configuration;

    public CoapCommandDestination(CoapConfiguration configuration) {
	this.configuration = configuration;

	setCommandExecutionEncoder(new JsonCommandExecutionEncoder());
	setCommandDeliveryParameterExtractor(
		new MetadataCoapParameterExtractor(new MetadataCoapParameterExtractorConfiguration(this)));
	setCommandDeliveryProvider(new CoapCommandDeliveryProvider());
    }

    protected CoapConfiguration getConfiguration() {
	return configuration;
    }
}