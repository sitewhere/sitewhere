/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import com.sitewhere.rest.model.asset.AssetModule;
import com.sitewhere.spi.SiteWhereException;

public class Assets {

	public static class GetAssetModuleResponse extends AssetModule {

		public GetAssetModuleResponse() throws SiteWhereException {
			setId(ExampleData.AM_PERSONS.getId());
			setName(ExampleData.AM_PERSONS.getName());
			setAssetType(ExampleData.AM_PERSONS.getAssetType());
		}
	}
}