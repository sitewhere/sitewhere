package com.sitewhere.spring.parser;

/**
 * Enumerates elements used by tenant management parser.
 * 
 * @author Derek
 */
public interface ITenantManagementParser {

    public static enum Elements {

	/** Default MongoDB datastore */
	DefaultMongoDatastore("default-mongodb-datastore");

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