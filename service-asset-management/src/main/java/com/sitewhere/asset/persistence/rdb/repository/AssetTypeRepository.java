/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.persistence.rdb.repository;

import java.util.Optional;
import java.util.UUID;

import com.sitewhere.asset.persistence.rdb.entity.RdbAssetType;
import com.sitewhere.rdb.CrudRepository;
import com.sitewhere.rdb.JpaSpecificationExecutor;
import com.sitewhere.rdb.PagingAndSortingRepository;

public interface AssetTypeRepository extends CrudRepository<RdbAssetType, UUID>,
	PagingAndSortingRepository<RdbAssetType, UUID>, JpaSpecificationExecutor<RdbAssetType> {

    Optional<RdbAssetType> findByToken(String token);
}
