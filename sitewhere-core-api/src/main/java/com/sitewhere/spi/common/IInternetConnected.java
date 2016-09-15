/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.common;

/**
 * Interface for components that SiteWhere connects to using a hostname and
 * port.
 * 
 * @author Derek
 */
public interface IInternetConnected {

    /**
     * Get hostname to connect to.
     * 
     * @return
     */
    public String getHostname();

    /**
     * Set hostname to connect to.
     * 
     * @param hostname
     */
    public void setHostname(String hostname);

    /**
     * Get port to connect to.
     * 
     * @return
     */
    public int getPort();

    /**
     * Set port to connect to.
     * 
     * @param port
     */
    public void setPort(int port);
}