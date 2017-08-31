/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.assetmanagement.persistence.mongodb;

import java.util.HashMap;
import java.util.Map;

import com.sitewhere.mongodb.IMongoConverterLookup;
import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.IHardwareAsset;
import com.sitewhere.spi.asset.ILocationAsset;
import com.sitewhere.spi.asset.IPersonAsset;

/**
 * Manages classes used to convert between Mongo and SPI objects.
 * 
 * @author Derek
 */
public class MongoConverters implements IMongoConverterLookup {

    /** Maps interface classes to their associated converters */
    private static Map<Class<?>, MongoConverter<?>> CONVERTERS = new HashMap<Class<?>, MongoConverter<?>>();

    /** Create a list of converters for various types */
    static {
	// Converters for asset management.
	CONVERTERS.put(IAssetCategory.class, new MongoAssetCategory());
	CONVERTERS.put(IHardwareAsset.class, new MongoHardwareAsset());
	CONVERTERS.put(IPersonAsset.class, new MongoPersonAsset());
	CONVERTERS.put(ILocationAsset.class, new MongoLocationAsset());
	CONVERTERS.put(IAsset.class, new MongoAsset());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.mongodb.IMongoConverterLookup#getConverterFor(java.lang.
     * Class)
     */
    @SuppressWarnings("unchecked")
    public <T> MongoConverter<T> getConverterFor(Class<T> api) {
	MongoConverter<T> result = (MongoConverter<T>) CONVERTERS.get(api);
	if (result == null) {
	    throw new RuntimeException("No Mongo converter registered for " + api.getName());
	}
	return result;
    }
}