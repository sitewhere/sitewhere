/*
 * UsersController.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.SiteWhere;
import com.sitewhere.core.user.SitewhereRoles;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.rest.model.user.User;
import com.sitewhere.rest.model.user.UserSearchCriteria;
import com.sitewhere.rest.model.user.request.UserCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.user.AccountStatus;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUser;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for user operations.
 * 
 * @author Derek Adams
 */
@Controller
@RequestMapping(value = "/users")
@Api(value = "", description = "Operations related to SiteWhere users.")
public class UsersController extends SiteWhereController {

	/**
	 * Create a new user.
	 * 
	 * @param input
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create a new user")
	@Secured({ SitewhereRoles.ROLE_ADMINISTER_USERS })
	public User createUser(@RequestBody UserCreateRequest input) throws SiteWhereException {
		if ((input.getUsername() == null) || (input.getPassword() == null) || (input.getFirstName() == null)
				|| (input.getLastName() == null)) {
			throw new SiteWhereSystemException(ErrorCode.InvalidUserInformation, ErrorLevel.ERROR);
		}
		if (input.getStatus() == null) {
			input.setStatus(AccountStatus.Active);
		}
		IUser user = SiteWhere.getServer().getUserManagement().createUser(input);
		return User.copy(user);
	}

	/**
	 * Update an existing user.
	 * 
	 * @param input
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{username}", method = RequestMethod.PUT)
	@ResponseBody
	@ApiOperation(value = "Update an existing user.")
	@Secured({ SitewhereRoles.ROLE_ADMINISTER_USERS })
	public User updateUser(
			@ApiParam(value = "Unique username", required = true) @PathVariable String username,
			@RequestBody UserCreateRequest input) throws SiteWhereException {
		IUser user = SiteWhere.getServer().getUserManagement().updateUser(username, input);
		return User.copy(user);
	}

	/**
	 * Get a user by unique username.
	 * 
	 * @param username
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{username}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Find user by unique username")
	@Secured({ SitewhereRoles.ROLE_ADMINISTER_USERS })
	public User getUserByUsername(
			@ApiParam(value = "Unique username", required = true) @PathVariable String username)
			throws SiteWhereException {
		IUser user = SiteWhere.getServer().getUserManagement().getUserByUsername(username);
		if (user == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidUsername, ErrorLevel.ERROR,
					HttpServletResponse.SC_NOT_FOUND);
		}
		return User.copy(user);
	}

	/**
	 * Delete information for a given user based on username.
	 * 
	 * @param siteToken
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{username}", method = RequestMethod.DELETE)
	@ResponseBody
	@ApiOperation(value = "Delete a user by unique username")
	@Secured({ SitewhereRoles.ROLE_ADMINISTER_USERS })
	public User deleteUserByUsername(
			@ApiParam(value = "Unique username", required = true) @PathVariable String username,
			@ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force)
			throws SiteWhereException {
		IUser user = SiteWhere.getServer().getUserManagement().deleteUser(username, force);
		return User.copy(user);
	}

	/**
	 * Get a list of detailed authority information for a given user.
	 * 
	 * @param username
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{username}/authorities", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Find authorities assigned to a given user")
	@Secured({ SitewhereRoles.ROLE_ADMINISTER_USERS })
	public SearchResults<GrantedAuthority> getAuthoritiesForUsername(
			@ApiParam(value = "Unique username", required = true) @PathVariable String username)
			throws SiteWhereException {
		List<IGrantedAuthority> matches =
				SiteWhere.getServer().getUserManagement().getGrantedAuthorities(username);
		List<GrantedAuthority> converted = new ArrayList<GrantedAuthority>();
		for (IGrantedAuthority auth : matches) {
			converted.add(GrantedAuthority.copy(auth));
		}
		return new SearchResults<GrantedAuthority>(converted);
	}

	/**
	 * List devices that match given criteria.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "List users that match certain criteria")
	@Secured({ SitewhereRoles.ROLE_ADMINISTER_USERS })
	public SearchResults<User> listUsers(
			@ApiParam(value = "Include deleted", required = false) @RequestParam(defaultValue = "false") boolean includeDeleted,
			@ApiParam(value = "Max records to return", required = false) @RequestParam(defaultValue = "100") int count)
			throws SiteWhereException {
		List<User> usersConv = new ArrayList<User>();
		UserSearchCriteria criteria = new UserSearchCriteria();
		criteria.setIncludeDeleted(includeDeleted);
		List<IUser> users = SiteWhere.getServer().getUserManagement().listUsers(criteria);
		for (IUser user : users) {
			usersConv.add(User.copy(user));
		}
		SearchResults<User> results = new SearchResults<User>(usersConv);
		return results;
	}
}