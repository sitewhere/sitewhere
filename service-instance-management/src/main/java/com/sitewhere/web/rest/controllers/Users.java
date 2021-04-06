/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.web.rest.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.user.UserSearchCriteria;
import com.sitewhere.rest.model.user.Role;
import com.sitewhere.rest.model.user.request.UserCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.user.IUserManagement;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.IRole;
import com.sitewhere.spi.user.IUser;

/**
 * Controller for user operations.
 */
@RestController
@RequestMapping("/api/users")
public class Users {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Users.class);

    @Autowired
    private IInstanceManagementMicroservice microservice;

    /**
     * Create a new user.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @PostMapping
    public IUser createUser(@RequestBody UserCreateRequest input) throws SiteWhereException {
	return getUserManagement().createUser(input);
    }

    /**
     * Update an existing user.
     * 
     * @param username
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @PutMapping("/{username}")
    public IUser updateUser(@PathVariable String username, @RequestBody UserCreateRequest input)
	    throws SiteWhereException {
	return getUserManagement().updateUser(username, input, true);
    }

    /**
     * Get a user by unique username.
     * 
     * @param username
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) throws SiteWhereException {
	IUser user = getUserManagement().getUserByUsername(username);
	if (user == null) {
	    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}
	return ResponseEntity.ok(user);
    }

    /**
     * Delete information for a given user based on username.
     * 
     * @param username
     * @return
     * @throws SiteWhereException
     */
    @DeleteMapping("/{username}")
    public IUser deleteUserByUsername(@PathVariable String username) throws SiteWhereException {
	return getUserManagement().deleteUser(username);
    }

    /**
     * List users matching criteria.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GetMapping
    public ISearchResults<IUser> listUsers() throws SiteWhereException {
	UserSearchCriteria criteria = new UserSearchCriteria();
	return getUserManagement().listUsers(criteria);
    }

    /**
     * Get a list of detailed role information for a given user.
     * 
     * @param username
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{username}/roles")
    public SearchResults<Role> getRolesForUsername(@PathVariable String username) throws SiteWhereException {
	List<IRole> matches = getUserManagement().getRoles(username);
	List<Role> converted = new ArrayList<>();
	for (IRole role : matches) {
	    converted.add(Role.copy(role));
	}
	return new SearchResults<Role>(converted);
    }

    /**
     * Add roles to a user.
     * 
     * @param username
     * @param roles
     * @return
     * @throws SiteWhereException
     */
    @PutMapping("/{username}/roles")
    public List<IRole> addRoles(@PathVariable String username, @RequestBody String[] roles) throws SiteWhereException {
	if ((roles == null) || (roles.length == 0)) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidUserInformation, ErrorLevel.ERROR);
	}
	return getUserManagement().addRoles(username, Arrays.asList(roles));
    }

    /**
     * Remove roles from a user.
     * 
     * @param username
     * @param roles
     * @return
     * @throws SiteWhereException
     */
    @DeleteMapping("/{username}/roles")
    public List<IRole> removeRoles(@PathVariable String username, @RequestBody String[] roles)
	    throws SiteWhereException {
	if ((roles == null) || (roles.length == 0)) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidUserInformation, ErrorLevel.ERROR);
	}

	return getUserManagement().removeRoles(username, Arrays.asList(roles));
    }

    protected IUserManagement getUserManagement() throws SiteWhereException {
	return getMicroservice().getUserManagement();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}