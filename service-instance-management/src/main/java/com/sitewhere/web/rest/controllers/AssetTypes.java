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
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.api.asset.AssetTypeMarshalHelper;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.label.ILabelGeneration;
import com.sitewhere.rest.model.asset.AssetType;
import com.sitewhere.rest.model.asset.request.AssetTypeCreateRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.asset.AssetTypeSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Controller for asset type operations.
 */
@RestController
@RequestMapping("/api/assettypes")
public class AssetTypes {

    @Autowired
    private IInstanceManagementMicroservice microservice;

    /**
     * Create a new asset type.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PostMapping
    public IAssetType createAssetType(@RequestBody AssetTypeCreateRequest request) throws SiteWhereException {
	return getAssetManagement().createAssetType(request);
    }

    /**
     * Get information for an asset type based on token.
     * 
     * @param assetTypeToken
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{assetTypeToken}")
    public AssetType getAssetTypeByToken(@PathVariable String assetTypeToken) throws SiteWhereException {
	IAssetType existing = assureAssetType(assetTypeToken);
	AssetTypeMarshalHelper helper = new AssetTypeMarshalHelper(getAssetManagement());
	return helper.convert(existing);
    }

    /**
     * Update an existing asset type.
     * 
     * @param assetTypeToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PutMapping("/{assetTypeToken}")
    public IAssetType updateAssetType(@PathVariable String assetTypeToken, @RequestBody AssetTypeCreateRequest request)
	    throws SiteWhereException {
	IAssetType existing = assureAssetType(assetTypeToken);
	return getAssetManagement().updateAssetType(existing.getId(), request);
    }

    /**
     * Get label for asset type based on a specific generator.
     * 
     * @param assetTypeToken
     * @param generatorId
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{assetTypeToken}/label/{generatorId}")
    public ResponseEntity<?> getAssetTypeLabel(@PathVariable String assetTypeToken, @PathVariable String generatorId)
	    throws SiteWhereException {
	IAssetType existing = assureAssetType(assetTypeToken);
	ILabel label = getLabelGeneration().getAssetTypeLabel(generatorId, existing.getId());
	if (label == null) {
	    return ResponseEntity.notFound().build();
	}
	return ResponseEntity.ok(label.getContent());
    }

    /**
     * List asset types matching criteria.
     * 
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GetMapping
    public SearchResults<IAssetType> listAssetTypes(@RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize) throws SiteWhereException {
	// Build criteria.
	AssetTypeSearchCriteria criteria = new AssetTypeSearchCriteria(page, pageSize);

	// Perform search.
	ISearchResults<? extends IAssetType> matches = getAssetManagement().listAssetTypes(criteria);
	AssetTypeMarshalHelper helper = new AssetTypeMarshalHelper(getAssetManagement());

	List<IAssetType> results = new ArrayList<IAssetType>();
	for (IAssetType assetType : matches.getResults()) {
	    results.add(helper.convert(assetType));
	}
	return new SearchResults<IAssetType>(results, matches.getNumResults());
    }

    /**
     * Delete information for an asset type based on token.
     * 
     * @param assetTypeToken
     * @return
     * @throws SiteWhereException
     */
    @DeleteMapping("/{assetTypeToken}")
    public IAssetType deleteAsset(@PathVariable String assetTypeToken) throws SiteWhereException {
	IAssetType existing = assureAssetType(assetTypeToken);
	return getAssetManagement().deleteAssetType(existing.getId());
    }

    /**
     * Find an asset type by token or throw an exception if not found.
     * 
     * @param assetTypeToken
     * @return
     * @throws SiteWhereException
     */
    private IAssetType assureAssetType(String assetTypeToken) throws SiteWhereException {
	IAssetType assetType = getAssetManagement().getAssetTypeByToken(assetTypeToken);
	if (assetType == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAssetTypeToken, ErrorLevel.ERROR);
	}
	return assetType;
    }

    protected IAssetManagement getAssetManagement() throws SiteWhereException {
	return getMicroservice().getAssetManagement();
    }

    protected ILabelGeneration getLabelGeneration() {
	return getMicroservice().getLabelGenerationApiChannel();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}