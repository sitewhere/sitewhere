/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Interface for classes that can convert Mongo objects to SiteWhere SPI objects.
 * 
 * @author Derek
 * 
 * @param <T>
 */
public interface MongoConverter<I> {

	/**
	 * Create a Mongo {@link BasicDBObject} based on SPI input.
	 * 
	 * @param source
	 * @return
	 */
	public BasicDBObject convert(I source);

	/**
	 * Create the REST object from a Mongo DBObject.
	 * 
	 * @param source
	 * @return
	 */
	public I convert(DBObject source);
}