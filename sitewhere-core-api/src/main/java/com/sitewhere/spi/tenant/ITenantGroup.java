/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.tenant;

import com.sitewhere.spi.common.IMetadataProviderEntity;

/**
 * Information about a group of related tenants.
 * 
 * @author Derek
 */
public interface ITenantGroup extends IMetadataProviderEntity {

	/**
	 * Get unique token for group.
	 * 
	 * @return
	 */
	public String getToken();

	/**
	 * Get name for group.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Get description for group.
	 * 
	 * @return
	 */
	public String getDescription();

	/**
	 * Get URL for image associated with group.
	 * 
	 * @return
	 */
	public String getImageUrl();
}