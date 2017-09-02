/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset;

import org.springframework.boot.SpringApplication;

/**
 * Spring boot application for the asset management microservice.
 * 
 * @author Derek
 */
public class AssetManagementApplication {

    public AssetManagementApplication() {
    }

    public static void main(String[] args) {
	SpringApplication.run(AssetManagementApplication.class, args);
    }
}