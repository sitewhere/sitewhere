/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest;

import java.util.ArrayList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.sitewhere.SiteWhere;
import com.sitewhere.web.rest.controllers.Assets;

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
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableWebMvc
@ComponentScan(basePackageClasses = { Assets.class })
public class RestMvcConfiguration extends WebMvcConfigurerAdapter {

    /** URL prefix for matching REST API calls */
    public static final String REST_API_MATCHER = "/api/*";

    /** Title for API page */
    private static final String API_TITLE = "SiteWhere REST APIs";

    /** Description for API page */
    private static final String API_DESCRIPTION = "Operations that allow remote clients to interact with the core SiteWhere data model.";

    /** License type information */
    private static final String API_LICENSE_TYPE = "Common Public Attribution License Version 1.0 (CPAL-1.0)";

    /** Contact email for API questions */
    private static final String API_LICENSE_URL = "https://github.com/sitewhere/sitewhere/blob/master/LICENSE.txt";

    @Bean
    public Docket sitewhereApi() {
	AuthorizationScope[] scopes = new AuthorizationScope[0];
	SecurityReference securityReference = SecurityReference.builder().reference("basicAuth").scopes(scopes).build();

	ArrayList<SecurityReference> reference = new ArrayList<SecurityReference>(1);
	reference.add(securityReference);

	ArrayList<SecurityContext> securityContexts = new ArrayList<SecurityContext>(1);
	securityContexts.add(SecurityContext.builder().securityReferences(reference).build());

	ArrayList<SecurityScheme> auth = new ArrayList<SecurityScheme>(1);
	auth.add(new BasicAuth("basicAuth"));

	return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).securitySchemes(auth)
		.securityContexts(securityContexts).select().paths(PathSelectors.any()).build();
    }

    @Bean
    public ApiInfo apiInfo() {
	return new ApiInfoBuilder().title(API_TITLE).description(API_DESCRIPTION)
		.termsOfServiceUrl("http://springfox.io").license(API_LICENSE_TYPE).licenseUrl(API_LICENSE_URL)
		.version(SiteWhere.getVersion().getVersionIdentifier()).build();
    }

    @Bean
    public UiConfiguration uiConfig() {
	return new UiConfiguration("validatorUrl", "none", "alpha", "schema",
		UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS, false, true, 60000L);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
	registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
	registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}