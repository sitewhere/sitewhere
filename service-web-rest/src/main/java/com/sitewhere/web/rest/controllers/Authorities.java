/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.rest.model.user.GrantedAuthoritySearchCriteria;
import com.sitewhere.rest.model.user.request.GrantedAuthorityCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.spi.user.SiteWhereAuthority;
import com.sitewhere.web.annotation.SiteWhereCrossOrigin;
import com.sitewhere.web.rest.RestControllerBase;
import com.sitewhere.web.rest.model.GrantedAuthorityHierarchyBuilder;
import com.sitewhere.web.rest.model.GrantedAuthorityHierarchyNode;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for user operations.
 * 
 * @author Derek Adams
 */
@RestController
@SiteWhereCrossOrigin
@RequestMapping(value = "/authorities")
@Api(value = "authorities")
public class Authorities extends RestControllerBase {

    /**
     * Create a new authority.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @PostMapping
    @ApiOperation(value = "Create a new authority")
    public GrantedAuthority createAuthority(@RequestBody GrantedAuthorityCreateRequest input)
	    throws SiteWhereException {
	checkAuthForAll(SiteWhereAuthority.REST, SiteWhereAuthority.AdminUsers);
	IGrantedAuthority auth = getUserManagement().createGrantedAuthority(input);
	return GrantedAuthority.copy(auth);
    }

    /**
     * Get an authority by unique name.
     * 
     * @param name
     * @return
     * @throws SiteWhereException
     */
    @GetMapping(value = "/{name}")
    @ApiOperation(value = "Get authority by id")
    public GrantedAuthority getAuthorityByName(
	    @ApiParam(value = "Authority name", required = true) @PathVariable String name) throws SiteWhereException {
	checkAuthForAll(SiteWhereAuthority.REST, SiteWhereAuthority.AdminUsers);
	IGrantedAuthority auth = getUserManagement().getGrantedAuthorityByName(name);
	if (auth == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAuthority, ErrorLevel.ERROR,
		    HttpServletResponse.SC_NOT_FOUND);
	}
	return GrantedAuthority.copy(auth);
    }

    /**
     * List authorities that match given criteria.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GetMapping
    @ApiOperation(value = "List authorities that match criteria")
    public ISearchResults<IGrantedAuthority> listAuthorities() throws SiteWhereException {
	checkAuthForAll(SiteWhereAuthority.REST, SiteWhereAuthority.AdminUsers);
	GrantedAuthoritySearchCriteria criteria = new GrantedAuthoritySearchCriteria();
	return getUserManagement().listGrantedAuthorities(criteria);
    }

    /**
     * Get the hierarchy of granted authorities.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GetMapping(value = "/hierarchy")
    @ApiOperation(value = "Get authorities hierarchy")
    public List<GrantedAuthorityHierarchyNode> getAuthoritiesHierarchy() throws SiteWhereException {
	checkAuthForAll(SiteWhereAuthority.REST, SiteWhereAuthority.AdminUsers);
	GrantedAuthoritySearchCriteria criteria = new GrantedAuthoritySearchCriteria(1, 0);
	ISearchResults<IGrantedAuthority> auths = getUserManagement().listGrantedAuthorities(criteria);
	return GrantedAuthorityHierarchyBuilder.build(auths.getResults());
    }

    /**
     * Get user management implementation.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected IUserManagement getUserManagement() throws SiteWhereException {
	return getMicroservice().getUserManagementApiDemux().getApiChannel();
    }
}