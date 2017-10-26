/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.auth;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.sitewhere.web.auth.controllers.JwtService;
import com.sitewhere.web.spi.microservice.IWebRestMicroservice;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableWebMvc
@ComponentScan(basePackageClasses = { JwtService.class })
public class RestAuthConfiguration extends WebMvcConfigurerAdapter {

    /** URL prefix for matching REST authentication calls */
    public static final String REST_AUTH_MATCHER = "/authapi/*";

    /** Title for auth page */
    private static final String API_TITLE = "SiteWhere Authentication APIs";

    /** Description for auth page */
    private static final String API_DESCRIPTION = "Operations that provide authentication for SiteWhere REST services.";

    /** License type information */
    private static final String API_LICENSE_TYPE = "Common Public Attribution License Version 1.0 (CPAL-1.0)";

    /** Contact email for API questions */
    private static final String API_LICENSE_URL = "https://github.com/sitewhere/sitewhere/blob/master/LICENSE.txt";

    @Autowired
    private IWebRestMicroservice microservice;

    @Bean
    public Docket sitewhereAuth() {
	AuthorizationScope[] scopes = new AuthorizationScope[0];
	SecurityReference basicAuthRef = SecurityReference.builder().reference("basicAuth").scopes(scopes).build();

	ArrayList<SecurityReference> reference = new ArrayList<SecurityReference>();
	reference.add(basicAuthRef);

	ArrayList<SecurityContext> securityContexts = new ArrayList<SecurityContext>();
	securityContexts.add(SecurityContext.builder().securityReferences(reference).build());

	ArrayList<SecurityScheme> auth = new ArrayList<SecurityScheme>();
	auth.add(basicAuth());

	return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).securitySchemes(auth)
		.securityContexts(securityContexts).select().paths(PathSelectors.any()).build();
    }

    @Bean
    public BasicAuth basicAuth() {
	return new BasicAuth("basicAuth");
    }

    @Bean
    public ApiInfo apiInfo() {
	return new ApiInfoBuilder().title(API_TITLE).description(API_DESCRIPTION)
		.termsOfServiceUrl("http://www.sitewhere.com").license(API_LICENSE_TYPE).licenseUrl(API_LICENSE_URL)
		.version(microservice.getVersion().getVersionIdentifier()).build();
    }
}