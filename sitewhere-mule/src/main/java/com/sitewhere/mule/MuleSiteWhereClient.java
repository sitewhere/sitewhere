/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mule;

import java.util.List;

import org.springframework.http.converter.HttpMessageConverter;

import com.sitewhere.mule.spring.MappingJackson2HttpMessageConverter;
import com.sitewhere.rest.client.SiteWhereClient;

/**
 * Customized version of {@link SiteWhereClient} that hacks around classpath issues when
 * running inside Mule.
 * 
 * @author Derek
 */
public class MuleSiteWhereClient extends SiteWhereClient {

	public MuleSiteWhereClient(String url, String username, String password) {
		super(url, username, password);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.rest.service.SiteWhereClient#addMessageConverters(java.util.List)
	 */
	protected void addMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new MappingJackson2HttpMessageConverter());
	}
}