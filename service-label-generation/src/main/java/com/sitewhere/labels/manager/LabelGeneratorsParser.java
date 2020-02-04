/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.labels.manager;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.labels.configuration.LabelGenerationTenantConfiguration;
import com.sitewhere.labels.configuration.manager.LabelGeneratorGenericConfiguration;
import com.sitewhere.labels.configuration.qrcode.QrCodeGeneratorConfiguration;
import com.sitewhere.labels.qrcode.QrCodeGenerator;
import com.sitewhere.labels.spi.ILabelGenerator;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Supports parsing event sources configuration into event source components.
 */
public class LabelGeneratorsParser {

    /** Static logger instance */
    private static final Logger LOGGER = LoggerFactory.getLogger(LabelGeneratorsParser.class);

    /** Type for QR code generator */
    public static final String TYPE_QRCODE = "qrCode";

    /**
     * Parse label generator configurations to build runtime components.
     * 
     * @param component
     * @param configuration
     * @return
     * @throws SiteWhereException
     */
    public static List<ILabelGenerator> parse(ITenantEngineLifecycleComponent component,
	    LabelGenerationTenantConfiguration configuration) throws SiteWhereException {
	List<ILabelGenerator> generators = new ArrayList<>();
	for (LabelGeneratorGenericConfiguration genConfig : configuration.getGenerators()) {
	    switch (genConfig.getType()) {
	    case TYPE_QRCODE: {
		generators.add(createQrCodeGenerator(component, genConfig));
		break;
	    }
	    default: {
		throw new SiteWhereException(
			String.format("Unknown label generator type '%s' for generator with id '%s'",
				genConfig.getType(), genConfig.getId()));
	    }
	    }
	}
	return generators;
    }

    /**
     * Create QR code label generator.
     * 
     * @param component
     * @param genConfig
     * @return
     * @throws SiteWhereException
     */
    protected static QrCodeGenerator createQrCodeGenerator(ITenantEngineLifecycleComponent component,
	    LabelGeneratorGenericConfiguration genConfig) throws SiteWhereException {
	QrCodeGeneratorConfiguration qrConfig = new QrCodeGeneratorConfiguration(component);
	qrConfig.apply(genConfig);
	LOGGER.info(String.format("Creating QR code label generator with configuration:\n%s\n\n",
		MarshalUtils.marshalJsonAsPrettyString(qrConfig)));
	QrCodeGenerator generator = new QrCodeGenerator(qrConfig);
	return generator;
    }
}
