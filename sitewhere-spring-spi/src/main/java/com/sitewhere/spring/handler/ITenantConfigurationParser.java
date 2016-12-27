package com.sitewhere.spring.handler;

/**
 * Enumerates elements used by tenant configuration parser.
 * 
 * @author Derek
 */
public interface ITenantConfigurationParser {

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** Globals */
	Globals("globals"),

	/** Tenant datastore */
	TenantDatastore("tenant-datastore"),

	/** Provisioning (DEPRECATED) */
	@Deprecated
	Provisioning("provisioning"),

	/** Device Communication Subsystem */
	DeviceCommunication("device-communication"),

	/** Event processing Subsystem */
	EventProcessing("event-processing"),

	/** Asset management */
	AssetManagement("asset-management"),

	/** Search providers */
	SearchProviders("search-providers");

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