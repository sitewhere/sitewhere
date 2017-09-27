package com.sitewhere.microservice.spi.instance;

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
     * Get root filesystem path where microservice resources may be stored.
     * 
     * @return
     */
    public String getFileSystemStorageRoot();
}