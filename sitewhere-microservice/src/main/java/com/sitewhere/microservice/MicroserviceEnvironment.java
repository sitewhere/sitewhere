package com.sitewhere.microservice;

/**
 * Constants for interacting with Microservice environment.
 * 
 * @author Derek
 */
public class MicroserviceEnvironment {

    /** Default port used for GRPC servers */
    public static final int DEFAULT_GRPC_PORT = 9000;

    /** Environment variable for SiteWhere instance id */
    public static final String ENV_INSTANCE_ID = "SW_INSTANCE_ID";

    /** Environment variable for Zookeeper connection string */
    public static final String ENV_ZOOKEEPER_CONNECT = "SW_ZOOKEEPER_CONNECT";

    /** Environment variable for SiteWhere instance id */
    public static final String ENV_GRPC_PORT_OVERRIDE = "SW_GRPC_PORT_OVERRIDE";
}
