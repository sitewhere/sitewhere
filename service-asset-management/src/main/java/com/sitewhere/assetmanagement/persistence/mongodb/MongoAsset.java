/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.assetmanagement.persistence.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.rest.model.asset.Asset;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAsset;

/**
 * Used to load or save asset information to MongoDB.
 * 
 * @author Derek
 */
public class MongoAsset implements MongoConverter<IAsset> {

    /** Property for asset id */
    public static final String PROP_ID = "id";

    /** Property for asset name */
    public static final String PROP_NAME = "name";

    /** Property for asset type */
    public static final String PROP_ASSET_TYPE = "type";

    /** Property for category id */
    public static final String PROP_CATEGORY_ID = "category";

    /** Property for asset image URL */
    public static final String PROP_IMAGE_URL = "image";

    /** Property for asset properties */
    public static final String PROP_PROPERTIES = "properties";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(IAsset source) {
	try {
	    return (Document) MongoAssetManagement.marshalAsset(source);
	} catch (SiteWhereException e) {
	    throw new RuntimeException("Error marshaling asset.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public IAsset convert(Document source) {
	try {
	    return MongoAssetManagement.unmarshalAsset(source);
	} catch (SiteWhereException e) {
	    throw new RuntimeException("Error unmarshaling asset.", e);
	}
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IAsset source, Document target) {
	target.append(PROP_ID, source.getId());
	target.append(PROP_NAME, source.getName());
	target.append(PROP_ASSET_TYPE, source.getType().name());
	target.append(PROP_CATEGORY_ID, source.getAssetCategoryId());
	target.append(PROP_IMAGE_URL, source.getImageUrl());

	// Save nested list of properties.
	List<Document> props = new ArrayList<Document>();
	for (String name : source.getProperties().keySet()) {
	    Document prop = new Document();
	    prop.put("name", name);
	    prop.put("value", source.getProperties().get(name));
	    props.add(prop);
	}
	target.append(PROP_PROPERTIES, props);
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    @SuppressWarnings("unchecked")
    public static void fromDocument(Document source, Asset target) {
	String id = (String) source.get(PROP_ID);
	String name = (String) source.get(PROP_NAME);
	String assetTypeStr = (String) source.get(PROP_ASSET_TYPE);
	String categoryId = (String) source.get(PROP_CATEGORY_ID);
	String imageUrl = (String) source.get(PROP_IMAGE_URL);

	target.setId(id);
	target.setName(name);
	target.setType(AssetType.valueOf(assetTypeStr));
	target.setAssetCategoryId(categoryId);
	target.setImageUrl(imageUrl);

	List<Document> props = (List<Document>) source.get(PROP_PROPERTIES);
	if (props != null) {
	    for (Document prop : props) {
		String pname = (String) prop.get("name");
		String pvalue = (String) prop.get("value");
		target.getProperties().put(pname, pvalue);
	    }
	}
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IAsset source) {
	Document result = new Document();
	MongoAsset.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static Asset fromDocument(Document source) {
	Asset result = new Asset();
	MongoAsset.fromDocument(source, result);
	return result;
    }
}