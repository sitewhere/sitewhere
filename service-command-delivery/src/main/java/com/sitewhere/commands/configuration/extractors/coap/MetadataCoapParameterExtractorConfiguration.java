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
package com.sitewhere.commands.configuration.extractors.coap;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.commands.configuration.extractors.ParameterExtractorConfiguration;
import com.sitewhere.commands.destination.coap.MetadataCoapParameterExtractor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Configuration parameters for {@link MetadataCoapParameterExtractor}.
 */
public class MetadataCoapParameterExtractorConfiguration extends ParameterExtractorConfiguration {

    /** Default metadata field for remote hostname */
    public static final String DEFAULT_HOSTNAME_METADATA = "coap_hostname";

    /** Default metadata field for remote port */
    public static final String DEFAULT_PORT_METADATA = "coap_port";

    /** Default metadata field for CoAP URL */
    public static final String DEFAULT_URL_METADATA = "coap_url";

    /** Default metadata field for CoAP invocation method */
    public static final String DEFAULT_METHOD_METADATA = "coap_method";

    /** Hostname metadata field name */
    private String hostnameMetadataField;

    /** Port metadata field name */
    private String portMetadataField;

    /** CoAP URL metadata field name */
    private String urlMetadataField;

    /** CoAP invocation method metadata field name */
    private String methodMetadataField;

    public MetadataCoapParameterExtractorConfiguration(ITenantEngineLifecycleComponent component) {
	super(component);
    }

    /*
     * @see com.sitewhere.commands.configuration.extractors.
     * ParameterExtractorConfiguration#loadFrom(com.fasterxml.jackson.databind.
     * JsonNode)
     */
    @Override
    public void loadFrom(JsonNode json) throws SiteWhereException {
	this.hostnameMetadataField = configurableString("hostnameMetadataField", json, DEFAULT_HOSTNAME_METADATA);
	this.portMetadataField = configurableString("portMetadataField", json, DEFAULT_PORT_METADATA);
	this.urlMetadataField = configurableString("urlMetadataField", json, DEFAULT_URL_METADATA);
	this.methodMetadataField = configurableString("methodMetadataField", json, DEFAULT_METHOD_METADATA);
    }

    public String getHostnameMetadataField() {
	return hostnameMetadataField;
    }

    public void setHostnameMetadataField(String hostnameMetadataField) {
	this.hostnameMetadataField = hostnameMetadataField;
    }

    public String getPortMetadataField() {
	return portMetadataField;
    }

    public void setPortMetadataField(String portMetadataField) {
	this.portMetadataField = portMetadataField;
    }

    public String getUrlMetadataField() {
	return urlMetadataField;
    }

    public void setUrlMetadataField(String urlMetadataField) {
	this.urlMetadataField = urlMetadataField;
    }

    public String getMethodMetadataField() {
	return methodMetadataField;
    }

    public void setMethodMetadataField(String methodMetadataField) {
	this.methodMetadataField = methodMetadataField;
    }
}
