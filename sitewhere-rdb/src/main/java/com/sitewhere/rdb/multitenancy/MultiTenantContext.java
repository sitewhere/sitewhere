/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rdb.multitenancy;

/**
 * Tenant context
 *
 * Simeon Chen
 */
public class MultiTenantContext {

    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    public static void setTenantId(String tenantId) {
	CONTEXT.set(tenantId);
    }

    public static String getTenantId() {
	return CONTEXT.get();
    }

    public static void clear() {
	CONTEXT.remove();
    }
}