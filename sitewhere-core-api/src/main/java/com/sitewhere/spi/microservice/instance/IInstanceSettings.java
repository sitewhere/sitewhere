/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.instance;

/**
 * Common settings used in a SiteWhere instance.
 * 
 * @author Derek
 */
public interface IInstanceSettings {

    /**
     * Get unique id for instance.
     * 
     * @return
     */
    public String getInstanceId();

    /**
     * Get id of instance template to use.
     * 
     * @return
     */
    public String getInstanceTemplateId();

    /**
     * Get hostname used by microservices to connect to Zookeeper.
     * 
     * @return
     */
    public String getZookeeperHost();

    /**
     * Get port used by microservices to connect to Zookeeper.
     * 
     * @return
     */
    public int getZookeeperPort();

    /**
     * Get Kafka bootstrap servers configuration string.
     * 
     * @return
     */
    public String getKafkaBootstrapServers();

    /**
     * Get root filesystem path where microservice resources may be stored.
     * 
     * @return
     */
    public String getFileSystemStorageRoot();

    /**
     * Get port to which GRPC server is bound.
     * 
     * @return
     */
    public int getGrpcPort();

    /**
     * Get port to which management GRPC service is bound.
     * 
     * @return
     */
    public int getManagementGrpcPort();

    /**
     * Get server information for tracer collector.
     * 
     * @return
     */
    public String getTracerServer();
}