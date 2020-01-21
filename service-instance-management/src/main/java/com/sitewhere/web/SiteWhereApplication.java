/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Declares base path for REST application.
 */
@ApplicationPath("sitewhere")
public class SiteWhereApplication extends Application {
}
