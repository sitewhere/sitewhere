/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.labels.spi.microservice;

import com.sitewhere.grpc.service.LabelGenerationGrpc;
import com.sitewhere.spi.label.ILabelGeneratorManager;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;

/**
 * Extends {@link IMicroserviceTenantEngine} with features specific to label
 * generation.
 * 
 * @author Derek
 */
public interface ILabelGenerationTenantEngine extends IMicroserviceTenantEngine {

    /**
     * Get label generator manager implementation.
     * 
     * @return
     */
    public ILabelGeneratorManager getLabelGeneratorManager();

    /**
     * Get implementation class that wraps label generation with GRPC conversions.
     * 
     * @return
     */
    public LabelGenerationGrpc.LabelGenerationImplBase getLabelGenerationImpl();

}