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
	HBaseTenantDatastore("hbase-tenant-datastore"),

	/** EHCache device mananagement cache provider */
	@Deprecated
	EHCacheDeviceManagementCache("ehcache-device-management-cache"),

	/** Hazelcast cache provider */
	@Deprecated
	HazelcastCache("hazelcast-cache"),

	/** Creates sample data if no device data is present */
	@Deprecated
	DefaultDeviceModelInitializer("default-device-model-initializer"),

	/** Create sample device data based on logic in a Groovy script */
	@Deprecated
	GroovyDeviceModelInitializer("groovy-device-model-initializer"),

	/** Creates sample data if no asset data is present */
	@Deprecated
	DefaultAssetModelInitializer("default-asset-model-initializer"),

	/** Creates sample data if no schedule data is present */
	@Deprecated
	DefaultScheduleModelInitializer("default-schedule-model-initializer");

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