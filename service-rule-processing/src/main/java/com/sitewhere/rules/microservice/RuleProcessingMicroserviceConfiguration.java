/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rules.microservice;

import com.sitewhere.rules.spi.microservice.IRuleProcessingMicroservice;

/**
 * Spring bean configuration for microservice.
 */
public class RuleProcessingMicroserviceConfiguration {

    public IRuleProcessingMicroservice ruleProcessingMicroservice() {
	return new RuleProcessingMicroservice();
    }
}