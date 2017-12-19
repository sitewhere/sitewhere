/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.tenant.persistence;

import com.sitewhere.spi.microservice.ServiceNotAvailableException;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.tenant.TenantManagementDecorator;

/**
 * Allows an {@link ITenantManagement} implementation to be dynamically plugged
 * in to existing components and throw exceptions if the implementation is not
 * available yet.
 * 
 * @author Derek
 */
public class TenantManagementAccessor extends TenantManagementDecorator {

    public TenantManagementAccessor() {
	super(null);
    }

    /*
     * @see com.sitewhere.server.lifecycle.LifecycleComponentDecorator#getDelegate()
     */
    @Override
    public ITenantManagement getDelegate() {
	ITenantManagement delegate = super.getDelegate();
	if (delegate == null) {
	    throw new ServiceNotAvailableException();
	}
	return delegate;
    }
}