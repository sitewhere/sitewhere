/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.instance;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.sitewhere.spi.microservice.instance.IInstanceSettings;

/**
 * SiteWhere instance settings.
 */
@ApplicationScoped
public class InstanceSettings implements IInstanceSettings {

    /** Product id */
    @ConfigProperty(name = "sitewhere.product.id", defaultValue = "sitewhere")
    private String productId;

    /** Instance id service belongs to */
    @ConfigProperty(name = "sitewhere.instance.id", defaultValue = "sitewhere1")
    private String instanceId;

    /** Id of instance template to use */
    @ConfigProperty(name = "sitewhere.instance.template.id", defaultValue = "default")
    private String instanceTemplateId;

    /** Kafka bootstrap services configuration for microservices */
    @ConfigProperty(name = "sitewhere.kafka.bootstrap.servers", defaultValue = "cp-kafka:9092")
    private String kafkaBootstrapServers;

    /** Kafka default number of partitions for topics */
    @ConfigProperty(name = "sitewhere.kafka.defaultTopicPartitions", defaultValue = "8")
    private int kafkaDefaultTopicPartitions;

    /** Kafka default number of partitions for topics */
    @ConfigProperty(name = "sitewhere.kafka.defaultTopicReplicationFactor", defaultValue = "3")
    private int kafkaDefaultTopicReplicationFactor;

    /** Apache Synote hostname info for microservices */
    @ConfigProperty(name = "sitewhere.syncope.host", defaultValue = "syncope")
    private String syncopeHost;

    /** Apache Synote port info for microservices */
    @ConfigProperty(name = "sitewhere.syncope.port", defaultValue = "8080")
    private int syncopePort;

    /** Prometheus HTTP port info for microservices */
    @ConfigProperty(name = "sitewhere.metrics.port", defaultValue = "9090")
    private int metricsHttpPort;

    /** Number of retries on gRPC exponential backoff */
    @ConfigProperty(name = "sitewhere.grpc.maxRetryCount", defaultValue = "6")
    private double grpcMaxRetryCount;

    /** Initial backoff in seconds for gRPC exponential backoff */
    @ConfigProperty(name = "sitewhere.grpc.initialBackoffSeconds", defaultValue = "10")
    private int grpcInitialBackoffInSeconds;

    /** Max backoff in seconds for gRPC exponential backoff */
    @ConfigProperty(name = "sitewhere.grpc.maxBackoffSeconds", defaultValue = "600")
    private int grpcMaxBackoffInSeconds;

    /** Mulitplier used for gRPC exponential backoff */
    @ConfigProperty(name = "sitewhere.grpc.backoffMultiplier", defaultValue = "1.5")
    private double grpcBackoffMultiplier;

    /** Flag for whether to resolve via FQDN on gRPC calls */
    @ConfigProperty(name = "sitewhere.grpc.resolveFQDN", defaultValue = "false")
    private boolean grpcResolveFQDN;

    /** File system root for storing SiteWhere data for microservices */
    @ConfigProperty(name = "sitewhere.filesystem.storage.root", defaultValue = "/var/sitewhere/")
    private String fileSystemStorageRoot;

    /** Flag for whether to log metrics */
    @ConfigProperty(name = "sitewhere.log.metrics", defaultValue = "false")
    private boolean logMetrics;

    /** Microservice publicly resolvable hostname */
    @ConfigProperty(name = "sitewhere.service.public.hostname")
    private Optional<String> publicHostname;

    /** Microservice publicly resolvable hostname */
    @ConfigProperty(name = "sitewhere.namespace")
    private Optional<String> kubernetesNamespace;

    /** Microservice publicly resolvable hostname */
    @ConfigProperty(name = "sitewhere.k8s.pod.ip")
    private Optional<String> kubernetesPodAddress;

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
     * @see
     * com.sitewhere.spi.microservice.instance.IInstanceSettings#getMetricsHttpPort(
     * )
     */
    @Override
    public int getMetricsHttpPort() {
	return metricsHttpPort;
    }

    public void setMetricsHttpPort(int metricsHttpPort) {
	this.metricsHttpPort = metricsHttpPort;
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
    public Optional<String> getPublicHostname() {
	return publicHostname;
    }

    public void setPublicHostname(Optional<String> publicHostname) {
	this.publicHostname = publicHostname;
    }

    /*
     * @see com.sitewhere.spi.microservice.instance.IInstanceSettings#
     * getKubernetesNamespace()
     */
    @Override
    public Optional<String> getKubernetesNamespace() {
	return kubernetesNamespace;
    }

    public void setKubernetesNamespace(Optional<String> kubernetesNamespace) {
	this.kubernetesNamespace = kubernetesNamespace;
    }

    /*
     * @see com.sitewhere.spi.microservice.instance.IInstanceSettings#
     * getKubernetesPodAddress()
     */
    @Override
    public Optional<String> getKubernetesPodAddress() {
	return kubernetesPodAddress;
    }

    public void setKubernetesPodAddress(Optional<String> kubernetesPodAddress) {
	this.kubernetesPodAddress = kubernetesPodAddress;
    }
}