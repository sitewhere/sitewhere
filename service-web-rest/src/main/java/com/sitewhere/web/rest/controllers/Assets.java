/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.rest.model.asset.AssetReference;
import com.sitewhere.rest.model.asset.request.AssetCategoryCreateRequest;
import com.sitewhere.rest.model.asset.request.HardwareAssetCreateRequest;
import com.sitewhere.rest.model.asset.request.LocationAssetCreateRequest;
import com.sitewhere.rest.model.asset.request.PersonAssetCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.device.AssignmentsForAssetSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetModuleDescriptor;
import com.sitewhere.spi.asset.IAssetReference;
import com.sitewhere.spi.asset.IAssetResolver;
import com.sitewhere.spi.asset.IHardwareAsset;
import com.sitewhere.spi.asset.ILocationAsset;
import com.sitewhere.spi.asset.IPersonAsset;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.rest.RestControllerBase;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for site operations.
 * 
 * @author Derek Adams
 */
@RestController
@CrossOrigin(exposedHeaders = { "X-SiteWhere-Error", "X-SiteWhere-Error-Code" })
@RequestMapping(value = "/assets")
@Api(value = "assets")
public class Assets extends RestControllerBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    /**
     * Search for assets in an {@link IAssetModule} that meet the given criteria.
     * 
     * @param assetModuleId
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/modules/{assetModuleId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get an asset module by unique id")
    @Secured({ SiteWhereRoles.REST })
    public IAssetModuleDescriptor getAssetModule(
	    @ApiParam(value = "Unique asset module id", required = true) @PathVariable String assetModuleId,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	return getAssetResolver().getAssetModuleManagement().getAssetModuleDescriptor(assetModuleId);
    }

    /**
     * Search for assets in an {@link IAssetModule} that meet the given criteria.
     * 
     * @param assetModuleId
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/modules/{assetModuleId}/assets", method = RequestMethod.GET)
    @ApiOperation(value = "Search for assets in an asset module")
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Secured({ SiteWhereRoles.REST })
    public SearchResults<? extends IAsset> searchAssets(
	    @ApiParam(value = "Unique asset module id", required = true) @PathVariable String assetModuleId,
	    @ApiParam(value = "Criteria for search", required = false) @RequestParam(defaultValue = "") String criteria,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	List<? extends IAsset> found = getAssetResolver().getAssetModuleManagement().searchAssetModule(assetModuleId,
		criteria);
	SearchResults<? extends IAsset> results = new SearchResults(found);
	return results;
    }

    /**
     * Get an asset from an {@link IAssetModule} by unique id.
     * 
     * @param moduleId
     * @param assetId
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/modules/{assetModuleId}/assets/{assetId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get an asset by unique id")
    @Secured({ SiteWhereRoles.REST })
    public IAsset getAssetById(
	    @ApiParam(value = "Unique asset module id", required = true) @PathVariable String assetModuleId,
	    @ApiParam(value = "Unique asset id", required = true) @PathVariable String assetId,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	return getAssetResolver().getAssetModuleManagement()
		.getAsset(new AssetReference.Builder(assetModuleId, assetId).build());
    }

    /**
     * Get all assignments for a given asset.
     * 
     * @param moduleId
     * @param assetId
     * @param siteToken
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/modules/{assetModuleId}/assets/{assetId}/assignments", method = RequestMethod.GET)
    @ApiOperation(value = "List assignments associated with an asset")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceAssignment> getAssignmentsForAsset(
	    @ApiParam(value = "Unique asset module id", required = true) @PathVariable String moduleId,
	    @ApiParam(value = "Unique asset id", required = true) @PathVariable String assetId,
	    @ApiParam(value = "Limit results to the given area", required = false) @RequestParam(required = false) String areaToken,
	    @ApiParam(value = "Limit results to the given status", required = false) @RequestParam(required = false) String status,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	try {
	    AssignmentsForAssetSearchCriteria criteria = new AssignmentsForAssetSearchCriteria(page, pageSize);
	    DeviceAssignmentStatus decodedStatus = (status != null) ? DeviceAssignmentStatus.valueOf(status) : null;
	    criteria.setStatus(decodedStatus);
	    criteria.setAreaToken(areaToken);

	    IAssetReference assetReference = new AssetReference.Builder(moduleId, assetId).build();
	    return getDeviceManagement().getDeviceAssignmentsForAsset(assetReference, criteria);
	} catch (IllegalArgumentException e) {
	    throw new SiteWhereException("Invalid device assignment status: " + status);
	}
    }

    /**
     * List all asset modules.
     * 
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/modules", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "List asset modules that match criteria")
    @Secured({ SiteWhereRoles.REST })
    public List<IAssetModuleDescriptor> listAssetModules(
	    @ApiParam(value = "Asset type", required = false) @RequestParam(required = false) String assetType,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	try {
	    AssetType type = (assetType == null) ? null : AssetType.valueOf(assetType);
	    return getAssetResolver().getAssetModuleManagement().listAssetModuleDescriptors(type);
	} catch (IllegalArgumentException e) {
	    throw new SiteWhereSystemException(ErrorCode.UnknownAssetType, ErrorLevel.ERROR);
	}
    }

    /**
     * Create a new asset category.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/categories", method = RequestMethod.POST)
    @ApiOperation(value = "Create a new asset category")
    @Secured({ SiteWhereRoles.REST })
    public IAssetCategory createAssetCategory(@RequestBody AssetCategoryCreateRequest request,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	return getAssetManagement().createAssetCategory(request);
    }

    /**
     * Update an existing asset category.
     * 
     * @param categoryId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/categories/{categoryId}", method = RequestMethod.PUT)
    @ApiOperation(value = "Update an existing asset category")
    @Secured({ SiteWhereRoles.REST })
    public IAssetCategory updateAssetCategory(
	    @ApiParam(value = "Unique category id", required = true) @PathVariable String categoryId,
	    @RequestBody AssetCategoryCreateRequest request, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	return getAssetManagement().updateAssetCategory(categoryId, request);
    }

    /**
     * Get an asset category by unique id.
     * 
     * @param categoryId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/categories/{categoryId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get an asset category by unique id")
    @Secured({ SiteWhereRoles.REST })
    public IAssetCategory getAssetCategoryById(
	    @ApiParam(value = "Unique category id", required = true) @PathVariable String categoryId,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	return getAssetManagement().getAssetCategory(categoryId);
    }

    /**
     * Delete an existing asset category.
     * 
     * @param categoryId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/categories/{categoryId}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete an existing asset category")
    @Secured({ SiteWhereRoles.REST })
    public IAssetCategory deleteAssetCategory(
	    @ApiParam(value = "Unique category id", required = true) @PathVariable String categoryId,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	return getAssetManagement().deleteAssetCategory(categoryId);
    }

    /**
     * List asset categories that match the given search criteria.
     * 
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    @ApiOperation(value = "List asset categories that match criteria")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IAssetCategory> listAssetCategories(
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	return getAssetManagement().listAssetCategories(criteria);
    }

    /**
     * Creates a new person asset in the category. If the category does not support
     * person assets, an exception will be thrown.
     * 
     * @param categoryId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/categories/{categoryId}/persons", method = RequestMethod.POST)
    @ApiOperation(value = "Create a new person asset in category")
    @Secured({ SiteWhereRoles.REST })
    public IPersonAsset createPersonAsset(
	    @ApiParam(value = "Unique category id", required = true) @PathVariable String categoryId,
	    @RequestBody PersonAssetCreateRequest request, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	return getAssetManagement().createPersonAsset(categoryId, request);
    }

    /**
     * Update an existing person asset.
     * 
     * @param categoryId
     * @param assetId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/categories/{categoryId}/persons/{assetId}", method = RequestMethod.PUT)
    @ApiOperation(value = "Update an existing person asset in category")
    @Secured({ SiteWhereRoles.REST })
    public IPersonAsset updatePersonAsset(
	    @ApiParam(value = "Unique category id", required = true) @PathVariable String categoryId,
	    @ApiParam(value = "Unique asset id", required = true) @PathVariable String assetId,
	    @RequestBody PersonAssetCreateRequest request, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	return getAssetManagement().updatePersonAsset(categoryId, assetId, request);
    }

    /**
     * Creates a new hardware asset in the category. If the category does not
     * support hardware assets, an exception will be thrown.
     * 
     * @param categoryId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/categories/{categoryId}/hardware", method = RequestMethod.POST)
    @ApiOperation(value = "Create a new hardware asset in category")
    @Secured({ SiteWhereRoles.REST })
    public IHardwareAsset createHardwareAsset(
	    @ApiParam(value = "Unique category id", required = true) @PathVariable String categoryId,
	    @RequestBody HardwareAssetCreateRequest request, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	return getAssetManagement().createHardwareAsset(categoryId, request);
    }

    /**
     * Update an existing hardware asset.
     * 
     * @param categoryId
     * @param assetId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/categories/{categoryId}/hardware/{assetId}", method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation(value = "Update an existing hardware asset in category")
    @Secured({ SiteWhereRoles.REST })
    public IHardwareAsset updateHardwareAsset(
	    @ApiParam(value = "Unique category id", required = true) @PathVariable String categoryId,
	    @ApiParam(value = "Unique asset id", required = true) @PathVariable String assetId,
	    @RequestBody HardwareAssetCreateRequest request, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	return getAssetManagement().updateHardwareAsset(categoryId, assetId, request);
    }

    /**
     * Creates a new location asset in the category. If the category does not
     * support location assets, an exception will be thrown.
     * 
     * @param categoryId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/categories/{categoryId}/locations", method = RequestMethod.POST)
    @ApiOperation(value = "Create a new location asset in category")
    @Secured({ SiteWhereRoles.REST })
    public ILocationAsset createLocationAsset(
	    @ApiParam(value = "Unique category id", required = true) @PathVariable String categoryId,
	    @RequestBody LocationAssetCreateRequest request, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	return getAssetManagement().createLocationAsset(categoryId, request);
    }

    /**
     * Update an existing location asset.
     * 
     * @param categoryId
     * @param assetId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/categories/{categoryId}/locations/{assetId}", method = RequestMethod.PUT)
    @ApiOperation(value = "Update an existing location asset in category")
    @Secured({ SiteWhereRoles.REST })
    public ILocationAsset updateLocationAsset(
	    @ApiParam(value = "Unique category id", required = true) @PathVariable String categoryId,
	    @ApiParam(value = "Unique asset id", required = true) @PathVariable String assetId,
	    @RequestBody LocationAssetCreateRequest request, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	return getAssetManagement().updateLocationAsset(categoryId, assetId, request);
    }

    /**
     * Get an asset from a category by unique id.
     * 
     * @param categoryId
     * @param assetId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/categories/{categoryId}/assets/{assetId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get a category asset by unique id")
    @Secured({ SiteWhereRoles.REST })
    public IAsset getCategoryAsset(
	    @ApiParam(value = "Unique category id", required = true) @PathVariable String categoryId,
	    @ApiParam(value = "Unique asset id", required = true) @PathVariable String assetId,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	return getAssetManagement().getAsset(categoryId, assetId);
    }

    /**
     * Delete an asset from a category based on unique id.
     * 
     * @param categoryId
     * @param assetId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/categories/{categoryId}/assets/{assetId}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete an existing category asset")
    @Secured({ SiteWhereRoles.REST })
    public IAsset deleteCategoryAsset(
	    @ApiParam(value = "Unique category id", required = true) @PathVariable String categoryId,
	    @ApiParam(value = "Unique asset id", required = true) @PathVariable String assetId,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	return getAssetManagement().deleteAsset(categoryId, assetId);
    }

    /**
     * List all assets for a given category.
     * 
     * @param categoryId
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/categories/{categoryId}/assets", method = RequestMethod.GET)
    @ApiOperation(value = "List category assets that match criteria")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IAsset> listCategoryAssets(
	    @ApiParam(value = "Unique category id", required = true) @PathVariable String categoryId,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	return getAssetManagement().listAssets(categoryId, criteria);
    }

    private IAssetResolver getAssetResolver() {
	return getMicroservice().getAssetResolver();
    }

    private IAssetManagement getAssetManagement() throws SiteWhereException {
	return getMicroservice().getAssetManagementApiDemux().getApiChannel();
    }

    private IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagementApiDemux().getApiChannel();
    }
}