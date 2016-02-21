/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.sitewhere.SiteWhere;
import com.sitewhere.core.Boilerplate;
import com.sitewhere.spi.ServerStartupException;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.web.mvc.MvcConfiguration;
import com.sitewhere.web.rest.RestMvcConfiguration;
import com.sitewhere.web.swagger.SiteWhereSwaggerConfig;

/**
 * Root Spring Boot application that loads all other SiteWhere artifacts.
 * 
 * @author Derek
 */
@Configuration
@Import(SiteWhereSecurity.class)
@EnableAutoConfiguration(exclude = { HazelcastAutoConfiguration.class, ActiveMQAutoConfiguration.class })
public class SiteWhereApplication {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(SiteWhereApplication.class);

	/** URL prefix for matching REST API calls */
	private static final String REST_API_MATCHER = "/api/*";

	@Bean
	public EmbeddedServletContainerFactory servletContainer() {
		TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
		tomcat.setContextPath("/sitewhere");
		tomcat.setPort(8080);
		tomcat.setTldSkip("sitewhere*.jar,cxf*.jar,ehcache*.jar,hadoop*.jar,"
				+ "hazelcast*.jar,hbase*.jar,jersey*.jar,ksoap*.jar,scala*.jar");
		return tomcat;
	}

	@Bean
	public ServletRegistrationBean sitewhereRestInterface() {
		DispatcherServlet dispatcherServlet = new DispatcherServlet();
		AnnotationConfigWebApplicationContext applicationContext =
				new AnnotationConfigWebApplicationContext();
		applicationContext.register(RestMvcConfiguration.class, SiteWhereSwaggerConfig.class);
		dispatcherServlet.setApplicationContext(applicationContext);
		ServletRegistrationBean registration =
				new ServletRegistrationBean(dispatcherServlet, REST_API_MATCHER);
		registration.setName("sitewhereRestInterface");
		registration.setLoadOnStartup(1);
		return registration;
	}

	@Bean
	public ServletRegistrationBean sitewhereAdminInterface() {
		DispatcherServlet dispatcherServlet = new DispatcherServlet();
		AnnotationConfigWebApplicationContext applicationContext =
				new AnnotationConfigWebApplicationContext();
		applicationContext.register(MvcConfiguration.class);
		dispatcherServlet.setApplicationContext(applicationContext);
		ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet, "/admin/*");
		registration.setName("sitewhereAdminInterface");
		registration.setLoadOnStartup(2);
		return registration;
	}

	@Bean
	public FilterRegistrationBean methodOverrideFilter() {
		MethodOverrideFilter filter = new MethodOverrideFilter();
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(filter);
		registration.addUrlPatterns(REST_API_MATCHER);
		return registration;
	}

	@Bean
	public FilterRegistrationBean responseTimerFilter() {
		ResponseTimerFilter filter = new ResponseTimerFilter();
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(filter);
		registration.addUrlPatterns(REST_API_MATCHER);
		return registration;
	}

	@Bean
	public FilterRegistrationBean noCacheFilter() {
		NoCacheFilter filter = new NoCacheFilter();
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(filter);
		registration.addUrlPatterns(REST_API_MATCHER);
		return registration;
	}

	@Bean
	public FilterRegistrationBean jsonpFilter() {
		JsonpFilter filter = new JsonpFilter();
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(filter);
		registration.addUrlPatterns(REST_API_MATCHER);
		return registration;
	}

	public SiteWhereApplication() {
		try {
			SiteWhere.start();
			LOGGER.info("Server started successfully.");
			SiteWhere.getServer().logState();
		} catch (ServerStartupException e) {
			SiteWhere.getServer().setServerStartupError(e);
			List<String> messages = new ArrayList<String>();
			messages.add("!!!! SiteWhere Server Failed to Start !!!!");
			messages.add("");
			messages.add("Component: " + e.getDescription());
			messages.add("Error: " + e.getComponent().getLifecycleError().getMessage());
			String message = Boilerplate.boilerplate(messages, '*', 60);
			LOGGER.info("\n" + message + "\n");
		} catch (SiteWhereException e) {
			LOGGER.error("Exception on server startup.", e);
			List<String> messages = new ArrayList<String>();
			messages.add("!!!! SiteWhere Server Failed to Start !!!!");
			messages.add("");
			messages.add("Error: " + e.getMessage());
			String message = Boilerplate.boilerplate(messages, '*', 60);
			LOGGER.info("\n" + message + "\n");
		} catch (Throwable e) {
			LOGGER.error("Unhandled exception in server startup.", e);
			List<String> messages = new ArrayList<String>();
			messages.add("!!!! Unhandled Exception !!!!");
			messages.add("");
			messages.add("Error: " + e.getMessage());
			String message = Boilerplate.boilerplate(messages, '*', 60);
			LOGGER.info("\n" + message + "\n");
		}
	}

	/**
	 * Acts on shutdown hook to gracefully shut down SiteWhere server components.
	 * 
	 * @return
	 */
	@Bean
	public ShutdownListener shutdownListener() {
		return new ShutdownListener();
	}

	public static void main(String[] args) {
		SpringApplication.run(SiteWhereApplication.class, args);
	}
}