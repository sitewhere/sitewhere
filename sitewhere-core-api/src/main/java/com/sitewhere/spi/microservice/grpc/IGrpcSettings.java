/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.grpc;

/**
 * Default settings used for gRPC configuration.
 */
public interface IGrpcSettings {

    /** Default gRPC API Port */
    public static final int DEFAULT_API_PORT = 9000;

    /** Default gRPC Management Port */
    public static final int DEFAULT_MANAGEMENT_PORT = 9001;

    /** Default gRPC API Health Protocol Port */
    public static final int DEFAULT_API_HEALTH_PORT = 9002;

    /** Default gRPC Management Health Protocol Port */
    public static final int DEFAULT_MANAGEMENT_HEALTH_PORT = 9003;

    /** User management API port for instance management microservice */
    public static final int USER_MANAGEMENT_API_PORT = 9004;

    /** User management API port for instance management microservice */
    public static final int USER_MANAGEMENT_API_HEALTH_PORT = 9006;
}
