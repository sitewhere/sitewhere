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
}
