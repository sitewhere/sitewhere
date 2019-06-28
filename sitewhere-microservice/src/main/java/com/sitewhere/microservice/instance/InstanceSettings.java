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

    /** Product id */
    @Value("#{systemEnvironment['sitewhere.product.id'] ?: 'sitewhere'}")
    private String productId;

    /** Instance id service belongs to */
    @Value("#{systemEnvironment['sitewhere.instance.id'] ?: 'sitewhere1'}")
    private String instanceId;

    /** Id of instance template to use */
    @Value("#{systemEnvironment['sitewhere.instance.template.id'] ?: 'default'}")
    private String instanceTemplateId;

    /** Zookeeper hostname info for microservices */
    @Value("#{systemEnvironment['sitewhere.zookeeper.host'] ?: 'cp-zookeeper'}")
    private String zookeeperHost;

    /** Zookeeper port info for microservices */
    @Value("#{systemEnvironment['sitewhere.zookeeper.port'] ?: '2181'}")
    private int zookeeperPort;

    /** Kafka bootstrap services configuration for microservices */
    @Value("#{systemEnvironment['sitewhere.kafka.bootstrap.servers'] ?: 'cp-kafka:9092'}")
    private String kafkaBootstrapServers;

    /** Kafka default number of partitions for topics */
    @Value("#{systemEnvironment['sitewhere.kafka.defaultTopicPartitions'] ?: '8'}")
    private int kafkaDefaultTopicPartitions;

    /** Kafka default number of partitions for topics */
    @Value("#{systemEnvironment['sitewhere.kafka.defaultTopicReplicationFactor'] ?: '3'}")
    private int kafkaDefaultTopicReplicationFactor;

    /** Apache Synote hostname info for microservices */
    @Value("#{systemEnvironment['sitewhere.syncope.host'] ?: 'syncope'}")
    private String syncopeHost;

    /** Apache Synote port info for microservices */
    @Value("#{systemEnvironment['sitewhere.syncope.port'] ?: '8080'}")
    private int syncopePort;

    /** Number of retries on gRPC exponential backoff */
    @Value("#{systemEnvironment['sitewhere.grpc.maxRetryCount'] ?: '6'}")
    private double grpcMaxRetryCount;

    /** Initial backoff in seconds for gRPC exponential backoff */
    @Value("#{systemEnvironment['sitewhere.grpc.initialBackoffSeconds'] ?: '10'}")
    private int grpcInitialBackoffInSeconds;

    /** Max backoff in seconds for gRPC exponential backoff */
    @Value("#{systemEnvironment['sitewhere.grpc.maxBackoffSeconds'] ?: '600'}")
    private int grpcMaxBackoffInSeconds;

    /** Mulitplier used for gRPC exponential backoff */
    @Value("#{systemEnvironment['sitewhere.grpc.backoffMultiplier'] ?: '1.5'}")
    private double grpcBackoffMultiplier;

    /** Flag for whether to resolve via FQDN on gRPC calls */
    @Value("#{systemEnvironment['sitewhere.grpc.resolveFQDN'] ?: 'false'}")
    private boolean grpcResolveFQDN;

    /** File system root for storing SiteWhere data for microservices */
    @Value("#{systemEnvironment['sitewhere.filesystem.storage.root'] ?: '/var/sitewhere'}")
    private String fileSystemStorageRoot;

    /** Flag for whether to log metrics */
    @Value("#{systemEnvironment['sitewhere.log.metrics'] ?: 'false'}")
    private boolean logMetrics;

    /** Microservice publicly resolvable hostname */
    @Value("#{systemEnvironment['sitewhere.service.public.hostname'] ?: '#{null}'}")
    private String publicHostname;

    /** Microservice publicly resolvable hostname */
    @Value("#{systemEnvironment['sitewhere.namespace'] ?: '#{null}'}")
    private String kubernetesNamespace;

    /** Microservice publicly resolvable hostname */
    @Value("#{systemEnvironment['sitewhere.k8s.pod.ip'] ?: '#{null}'}")
    private String kubernetesPodAddress;

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
     * getKafkaDefaultTopicPartitions()
     */
    @Override
    public int getKafkaDefaultTopicPartitions() {
	return kafkaDefaultTopicPartitions;
    }

    public void setKafkaDefaultTopicPartitions(int kafkaDefaultTopicPartitions) {
	this.kafkaDefaultTopicPartitions = kafkaDefaultTopicPartitions;
    }

    /*
     * @see com.sitewhere.spi.microservice.instance.IInstanceSettings#
     * getKafkaDefaultTopicReplicationFactor()
     */
    @Override
    public int getKafkaDefaultTopicReplicationFactor() {
	return kafkaDefaultTopicReplicationFactor;
    }

    public void setKafkaDefaultTopicReplicationFactor(int kafkaDefaultTopicReplicationFactor) {
	this.kafkaDefaultTopicReplicationFactor = kafkaDefaultTopicReplicationFactor;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.instance.IInstanceSettings#getSyncopeHost()
     */
    @Override
    public String getSyncopeHost() {
	return syncopeHost;
    }

    public void setSyncopeHost(String syncopeHost) {
	this.syncopeHost = syncopeHost;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.instance.IInstanceSettings#getSyncopePort()
     */
    @Override
    public int getSyncopePort() {
	return syncopePort;
    }

    public void setSyncopePort(int syncopePort) {
	this.syncopePort = syncopePort;
    }

    /*
     * @see com.sitewhere.spi.microservice.instance.IInstanceSettings#
     * getGrpcMaxRetryCount()
     */
    @Override
    public double getGrpcMaxRetryCount() {
	return grpcMaxRetryCount;
    }

    public void setGrpcMaxRetryCount(double grpcMaxRetryCount) {
	this.grpcMaxRetryCount = grpcMaxRetryCount;
    }

    /*
     * @see com.sitewhere.spi.microservice.instance.IInstanceSettings#
     * getGrpcInitialBackoffInSeconds()
     */
    @Override
    public int getGrpcInitialBackoffInSeconds() {
	return grpcInitialBackoffInSeconds;
    }

    public void setGrpcInitialBackoffInSeconds(int grpcInitialBackoffInSeconds) {
	this.grpcInitialBackoffInSeconds = grpcInitialBackoffInSeconds;
    }

    /*
     * @see com.sitewhere.spi.microservice.instance.IInstanceSettings#
     * getGrpcMaxBackoffInSeconds()
     */
    @Override
    public int getGrpcMaxBackoffInSeconds() {
	return grpcMaxBackoffInSeconds;
    }

    public void setGrpcMaxBackoffInSeconds(int grpcMaxBackoffInSeconds) {
	this.grpcMaxBackoffInSeconds = grpcMaxBackoffInSeconds;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.instance.IInstanceSettings#isGrpcResolveFQDN()
     */
    @Override
    public boolean isGrpcResolveFQDN() {
	return grpcResolveFQDN;
    }

    public void setGrpcResolveFQDN(boolean grpcResolveFQDN) {
	this.grpcResolveFQDN = grpcResolveFQDN;
    }

    /*
     * @see com.sitewhere.spi.microservice.instance.IInstanceSettings#
     * getGrpcBackoffMultiplier()
     */
    @Override
    public double getGrpcBackoffMultiplier() {
	return grpcBackoffMultiplier;
    }

    public void setGrpcBackoffMultiplier(double grpcBackoffMultiplier) {
	this.grpcBackoffMultiplier = grpcBackoffMultiplier;
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
     * com.sitewhere.spi.microservice.instance.IInstanceSettings#getPublicHostname()
     */
    @Override
    public String getPublicHostname() {
	return publicHostname;
    }

    public void setPublicHostname(String publicHostname) {
	this.publicHostname = publicHostname;
    }

    /*
     * @see com.sitewhere.spi.microservice.instance.IInstanceSettings#
     * getKubernetesNamespace()
     */
    @Override
    public String getKubernetesNamespace() {
	return kubernetesNamespace;
    }

    public void setKubernetesNamespace(String kubernetesNamespace) {
	this.kubernetesNamespace = kubernetesNamespace;
    }

    /*
     * @see com.sitewhere.spi.microservice.instance.IInstanceSettings#
     * getKubernetesPodAddress()
     */
    @Override
    public String getKubernetesPodAddress() {
	return kubernetesPodAddress;
    }

    public void setKubernetesPodAddress(String kubernetesPodAddress) {
	this.kubernetesPodAddress = kubernetesPodAddress;
    }
}