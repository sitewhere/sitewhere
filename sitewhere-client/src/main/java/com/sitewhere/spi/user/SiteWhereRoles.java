/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.user;

/**
 * Constants related to SiteWhere granted authorities.
 * 
 * @author Derek
 */
public interface SiteWhereRoles {

	/** Group for system access authorities */
	public static final String GRP_ACCESS = "GRP_ACCESS";

	/** Authority to access REST serAUTH_RESTvices */
	public static final String AUTH_REST = "REST";

	/** Authority to access administrative console */
	public static final String AUTH_ADMIN_CONSOLE = "ADMIN_CONSOLE";

	/** Group for user authorities */
	public static final String GRP_USERS = "GRP_USERS";

	/** Authority to administer all system users */
	public static final String AUTH_ADMINISTER_USERS = "ADMINISTER_USERS";

	/** Authority to administer own user account */
	public static final String AUTH_ADMINISTER_USER_SELF = "ADMINISTER_USER_SELF";

	/** Group for tenant authorities */
	public static final String GRP_TENANTS = "GRP_TENANTS";

	/** Authority to administer all system tenants */
	public static final String AUTH_ADMINISTER_TENANTS = "ADMINISTER_TENANTS";

	/** Authority to administer own tenant */
	public static final String AUTH_ADMINISTER_TENANT_SELF = "ADMINISTER_TENANT_SELF";

	/********************
	 * ROLE DEFINITIONS *
	 ********************/

	/** Prefix for Spring Security roles */
	public static final String ROLE_PREFIX = "ROLE_";

	/** Role for access to REST services */
	public static final String REST = ROLE_PREFIX + AUTH_REST;

	/** Role for access to administrative console */
	public static final String ADMIN_CONSOLE = ROLE_PREFIX + AUTH_ADMIN_CONSOLE;

	/** Role to administer all system users */
	public static final String ADMINISTER_USERS = ROLE_PREFIX + AUTH_ADMINISTER_USERS;

	/** Role to administer own user account */
	public static final String ADMINISTER_USER_SELF = ROLE_PREFIX + AUTH_ADMINISTER_USER_SELF;
}