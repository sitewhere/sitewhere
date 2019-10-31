/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.scripting;

/**
 * Template that provides example content for a script.
 */
public interface IScriptTemplate {

    /**
     * Get unique id which is the base filename without extension.
     * 
     * @return
     */
    public String getId();

    /**
     * Get human-readable name shown in UI.
     * 
     * @return
     */
    public String getName();

    /**
     * Get description of script function.
     * 
     * @return
     */
    public String getDescription();

    /**
     * Get script type which is the file extension of the script content.
     * 
     * @return
     */
    public String getType();
}
