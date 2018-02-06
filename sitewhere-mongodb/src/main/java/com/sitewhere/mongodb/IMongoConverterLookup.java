/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb;

/**
 * Looks up a {@link MongoConverter}
 * 
 * @author Derek
 *
 */
public interface IMongoConverterLookup {

    /**
     * Get converter for a given class type.
     * 
     * @param api
     * @return
     */
    public <T> MongoConverter<T> getConverterFor(Class<T> api);
}