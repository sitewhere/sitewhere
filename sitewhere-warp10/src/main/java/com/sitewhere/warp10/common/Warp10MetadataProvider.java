/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.warp10.common;

import com.sitewhere.spi.common.IMetadataProvider;
import com.sitewhere.warp10.rest.GTSInput;
import com.sitewhere.warp10.rest.GTSOutput;

/**
 * Used to load or save device entity metdata to MongoDB.
 *
 * @author dadams
 */
public class Warp10MetadataProvider {

    /**
     * Property for entity metadata
     */
    public static final String PROP_METADATA = "meta_";

    /**
     * Store data into a {@link GTSInput} using default property name.
     *
     * @param source
     * @param target
     */
    public static void toGTS(IMetadataProvider source, GTSInput target) {
        Warp10MetadataProvider.toGTS(PROP_METADATA, source, target);
    }

    /**
     * Store data into a {@link GTSInput}.
     *
     * @param propertyName
     * @param source
     * @param target
     */
    public static void toGTS(String propertyName, IMetadataProvider source, GTSInput target) {
        for (String key : source.getMetadata().keySet()) {
            target.getAttributes().put(propertyName + key, source.getMetadata().get(key));
        }
    }

    /**
     * Load data from a DBObject using default property name.
     *
     * @param source
     * @param target
     */
    public static void fromGTS(GTSOutput source, IMetadataProvider target) {
        Warp10MetadataProvider.fromGTS(PROP_METADATA, source, target);
    }

    /**
     * Load data from a DBObject.
     *
     * @param propertyName
     * @param source
     * @param target
     */
    public static void fromGTS(String propertyName, GTSOutput source, IMetadataProvider target) {
        if (source.getAttributes() != null && source.getAttributes().size() > 0) {
            for (String key : source.getAttributes().keySet()) {
                if(key.contains(PROP_METADATA)) {
                    target.getMetadata().put(key, source.getAttributes().get(key));
                }
            }
        }
    }
}