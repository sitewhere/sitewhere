/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.asset;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
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
	public BasicDBObject convert(IHardwareAsset source) {
		return MongoHardwareAsset.toDBObject(source);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.MongoConverter#convert(com.mongodb.DBObject)
	 */
	@Override
	public IHardwareAsset convert(DBObject source) {
		return MongoHardwareAsset.fromDBObject(source);
	}

	/**
	 * Copy information from SPI into Mongo DBObject.
	 * 
	 * @param source
	 * @param target
	 */
	public static void toDBObject(IHardwareAsset source, BasicDBObject target) {
		MongoAsset.toDBObject(source, target);
		target.append(PROP_SKU, source.getSku());
		target.append(PROP_DESCRIPTION, source.getDescription());
	}

	/**
	 * Copy information from Mongo DBObject to model object.
	 * 
	 * @param source
	 * @param target
	 */
	public static void fromDBObject(DBObject source, HardwareAsset target) {
		MongoAsset.fromDBObject(source, target);
		String sku = (String) source.get(PROP_SKU);
		String description = (String) source.get(PROP_DESCRIPTION);

		target.setSku(sku);
		target.setDescription(description);
	}

	/**
	 * Convert SPI object to Mongo DBObject.
	 * 
	 * @param source
	 * @return
	 */
	public static BasicDBObject toDBObject(IHardwareAsset source) {
		BasicDBObject result = new BasicDBObject();
		MongoHardwareAsset.toDBObject(source, result);
		return result;
	}

	/**
	 * Convert a DBObject into the SPI equivalent.
	 * 
	 * @param source
	 * @return
	 */
	public static HardwareAsset fromDBObject(DBObject source) {
		HardwareAsset result = new HardwareAsset();
		MongoHardwareAsset.fromDBObject(source, result);
		return result;
	}
}