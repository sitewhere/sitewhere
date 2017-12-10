/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.configuration;

import com.sitewhere.configuration.model.DependencyResolvingConfigurationModel;
import com.sitewhere.configuration.old.IDeviceCommunicationParser;
import com.sitewhere.configuration.parser.IBatchOperationsParser;
import com.sitewhere.rest.model.configuration.AttributeNode;
import com.sitewhere.rest.model.configuration.ElementNode;
import com.sitewhere.spi.microservice.configuration.model.AttributeType;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;

/**
 * Configuration model for batch operations microservice.
 * 
 * @author Derek
 */
public class BatchOperationsModel extends DependencyResolvingConfigurationModel {

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.IConfigurationModel#
     * getDefaultXmlNamespace()
     */
    @Override
    public String getDefaultXmlNamespace() {
	return "http://sitewhere.io/schema/sitewhere/microservice/batch-operations";
    }

    /*
     * @see com.sitewhere.configuration.model.DependencyResolvingConfigurationModel#
     * getRootRole()
     */
    @Override
    public IConfigurationRoleProvider getRootRole() {
	return BatchOperationsRoles.BatchOperations;
    }

    /*
     * @see
     * com.sitewhere.configuration.model.MicroserviceConfigurationModel#addElements(
     * )
     */
    @Override
    public void addElements() {
	addElement(createBatchOperationsElement());
	addElement(createBatchOperationManagerElement());
    }

    /**
     * Create element configuration for batch operations.
     * 
     * @return
     */
    protected ElementNode createBatchOperationsElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Batch Operation Management",
		IDeviceCommunicationParser.Elements.BatchOperations.getLocalName(), "server",
		BatchOperationsRoleKeys.BatchOperations);

	builder.description("Manages how batch operations are processed. Batch operations are "
		+ "actions that are executed asynchronously for many devices with the ability to monitor "
		+ "progress at both the batch and element level.");
	return builder.build();
    }

    /**
     * Create element configuration for batch operation manager.
     * 
     * @return
     */
    protected ElementNode createBatchOperationManagerElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Batch Operation Manager",
		IBatchOperationsParser.Elements.DefaultBatchOperationManager.getLocalName(), "server",
		BatchOperationsRoleKeys.BatchOperationManager);

	builder.description("Manages how batch operations are processed.");
	builder.attribute((new AttributeNode.Builder("Throttle delay (ms)", "throttleDelayMs", AttributeType.Integer)
		.description("Number of milliseconds to wait between processing elements in a "
			+ "batch operation. This throttles the output to prevent overloading the system.")
		.defaultValue("0").build()));
	return builder.build();
    }
}