package com.sitewhere.microservice.instance;

import org.springframework.beans.factory.annotation.Value;

import com.sitewhere.microservice.spi.instance.IInstanceSettings;

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
}