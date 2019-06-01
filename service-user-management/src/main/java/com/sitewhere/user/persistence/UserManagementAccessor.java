/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.user.persistence;

import com.sitewhere.spi.microservice.RuntimeServiceNotAvailableException;
import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.user.UserManagementDecorator;

/**
 * Allows an {@link IUserManagement} implementation to be dynamically plugged in
 * to existing components and throw exceptions if the implementation is not
 * available yet.
 * 
 * @author Derek
 */
public class UserManagementAccessor extends UserManagementDecorator {

    public UserManagementAccessor() {
	super(null);
    }

    /*
     * @see com.sitewhere.server.lifecycle.LifecycleComponentDecorator#getDelegate()
     */
    @Override
    public IUserManagement getDelegate() {
	IUserManagement delegate = super.getDelegate();
	if (delegate == null) {
	    getLogger().warn("Attempted to access user management via gRPC before implementation was initialized.");
	    throw new RuntimeServiceNotAvailableException();
	}
	return delegate;
    }
}