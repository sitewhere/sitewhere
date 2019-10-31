/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.security;

import java.util.List;

import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUser;

public class SiteWhereUserDetails {

    public SiteWhereUserDetails(IUser user, List<IGrantedAuthority> auths) {
    }
}
