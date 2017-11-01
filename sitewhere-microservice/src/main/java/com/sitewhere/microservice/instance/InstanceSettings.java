/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.instance;

import org.springframework.beans.factory.annotation.Value;

import com.sitewhere.spi.microservice.instance.IInstanceSettings;

/**
 * SiteWhere instance settings.
 * 
 * @author Derek
 */
public class InstanceSettings implements IInstanceSettings {

    /** Instance id service belongs to */
    @Value("${sitewhere.instance.id:default}")
    private String instanceId;

    /** Id of instance template to use */
    @Value("${sitewhere.instance.template.id:mongodb-default}")
    private String instanceTemplateId;

    /** Zookeeper hostname info for microservices */
    @Value("${sitewhere.zookeeper.host:localhost}")
    private String zookeeperHost;

    /** Zookeeper port info for microservices */
    @Value("${sitewhere.zookeeper.port:2181}")
    private int zookeeperPort;

    /** Kafka bootstrap services configuration for microservices */
    @Value("${sitewhere.kafka.bootstrap.servers:kafka:9092}")
    private String kafkaBootstrapServers;

    /** File system root for storing SiteWhere data for microservices */
    @Value("${sitewhere.filesystem.storage.root:/var/sitewhere}")
    private String fileSystemStorageRoot;

    /** GRPC port info for microservices */
    @Value("${sitewhere.grpc.port:9000}")
    private int grpcPort;

    /** Tracer server information */
    @Value("${sitewhere.tracer.server:jaeger}")
    private String tracerServer;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.instance.IInstanceSettings#getInstanceId()
     */
    @Override
    public String getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(String instanceId) {
	this.instanceId = instanceId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.instance.IInstanceSettings#
     * getInstanceTemplateId()
     */
    @Override
    public String getInstanceTemplateId() {
	return instanceTemplateId;
    }

    public void setInstanceTemplateId(String instanceTemplateId) {
	this.instanceTemplateId = instanceTemplateId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.instance.IInstanceSettings#
     * getZookeeperHost()
     */
    @Override
    public String getZookeeperHost() {
	return zookeeperHost;
    }

    public void setZookeeperHost(String zookeeperHost) {
	this.zookeeperHost = zookeeperHost;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.instance.IInstanceSettings#
     * getZookeeperPort()
     */
    @Override
    public int getZookeeperPort() {
	return zookeeperPort;
    }

    public void setZookeeperPort(int zookeeperPort) {
	this.zookeeperPort = zookeeperPort;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.instance.IInstanceSettings#
     * getKafkaBootstrapServers()
     */
    @Override
    public String getKafkaBootstrapServers() {
	return kafkaBootstrapServers;
    }

    public void setKafkaBootstrapServers(String kafkaBootstrapServers) {
	this.kafkaBootstrapServers = kafkaBootstrapServers;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.instance.IInstanceSettings#
     * getFileSystemStorageRoot()
     */
    @Override
    public String getFileSystemStorageRoot() {
	return fileSystemStorageRoot;
    }

    public void setFileSystemStorageRoot(String fileSystemStorageRoot) {
	this.fileSystemStorageRoot = fileSystemStorageRoot;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.instance.IInstanceSettings#getGrpcPort()
     */
    @Override
    public int getGrpcPort() {
	return grpcPort;
    }

    public void setGrpcPort(int grpcPort) {
	this.grpcPort = grpcPort;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.instance.IInstanceSettings#getTracerServer
     * ()
     */
    @Override
    public String getTracerServer() {
	return tracerServer;
    }

    public void setTracerServer(String tracerServer) {
	this.tracerServer = tracerServer;
    }
}