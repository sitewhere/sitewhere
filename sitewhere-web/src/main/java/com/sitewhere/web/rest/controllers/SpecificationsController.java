/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.SiteWhere;
import com.sitewhere.Tracer;
import com.sitewhere.core.user.SitewhereRoles;
import com.sitewhere.device.communication.protobuf.SpecificationProtoBuilder;
import com.sitewhere.device.marshaling.DeviceSpecificationMarshalHelper;
import com.sitewhere.rest.model.device.command.DeviceCommandNamespace;
import com.sitewhere.rest.model.device.request.DeviceCommandCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceSpecificationCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.command.IDeviceCommandNamespace;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.debug.TracerCategory;
import com.sitewhere.spi.user.ITenant;
import com.sitewhere.web.rest.annotations.Concerns;
import com.sitewhere.web.rest.annotations.Concerns.ConcernType;
import com.sitewhere.web.rest.annotations.Documented;
import com.sitewhere.web.rest.annotations.DocumentedController;
import com.sitewhere.web.rest.annotations.Example;
import com.sitewhere.web.rest.annotations.Example.Stage;
import com.sitewhere.web.rest.documentation.Specifications;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for device specification operations.
 * 
 * @author Derek Adams
 */
@Controller
@RequestMapping(value = "/specifications")
@Api(value = "specifications", description = "Operations related to SiteWhere device specifications.")
@DocumentedController(name = "Device Specifications")
public class SpecificationsController extends SiteWhereController {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(SpecificationsController.class);

	/**
	 * Create a device specification.
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create new device specification")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	@Documented(examples = {
			@Example(stage = Stage.Request, json = Specifications.CreateSpecificationRequest.class, description = "createDeviceSpecificationRequest.md"),
			@Example(stage = Stage.Response, json = Specifications.CreateSpecificationResponse.class, description = "createDeviceSpecificationResponse.md") })
	public IDeviceSpecification createDeviceSpecification(
			@RequestBody DeviceSpecificationCreateRequest request, HttpServletRequest servletRequest)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "createDeviceSpecification", LOGGER);
		try {
			IAsset asset =
					SiteWhere.getServer().getAssetModuleManager(getTenant(servletRequest)).getAssetById(
							request.getAssetModuleId(), request.getAssetId());
			if (asset == null) {
				throw new SiteWhereSystemException(ErrorCode.InvalidAssetReferenceId, ErrorLevel.ERROR,
						HttpServletResponse.SC_NOT_FOUND);
			}
			IDeviceSpecification result =
					SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).createDeviceSpecification(
							request);
			DeviceSpecificationMarshalHelper helper =
					new DeviceSpecificationMarshalHelper(getTenant(servletRequest));
			helper.setIncludeAsset(true);
			return helper.convert(result,
					SiteWhere.getServer().getAssetModuleManager(getTenant(servletRequest)));
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Get a device specification by unique token.
	 * 
	 * @param hardwareId
	 * @return
	 */
	@RequestMapping(value = "/{token}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get specification by unique token")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	@Documented(examples = { @Example(stage = Stage.Response, json = Specifications.CreateSpecificationResponse.class, description = "getDeviceSpecificationByTokenResponse.md") })
	public IDeviceSpecification getDeviceSpecificationByToken(
			@ApiParam(value = "Token", required = true) @PathVariable String token,
			@ApiParam(value = "Include detailed asset information", required = false) @RequestParam(defaultValue = "true") boolean includeAsset,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "getDeviceSpecificationByToken", LOGGER);
		try {
			IDeviceSpecification result = assertDeviceSpecificationByToken(token, servletRequest);
			DeviceSpecificationMarshalHelper helper =
					new DeviceSpecificationMarshalHelper(getTenant(servletRequest));
			helper.setIncludeAsset(includeAsset);
			return helper.convert(result,
					SiteWhere.getServer().getAssetModuleManager(getTenant(servletRequest)));
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Get a device specification by unique token.
	 * 
	 * @param hardwareId
	 * @return
	 */
	@RequestMapping(value = "/{token}/proto", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get specification GPB by unique token")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	@Documented()
	public String getDeviceSpecificationProtoByToken(
			@ApiParam(value = "Token", required = true) @PathVariable String token,
			HttpServletRequest servletRequest, HttpServletResponse response) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "getDeviceSpecificationProtoByToken", LOGGER);
		try {
			IDeviceSpecification specification = assertDeviceSpecificationByToken(token, servletRequest);
			String proto =
					SpecificationProtoBuilder.getProtoForSpecification(specification,
							getTenant(servletRequest));
			response.setContentType("text/plain");
			return proto;
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Get a device specification by unique token.
	 * 
	 * @param hardwareId
	 * @return
	 */
	@RequestMapping(value = "/{token}/spec.proto", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get specification GPB file by unique token")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	@Documented()
	public ResponseEntity<byte[]> getDeviceSpecificationProtoFileByToken(
			@ApiParam(value = "Token", required = true) @PathVariable String token,
			HttpServletRequest servletRequest, HttpServletResponse response) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "getDeviceSpecificationProtoFileByToken", LOGGER);
		try {
			IDeviceSpecification specification = assertDeviceSpecificationByToken(token, servletRequest);
			String proto =
					SpecificationProtoBuilder.getProtoForSpecification(specification,
							getTenant(servletRequest));

			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.set("Content-Disposition", "attachment; filename=Spec_" + specification.getToken()
					+ ".proto");
			return new ResponseEntity<byte[]>(proto.getBytes(), headers, HttpStatus.OK);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Update an existing device specification.
	 * 
	 * @param token
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}", method = RequestMethod.PUT)
	@ResponseBody
	@ApiOperation(value = "Update existing device specification")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	@Documented(examples = {
			@Example(stage = Stage.Request, json = Specifications.UpdateSpecificationRequest.class, description = "updateDeviceSpecificationRequest.md"),
			@Example(stage = Stage.Response, json = Specifications.UpdateSpecificationResponse.class, description = "updateDeviceSpecificationResponse.md") })
	public IDeviceSpecification updateDeviceSpecification(
			@ApiParam(value = "Token", required = true) @PathVariable String token,
			@RequestBody DeviceSpecificationCreateRequest request, HttpServletRequest servletRequest)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "updateDeviceSpecification", LOGGER);
		try {
			IDeviceSpecification result =
					SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).updateDeviceSpecification(
							token, request);
			DeviceSpecificationMarshalHelper helper =
					new DeviceSpecificationMarshalHelper(getTenant(servletRequest));
			helper.setIncludeAsset(true);
			return helper.convert(result,
					SiteWhere.getServer().getAssetModuleManager(getTenant(servletRequest)));
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * List device specifications that meet the given criteria.
	 * 
	 * @param includeDeleted
	 * @param includeAsset
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "List specifications that match criteria")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	@Documented(examples = { @Example(stage = Stage.Response, json = Specifications.ListSpecificationsResponse.class, description = "listDeviceSpecificationsResponse.md") })
	public ISearchResults<IDeviceSpecification> listDeviceSpecifications(
			@ApiParam(value = "Include deleted", required = false) @RequestParam(defaultValue = "false") boolean includeDeleted,
			@ApiParam(value = "Include detailed asset information", required = false) @RequestParam(defaultValue = "true") boolean includeAsset,
			@ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") @Concerns(values = { ConcernType.Paging }) int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") @Concerns(values = { ConcernType.Paging }) int pageSize,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "listDeviceSpecifications", LOGGER);
		try {
			SearchCriteria criteria = new SearchCriteria(page, pageSize);
			ISearchResults<IDeviceSpecification> results =
					SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).listDeviceSpecifications(
							includeDeleted, criteria);
			DeviceSpecificationMarshalHelper helper =
					new DeviceSpecificationMarshalHelper(getTenant(servletRequest));
			helper.setIncludeAsset(includeAsset);
			List<IDeviceSpecification> specsConv = new ArrayList<IDeviceSpecification>();
			for (IDeviceSpecification device : results.getResults()) {
				specsConv.add(helper.convert(device,
						SiteWhere.getServer().getAssetModuleManager(getTenant(servletRequest))));
			}
			Collections.sort(specsConv, new Comparator<IDeviceSpecification>() {
				public int compare(IDeviceSpecification o1, IDeviceSpecification o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			return new SearchResults<IDeviceSpecification>(specsConv, results.getNumResults());
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Delete an existing device specification.
	 * 
	 * @param token
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}", method = RequestMethod.DELETE)
	@ResponseBody
	@ApiOperation(value = "Delete existing device specification")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	@Documented(examples = { @Example(stage = Stage.Response, json = Specifications.CreateSpecificationResponse.class, description = "deleteDeviceSpecificationResponse.md") })
	public IDeviceSpecification deleteDeviceSpecification(
			@ApiParam(value = "Token", required = true) @PathVariable String token,
			@ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "deleteDeviceSpecification", LOGGER);
		try {
			ITenant tenant = getTenant(servletRequest);
			IDeviceSpecification result =
					SiteWhere.getServer().getDeviceManagement(tenant).deleteDeviceSpecification(token, force);
			DeviceSpecificationMarshalHelper helper = new DeviceSpecificationMarshalHelper(tenant);
			helper.setIncludeAsset(true);
			return helper.convert(result, SiteWhere.getServer().getAssetModuleManager(tenant));
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Create a device specification.
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/{token}/commands", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create device command for specification.")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	@Documented(examples = {
			@Example(stage = Stage.Request, json = Specifications.CreateDeviceCommandRequest.class, description = "createDeviceCommandRequest.md"),
			@Example(stage = Stage.Response, json = Specifications.CreateDeviceCommandResponse.class, description = "createDeviceCommandResponse.md") })
	public IDeviceCommand createDeviceCommand(
			@ApiParam(value = "Token", required = true) @PathVariable String token,
			@RequestBody DeviceCommandCreateRequest request, HttpServletRequest servletRequest)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "createDeviceCommand", LOGGER);
		try {
			IDeviceSpecification spec = assertDeviceSpecificationByToken(token, servletRequest);
			IDeviceCommand result =
					SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).createDeviceCommand(
							spec, request);
			return result;
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	@RequestMapping(value = "/{token}/commands", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "List device commands for specification")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	@Documented(examples = { @Example(stage = Stage.Response, json = Specifications.ListDeviceCommandsResponse.class, description = "listDeviceCommandsResponse.md") })
	public ISearchResults<IDeviceCommand> listDeviceCommands(
			@ApiParam(value = "Token", required = true) @PathVariable String token,
			@ApiParam(value = "Include deleted", required = false) @RequestParam(defaultValue = "false") boolean includeDeleted,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "listDeviceCommands", LOGGER);
		try {
			List<IDeviceCommand> results =
					SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).listDeviceCommands(
							token, includeDeleted);
			Collections.sort(results, new Comparator<IDeviceCommand>() {
				public int compare(IDeviceCommand o1, IDeviceCommand o2) {
					if (o1.getName().equals(o2.getName())) {
						return o1.getNamespace().compareTo(o2.getNamespace());
					}
					return o1.getName().compareTo(o2.getName());
				}
			});
			return new SearchResults<IDeviceCommand>(results);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * List commands grouped by namespace.
	 * 
	 * @param token
	 * @param includeDeleted
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/namespaces", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "List device commands by namespace")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	@Documented(examples = { @Example(stage = Stage.Response, json = Specifications.ListDeviceCommandsByNamespaceResponse.class, description = "listDeviceCommandsByNamespaceResponse.md") })
	public ISearchResults<IDeviceCommandNamespace> listDeviceCommandsByNamespace(
			@ApiParam(value = "Token", required = true) @PathVariable String token,
			@ApiParam(value = "Include deleted", required = false) @RequestParam(defaultValue = "false") boolean includeDeleted,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "listDeviceCommandsByNamespace", LOGGER);
		try {
			List<IDeviceCommand> results =
					SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).listDeviceCommands(
							token, includeDeleted);
			Collections.sort(results, new Comparator<IDeviceCommand>() {
				public int compare(IDeviceCommand o1, IDeviceCommand o2) {
					if ((o1.getNamespace() == null) && (o2.getNamespace() != null)) {
						return -1;
					}
					if ((o1.getNamespace() != null) && (o2.getNamespace() == null)) {
						return 1;
					}
					if ((o1.getNamespace() == null) && (o2.getNamespace() == null)) {
						return o1.getName().compareTo(o2.getName());
					}
					if (!o1.getNamespace().equals(o2.getNamespace())) {
						return o1.getNamespace().compareTo(o2.getNamespace());
					}
					return o1.getName().compareTo(o2.getName());
				}
			});
			List<IDeviceCommandNamespace> namespaces = new ArrayList<IDeviceCommandNamespace>();
			DeviceCommandNamespace current = null;
			for (IDeviceCommand command : results) {
				if ((current == null)
						|| ((current.getValue() == null) && (command.getNamespace() != null))
						|| ((current.getValue() != null) && (!current.getValue().equals(
								command.getNamespace())))) {
					current = new DeviceCommandNamespace();
					current.setValue(command.getNamespace());
					namespaces.add(current);
				}
				current.getCommands().add(command);
			}
			return new SearchResults<IDeviceCommandNamespace>(namespaces);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Gets a device specification by token and throws an exception if not found.
	 * 
	 * @param token
	 * @param servletRequest
	 * @return
	 * @throws SiteWhereException
	 */
	protected IDeviceSpecification assertDeviceSpecificationByToken(String token,
			HttpServletRequest servletRequest) throws SiteWhereException {
		IDeviceSpecification result =
				SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).getDeviceSpecificationByToken(
						token);
		if (result == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceSpecificationToken, ErrorLevel.ERROR,
					HttpServletResponse.SC_NOT_FOUND);
		}
		return result;
	}
}