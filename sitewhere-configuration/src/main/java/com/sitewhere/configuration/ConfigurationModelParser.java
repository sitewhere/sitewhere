/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.configuration.model.ConfigurationModel;
import com.sitewhere.spi.SiteWhereException;

/**
 * Allows a configuration model to marshaled to and from JSON.
 * 
 * @author Derek
 */
public class ConfigurationModelParser {

    /**
     * Unmarshal a {@link ConfigurationModel} from a JSON byte array.
     * 
     * @param json
     * @return
     * @throws SiteWhereException
     */
    public static ConfigurationModel unmarshalModel(byte[] json) throws SiteWhereException {
	return MarshalUtils.unmarshalJson(json, ConfigurationModel.class);
    }

    /**
     * Marshal a {@link ConfigurationModel} to a JSON byte array.
     * 
     * @param model
     * @return
     * @throws SiteWhereException
     */
    public static byte[] marshalModel(ConfigurationModel model) throws SiteWhereException {
	return MarshalUtils.marshalJson(model);
    }
}