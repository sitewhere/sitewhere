/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
