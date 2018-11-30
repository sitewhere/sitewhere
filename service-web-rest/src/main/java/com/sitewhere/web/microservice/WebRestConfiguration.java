/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.microservice;

import java.util.Arrays;

import org.apache.catalina.Context;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.tomcat.websocket.server.WsSci;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
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
import com.sitewhere.web.security.AuthApiSecurity;
import com.sitewhere.web.security.RestApiSecurity;
import com.sitewhere.web.security.WsApiSecurity;
import com.sitewhere.web.spi.microservice.IWebRestMicroservice;
import com.sitewhere.web.ws.WebSocketApiConfiguration;

import io.opentracing.contrib.web.servlet.filter.TracingFilter;

/**
 * Configures web server and related artifacts.
 * 
 * @author Derek
 */
@Configuration
@Import({ AuthApiSecurity.class, RestApiSecurity.class, WsApiSecurity.class })
public class WebRestConfiguration {

    @Autowired
    private IWebRestMicroservice<?> microservice;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
	return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public ServletWebServerFactory servletContainer() {
	TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
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
	tomcat.setTomcatContextCustomizers(Arrays.asList(new TomcatContextCustomizer[] { tomcatContextCustomizer() }));
	return tomcat;
    }

    @Bean
    public TomcatContextCustomizer tomcatContextCustomizer() {
	return new TomcatContextCustomizer() {
	    @Override
	    public void customize(Context context) {
		context.addServletContainerInitializer(new WsSci(), null);
	    }
	};
    }

    @Bean
    public ServletRegistrationBean<DispatcherServlet> sitewhereAuthInterface() {
	DispatcherServlet dispatcherServlet = new DispatcherServlet();
	AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
	applicationContext.register(RestAuthConfiguration.class);
	dispatcherServlet.setApplicationContext(applicationContext);
	ServletRegistrationBean<DispatcherServlet> registration = new ServletRegistrationBean<>(dispatcherServlet,
		RestAuthConfiguration.REST_AUTH_MATCHER);
	registration.setName("sitewhereAuthInterface");
	registration.setLoadOnStartup(1);
	return registration;
    }

    @Bean
    public ServletRegistrationBean<DispatcherServlet> sitewhereAuthSwagger() {
	DispatcherServlet dispatcherServlet = new DispatcherServlet();
	AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
	applicationContext.register(RestAuthSwaggerConfiguration.class);
	dispatcherServlet.setApplicationContext(applicationContext);
	ServletRegistrationBean<DispatcherServlet> registration = new ServletRegistrationBean<DispatcherServlet>(
		dispatcherServlet, RestAuthSwaggerConfiguration.SWAGGER_MATCHER);
	registration.setName("sitewhereAuthSwagger");
	registration.setLoadOnStartup(3);
	return registration;
    }

    @Bean
    public ServletRegistrationBean<DispatcherServlet> sitewhereRestInterface() {
	DispatcherServlet dispatcherServlet = new DispatcherServlet();
	AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
	applicationContext.register(RestApiConfiguration.class);
	dispatcherServlet.setApplicationContext(applicationContext);
	ServletRegistrationBean<DispatcherServlet> registration = new ServletRegistrationBean<>(dispatcherServlet,
		RestApiConfiguration.REST_API_MATCHER);
	registration.setName("sitewhereRestInterface");
	registration.setLoadOnStartup(1);
	return registration;
    }

    @Bean
    public ServletRegistrationBean<DispatcherServlet> sitewhereRestSwagger() {
	DispatcherServlet dispatcherServlet = new DispatcherServlet();
	AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
	applicationContext.register(RestApiSwaggerConfiguration.class);
	dispatcherServlet.setApplicationContext(applicationContext);
	ServletRegistrationBean<DispatcherServlet> registration = new ServletRegistrationBean<DispatcherServlet>(
		dispatcherServlet, RestApiSwaggerConfiguration.SWAGGER_MATCHER);
	registration.setName("sitewhereRestSwagger");
	registration.setLoadOnStartup(3);
	return registration;
    }

    @Bean
    public ServletRegistrationBean<DispatcherServlet> sitewhereWebSocketAdminInterface() {
	DispatcherServlet dispatcherServlet = new DispatcherServlet();
	AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
	applicationContext.register(WebSocketApiConfiguration.class);
	dispatcherServlet.setApplicationContext(applicationContext);
	ServletRegistrationBean<DispatcherServlet> registration = new ServletRegistrationBean<>(dispatcherServlet,
		WebSocketApiConfiguration.WEB_SOCKET_MATCHER);
	registration.setName("sitewhereWebSocketInterface");
	registration.setLoadOnStartup(1);
	return registration;
    }

    @Bean
    public ServletRegistrationBean<RedirectServlet> redirectServlet() {
	RedirectServlet redirect = new RedirectServlet();
	ServletRegistrationBean<RedirectServlet> registration = new ServletRegistrationBean<>(redirect, "/admin");
	registration.setName("sitewhereRedirect");
	registration.setLoadOnStartup(3);
	return registration;
    }

    @Bean
    public FilterRegistrationBean<TracingFilter> tracingFilter() {
	TracingFilter tracingFilter = new TracingFilter(getMicroservice().getTracer());
	FilterRegistrationBean<TracingFilter> registration = new FilterRegistrationBean<>(tracingFilter);
	registration.addUrlPatterns(RestApiConfiguration.REST_API_MATCHER);
	registration.setOrder(Integer.MIN_VALUE);
	return registration;
    }

    @Bean
    public FilterRegistrationBean<MethodOverrideFilter> methodOverrideFilter() {
	MethodOverrideFilter filter = new MethodOverrideFilter();
	FilterRegistrationBean<MethodOverrideFilter> registration = new FilterRegistrationBean<>();
	registration.setFilter(filter);
	registration.addUrlPatterns(RestApiConfiguration.REST_API_MATCHER);
	return registration;
    }

    @Bean
    public FilterRegistrationBean<ResponseTimerFilter> responseTimerFilter() {
	ResponseTimerFilter filter = new ResponseTimerFilter();
	FilterRegistrationBean<ResponseTimerFilter> registration = new FilterRegistrationBean<>();
	registration.setFilter(filter);
	registration.addUrlPatterns(RestApiConfiguration.REST_API_MATCHER);
	return registration;
    }

    @Bean
    public FilterRegistrationBean<NoCacheFilter> noCacheFilter() {
	NoCacheFilter filter = new NoCacheFilter();
	FilterRegistrationBean<NoCacheFilter> registration = new FilterRegistrationBean<>();
	registration.setFilter(filter);
	registration.addUrlPatterns(RestApiConfiguration.REST_API_MATCHER);
	return registration;
    }

    @Bean
    public FilterRegistrationBean<JsonpFilter> jsonpFilter() {
	JsonpFilter filter = new JsonpFilter();
	FilterRegistrationBean<JsonpFilter> registration = new FilterRegistrationBean<>();
	registration.setFilter(filter);
	registration.addUrlPatterns(RestApiConfiguration.REST_API_MATCHER);
	return registration;
    }

    public IWebRestMicroservice<?> getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IWebRestMicroservice<?> microservice) {
	this.microservice = microservice;
    }
}