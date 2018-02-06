/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.asset;

import java.io.Serializable;

/**
 * Reference to a SiteWhere asset.
 * 
 * @author Derek
 */
public interface IAssetReference extends Serializable {

    /**
     * Get unique module identifier.
     * 
     * @return
     */
    public String getModule();

    /**
     * Get asset id.
     * 
     * @return
     */
    public String getId();
}