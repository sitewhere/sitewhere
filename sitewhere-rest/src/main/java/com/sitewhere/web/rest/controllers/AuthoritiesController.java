/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.SiteWhere;
import com.sitewhere.Tracer;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.rest.model.user.GrantedAuthoritySearchCriteria;
import com.sitewhere.rest.model.user.request.GrantedAuthorityCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.server.debug.TracerCategory;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.rest.RestController;
import com.sitewhere.web.rest.annotations.Documented;
import com.sitewhere.web.rest.annotations.DocumentedController;
import com.sitewhere.web.rest.annotations.Example;
import com.sitewhere.web.rest.annotations.Example.Stage;
import com.sitewhere.web.rest.documentation.Authorities;
import com.sitewhere.web.rest.model.GrantedAuthorityHierarchyBuilder;
import com.sitewhere.web.rest.model.GrantedAuthorityHierarchyNode;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for user operations.
 * 
 * @author Derek Adams
 */
@Controller
@CrossOrigin
@RequestMapping(value = "/authorities")
@Api(value = "authorities", description = "Operations related to SiteWhere authorities.")
@DocumentedController(name = "Granted Authorities")
public class AuthoritiesController extends RestController {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /**
     * Create a new authority.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "Create a new authority")
    @PreAuthorize(value = SiteWhereRoles.PREAUTH_REST_AND_USER_ADMIN)
    @Documented(examples = {
	    @Example(stage = Stage.Request, json = Authorities.CreateAuthorityRequest.class, description = "createUnassociatedRequest.md"),
	    @Example(stage = Stage.Response, json = Authorities.CreateAuthorityResponse.class, description = "createAssociatedResponse.md") })
    public GrantedAuthority createAuthority(@RequestBody GrantedAuthorityCreateRequest input)
	    throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "createAuthority", LOGGER);
	try {
	    IGrantedAuthority auth = getUserManagement().createGrantedAuthority(input);
	    return GrantedAuthority.copy(auth);
	} finally {
	    Tracer.stop(LOGGER);
	}
    }

    /**
     * Get an authority by unique name.
     * 
     * @param name
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Get authority by id")
    @Secured({ SiteWhereRoles.REST })
    @Documented(examples = {
	    @Example(stage = Stage.Response, json = Authorities.CreateAuthorityResponse.class, description = "getAuthorityByNameResponse.md") })
    public GrantedAuthority getAuthorityByName(
	    @ApiParam(value = "Authority name", required = true) @PathVariable String name) throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "getAuthorityByName", LOGGER);
	try {
	    IGrantedAuthority auth = getUserManagement().getGrantedAuthorityByName(name);
	    if (auth == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidAuthority, ErrorLevel.ERROR,
			HttpServletResponse.SC_NOT_FOUND);
	    }
	    return GrantedAuthority.copy(auth);
	} finally {
	    Tracer.stop(LOGGER);
	}
    }

    /**
     * List authorities that match given criteria.
     * 
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "List authorities that match criteria")
    @Secured({ SiteWhereRoles.REST })
    @Documented(examples = {
	    @Example(stage = Stage.Response, json = Authorities.ListAuthoritiesResponse.class, description = "listAuthoritiesResponse.md") })
    public SearchResults<GrantedAuthority> listAuthorities(
	    @ApiParam(value = "Max records to return", required = false) @RequestParam(defaultValue = "100") int count)
	    throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "listAuthorities", LOGGER);
	try {
	    List<GrantedAuthority> authsConv = new ArrayList<GrantedAuthority>();
	    GrantedAuthoritySearchCriteria criteria = new GrantedAuthoritySearchCriteria();
	    List<IGrantedAuthority> auths = getUserManagement().listGrantedAuthorities(criteria);
	    for (IGrantedAuthority auth : auths) {
		authsConv.add(GrantedAuthority.copy(auth));
	    }
	    return new SearchResults<GrantedAuthority>(authsConv);
	} finally {
	    Tracer.stop(LOGGER);
	}
    }

    /**
     * Get the hierarchy of granted authorities.
     * 
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/hierarchy", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Get authorities hierarchy")
    @Secured({ SiteWhereRoles.REST })
    public List<GrantedAuthorityHierarchyNode> getAuthoritiesHierarchy() throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "getAuthoritiesHierarchy", LOGGER);
	try {
	    GrantedAuthoritySearchCriteria criteria = new GrantedAuthoritySearchCriteria();
	    List<IGrantedAuthority> auths = getUserManagement().listGrantedAuthorities(criteria);
	    return GrantedAuthorityHierarchyBuilder.build(auths);
	} finally {
	    Tracer.stop(LOGGER);
	}
    }

    /**
     * Get user management implementation.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected IUserManagement getUserManagement() throws SiteWhereException {
	return SiteWhere.getServer().getUserManagement();
    }
}