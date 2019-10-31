/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.user.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.request.PersistentEntityCreateRequest;
import com.sitewhere.spi.user.AccountStatus;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.request.IUserCreateRequest;

/**
 * Holds fields needed to create a new user.
 */
@JsonInclude(Include.NON_NULL)
public class UserCreateRequest extends PersistentEntityCreateRequest implements IUserCreateRequest {

    /** Serialization version identifier */
    private static final long serialVersionUID = -8552286827982676928L;

    /** Username */
    private String username;

    /** Password */
    private String password;

    /** First name */
    private String firstName;

    /** Last name */
    private String lastName;

    /** Account status */
    private AccountStatus status;

    /** List of granted authorities */
    private List<String> authorities;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.user.request.IUserCreateRequest#getUsername()
     */
    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.user.request.IUserCreateRequest#getPassword()
     */
    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.user.request.IUserCreateRequest#getFirstName()
     */
    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.user.request.IUserCreateRequest#getLastName()
     */
    public String getLastName() {
	return lastName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.user.request.IUserCreateRequest#getStatus()
     */
    public AccountStatus getStatus() {
	return status;
    }

    public void setStatus(AccountStatus status) {
	this.status = status;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.user.request.IUserCreateRequest#getAuthorities()
     */
    public List<String> getAuthorities() {
	return authorities;
    }

    public void setAuthorities(List<String> authorities) {
	this.authorities = authorities;
    }

    public static class Builder {

	/** Request being built */
	private UserCreateRequest request = new UserCreateRequest();

	public Builder(String username, String password, String firstName, String lastName) {
	    request.setUsername(username);
	    request.setPassword(password);
	    request.setFirstName(firstName);
	    request.setLastName(lastName);
	}

	public Builder(IUser existing) {
	    request.setUsername(existing.getUsername());
	    request.setPassword(existing.getHashedPassword());
	    request.setFirstName(existing.getFirstName());
	    request.setLastName(existing.getLastName());
	    request.setStatus(existing.getStatus());
	    request.setAuthorities(existing.getAuthorities());
	    request.setMetadata(existing.getMetadata());
	}

	public Builder withStatus(AccountStatus status) {
	    request.setStatus(status);
	    return this;
	}

	public Builder withAuthority(String authority) {
	    if (request.getAuthorities() == null) {
		request.setAuthorities(new ArrayList<String>());
	    }
	    request.getAuthorities().add(authority);
	    return this;
	}

	public Builder withAuthorities(List<String> auths) {
	    if (request.getAuthorities() == null) {
		request.setAuthorities(new ArrayList<String>());
	    }
	    request.getAuthorities().addAll(auths);
	    return this;
	}

	public Builder metadata(String name, String value) {
	    if (request.getMetadata() == null) {
		request.setMetadata(new HashMap<String, String>());
	    }
	    request.getMetadata().put(name, value);
	    return this;
	}

	public UserCreateRequest build() {
	    return request;
	}
    }
}