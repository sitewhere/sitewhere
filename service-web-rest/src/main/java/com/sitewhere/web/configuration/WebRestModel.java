/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.configuration;

import com.sitewhere.configuration.model.MicroserviceConfigurationModel;
import com.sitewhere.spi.microservice.IMicroservice;

/**
 * Configuration model for web/REST microservice.
 * 
 * @author Derek
 */
public class WebRestModel extends MicroserviceConfigurationModel {

    public WebRestModel(IMicroservice microservice) {
	super(microservice, null, null, null);
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.IConfigurationModel#
     * getDefaultXmlNamespace()
     */
    @Override
    public String getDefaultXmlNamespace() {
	return "http://sitewhere.io/schema/sitewhere/microservice/web-rest";
    }

    /*
     * @see
     * com.sitewhere.configuration.model.MicroserviceConfigurationModel#addElements(
     * )
     */
    @Override
    public void addElements() {
    }
}