package com.sitewhere.spring.handler;

/**
 * Enumerates elements used by datastore parser.
 * 
 * @author Derek
 */
public interface IDatastoreParser {

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** Mongo datastore and service providers */
	Mongo("mongo-datastore"),

	/** HBase datastore and service providers */
	HBase("hbase-datastore"),

	/** Creates sample data if no user data is present */
	DefaultUserModelInitializer("default-user-model-initializer"),

	/** Uses Groovy script to create user data */
	GroovyUserModelInitializer("groovy-user-model-initializer"),

	/** Creates sample data if no tenant data is present */
	DefaultTenantModelInitializer("default-tenant-model-initializer"),

	/** Uses Groovy script to create tenant data */
	GroovyTenantModelInitializer("groovy-tenant-model-initializer");

	/** Event code */
	private String localName;

	private Elements(String localName) {
	    this.localName = localName;
	}

	public static Elements getByLocalName(String localName) {
	    for (Elements value : Elements.values()) {
		if (value.getLocalName().equals(localName)) {
		    return value;
		}
	    }
	    return null;
	}

	public String getLocalName() {
	    return localName;
	}

	public void setLocalName(String localName) {
	    this.localName = localName;
	}
    }
}