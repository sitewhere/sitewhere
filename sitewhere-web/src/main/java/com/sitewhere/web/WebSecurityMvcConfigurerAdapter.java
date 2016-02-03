/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web;

import java.util.List;

import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Adds {@link WebMvcConfigurer} interface to {@link WebSecurityConfigurerAdapter}.
 * 
 * @author Derek
 */
public class WebSecurityMvcConfigurerAdapter extends WebSecurityConfigurerAdapter
		implements WebMvcConfigurer {

	public WebSecurityMvcConfigurerAdapter() {
		super();
	}

	public WebSecurityMvcConfigurerAdapter(boolean disableDefaults) {
		super(disableDefaults);
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> arg0) {
	}

	@Override
	public void addFormatters(FormatterRegistry arg0) {
	}

	@Override
	public void addInterceptors(InterceptorRegistry arg0) {
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry arg0) {
	}

	@Override
	public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> arg0) {
	}

	@Override
	public void addViewControllers(ViewControllerRegistry arg0) {
	}

	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer arg0) {
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer arg0) {
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer arg0) {
	}

	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> arg0) {
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> arg0) {
	}

	@Override
	public MessageCodesResolver getMessageCodesResolver() {
		return null;
	}

	@Override
	public Validator getValidator() {
		return null;
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
	}

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
	}

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
	}
}