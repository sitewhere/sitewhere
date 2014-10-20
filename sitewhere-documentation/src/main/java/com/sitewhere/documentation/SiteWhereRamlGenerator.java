/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.documentation;

import com._8x8.cloud.swagger2raml.RamlGenerator;

/**
 * Generates RAML based on Swagger artifacts from a remote SiteWhere instance.
 * 
 * @author Derek
 */
public class SiteWhereRamlGenerator {

	/**
	 * Executed to generate RAML from Swagger on remote server.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		RamlGenerator.generateFromSwaggerUrl(args[0], args[1]);
	}
}