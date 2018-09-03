/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.user;

import java.util.Date;
import java.util.List;

import com.sitewhere.spi.common.IPersistentEntity;

/**
 * Interface for accessing user information.
 * 
 * @author Derek
 */
public interface IUser extends IPersistentEntity {

    /**
     * Get the username.
     * 
     * @return
     */
    public String getUsername();

    /**
     * Get the password.
     * 
     * @return
     */
    public String getHashedPassword();

    /**
     * Get the common name.
     * 
     * @return
     */
    public String getFirstName();

    /**
     * Get the surname.
     * 
     * @return
     */
    public String getLastName();

    /**
     * Get the last login date.
     * 
     * @return
     */
    public Date getLastLogin();

    /**
     * Get the account status.
     * 
     * @return
     */
    public AccountStatus getStatus();

    /**
     * Get the list of granted authorities.
     * 
     * @return
     */
    public List<String> getAuthorities();
}