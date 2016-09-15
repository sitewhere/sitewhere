/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.asset.datastore;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.IAssetModule;
import com.sitewhere.spi.asset.IHardwareAsset;

/**
 * Implementation of {@link IAssetModule} that loads hardware assets from a
 * datastore.
 * 
 * @author Derek
 */
public class HardwareAssetModule extends DataStoreAssetModule<IHardwareAsset> implements IAssetModule<IHardwareAsset> {

    /** Serial version UID */
    private static final long serialVersionUID = 8273484848445563832L;

    public HardwareAssetModule(IAssetCategory category) {
	super(category);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetModule#getAssetById(java.lang.String)
     */
    @Override
    public IHardwareAsset getAssetById(String id) throws SiteWhereException {
	return doGetAsset(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetModule#search(java.lang.String)
     */
    @Override
    public List<IHardwareAsset> search(String criteria) throws SiteWhereException {
	return doSearch(criteria);
    }
}