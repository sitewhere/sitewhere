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
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.pegdown.PegDownProcessor;
import org.reflections.Reflections;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.web.rest.annotations.Concerns;
import com.sitewhere.web.rest.annotations.Documented;
import com.sitewhere.web.rest.annotations.DocumentedController;
import com.sitewhere.web.rest.annotations.Example;
import com.sitewhere.web.rest.documentation.ParsedParameter.ParameterType;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.Paranamer;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

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

		completeDoc += "<div class=\"col-md-8\">\n";
		for (ParsedController controller : controllers) {
			String controllerDoc = generateControllerDocumentation(controller);
			completeDoc += controllerDoc;
		}
		completeDoc += "</div>\n";

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
		String html = "<nav id=\"rest-navigation\" class=\"col-md-4 bs-docs-sidenav\">\n";
		html += "<ul class=\"nav nav-list affix\">\n";

		boolean setActive = false;
		for (ParsedController controller : controllers) {
			html +=
					"<li" + ((!setActive) ? " class=\"active\"" : "") + "><a href=\"#"
							+ controller.getResource() + "\"><i class=\"icon-chevron-right\"></i> "
							+ controller.getName() + "</a>\n";
			html += "<ul class=\"nav\">\n";
			for (ParsedMethod method : controller.getMethods()) {
				html +=
						"<li><a href=\"#" + method.getName() + "\"><i class=\"icon-chevron-right\"></i> "
								+ method.getSummary() + "</a>\n";
			}
			html += "</ul>\n";
			html += "</li>";
			setActive = true;
		}

		html += "</ul>\n";
		html += "</nav>\n";
		return html;
	}

	/**
	 * Generate documentation for a single controller.
	 * 
	 * @param controller
	 * @return
	 */
	protected static String generateControllerDocumentation(ParsedController controller) {
		String html = "<a id=\"" + controller.getResource() + "\"></a>\n";
		html += controller.getDescription();
		for (ParsedMethod method : controller.getMethods()) {
			RequestMethodColors colors = getRequestMethodColors(method);
			String methodHtml =
					createSplitter() + "<a id=\"" + method.getName() + "\" style=\"display:block;\"></a>\n"
							+ method.getDescription();
			methodHtml += createUriBlock(method, colors) + "\n";
			methodHtml += createParametersBlock(method, colors) + "\n";
			for (ParsedExample example : method.getExamples()) {
				String exampleHtml = "";
				if (example.getDescription() != null) {
					exampleHtml += "<div>" + example.getDescription() + "</div>\n";
				}
				if (example.getJson() != null) {
					exampleHtml +=
							"<pre><code class='json'>" + example.getJson()
									+ "</code><span class=\"code-tag\">" + example.getStage().toString()
									+ "</span></pre>\n";
				}
				methodHtml += exampleHtml;
			}
			html += methodHtml;
		}
		html += createSplitter();
		return html;
	}

	/**
	 * Create a splitter between sections.
	 * 
	 * @return
	 */
	protected static String createSplitter() {
		return "<div style=\"border-bottom: 1px solid #eee; margin-top: 30px; margin-bottom: 30px;\"></div>\n";
	}

	/**
	 * Create block that displays REST service URI.
	 * 
	 * @param method
	 * @param colors
	 * @return
	 */
	protected static String createUriBlock(ParsedMethod method, RequestMethodColors colors) {
		String uri =
				"<h3>Request URI</h3><div style=\"background-color: "
						+ colors.getBgColor()
						+ "; border: 1px solid "
						+ colors.getBrdColor()
						+ "; font-size: 13px; margin: 20px 0px;\"><span style=\"width: 70px; background-color: "
						+ colors.getTagColor()
						+ "; color: #fff; text-align: center; display: inline-block; margin-right: 15px; padding: 5px;\">"
						+ colors.getTagName()
						+ "</span><span style=\"width: 100%; font-family: courier; font-size: 12px; color: #000;\">"
						+ method.getBaseUri() + method.getRelativeUri() + "</span></div>";

		int pathParamsCount = 0;
		String table =
				"<h3>Path Parameters</h3><table class=\"param-table\"><thead><tr style=\"background-color: "
						+ colors.getTagColor() + "; color: #fff;\"><th>Name</th>"
						+ "<th>Description</th></thead><tbody>";
		for (ParsedParameter param : method.getParameters()) {
			if (param.getType() == ParameterType.Path) {
				table +=
						"<tr style=\"background-color: " + colors.getBgColor()
								+ "\"><td style=\"border: 1px solid " + colors.getBrdColor() + ";\">"
								+ param.getName() + "</td><td>" + param.getDescription() + "</td></tr>";
				pathParamsCount++;
			}
		}
		table += "</table>";

		if (pathParamsCount > 0) {
			uri += table;
		}

		return uri;
	}

	/**
	 * Find colors associated with request method.
	 * 
	 * @param method
	 * @return
	 */
	protected static RequestMethodColors getRequestMethodColors(ParsedMethod method) {
		String tagName, tagColor, bgColor, brdColor;
		switch (method.getRequestMethod()) {
		case GET: {
			tagName = "GET";
			tagColor = "#10A54A";
			bgColor = "#E7F6EC";
			brdColor = "C3E8D1";
			break;
		}
		case POST: {
			tagName = "POST";
			tagColor = "#0F6AB4";
			bgColor = "#E7F0F7";
			brdColor = "#C3D9EC";
			break;
		}
		case PUT: {
			tagName = "PUT";
			tagColor = "#C5862B";
			bgColor = "#F9F2E9";
			brdColor = "#F0E0CA";
			break;
		}
		case DELETE: {
			tagName = "DELETE";
			tagColor = "#A41E22";
			bgColor = "#F5E8E8";
			brdColor = "#E8C6C7";
			break;
		}
		default: {
			tagName = "???";
			tagColor = "#999";
			bgColor = "#ccc";
			brdColor = "#aaa";
			break;
		}
		}
		RequestMethodColors colors = new RequestMethodColors();
		colors.setTagName(tagName);
		colors.setTagColor(tagColor);
		colors.setBgColor(bgColor);
		colors.setBrdColor(brdColor);
		return colors;
	}

	/**
	 * Holder object for colors associated with request method;
	 * 
	 * @author Derek
	 */
	protected static class RequestMethodColors {
		private String tagName;
		private String tagColor;
		private String bgColor;
		private String brdColor;

		public String getTagName() {
			return tagName;
		}

		public void setTagName(String tagName) {
			this.tagName = tagName;
		}

		public String getTagColor() {
			return tagColor;
		}

		public void setTagColor(String tagColor) {
			this.tagColor = tagColor;
		}

		public String getBgColor() {
			return bgColor;
		}

		public void setBgColor(String bgColor) {
			this.bgColor = bgColor;
		}

		public String getBrdColor() {
			return brdColor;
		}

		public void setBrdColor(String brdColor) {
			this.brdColor = brdColor;
		}
	}

	/**
	 * Create the table of parameters.
	 * 
	 * @param method
	 * @return
	 */
	protected static String createParametersBlock(ParsedMethod method, RequestMethodColors colors) {
		if (method.getParameters().isEmpty()) {
			return "";
		}
		String table =
				"<h3>Request Parameters</h3><table class=\"param-table\"><thead><tr><th>Name</th>"
						+ "<th>Description</th><th>Required</th></thead><tbody>";

		int reqParamsCount = 0;
		for (ParsedParameter param : method.getParameters()) {
			if (param.getType() == ParameterType.Request) {
				table +=
						"<tr><td>" + param.getName() + "</td><td>" + param.getDescription() + "</td><td>"
								+ param.isRequired() + "</td></tr>";
				reqParamsCount++;
			}
		}

		table += "</tbody></table>";
		if (reqParamsCount > 0) {
			return table;
		} else {
			return "";
		}
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
		Collections.sort(parsed.getMethods(), new Comparator<ParsedMethod>() {

			@Override
			public int compare(ParsedMethod o1, ParsedMethod o2) {
				return o1.getSummary().compareTo(o2.getSummary());
			}
		});
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
		parsed.setName(method.getName());
		parsed.setBaseUri(baseUri);

		ApiOperation op = method.getAnnotation(ApiOperation.class);
		if (op == null) {
			throw new SiteWhereException("Spring ApiOperation annotation missing on documented method: "
					+ method.getName());
		}
		parsed.setSummary(op.value());

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

		// Parse method-level markdown description.
		File markdownFile = new File(resources, markdownFilename);
		if (!markdownFile.exists()) {
			throw new SiteWhereException("Method markdown file missing: " + markdownFile.getAbsolutePath());
		}
		PegDownProcessor processor = new PegDownProcessor();
		String markdown = readFile(markdownFile);
		parsed.setDescription(processor.markdownToHtml(markdown));

		// Parse parameters.
		List<ParsedParameter> params = parseParameters(method);
		Collections.sort(params, new Comparator<ParsedParameter>() {

			@Override
			public int compare(ParsedParameter o1, ParsedParameter o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		parsed.setParameters(params);

		parseExamples(method, parsed, resources);

		return parsed;
	}

	/**
	 * Parse method parameters.
	 * 
	 * @param method
	 * @return
	 * @throws SiteWhereException
	 */
	protected static List<ParsedParameter> parseParameters(Method method) throws SiteWhereException {
		List<ParsedParameter> parsed = new ArrayList<ParsedParameter>();

		Paranamer paranamer = new BytecodeReadingParanamer();
		String[] paramNames = paranamer.lookupParameterNames(method);

		if (paramNames.length > 0) {
			int i = 0;
			for (Annotation[] annotations : method.getParameterAnnotations()) {
				RequestParam request = null;
				PathVariable path = null;
				ApiParam api = null;
				Concerns concerns = null;
				for (Annotation annotation : annotations) {
					if (annotation instanceof RequestParam) {
						request = (RequestParam) annotation;
					} else if (annotation instanceof PathVariable) {
						path = (PathVariable) annotation;
					} else if (annotation instanceof ApiParam) {
						api = (ApiParam) annotation;
					} else if (annotation instanceof Concerns) {
						concerns = (Concerns) annotation;
					}
				}
				if (request != null) {
					ParsedParameter param = new ParsedParameter();
					param.setType(ParameterType.Request);
					param.setName(paramNames[i]);
					param.setRequired(request.required());
					if (api != null) {
						param.setRequired(api.required());
						param.setDescription(api.value());
					}
					if (concerns != null) {
						param.getConcerns().addAll(Arrays.asList(concerns.values()));
					}
					parsed.add(param);
				} else if (path != null) {
					ParsedParameter param = new ParsedParameter();
					param.setType(ParameterType.Path);
					param.setName(paramNames[i]);
					param.setRequired(true);
					if (api != null) {
						param.setDescription(api.value());
					}
					if (concerns != null) {
						param.getConcerns().addAll(Arrays.asList(concerns.values()));
					}
					parsed.add(param);
				}
				i++;
			}
		}

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
					try {
						Method generate = jsonObj.getMethod("generate");
						instance = generate.invoke(instance);
					} catch (NoSuchMethodException e) {
						// Fall through if method is not implemented.
					} catch (SecurityException e) {
						throw new SiteWhereException("Error executing generator method.", e);
					} catch (IllegalArgumentException e) {
						throw new SiteWhereException("Error executing generator method.", e);
					} catch (InvocationTargetException e) {
						throw new SiteWhereException("Error executing generator method.", e);
					}
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