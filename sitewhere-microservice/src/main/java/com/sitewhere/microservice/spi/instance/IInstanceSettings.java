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
}