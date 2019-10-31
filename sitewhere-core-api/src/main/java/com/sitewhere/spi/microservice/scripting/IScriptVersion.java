/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.scripting;

import java.util.Date;

/**
 * Get information about a version of a script.
 */
public interface IScriptVersion {

    /**
     * Get unique identifier for version.
     * 
     * @return
     */
    public String getVersionId();

    /**
     * Get comment indicating why version was created.
     * 
     * @return
     */
    public String getComment();

    /**
     * Get date on which the version was created.
     * 
     * @return
     */
    public Date getCreatedDate();
}