/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.version;

import java.util.List;

/**
 * Contains information about the latest version of SiteWhere.
 * 
 * @author Derek
 */
public class LatestVersion {

	/** List of products */
	private List<Product> products;

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	/**
	 * Contains information about a SiteWhere product.
	 * 
	 * @author Derek
	 */
	public static class Product {

		/** Product type */
		private String type;

		/** Product name */
		private String name;

		/** Product edition */
		private String edition;

		/** Product version */
		private String currentVersion;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getEdition() {
			return edition;
		}

		public void setEdition(String edition) {
			this.edition = edition;
		}

		public String getCurrentVersion() {
			return currentVersion;
		}

		public void setCurrentVersion(String currentVersion) {
			this.currentVersion = currentVersion;
		}
	}
}