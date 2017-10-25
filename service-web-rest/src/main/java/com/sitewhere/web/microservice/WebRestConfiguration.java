/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.microservice;

import org.apache.catalina.Context;
import org.apache.catalina.webresources.StandardRoot;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.sitewhere.web.RedirectServlet;
import com.sitewhere.web.auth.RestAuthConfiguration;
import com.sitewhere.web.auth.RestAuthSwaggerConfiguration;
import com.sitewhere.web.filters.JsonpFilter;
import com.sitewhere.web.filters.MethodOverrideFilter;
import com.sitewhere.web.filters.NoCacheFilter;
import com.sitewhere.web.filters.ResponseTimerFilter;
import com.sitewhere.web.rest.RestApiConfiguration;
import com.sitewhere.web.rest.RestApiSwaggerConfiguration;
import com.sitewhere.web.security.RestSecurity;
import com.sitewhere.web.vue.VueConfiguration;

/**
 * Configures web server and related artifacts.
 * 
 * @author Derek
 */
@Configuration
@Import(RestSecurity.class)
public class WebRestConfiguration {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
	return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
	TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {
	    protected void postProcessContext(Context context) {
		final int cacheSize = 100 * 1024 * 1024;
		StandardRoot standardRoot = new StandardRoot(context);
		standardRoot.setCacheMaxSize(cacheSize);
		standardRoot.setCacheObjectMaxSize(Integer.MAX_VALUE / 1024);
		context.setResources(standardRoot);
	    }
	};
	tomcat.setContextPath("/sitewhere");
	tomcat.setPort(8080);
	return tomcat;
    }

    @Bean
    public ServletRegistrationBean sitewhereAuthInterface() {
	DispatcherServlet dispatcherServlet = new DispatcherServlet();
	AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
	applicationContext.register(RestAuthConfiguration.class);
	dispatcherServlet.setApplicationContext(applicationContext);
	ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet,
		RestAuthConfiguration.REST_AUTH_MATCHER);
	registration.setName("sitewhereAuthInterface");
	registration.setLoadOnStartup(1);
	return registration;
    }

    @Bean
    public ServletRegistrationBean sitewhereAuthSwagger() {
	DispatcherServlet dispatcherServlet = new DispatcherServlet();
	AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
	applicationContext.register(RestAuthSwaggerConfiguration.class);
	dispatcherServlet.setApplicationContext(applicationContext);
	ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet,
		RestAuthSwaggerConfiguration.SWAGGER_MATCHER);
	registration.setName("sitewhereAuthSwagger");
	registration.setLoadOnStartup(3);
	return registration;
    }

    @Bean
    public ServletRegistrationBean sitewhereRestInterface() {
	DispatcherServlet dispatcherServlet = new DispatcherServlet();
	AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
	applicationContext.register(RestApiConfiguration.class);
	dispatcherServlet.setApplicationContext(applicationContext);
	ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet,
		RestApiConfiguration.REST_API_MATCHER);
	registration.setName("sitewhereRestInterface");
	registration.setLoadOnStartup(1);
	return registration;
    }

    @Bean
    public ServletRegistrationBean sitewhereRestSwagger() {
	DispatcherServlet dispatcherServlet = new DispatcherServlet();
	AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
	applicationContext.register(RestApiSwaggerConfiguration.class);
	dispatcherServlet.setApplicationContext(applicationContext);
	ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet,
		RestApiSwaggerConfiguration.SWAGGER_MATCHER);
	registration.setName("sitewhereRestSwagger");
	registration.setLoadOnStartup(3);
	return registration;
    }

    @Bean
    public ServletRegistrationBean sitewhereVueAdminInterface() {
	DispatcherServlet dispatcherServlet = new DispatcherServlet();
	AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
	applicationContext.register(VueConfiguration.class);
	dispatcherServlet.setApplicationContext(applicationContext);
	ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet,
		VueConfiguration.VUE_ADMIN_MATCHER);
	registration.setName("vueAdminInterface");
	registration.setLoadOnStartup(2);
	return registration;
    }

    @Bean
    public ServletRegistrationBean redirectServlet() {
	RedirectServlet redirect = new RedirectServlet();
	ServletRegistrationBean registration = new ServletRegistrationBean(redirect, "/admin");
	registration.setName("sitewhereRedirect");
	registration.setLoadOnStartup(3);
	return registration;
    }

    @Bean
    public FilterRegistrationBean methodOverrideFilter() {
	MethodOverrideFilter filter = new MethodOverrideFilter();
	FilterRegistrationBean registration = new FilterRegistrationBean();
	registration.setFilter(filter);
	registration.addUrlPatterns(RestApiConfiguration.REST_API_MATCHER);
	return registration;
    }

    @Bean
    public FilterRegistrationBean responseTimerFilter() {
	ResponseTimerFilter filter = new ResponseTimerFilter();
	FilterRegistrationBean registration = new FilterRegistrationBean();
	registration.setFilter(filter);
	registration.addUrlPatterns(RestApiConfiguration.REST_API_MATCHER);
	return registration;
    }

    @Bean
    public FilterRegistrationBean noCacheFilter() {
	NoCacheFilter filter = new NoCacheFilter();
	FilterRegistrationBean registration = new FilterRegistrationBean();
	registration.setFilter(filter);
	registration.addUrlPatterns(RestApiConfiguration.REST_API_MATCHER);
	return registration;
    }

    @Bean
    public FilterRegistrationBean jsonpFilter() {
	JsonpFilter filter = new JsonpFilter();
	FilterRegistrationBean registration = new FilterRegistrationBean();
	registration.setFilter(filter);
	registration.addUrlPatterns(RestApiConfiguration.REST_API_MATCHER);
	return registration;
    }
}