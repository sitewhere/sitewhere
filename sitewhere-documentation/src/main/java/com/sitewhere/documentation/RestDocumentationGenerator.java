/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.documentation;

import io.swagger.parser.SwaggerParser;

import java.io.File;

import com.wordnik.swagger.codegen.ClientOptInput;
import com.wordnik.swagger.codegen.ClientOpts;
import com.wordnik.swagger.codegen.CodegenConfig;
import com.wordnik.swagger.codegen.DefaultGenerator;
import com.wordnik.swagger.codegen.languages.StaticHtmlGenerator;
import com.wordnik.swagger.models.Swagger;

/**
 * Generates REST documentation.
 * 
 * @author Derek
 */
public class RestDocumentationGenerator extends DefaultGenerator {

	/**
	 * Execution entry point.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			throw new IllegalArgumentException("Expected arguments not passed.");
		}

		// Load Swagger metadata.
		String metadata = args[0];
		System.out.println("Loading Swagger metadata from: " + metadata);

		// Load Swagger metadata.
		String output = args[1];
		System.out.println("Writing generated output to: " + output);

		ClientOptInput input = new ClientOptInput();
		CodegenConfig config = new StaticHtmlGenerator();
		config.setOutputDir(new File(output).getAbsolutePath());
		input.setConfig(config);

		Swagger swagger = new SwaggerParser().read(metadata, input.getAuthorizationValues(), true);
		new RestDocumentationGenerator().opts(input.opts(new ClientOpts()).swagger(swagger)).generate();
	}
}