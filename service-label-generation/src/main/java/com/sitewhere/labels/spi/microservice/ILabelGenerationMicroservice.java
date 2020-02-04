/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.labels.spi.microservice;

import com.sitewhere.labels.configuration.LabelGenerationConfiguration;
import com.sitewhere.labels.spi.grpc.ILabelGenerationGrpcServer;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;

/**
 * Microservice that provides label generation functionality.
 */
public interface ILabelGenerationMicroservice extends
	IMultitenantMicroservice<MicroserviceIdentifier, LabelGenerationConfiguration, ILabelGenerationTenantEngine> {

    /**
     * Get label generation GRPC server.
     * 
     * @return
     */
    public ILabelGenerationGrpcServer getLabelGenerationGrpcServer();

    /**
     * Get device management API access via GRPC channel.
     * 
     * @return
     */
    public IDeviceManagement getDeviceManagement();

    /**
     * Get asset management API access via GRPC channel.
     * 
     * @return
     */
    public IAssetManagement getAssetManagement();
}