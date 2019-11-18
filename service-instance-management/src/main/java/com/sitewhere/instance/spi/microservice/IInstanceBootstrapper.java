/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.spi.microservice;

import com.sitewhere.spi.microservice.lifecycle.ILifecycleComponent;

/**
 * Checks whether an instance is bootstrapped with initial data and, if not,
 * handles the bootstrap process.
 */
public interface IInstanceBootstrapper extends ILifecycleComponent {
}
