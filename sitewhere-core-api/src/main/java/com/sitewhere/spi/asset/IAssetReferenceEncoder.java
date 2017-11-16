/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.asset;

/**
 * Encodes information about an asset reference.
 * 
 * @author Derek
 */
public interface IAssetReferenceEncoder {

    /**
     * Encode an asset reference as a string.
     * 
     * @param reference
     * @return
     */
    public String encode(IAssetReference reference);

    /**
     * Decode an encoded asset reference.
     * 
     * @param encoded
     * @return
     */
    public IAssetReference decode(String encoded);
}
