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

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class CorsFilter implements ContainerResponseFilter {

    /*
     * @see
     * javax.ws.rs.container.ContainerResponseFilter#filter(javax.ws.rs.container.
     * ContainerRequestContext, javax.ws.rs.container.ContainerResponseContext)
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
	    throws IOException {
	responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
	responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
	responseContext.getHeaders().add("Access-Control-Allow-Headers",
		"origin, content-type, accept, authorization, x-sitewhere-tenant-id, x-sitewhere-tenant-auth");
	responseContext.getHeaders().add("Access-Control-Expose-Headers",
		"x-sitewhere-jwt, x-sitewhere-error, x-sitewhere-error-code");
	responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
    }
}