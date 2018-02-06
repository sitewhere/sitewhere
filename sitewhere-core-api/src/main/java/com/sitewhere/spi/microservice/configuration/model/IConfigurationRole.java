/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.configuration.model;

public interface IConfigurationRole {

    public IRoleKey getKey();

    public String getName();

    public boolean isOptional();

    public boolean isMultiple();

    public boolean isReorderable();

    public boolean isPermanent();

    public IRoleKey[] getChildren();

    public IRoleKey[] getSubtypes();
}