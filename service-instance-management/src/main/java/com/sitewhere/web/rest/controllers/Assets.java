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
import com.sitewhere.microservice.api.asset.AssetMarshalHelper;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.label.ILabelGeneration;
import com.sitewhere.rest.model.asset.marshaling.MarshaledAsset;
import com.sitewhere.rest.model.asset.request.AssetCreateRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.asset.AssetSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Controller for asset operations.
 */
@RestController
@RequestMapping("/api/assets")
public class Assets {

    @Autowired
    private IInstanceManagementMicroservice microservice;

    /**
     * Create a new asset.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PostMapping
    public IAsset createAsset(@RequestBody AssetCreateRequest request) throws SiteWhereException {
	return getAssetManagement().createAsset(request);
    }

    /**
     * Get information for an asset based on token.
     * 
     * @param assetToken
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{assetToken}")
    public MarshaledAsset getAssetByToken(@PathVariable String assetToken) throws SiteWhereException {
	IAsset existing = assureAsset(assetToken);
	AssetMarshalHelper helper = new AssetMarshalHelper(getAssetManagement());
	helper.setIncludeAssetType(true);
	return helper.convert(existing);
    }

    /**
     * Update an existing asset.
     * 
     * @param assetToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PutMapping("/{assetToken}")
    public IAsset updateAsset(@PathVariable String assetToken, @RequestBody AssetCreateRequest request)
	    throws SiteWhereException {
	IAsset existing = assureAsset(assetToken);
	return getAssetManagement().updateAsset(existing.getId(), request);
    }

    /**
     * Get label for asset based on a specific generator.
     * 
     * @param assetToken
     * @param generatorId
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{assetToken}/label/{generatorId}")
    public ResponseEntity<?> getAssetLabel(@PathVariable String assetToken, @PathVariable String generatorId)
	    throws SiteWhereException {
	IAsset existing = assureAsset(assetToken);
	ILabel label = getLabelGeneration().getAssetLabel(generatorId, existing.getId());
	if (label == null) {
	    return ResponseEntity.notFound().build();
	}
	return ResponseEntity.ok(label.getContent());
    }

    /**
     * List assets matching criteria.
     * 
     * @param assetTypeToken
     * @param includeAssetType
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GetMapping
    public SearchResults<IAsset> listAssets(@RequestParam(required = false) String assetTypeToken,
	    @RequestParam(defaultValue = "false", required = false) boolean includeAssetType,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize) throws SiteWhereException {
	// Build criteria.
	AssetSearchCriteria criteria = new AssetSearchCriteria(page, pageSize);
	criteria.setAssetTypeToken(assetTypeToken);

	// Perform search.
	ISearchResults<? extends IAsset> matches = getAssetManagement().listAssets(criteria);
	AssetMarshalHelper helper = new AssetMarshalHelper(getAssetManagement());
	helper.setIncludeAssetType(includeAssetType);

	List<IAsset> results = new ArrayList<IAsset>();
	for (IAsset asset : matches.getResults()) {
	    results.add(helper.convert(asset));
	}
	return new SearchResults<IAsset>(results, matches.getNumResults());
    }

    /**
     * Delete information for an asset based on token.
     * 
     * @param assetToken
     * @return
     * @throws SiteWhereException
     */
    @DeleteMapping("/{assetToken}")
    public IAsset deleteAsset(@PathVariable String assetToken) throws SiteWhereException {
	IAsset existing = assureAsset(assetToken);
	return getAssetManagement().deleteAsset(existing.getId());
    }

    /**
     * Find an asset by token or throw an exception if not found.
     * 
     * @param assetId
     * @return
     * @throws SiteWhereException
     */
    private IAsset assureAsset(String assetToken) throws SiteWhereException {
	IAsset asset = getAssetManagement().getAssetByToken(assetToken);
	if (asset == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAssetToken, ErrorLevel.ERROR);
	}
	return asset;
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