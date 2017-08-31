/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.modules.datastore;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetModule;
import com.sitewhere.spi.asset.IPersonAsset;

/**
 * Implementation of {@link IAssetModule} that loads person assets from a
 * datastore.
 * 
 * @author Derek
 */
public class PersonAssetModule extends DataStoreAssetModule<IPersonAsset> implements IAssetModule<IPersonAsset> {

    /** Serial version UID */
    private static final long serialVersionUID = -6530318818641755281L;

    public PersonAssetModule(IAssetCategory category, IAssetManagement assetManagement) {
	super(category, assetManagement);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetModule#getAsset(java.lang.String)
     */
    @Override
    public IPersonAsset getAsset(String id) throws SiteWhereException {
	return doGetAsset(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetModule#putAsset(java.lang.String,
     * com.sitewhere.spi.asset.IAsset)
     */
    @Override
    public void putAsset(String id, IPersonAsset asset) throws SiteWhereException {
	doPutAsset(id, asset);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetModule#removeAsset(java.lang.String)
     */
    @Override
    public void removeAsset(String id) throws SiteWhereException {
	doRemoveAsset(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetModule#search(java.lang.String)
     */
    @Override
    public List<IPersonAsset> search(String criteria) throws SiteWhereException {
	return doSearch(criteria);
    }
}