package com.sitewhere.web.rest.controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
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
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.tenant.TenantGroup;
import com.sitewhere.rest.model.tenant.request.TenantGroupCreateRequest;
import com.sitewhere.rest.model.tenant.request.TenantGroupElementCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.debug.TracerCategory;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.ITenantGroup;
import com.sitewhere.spi.tenant.ITenantGroupElement;
import com.sitewhere.spi.tenant.request.ITenantGroupElementCreateRequest;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.tenant.marshaling.TenantGroupElementMarshalHelper;
import com.sitewhere.web.rest.RestController;
import com.sitewhere.web.rest.annotations.Concerns;
import com.sitewhere.web.rest.annotations.Concerns.ConcernType;
import com.sitewhere.web.rest.annotations.DocumentedController;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for tenant group operations.
 * 
 * @author Derek Adams
 */
@Controller
@CrossOrigin
@RequestMapping(value = "/tgroups")
@Api(value = "tgroups", description = "Operations related to SiteWhere tenant groups.")
@DocumentedController(name = "Tenant Groups")
public class TenantGroupsController extends RestController {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(TenantGroupsController.class);

	/**
	 * Create a tenant group.
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create new tenant group")
	@Secured({ SiteWhereRoles.REST })
	public ITenantGroup createTenantGroup(@RequestBody TenantGroupCreateRequest request) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "createTenantGroup", LOGGER);
		try {
			ITenantGroup result = SiteWhere.getServer().getTenantManagement().createTenantGroup(request);
			return TenantGroup.copy(result);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Get a tenant group by unique id.
	 * 
	 * @param groupId
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get a tenant group by unique token")
	@Secured({ SiteWhereRoles.REST })
	public ITenantGroup getTenantGroupById(
			@ApiParam(value = "Unique token that identifies group", required = true) @PathVariable String token)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "getTenantGroupById", LOGGER);
		try {
			ITenantGroup group = SiteWhere.getServer().getTenantManagement().getTenantGroupByToken(token);
			if (group == null) {
				throw new SiteWhereSystemException(ErrorCode.InvalidTenantGroupId, ErrorLevel.ERROR);
			}
			return TenantGroup.copy(group);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Update an existing tenant group.
	 * 
	 * @param groupId
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{groupId}", method = RequestMethod.PUT)
	@ResponseBody
	@ApiOperation(value = "Update an existing tenant group")
	@Secured({ SiteWhereRoles.REST })
	public ITenantGroup updateTenantGroup(
			@ApiParam(value = "Unique tenant group id", required = true) @PathVariable String groupId,
			@RequestBody TenantGroupCreateRequest request) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "updateTenantGroup", LOGGER);
		try {
			ITenantGroup group = SiteWhere.getServer().getTenantManagement().updateTenantGroup(groupId, request);
			return TenantGroup.copy(group);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Delete an existing tenant group.
	 * 
	 * @param groupId
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{groupId}", method = RequestMethod.DELETE)
	@ResponseBody
	@ApiOperation(value = "Delete tenant group by unique id")
	@Secured({ SiteWhereRoles.REST })
	public ITenantGroup deleteTenantGroup(
			@ApiParam(value = "Unique tenant group id", required = true) @PathVariable String groupId)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "deleteTenantGroup", LOGGER);
		try {
			ITenantGroup group = SiteWhere.getServer().getTenantManagement().deleteTenantGroup(groupId, true);
			return TenantGroup.copy(group);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * List tenant groups that match the given criteria.
	 * 
	 * @param includeDeleted
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "List tenant groups that match criteria")
	@Secured({ SiteWhereRoles.REST })
	public ISearchResults<ITenantGroup> listTenantGroups(
			@ApiParam(value = "Include deleted", required = false) @RequestParam(defaultValue = "false") boolean includeDeleted,
			@ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") @Concerns(values = {
					ConcernType.Paging }) int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") @Concerns(values = {
					ConcernType.Paging }) int pageSize)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "listTenantGroups", LOGGER);
		try {
			SearchCriteria criteria = new SearchCriteria(page, pageSize);
			ISearchResults<ITenantGroup> results = SiteWhere.getServer().getTenantManagement()
					.listTenantGroups(criteria);
			List<ITenantGroup> converted = new ArrayList<ITenantGroup>();
			for (ITenantGroup group : results.getResults()) {
				converted.add(TenantGroup.copy(group));
			}
			return new SearchResults<ITenantGroup>(converted, results.getNumResults());
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * List tenant group elements that meet the given criteria.
	 * 
	 * @param groupId
	 * @param includeTenantDetails
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{groupId}/elements", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "List elements in a tenant group")
	@Secured({ SiteWhereRoles.REST })
	public ISearchResults<ITenantGroupElement> listTenantGroupElements(
			@ApiParam(value = "Unique tenant group id", required = true) @PathVariable String groupId,
			@ApiParam(value = "Include detailed tenant information", required = false) @RequestParam(defaultValue = "false") boolean includeTenantDetails,
			@ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") @Concerns(values = {
					ConcernType.Paging }) int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") @Concerns(values = {
					ConcernType.Paging }) int pageSize)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "listTenantGroupElements", LOGGER);
		try {
			TenantGroupElementMarshalHelper helper = new TenantGroupElementMarshalHelper()
					.setIncludeTenantDetails(includeTenantDetails);
			SearchCriteria criteria = new SearchCriteria(page, pageSize);
			ISearchResults<ITenantGroupElement> results = SiteWhere.getServer().getTenantManagement()
					.listTenantGroupElements(groupId, criteria);
			List<ITenantGroupElement> converted = new ArrayList<ITenantGroupElement>();
			for (ITenantGroupElement elm : results.getResults()) {
				converted.add(helper.convert(elm));
			}
			return new SearchResults<ITenantGroupElement>(converted, results.getNumResults());
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Add elements to a tenant group.
	 * 
	 * @param groupId
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{groupId}/elements", method = RequestMethod.PUT)
	@ResponseBody
	@ApiOperation(value = "Add elements to tenant group")
	@Secured({ SiteWhereRoles.REST })
	public ISearchResults<ITenantGroupElement> addTenantGroupElements(
			@ApiParam(value = "Unique tenant group id", required = true) @PathVariable String groupId,
			@RequestBody List<TenantGroupElementCreateRequest> request) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "addTenantGroupElements", LOGGER);
		try {
			TenantGroupElementMarshalHelper helper = new TenantGroupElementMarshalHelper();
			List<ITenantGroupElementCreateRequest> elements = (List<ITenantGroupElementCreateRequest>) (List<? extends ITenantGroupElementCreateRequest>) request;

			// Validate the list of new elements.
			validateTenantGroupElements(request);

			List<ITenantGroupElement> results = SiteWhere.getServer().getTenantManagement()
					.addTenantGroupElements(groupId, elements);
			List<ITenantGroupElement> converted = new ArrayList<ITenantGroupElement>();
			for (ITenantGroupElement elm : results) {
				converted.add(helper.convert(elm));
			}
			return new SearchResults<ITenantGroupElement>(converted);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Validate that all elements reference valid tenants.
	 * 
	 * @param elements
	 * @throws SiteWhereException
	 */
	protected void validateTenantGroupElements(List<TenantGroupElementCreateRequest> elements)
			throws SiteWhereException {
		for (TenantGroupElementCreateRequest request : elements) {
			ITenant tenant = SiteWhere.getServer().getTenantManagement().getTenantById(request.getTenantId());
			if (tenant == null) {
				throw new SiteWhereSystemException(ErrorCode.InvalidTenantId, ErrorLevel.ERROR);
			}
		}
	}

	/**
	 * Delete elements from a tenant group.
	 * 
	 * @param groupId
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{groupId}/elements", method = RequestMethod.DELETE)
	@ResponseBody
	@ApiOperation(value = "Delete elements from tenant group")
	@Secured({ SiteWhereRoles.REST })
	public ISearchResults<ITenantGroupElement> deleteTenantGroupElements(
			@ApiParam(value = "Unique tenant group id", required = true) @PathVariable String groupId,
			@RequestBody List<TenantGroupElementCreateRequest> request) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "deleteTenantGroupElements", LOGGER);
		try {
			TenantGroupElementMarshalHelper helper = new TenantGroupElementMarshalHelper();
			List<ITenantGroupElementCreateRequest> elements = (List<ITenantGroupElementCreateRequest>) (List<? extends ITenantGroupElementCreateRequest>) request;
			List<ITenantGroupElement> results = SiteWhere.getServer().getTenantManagement()
					.removeTenantGroupElements(groupId, elements);
			List<ITenantGroupElement> converted = new ArrayList<ITenantGroupElement>();
			for (ITenantGroupElement elm : results) {
				converted.add(helper.convert(elm));
			}
			return new SearchResults<ITenantGroupElement>(converted);
		} finally {
			Tracer.stop(LOGGER);
		}
	}
}