/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.mvc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;
import org.springframework.web.servlet.view.velocity.VelocityViewResolver;

import com.sitewhere.web.mvc.controllers.AdminInterfaceController;

@Configuration
@ComponentScan(basePackageClasses = { AdminInterfaceController.class })
public class MvcConfiguration extends WebMvcConfigurationSupport {

    /**
     * Ignore path extension on URLs.
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
	configurer.favorPathExtension(false);
    }

    @Bean
    public ViewResolver viewResolver() {
	VelocityViewResolver bean = new VelocityViewResolver();
	bean.setCache(true);
	bean.setPrefix("/");
	bean.setSuffix(".vm");
	bean.setRequestContextAttribute("request");
	return bean;
    }

    @Bean
    public VelocityConfigurer velocityConfig() {
	VelocityConfigurer velocityConfigurer = new VelocityConfigurer();
	velocityConfigurer.setResourceLoaderPath("/WEB-INF/views/");
	return velocityConfigurer;
    }
}