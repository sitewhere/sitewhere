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
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

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

    /**
     * Set up the JSP view resolver.
     * 
     * @return
     */
    @Bean
    public UrlBasedViewResolver viewResolver() {
	UrlBasedViewResolver resolver = new UrlBasedViewResolver();
	resolver.setViewClass(JstlView.class);
	resolver.setPrefix("/WEB-INF/jsp/");
	resolver.setSuffix(".jsp");
	return resolver;
    }
}