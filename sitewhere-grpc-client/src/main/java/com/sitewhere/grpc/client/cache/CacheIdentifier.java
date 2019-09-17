/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.cache;

/**
 * Enumeration of cache identifiers used for interacting with Hazelcast.
 */
public enum CacheIdentifier {

    AssetByToken("asset_by_token"),

    AssetById("asset_by_id"),

    AssetTypeByToken("asset_type_by_token"),

    AssetTypeById("asset_type_by_id"),

    AreaByToken("area_by_token"),

    AreaById("area_by_id"),

    DeviceByToken("device_by_token"),

    DeviceById("device_by_id"),

    DeviceAssignmentByToken("device_assignment_by_token"),

    DeviceAssignmentById("device_assignment_by_id"),

    DeviceTypeByToken("device_type_by_token"),

    DeviceTypeById("device_type_by_id"),

    GrantedAuthorityByToken("granted_authority_by_token"),

    GrantedAuthorityById("granted_authority_by_id"),

    TenantByToken("tenant_by_token"),

    TenantById("tenant_by_id"),

    UserByToken("user_by_token"),

    UserById("user_by_id");

    /** Cache key */
    private String cacheKey;

    private CacheIdentifier(String cacheKey) {
	this.cacheKey = cacheKey;
    }

    public String getCacheKey() {
	return cacheKey;
    }
}