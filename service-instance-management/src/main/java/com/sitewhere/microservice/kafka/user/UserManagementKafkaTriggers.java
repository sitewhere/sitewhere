/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.kafka.user;

import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.user.UserManagementDecorator;

/**
 * Decorator for user management APIs that triggers Kafka tenant model update
 * messages as the result of model changes.
 * 
 * @author Derek
 */
public class UserManagementKafkaTriggers extends UserManagementDecorator {

    public UserManagementKafkaTriggers(IUserManagement delegate) {
	super(delegate);
    }
}
