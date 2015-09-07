/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.pegdown.PegDownProcessor;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.web.rest.annotations.Documented;
import com.sitewhere.web.rest.annotations.DocumentedController;
import com.wordnik.swagger.annotations.Api;

/**
 * Introspects the REST controllers to pull out documentation and generate it into an HTML
 * file.
 * 
 * @author Derek
 */
public class RestDocumentationGenerator {

	public static void main(String[] args) {
		if (args.length < 2) {
			throw new RuntimeException("Missing arguments needed to create REST documentation.");
		}

		System.out.println("Generating SiteWhere REST documentation...");
		try {
			generateRestDocumentation(args[0], args[1]);
		} catch (SiteWhereException e) {
			System.err.println("Unable to generate SiteWhere REST documentation.");
			e.printStackTrace(System.err);
		}
		System.out.println("Finished generating SiteWhere REST documentation...");
	}

	/**
	 * Generate SiteWhere REST documentation.
	 * 
	 * @param outputFolder
	 * @throws SiteWhereException
	 */
	protected static void generateRestDocumentation(String resourcesFolder, String outputFolder)
			throws SiteWhereException {
		System.out.println("Writing REST documentation to: " + outputFolder);
		Reflections reflections = new Reflections("com.sitewhere.web.rest.controllers");
		Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(DocumentedController.class);
		System.out.println("Found " + controllers.size() + " documented controllers.");

		File resources = new File(resourcesFolder);
		if (!resources.exists()) {
			throw new SiteWhereException("Unable to find REST documentation resources folder.");
		}

		String completeDoc = "";
		for (Class<?> controller : controllers) {
			String controllerDoc = generateControllerDocumentation(controller, resources);
			completeDoc += controllerDoc;
		}
		File output = new File(outputFolder);
		if (!output.exists()) {
			output.mkdir();
		}
		File single = new File(output, "single.html");
		try {
			FileOutputStream out = new FileOutputStream(single);
			out.write(completeDoc.getBytes());
			out.close();
		} catch (IOException e) {
			throw new SiteWhereException("Unable to write REST documentation file.", e);
		}
	}

	/**
	 * Generate output for a controller and return it as a String.
	 * 
	 * @param controller
	 * @return
	 * @throws SiteWhereException
	 */
	protected static String generateControllerDocumentation(Class<?> controller, File resourcesFolder)
			throws SiteWhereException {
		Api api = controller.getAnnotation(Api.class);
		if (api == null) {
			throw new SiteWhereException("Swagger Api annotation missing on documented controller.");
		}
		String resource = api.value();

		RequestMapping mapping = controller.getAnnotation(RequestMapping.class);
		if (mapping == null) {
			throw new SiteWhereException(
					"Spring RequestMapping annotation missing on documented controller: "
							+ controller.getName());
		}
		String baseUri = "/sitewhere/api" + mapping.value()[0];

		// Verify controller markdown file.
		File markdownFile = new File(resourcesFolder, resource + ".md");
		if (!markdownFile.exists()) {
			throw new SiteWhereException("Controller markdown file missing: "
					+ markdownFile.getAbsolutePath());
		}

		// Verify controller resources folder.
		File resources = new File(resourcesFolder, resource);
		if (!resources.exists()) {
			throw new SiteWhereException("Controller markdown folder missing: " + resources.getAbsolutePath());
		}

		try {
			PegDownProcessor processor = new PegDownProcessor();
			String markdown = readFile(markdownFile);
			String html = processor.markdownToHtml(markdown);

			Reflections reflections =
					new Reflections("com.sitewhere.web.rest.controllers", new MethodAnnotationsScanner());
			Set<Method> methods = reflections.getMethodsAnnotatedWith(Documented.class);
			for (Method method : methods) {
				ParsedMethod parsedMethod = parseMethod(baseUri, method, resources);
				String methodHtml =
						"<div><p>" + parsedMethod.getRequestMethod().toString() + " "
								+ parsedMethod.getBaseUri() + parsedMethod.getRelativeUri() + "</p>"
								+ parsedMethod.getDescription() + "</div>";
				html += methodHtml;
			}
			return html;
		} catch (IOException e) {
			throw new SiteWhereException("Unable to read markdown from: " + markdownFile.getAbsolutePath(), e);
		}
	}

	/**
	 * Generate documentation for a documented method.
	 * 
	 * @param baseUri
	 * @param method
	 * @return
	 * @throws SiteWhereException
	 */
	protected static ParsedMethod parseMethod(String baseUri, Method method, File resources)
			throws SiteWhereException {
		ParsedMethod parsed = new ParsedMethod();
		parsed.setBaseUri(baseUri);

		RequestMapping mapping = method.getAnnotation(RequestMapping.class);
		if (mapping == null) {
			throw new SiteWhereException("Spring RequestMapping annotation missing on documented method: "
					+ method.getName());
		}

		// Find URI mapping.
		String[] mappings = mapping.value();
		parsed.setRelativeUri("/");
		if (mappings.length > 0) {
			parsed.setRelativeUri(mappings[0]);
		}

		// Find request method.
		RequestMethod[] methods = mapping.method();
		if (methods.length == 0) {
			throw new SiteWhereException("No request methods configured.");
		}
		parsed.setRequestMethod(methods[0]);

		Documented documented = method.getAnnotation(Documented.class);
		String markdownFilename = documented.description();
		if (markdownFilename.length() == 0) {
			markdownFilename = method.getName() + ".md";
		}

		File markdownFile = new File(resources, markdownFilename);
		if (!markdownFile.exists()) {
			throw new SiteWhereException("Method markdown file missing: " + markdownFile.getAbsolutePath());
		}
		PegDownProcessor processor = new PegDownProcessor();
		String markdown = readFile(markdownFile);
		parsed.setDescription(processor.markdownToHtml(markdown));

		return parsed;
	}

	/**
	 * Read contents of a file into a String.
	 * 
	 * @param file
	 * @return
	 * @throws SiteWhereException
	 */
	protected static String readFile(File file) throws SiteWhereException {
		try {
			FileInputStream in = new FileInputStream(file);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			IOUtils.copy(in, out);
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
			return out.toString();
		} catch (IOException e) {
			throw new SiteWhereException("Unable to read content from: " + file.getAbsolutePath());
		}
	}
}