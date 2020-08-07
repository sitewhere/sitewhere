/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;

/**
 * Declares base path for REST application.
 */
@OpenAPIDefinition(info = @Info(title = "SiteWhere REST API", version = "3.0.0", description = "REST APIs for interacting with the SiteWhere data model.", license = @License(name = "CPAL 1.0", url = "https://opensource.org/licenses/CPAL-1.0")), components = @Components(securitySchemes = {
	@SecurityScheme(securitySchemeName = "basicAuth", type = SecuritySchemeType.HTTP, scheme = "basic", description = "Only used for getting JWT."),
	@SecurityScheme(securitySchemeName = "jwtAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", description = "Used for all REST calls."),
	@SecurityScheme(securitySchemeName = "tenantIdHeader", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER, apiKeyName = "X-SiteWhere-Tenant-Id", description = "Id of tenant to access."),
	@SecurityScheme(securitySchemeName = "tenantAuthHeader", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER, apiKeyName = "X-SiteWhere-Tenant-Auth", description = "Auth token of tenant to access.") }))
@ApplicationPath("/sitewhere")
public class SiteWhereApplication extends Application {
}
