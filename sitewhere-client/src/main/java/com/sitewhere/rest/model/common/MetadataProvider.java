/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.sitewhere.rest.model.common;

import java.util.HashMap;
import java.util.Map;

import com.sitewhere.spi.common.IMetadataProvider;

/**
 * Holds arbitrary metadata associated with an entity.
 * 
 * @author dadams
 */
public class MetadataProvider implements IMetadataProvider {

	/** Map of metadata entries */
	private Map<String, String> entries = new HashMap<String, String>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IMetadataProvider#addOrReplaceMetadata(java.lang.String,
	 * java.lang.String)
	 */
	public void addOrReplaceMetadata(String name, String value) {
		entries.put(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IMetadataProvider#removeMetadata(java.lang.String)
	 */
	public String removeMetadata(String name) {
		return entries.remove(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IMetadataProvider#getMetadata(java.lang.String)
	 */
	public String getMetadata(String name) {
		return entries.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IMetadataProvider#getMetadata()
	 */
	public Map<String, String> getMetadata() {
		return entries;
	}

	public void setMetadata(Map<String, String> entries) {
		this.entries = entries;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.common.IMetadataProvider#clearMetadata()
	 */
	@Override
	public void clearMetadata() {
		entries.clear();
	}

	/**
	 * Copy contents of one metadata provider to another.
	 * 
	 * @param source
	 * @param target
	 */
	public static void copy(IMetadataProvider source, MetadataProvider target) {
		if (source != null) {
			for (String key : source.getMetadata().keySet()) {
				target.addOrReplaceMetadata(key, source.getMetadata(key));
			}
		}
	}
}