/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.instance;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Value;

import com.sitewhere.spi.microservice.instance.IInstanceSettings;

/**
 * SiteWhere instance settings.
 * 
 * @author Derek
 */
public class InstanceSettings implements IInstanceSettings {

    /** Product id */
    @Value("${sitewhere.product.id:sitewhere}")
    private String productId;

    /** Instance id service belongs to */
    @Value("${sitewhere.instance.id:sitewhere1}")
    private String instanceId;

    /** Id of instance template to use */
    @Value("${sitewhere.instance.template.id:default}")
    private String instanceTemplateId;

    /** Consul hostname info for microservices */
    @Value("${sitewhere.consul.host:consul}")
    private String consulHost;

    /** Consul port info for microservices */
    @Value("${sitewhere.consul.port:8500}")
    private int consulPort;

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

    /** GRPC port for serving APIs */
    @Value("${sitewhere.grpc.port:9000}")
    private int grpcPort;

    /** GRPC port info for microservice management */
    @Value("${sitewhere.management.grpc.port:9001}")
    private int managementGrpcPort;

    /** Tracer server information */
    @Value("${sitewhere.tracer.server:jaeger}")
    private String tracerServer;

    /** Flag for whether to log metrics */
    @Value("${sitewhere.log.metrics:false}")
    private boolean logMetrics;

    /** Flag for whether to use near cache */
    @Value("${sitewhere.use.near.cache:false}")
    private boolean useNearCache;

    /** Microservice Service Port Name */
    @Value("${sitewhere.service.portName:localhost}")
    private String servicePortName;

    /*
     * @see com.sitewhere.spi.microservice.instance.IInstanceSettings#getProductId()
     */
    @Override
    public String getProductId() {
	return productId;
    }

    public void setProductId(String productId) {
	this.productId = productId;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.instance.IInstanceSettings#getInstanceId()
     */
    @Override
    public String getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(String instanceId) {
	this.instanceId = instanceId;
    }

    /*
     * @see com.sitewhere.spi.microservice.instance.IInstanceSettings#
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
     * @see
     * com.sitewhere.spi.microservice.instance.IInstanceSettings#getConsulHost()
     */
    @Override
    public String getConsulHost() {
	return consulHost;
    }

    public void setConsulHost(String consulHost) {
	this.consulHost = consulHost;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.instance.IInstanceSettings#getConsulPort()
     */
    @Override
    public int getConsulPort() {
	return consulPort;
    }

    public void setConsulPort(int consulPort) {
	this.consulPort = consulPort;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.instance.IInstanceSettings#getZookeeperHost()
     */
    @Override
    public String getZookeeperHost() {
	return zookeeperHost;
    }

    public void setZookeeperHost(String zookeeperHost) {
	this.zookeeperHost = zookeeperHost;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.instance.IInstanceSettings#getZookeeperPort()
     */
    @Override
    public int getZookeeperPort() {
	return zookeeperPort;
    }

    public void setZookeeperPort(int zookeeperPort) {
	this.zookeeperPort = zookeeperPort;
    }

    /*
     * @see com.sitewhere.spi.microservice.instance.IInstanceSettings#
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
     * @see com.sitewhere.spi.microservice.instance.IInstanceSettings#
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
     * @see com.sitewhere.spi.microservice.instance.IInstanceSettings#getGrpcPort()
     */
    @Override
    public int getGrpcPort() {
	return grpcPort;
    }

    public void setGrpcPort(int grpcPort) {
	this.grpcPort = grpcPort;
    }

    /*
     * @see com.sitewhere.spi.microservice.instance.IInstanceSettings#
     * getManagementGrpcPort()
     */
    @Override
    public int getManagementGrpcPort() {
	return managementGrpcPort;
    }

    public void setManagementGrpcPort(int managementGrpcPort) {
	this.managementGrpcPort = managementGrpcPort;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.instance.IInstanceSettings#getTracerServer()
     */
    @Override
    public String getTracerServer() {
	return tracerServer;
    }

    public void setTracerServer(String tracerServer) {
	this.tracerServer = tracerServer;
    }

    /*
     * @see com.sitewhere.spi.microservice.instance.IInstanceSettings#isLogMetrics()
     */
    @Override
    public boolean isLogMetrics() {
	return logMetrics;
    }

    public void setLogMetrics(boolean logMetrics) {
	this.logMetrics = logMetrics;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.instance.IInstanceSettings#isUseNearCache()
     */
    @Override
    public boolean isUseNearCache() {
	return useNearCache;
    }

    public void setUseNearCache(boolean useNearCache) {
	this.useNearCache = useNearCache;
    }

    @Override
    public String getServicePortName() {
	try {
	    InetAddress serviceName = InetAddress.getByName(this.servicePortName);
	    return serviceName.getHostName();
	} catch (UnknownHostException e) {
	    throw new RuntimeException("Unable to find hostname.", e);
	}
    }
    
    public void setServicePortName(String servicePortName) {
	this.servicePortName = servicePortName;
    }
}