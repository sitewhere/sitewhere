/*
* $Id$
* --------------------------------------------------------------------------------------
* Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
*
* The software in this package is published under the terms of the CPAL v1.0
* license, a copy of which has been included with this distribution in the
* LICENSE.txt file.
*/
package com.sitewhere.web.admin;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Configures the Spring context for service admin REST services.
 * 
 * @author dadams
 */
@EnableWebMvc
@Configuration
@ComponentScan(basePackages = "com.sitewhere.web.rest")
public class AdminRestConfiguration {
}