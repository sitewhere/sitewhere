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

import java.util.List;
import java.util.Map;

import com.sitewhere.commands.configuration.extractors.coap.MetadataCoapParameterExtractorConfiguration;
import com.sitewhere.commands.spi.ICommandDeliveryParameterExtractor;
import com.sitewhere.commands.spi.ICommandDestination;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Pulls CoAP parameters from device metadata.
 */
public class MetadataCoapParameterExtractor extends TenantEngineLifecycleComponent
	implements ICommandDeliveryParameterExtractor<CoapParameters> {

    private MetadataCoapParameterExtractorConfiguration configuration;

    public MetadataCoapParameterExtractor(MetadataCoapParameterExtractorConfiguration configuration) {
	super(LifecycleComponentType.CommandParameterExtractor);
	this.configuration = configuration;
    }

    /*
     * @see com.sitewhere.commands.spi.ICommandDeliveryParameterExtractor#
     * extractDeliveryParameters(com.sitewhere.commands.spi.ICommandDestination,
     * com.sitewhere.spi.device.IDeviceNestingContext, java.util.List,
     * com.sitewhere.spi.device.command.IDeviceCommandExecution)
     */
    @Override
    public CoapParameters extractDeliveryParameters(ICommandDestination<?, ?> destination,
	    IDeviceNestingContext nesting, List<? extends IDeviceAssignment> assignments,
	    IDeviceCommandExecution execution) throws SiteWhereException {
	// Load hostname and port from device metadata.
	Map<String, String> deviceMeta = nesting.getGateway().getMetadata();
	String hostname = deviceMeta.get(getConfiguration().getHostnameMetadataField());
	String port = deviceMeta.get(getConfiguration().getPortMetadataField());

	// Load url and method from command metadata.
	Map<String, String> commandMeta = execution.getCommand().getMetadata();
	String url = commandMeta.get(getConfiguration().getUrlMetadataField());
	String method = commandMeta.get(getConfiguration().getMethodMetadataField());

	CoapParameters coap = new CoapParameters();
	coap.setHostname(hostname);
	if (hostname == null) {
	    throw new SiteWhereException("Hostname not found in device metadata. Unable to deliver command.");
	}
	if (port != null) {
	    coap.setPort(Integer.parseInt(port));
	}
	if (url != null) {
	    coap.setUrl(url);
	}
	if (method != null) {
	    coap.setMethod(method);
	}
	return coap;
    }

    protected MetadataCoapParameterExtractorConfiguration getConfiguration() {
	return configuration;
    }
}