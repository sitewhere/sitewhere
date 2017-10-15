/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch;

import org.springframework.boot.SpringApplication;

/**
 * Spring boot application for the batch operations microservice.
 * 
 * @author Derek
 */
public class BatchOperationsApplication {

    public BatchOperationsApplication() {
    }

    public static void main(String[] args) {
	SpringApplication.run(BatchOperationsApplication.class, args);
    }
}