/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client;

import io.grpc.Context;

/**
 * Contains keys used for passing context between gRPC interceptors and
 * implementation classes.
 * 
 * @author Derek
 */
public class GrpcContextKeys {

    /** Key for accessing JWT */
    public static final Context.Key<String> JWT_KEY = Context.key("jwt");

    /** Key for accessing requested tenant token */
    public static final Context.Key<String> TENANT_TOKEN_KEY = Context.key("tenant");
}