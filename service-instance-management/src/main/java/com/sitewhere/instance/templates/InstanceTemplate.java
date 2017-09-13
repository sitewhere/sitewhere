package com.sitewhere.instance.templates;

import java.util.List;

import com.sitewhere.instance.spi.templates.IInstanceTemplate;

/**
 * Model object for an {@link IInstanceTemplate}.
 * 
 * @author Derek
 */
public class InstanceTemplate implements IInstanceTemplate {

    /** Template id */
    private String id;

    /** Template name */
    private String name;

    /** Model initializers */
    private Initializers initializers;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

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
    public static class Initializers implements IInstanceTemplate.Initializers {

	/** User management Groovy script locations */
	private List<String> userManagement;

	/** Tenant management Groovy scripts location */
	private List<String> tenantManagement;

	public List<String> getUserManagement() {
	    return userManagement;
	}

	public void setUserManagement(List<String> userManagement) {
	    this.userManagement = userManagement;
	}

	public List<String> getTenantManagement() {
	    return tenantManagement;
	}

	public void setTenantManagement(List<String> tenantManagement) {
	    this.tenantManagement = tenantManagement;
	}
    }
}
