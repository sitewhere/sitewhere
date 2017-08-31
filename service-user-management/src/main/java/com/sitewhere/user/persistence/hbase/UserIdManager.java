/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.user.persistence.hbase;

import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.IUserIdManager;
import com.sitewhere.hbase.uid.UniqueIdCounterMap;
import com.sitewhere.hbase.uid.UniqueIdType;
import com.sitewhere.spi.SiteWhereException;

/**
 * Singleton that keeps up with asset management entities.
 * 
 * @author Derek
 */
public class UserIdManager implements IUserIdManager {

    /** Manager for tenant ids */
    private UniqueIdCounterMap tenantKeys;

    /** Manager for tenant group ids */
    private UniqueIdCounterMap tenantGroupKeys;

    /**
     * Load existing keys from table.
     * 
     * @param context
     * @throws SiteWhereException
     */
    public void load(IHBaseContext context) throws SiteWhereException {
	tenantKeys = new UniqueIdCounterMap(context, UniqueIdType.TenantKey.getIndicator(),
		UniqueIdType.TenantValue.getIndicator());
	tenantKeys.refresh();
	tenantGroupKeys = new UniqueIdCounterMap(context, UniqueIdType.TenantGroupKey.getIndicator(),
		UniqueIdType.TenantGroupValue.getIndicator());
	tenantGroupKeys.refresh();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.user.IUserIdManager#getTenantKeys()
     */
    public UniqueIdCounterMap getTenantKeys() {
	return tenantKeys;
    }

    public void setTenantKeys(UniqueIdCounterMap tenantKeys) {
	this.tenantKeys = tenantKeys;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.user.IUserIdManager#getTenantGroupKeys()
     */
    public UniqueIdCounterMap getTenantGroupKeys() {
	return tenantGroupKeys;
    }

    public void setTenantGroupKeys(UniqueIdCounterMap tenantGroupKeys) {
	this.tenantGroupKeys = tenantGroupKeys;
    }
}