/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.asset;

import org.bson.Document;

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.spi.asset.IHardwareAsset;

/**
 * Used to load or save hardware asset data to MongoDB.
 * 
 * @author dadams
 */
public class MongoHardwareAsset implements MongoConverter<IHardwareAsset> {

    /** Property for asset SKU */
    public static final String PROP_SKU = "sku";

    /** Property for asset description */
    public static final String PROP_DESCRIPTION = "description";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(IHardwareAsset source) {
	return MongoHardwareAsset.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public IHardwareAsset convert(Document source) {
	return MongoHardwareAsset.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IHardwareAsset source, Document target) {
	MongoAsset.toDocument(source, target);

	target.append(PROP_SKU, source.getSku());
	target.append(PROP_DESCRIPTION, source.getDescription());
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDocument(Document source, HardwareAsset target) {
	MongoAsset.fromDocument(source, target);

	String sku = (String) source.get(PROP_SKU);
	String description = (String) source.get(PROP_DESCRIPTION);

	target.setSku(sku);
	target.setDescription(description);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IHardwareAsset source) {
	Document result = new Document();
	MongoHardwareAsset.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static HardwareAsset fromDocument(Document source) {
	HardwareAsset result = new HardwareAsset();
	MongoHardwareAsset.fromDocument(source, result);
	return result;
    }
}