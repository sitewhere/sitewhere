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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.sitewhere.rest.model.user.RoleSearchCriteria;
import com.sitewhere.rest.model.user.request.RoleCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.user.IUserManagement;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.IRole;

/**
 * Controller for user operations.
 */
@RestController
@RequestMapping("/api/roles")
public class Roles {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Roles.class);

    @Autowired
    private IInstanceManagementMicroservice microservice;

    /**
     * Get role by name.
     * 
     * @param roleName
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{roleName}")
    public IRole getRoleByName(@PathVariable String roleName) throws SiteWhereException {
	return getUserManagement().getRoleByName(roleName);
    }

    /**
     * List all roles.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GetMapping
    public ISearchResults<IRole> listRoles() throws SiteWhereException {
	RoleSearchCriteria criteria = new RoleSearchCriteria();
	return getUserManagement().listRoles(criteria);
    }

    /**
     * Delete information for a given user based on username.
     * 
     * @param roleName
     * @return
     * @throws SiteWhereException
     */
    @DeleteMapping("/{roleName}")
    public ResponseEntity<?> deleteRoleByRoleName(@PathVariable String roleName) throws SiteWhereException {
	getUserManagement().deleteRole(roleName);
	return ResponseEntity.ok().build();
    }

    /**
     * Create a new role.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @PostMapping
    public IRole createRole(@RequestBody RoleCreateRequest input) throws SiteWhereException {
	if ((input.getRole() == null)) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidUserInformation, ErrorLevel.ERROR);
	}
	return getUserManagement().createRole(input);
    }

    /**
     * Update an existing role.
     * 
     * @param roleName
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @PutMapping("/{roleName}")
    public IRole updateRole(@PathVariable String roleName, @RequestBody RoleCreateRequest input)
	    throws SiteWhereException {
	return getUserManagement().updateRole(roleName, input);
    }

    protected IUserManagement getUserManagement() throws SiteWhereException {
	return getMicroservice().getUserManagement();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}