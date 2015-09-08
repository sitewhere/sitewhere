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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.pegdown.PegDownProcessor;
import org.reflections.Reflections;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.web.rest.annotations.Documented;
import com.sitewhere.web.rest.annotations.DocumentedController;
import com.sitewhere.web.rest.annotations.Example;
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
			// Required since some internal operations require a user to be logged in.
			try {
				SecurityContextHolder.getContext().setAuthentication(
						SiteWhereServer.getSystemAuthentication());
			} catch (SiteWhereException e) {
				throw new RuntimeException("Unable to set system user.", e);
			}

			File resources = new File(args[0]);
			if (!resources.exists()) {
				throw new SiteWhereException("Unable to find REST documentation resources folder.");
			}
			List<ParsedController> controllers = parseControllers(resources);
			generateRestDocumentation(controllers, resources, args[1]);
		} catch (SiteWhereException e) {
			System.err.println("Unable to generate SiteWhere REST documentation.");
			e.printStackTrace(System.err);
		}
		System.out.println("Finished generating SiteWhere REST documentation...");
	}

	/**
	 * Parse all controllers using the {@link DocumentedController} annotation.
	 * 
	 * @param resourcesFolder
	 * @return
	 * @throws SiteWhereException
	 */
	protected static List<ParsedController> parseControllers(File resourcesFolder) throws SiteWhereException {
		Reflections reflections = new Reflections("com.sitewhere.web.rest.controllers");
		Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(DocumentedController.class);
		List<ParsedController> results = new ArrayList<ParsedController>();
		for (Class<?> controller : controllers) {
			results.add(parseController(controller, resourcesFolder));
		}
		Collections.sort(results, new Comparator<ParsedController>() {

			@Override
			public int compare(ParsedController o1, ParsedController o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		System.out.println("Found " + controllers.size() + " documented controllers.");
		return results;
	}

	/**
	 * Generate documentation.
	 * 
	 * @param controllers
	 * @param resourcesFolder
	 * @param outputFolder
	 * @throws SiteWhereException
	 */
	protected static void generateRestDocumentation(List<ParsedController> controllers, File resourcesFolder,
			String outputFolder) throws SiteWhereException {
		System.out.println("Writing REST documentation to: " + outputFolder);

		File header = new File(resourcesFolder, "header.htm");
		if (!header.exists()) {
			throw new SiteWhereException("Unable to find header file: " + header.getAbsolutePath());
		}

		String completeDoc = readFile(header);
		completeDoc += generateNavigation(controllers);

		completeDoc += "<div class=\"col-md-9\">";
		for (ParsedController controller : controllers) {
			String controllerDoc = generateControllerDocumentation(controller);
			completeDoc += controllerDoc;
		}
		completeDoc += "</div>";

		File footer = new File(resourcesFolder, "footer.htm");
		if (!footer.exists()) {
			throw new SiteWhereException("Unable to find footer file: " + footer.getAbsolutePath());
		}
		completeDoc += readFile(footer);

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
	 * Add the navigation panel.
	 * 
	 * @param controllers
	 * @return
	 */
	protected static String generateNavigation(List<ParsedController> controllers) {
		String html = "<div id=\"rest-navigation\" class=\"col-md-3 bs-docs-sidebar\">";
		html +=
				"<ul class=\"nav nav-list bs-docs-sidenav affix-top\""
						+ " data-spy=\"affix\" data-offset-top=\"10\">";

		boolean setActive = false;
		for (ParsedController controller : controllers) {
			html +=
					"<li" + ((!setActive) ? " class=\"active\"" : "") + "><a href=\"#"
							+ controller.getResource() + "\"><i class=\"icon-chevron-right\"></i> "
							+ controller.getName() + "</a></li>";
			setActive = true;
		}

		html += "</ul>";
		html += "</div>";
		return html;
	}

	/**
	 * Generate documentation for a single controller.
	 * 
	 * @param controller
	 * @return
	 */
	protected static String generateControllerDocumentation(ParsedController controller) {
		String html = "<a id=\"" + controller.getResource() + "\"></a>";
		html += controller.getDescription();
		for (ParsedMethod method : controller.getMethods()) {
			String methodHtml =
					"<div><p>" + method.getRequestMethod().toString() + " " + method.getBaseUri()
							+ method.getRelativeUri() + "</p>" + method.getDescription() + "</div>";
			for (ParsedExample example : method.getExamples()) {
				String exampleHtml = "";
				if (example.getDescription() != null) {
					exampleHtml += "<div>" + example.getDescription() + "</div>";
				}
				if (example.getJson() != null) {
					exampleHtml += "<pre><code class='json'>" + example.getJson() + "</code></pre>";
				}
				methodHtml += exampleHtml;
			}
			html += methodHtml;
		}
		return html;
	}

	/**
	 * Parse information for a given controller.
	 * 
	 * @param controller
	 * @param resourcesFolder
	 * @return
	 * @throws SiteWhereException
	 */
	protected static ParsedController parseController(Class<?> controller, File resourcesFolder)
			throws SiteWhereException {
		ParsedController parsed = new ParsedController();

		Api api = controller.getAnnotation(Api.class);
		if (api == null) {
			throw new SiteWhereException("Swagger Api annotation missing on documented controller.");
		}
		parsed.setResource(api.value());

		DocumentedController doc = controller.getAnnotation(DocumentedController.class);
		parsed.setName(doc.name());

		System.out.println("Processing controller: " + parsed.getName() + " (" + parsed.getResource() + ")");

		RequestMapping mapping = controller.getAnnotation(RequestMapping.class);
		if (mapping == null) {
			throw new SiteWhereException(
					"Spring RequestMapping annotation missing on documented controller: "
							+ controller.getName());
		}
		parsed.setBaseUri("/sitewhere/api" + mapping.value()[0]);

		// Verify controller markdown file.
		File markdownFile = new File(resourcesFolder, parsed.getResource() + ".md");
		if (!markdownFile.exists()) {
			throw new SiteWhereException("Controller markdown file missing: "
					+ markdownFile.getAbsolutePath());
		}

		// Verify controller resources folder.
		File resources = new File(resourcesFolder, parsed.getResource());
		if (!resources.exists()) {
			throw new SiteWhereException("Controller markdown folder missing: " + resources.getAbsolutePath());
		}

		try {
			PegDownProcessor processor = new PegDownProcessor();
			String markdown = readFile(markdownFile);
			parsed.setDescription(processor.markdownToHtml(markdown));
		} catch (IOException e) {
			throw new SiteWhereException("Unable to read markdown from: " + markdownFile.getAbsolutePath(), e);
		}

		Method[] methods = controller.getMethods();
		for (Method method : methods) {
			if (method.getAnnotation(Documented.class) != null) {
				ParsedMethod parsedMethod = parseMethod(parsed.getBaseUri(), method, resources);
				parsed.getMethods().add(parsedMethod);
			}
		}
		return parsed;
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

		parseExamples(method, parsed, resources);

		return parsed;
	}

	/**
	 * Parse examples for method.
	 * 
	 * @param method
	 * @param parsed
	 * @param resources
	 * @throws SiteWhereException
	 */
	protected static void parseExamples(Method method, ParsedMethod parsedMethod, File resources)
			throws SiteWhereException {
		Documented documented = method.getAnnotation(Documented.class);
		Example[] examples = documented.examples();
		File examplesResources = new File(resources, "examples");
		for (Example example : examples) {
			ParsedExample parsed = new ParsedExample();
			String mdFilename = example.description();
			if (mdFilename != null) {
				File markdownFile = new File(examplesResources, mdFilename);
				if (markdownFile.exists()) {
					PegDownProcessor processor = new PegDownProcessor();
					String markdown = readFile(markdownFile);
					parsed.setDescription(processor.markdownToHtml(markdown));
				}
			}
			Class<?> jsonObj = example.json();
			if (jsonObj != null) {
				try {
					Object instance = jsonObj.newInstance();
					String marshaled = MarshalUtils.marshalJsonAsPrettyString(instance);
					parsed.setJson(marshaled);
				} catch (InstantiationException e) {
					throw new SiteWhereException("Unable to create instance of JSON example object.", e);
				} catch (IllegalAccessException e) {
					throw new SiteWhereException("Unable to create instance of JSON example object.", e);
				}
			}
			parsed.setStage(example.stage());
			parsedMethod.getExamples().add(parsed);
		}
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