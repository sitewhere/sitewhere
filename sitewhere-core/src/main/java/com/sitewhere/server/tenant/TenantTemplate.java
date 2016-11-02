package com.sitewhere.server.tenant;

import com.sitewhere.spi.server.tenant.ITenantTemplate;

/**
 * Model object for an {@link ITenantTemplate}.
 * 
 * @author Derek
 */
public class TenantTemplate implements ITenantTemplate {

    /** Template id */
    private String id;

    /** Template name */
    private String name;

    /** Model initializers */
    private Initializers initializers;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.tenant.ITenantTemplate#getId()
     */
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.tenant.ITenantTemplate#getName()
     */
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.tenant.ITenantTemplate#getInitializers()
     */
    public Initializers getInitializers() {
	return initializers;
    }

    public void setInitializers(Initializers initializers) {
	this.initializers = initializers;
    }

    /**
     * Model initializers.
     * 
     * @author Derek
     */
    public static class Initializers implements ITenantTemplate.Initializers {

	/** Device management Groovy script location */
	private String deviceManagement;

	/** Asset management Groovy script location */
	private String assetManagement;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.tenant.ITenantTemplate.Initializers#
	 * getDeviceManagement()
	 */
	public String getDeviceManagement() {
	    return deviceManagement;
	}

	public void setDeviceManagement(String deviceManagement) {
	    this.deviceManagement = deviceManagement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.tenant.ITenantTemplate.Initializers#
	 * getAssetManagement()
	 */
	public String getAssetManagement() {
	    return assetManagement;
	}

	public void setAssetManagement(String assetManagement) {
	    this.assetManagement = assetManagement;
	}
    }
}