package com.sitewhere.spring.handler;

/**
 * Enumerates elements used by tenant datastore parser.
 * 
 * @author Derek
 */
public interface ITenantDatastoreParser {

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** Mongo tenant datastore service providers */
	MongoTenantDatastore("mongo-tenant-datastore"),

	/** Hybrid MongoDB/InfluxDB datastore configuration */
	MongoInfluxDbTenantDatastore("mongo-influxdb-tenant-datastore"),

	/** HBase tenant datastore service providers */
	HBaseTenantDatastore("hbase-tenant-datastore");

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