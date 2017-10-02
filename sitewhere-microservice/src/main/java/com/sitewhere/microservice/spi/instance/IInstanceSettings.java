package com.sitewhere.microservice.spi.instance;

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
}