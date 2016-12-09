/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.user.request.UserCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.user.AccountStatus;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.web.rest.documentation.ExampleData.User_John;

/**
 * Examples of REST payloads for various user methods.
 * 
 * @author Derek
 */
public class Users {

    public static class CreateUserRequest {

	public Object generate() throws SiteWhereException {
	    UserCreateRequest request = new UserCreateRequest();
	    request.setUsername(ExampleData.USER_ADMIN.getUsername());
	    request.setFirstName(ExampleData.USER_ADMIN.getFirstName());
	    request.setLastName(ExampleData.USER_ADMIN.getLastName());
	    request.setPassword("admin");
	    request.setAuthorities(ExampleData.USER_ADMIN.getAuthorities());
	    request.setMetadata(ExampleData.USER_ADMIN.getMetadata());
	    return request;
	}
    }

    public static class CreateUserResponse {

	public Object generate() throws SiteWhereException {
	    return ExampleData.USER_ADMIN;
	}
    }

    public static class UpdateUserRequest {

	public Object generate() throws SiteWhereException {
	    UserCreateRequest request = new UserCreateRequest();
	    request.setFirstName("Robert");
	    request.setStatus(AccountStatus.Locked);
	    request.setMetadata(null);
	    return request;
	}
    }

    public static class UpdateUserResponse {

	public Object generate() throws SiteWhereException {
	    User_John bob = new User_John();
	    bob.setFirstName("Robert");
	    return bob;
	}
    }

    public static class ListAuthoritiesForUserResponse {

	public Object generate() throws SiteWhereException {
	    List<IGrantedAuthority> list = new ArrayList<IGrantedAuthority>();
	    list.add(ExampleData.AUTH_ADMIN_REST);
	    list.add(ExampleData.AUTH_ADMIN_USERS);
	    return new SearchResults<IGrantedAuthority>(list, 2);
	}
    }

    public static class ListUsersResponse {

	public Object generate() throws SiteWhereException {
	    List<IUser> list = new ArrayList<IUser>();
	    list.add(ExampleData.USER_ADMIN);
	    list.add(ExampleData.USER_JOHN);
	    return new SearchResults<IUser>(list, 2);
	}
    }
}