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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.rest.model.user.GrantedAuthoritySearchCriteria;
import com.sitewhere.rest.model.user.request.GrantedAuthorityCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.user.IUserManagement;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.web.rest.model.GrantedAuthorityHierarchyBuilder;
import com.sitewhere.web.rest.model.GrantedAuthorityHierarchyNode;

/**
 * Controller for user operations.
 */
@RestController
@RequestMapping("/api/authorities")
public class Authorities {

    @Autowired
    private IInstanceManagementMicroservice microservice;

    /**
     * Create a new authority.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @PostMapping
    public GrantedAuthority createAuthority(@RequestBody GrantedAuthorityCreateRequest input)
	    throws SiteWhereException {
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
    @GetMapping("/{name}")
    public GrantedAuthority getAuthorityByName(@PathVariable String name) throws SiteWhereException {
	IGrantedAuthority auth = getUserManagement().getGrantedAuthorityByName(name);
	if (auth == null) {
	    return null;
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
    public ISearchResults<IGrantedAuthority> listAuthorities() throws SiteWhereException {
	GrantedAuthoritySearchCriteria criteria = new GrantedAuthoritySearchCriteria();
	return getUserManagement().listGrantedAuthorities(criteria);
    }

    /**
     * Get the hierarchy of granted authorities.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/hierarchy")
    public List<GrantedAuthorityHierarchyNode> getAuthoritiesHierarchy() throws SiteWhereException {
	GrantedAuthoritySearchCriteria criteria = new GrantedAuthoritySearchCriteria(1, 0);
	ISearchResults<IGrantedAuthority> auths = getUserManagement().listGrantedAuthorities(criteria);
	return GrantedAuthorityHierarchyBuilder.build(auths.getResults());
    }

    protected IUserManagement getUserManagement() throws SiteWhereException {
	return getMicroservice().getUserManagement();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}