/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.configuration.model;

import java.util.List;

public interface IElementRole {

    public String getName();

    public boolean isOptional();

    public boolean isMultiple();

    public boolean isReorderable();

    public boolean isPermanent();

    public List<String> getChildRoles();

    public List<String> getSubtypeRoles();
}