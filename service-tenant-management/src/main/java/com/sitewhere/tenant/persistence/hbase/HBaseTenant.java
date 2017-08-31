/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.tenant.persistence.hbase;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.uid.UniqueIdCounterMap;
import com.sitewhere.hbase.uid.UniqueIdCounterMapRowKeyBuilder;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.tenant.TenantSearchCriteria;
import com.sitewhere.rest.model.tenant.Tenant;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.common.IFilter;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.user.ITenantSearchCriteria;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;
import com.sitewhere.tenant.persistence.TenantManagementPersistence;

/**
 * HBase specifics for dealing with SiteWhere tenants.
 * 
 * @author Derek
 */
public class HBaseTenant {

    /** Length of group identifier (subset of 8 byte long) */
    public static final int IDENTIFIER_LENGTH = 4;

    /** Used to look up row keys from tokens */
    public static UniqueIdCounterMapRowKeyBuilder KEY_BUILDER = new UniqueIdCounterMapRowKeyBuilder() {

	@Override
	public UniqueIdCounterMap getMap(IHBaseContext context) {
	    return context.getUserIdManager().getTenantKeys();
	}

	@Override
	public byte getTypeIdentifier() {
	    // return UserRecordType.Tenant.getType();
	    throw new RuntimeException("Tenant management should be separated from user management.");
	}

	@Override
	public byte getPrimaryIdentifier() {
	    return TenantSubtype.Tenant.getType();
	}

	@Override
	public int getKeyIdLength() {
	    return IDENTIFIER_LENGTH;
	}

	@Override
	public void throwInvalidKey() throws SiteWhereException {
	    throw new SiteWhereSystemException(ErrorCode.InvalidTenantId, ErrorLevel.ERROR);
	}
    };

    /**
     * Create a new tenant.
     * 
     * @param context
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static Tenant createTenant(IHBaseContext context, ITenantCreateRequest request) throws SiteWhereException {
	if (getTenantById(context, request.getId()) != null) {
	    throw new SiteWhereSystemException(ErrorCode.DuplicateTenantId, ErrorLevel.ERROR);
	}

	// Add new key to table.
	String id = KEY_BUILDER.getMap(context).useExistingId(request.getId());

	// Use common logic so all backend implementations work the same.
	Tenant tenant = TenantManagementPersistence.tenantCreateLogic(request);

	Map<byte[], byte[]> qualifiers = new HashMap<byte[], byte[]>();
	return HBaseUtils.createOrUpdate(context, context.getPayloadMarshaler(), ISiteWhereHBase.USERS_TABLE_NAME,
		tenant, id, KEY_BUILDER, qualifiers);
    }

    /**
     * Get an existing tenant by unique id.
     * 
     * @param context
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public static Tenant getTenantById(IHBaseContext context, String id) throws SiteWhereException {
	if (KEY_BUILDER.getMap(context).getValue(id) == null) {
	    return null;
	}
	return HBaseUtils.get(context, ISiteWhereHBase.USERS_TABLE_NAME, id, KEY_BUILDER, Tenant.class);
    }

    /**
     * Update an existing tenant.
     * 
     * @param context
     * @param id
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static Tenant updateTenant(IHBaseContext context, String id, ITenantCreateRequest request)
	    throws SiteWhereException {
	Tenant updated = assertTenant(context, id);
	TenantManagementPersistence.tenantUpdateLogic(request, updated);
	return HBaseUtils.put(context, context.getPayloadMarshaler(), ISiteWhereHBase.USERS_TABLE_NAME, updated, id,
		KEY_BUILDER);
    }

    /**
     * Get tenant based on unique authentication token.
     * 
     * @param context
     * @param authToken
     * @return
     * @throws SiteWhereException
     */
    public static ITenant getTenantByAuthenticationToken(IHBaseContext context, String authToken)
	    throws SiteWhereException {
	SearchResults<ITenant> all = listTenants(context, new TenantSearchCriteria(1, 0));
	for (ITenant tenant : all.getResults()) {
	    if (tenant.getAuthenticationToken().equals(authToken)) {
		return tenant;
	    }
	}
	return null;
    }

    /**
     * List tenants that match the given criteria.
     * 
     * @param context
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static SearchResults<ITenant> listTenants(IHBaseContext context, final ITenantSearchCriteria criteria)
	    throws SiteWhereException {
	Comparator<Tenant> comparator = new Comparator<Tenant>() {

	    public int compare(Tenant a, Tenant b) {
		return a.getName().compareTo(b.getName());
	    }

	};
	IFilter<Tenant> filter = new IFilter<Tenant>() {

	    // Regular expression used for searching name and id.
	    Pattern regex = criteria.getTextSearch() != null ? Pattern.compile(Pattern.quote(criteria.getTextSearch()))
		    : null;

	    public boolean isExcluded(Tenant item) {
		if (regex != null) {
		    if ((!regex.matcher(item.getId()).matches()) && (!regex.matcher(item.getName()).matches())) {
			return true;
		    }
		}
		if (criteria.getUserId() != null) {
		    if (item.getAuthorizedUserIds().contains(criteria.getUserId())) {
			return false;
		    } else {
			return true;
		    }
		}
		return false;
	    }
	};
	SearchResults<ITenant> list = HBaseUtils.getFilteredList(context, ISiteWhereHBase.USERS_TABLE_NAME, KEY_BUILDER,
		true, ITenant.class, Tenant.class, filter, criteria, comparator);
	TenantManagementPersistence.tenantListLogic(list.getResults(), criteria);
	return list;
    }

    /**
     * Delete an existing tenant.
     * 
     * @param context
     * @param id
     * @param force
     * @return
     * @throws SiteWhereException
     */
    public static Tenant deleteTenant(IHBaseContext context, String id, boolean force) throws SiteWhereException {
	return HBaseUtils.delete(context, context.getPayloadMarshaler(), ISiteWhereHBase.USERS_TABLE_NAME, id, force,
		KEY_BUILDER, Tenant.class);
    }

    /**
     * Get a tenant by unique id. Throw exception if not found.
     * 
     * @param context
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public static Tenant assertTenant(IHBaseContext context, String id) throws SiteWhereException {
	Tenant existing = getTenantById(context, id);
	if (existing == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidTenantId, ErrorLevel.ERROR);
	}
	return existing;
    }
}