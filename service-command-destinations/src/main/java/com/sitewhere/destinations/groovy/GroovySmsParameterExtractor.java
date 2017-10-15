/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.destinations.groovy;

import com.sitewhere.device.communication.sms.SmsParameters;

/**
 * Command extractor that expects an {@link SmsParameters} object to be returned
 * from the Groovy script.
 * 
 * @author Derek
 */
public class GroovySmsParameterExtractor extends GroovyParameterExtractor<SmsParameters> {
}