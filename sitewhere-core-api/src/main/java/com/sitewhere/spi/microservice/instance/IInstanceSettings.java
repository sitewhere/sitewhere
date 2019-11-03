/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.instance;

import java.util.Optional;

/**
 * Common settings used in a SiteWhere instance.
 */
public interface IInstanceSettings {

    /**
     * Get product identifier.
     * 
     * @return
     */
    public String getProductId();

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
     * Get Kafka bootstrap servers configuration string.
     * 
     * @return
     */
    public String getKafkaBootstrapServers();

    /**
     * Get default number of partitions used for Kafka topics.
     * 
     * @return
     */
    public int getKafkaDefaultTopicPartitions();

    /**
     * Get default replication factor used for Kafka topics.
     * 
     * @return
     */
    public int getKafkaDefaultTopicReplicationFactor();

    /**
     * Get hostname used by microservices to connect to Apache Syncope API.
     * 
     * @return
     */
    public String getSyncopeHost();

    /**
     * Get port used by microservices to connect to Apache Syncope API.
     * 
     * @return
     */
    public int getSyncopePort();

    /**
     * Get port used to allow HTTP metrics scraping.
     * 
     * @return
     */
    public int getMetricsHttpPort();

    /**
     * Get max retries for gRPC exponential backoff.
     * 
     * @return
     */
    public double getGrpcMaxRetryCount();

    /**
     * Get initial wait time for exponential backoff on gRPC calls.
     * 
     * @return
     */
    public int getGrpcInitialBackoffInSeconds();

    /**
     * Get max time for exponential backoff on gRPC calls.
     * 
     * @return
     */
    public int getGrpcMaxBackoffInSeconds();

    /**
     * Get multiplier used for exponential backoff.
     * 
     * @return
     */
    public double getGrpcBackoffMultiplier();

    /**
     * Indicates whether FQDN is used when resolving gRPC services.
     * 
     * @return
     */
    public boolean isGrpcResolveFQDN();

    /**
     * Get root filesystem path where microservice resources may be stored.
     * 
     * @return
     */
    public String getFileSystemStorageRoot();

    /**
     * Indicates whether to log metrics.
     * 
     * @return
     */
    public boolean isLogMetrics();

    /**
     * Identifies public hostname used to access microservice instance.
     * 
     * @return
     */
    public Optional<String> getPublicHostname();

    /**
     * Get Kubernetes namespace.
     * 
     * @return
     */
    public Optional<String> getKubernetesNamespace();

    /**
     * Get IP address for Kubernetes Pod running microservice.
     * 
     * @return
     */
    public Optional<String> getKubernetesPodAddress();
}