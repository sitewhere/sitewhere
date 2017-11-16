/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.asset;

import com.sitewhere.spi.asset.IAssetReference;
import com.sitewhere.spi.asset.IAssetReferenceEncoder;

/**
 * Default implementation of {@link IAssetReferenceEncoder}.
 * 
 * @author Derek
 */
public class DefaultAssetReferenceEncoder implements IAssetReferenceEncoder {

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetReferenceEncoder#encode(com.sitewhere.spi.asset
     * .IAssetReference)
     */
    @Override
    public String encode(IAssetReference reference) {
	return reference.getModule() + ":" + reference.getId();
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetReferenceEncoder#decode(java.lang.String)
     */
    @Override
    public IAssetReference decode(String encoded) {
	int index = encoded.indexOf(":");
	if (index > -1) {
	    String module = encoded.substring(0, index);
	    String asset = encoded.substring(index + 1);
	    return new AssetReference.Builder(module, asset).build();
	}
	return null;
    }
}