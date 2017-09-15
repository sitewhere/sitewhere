package com.sitewhere.spring.parser;

/**
 * Enumerates elements used by instance global parser.
 * 
 * @author Derek
 */
public interface IInstanceGlobalParser {

    public static enum Elements {

	/** MongoDB configuration elements */
	MongoConfiguration("mongodb-configuration");

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

    public static enum MongoDbElements {

	/** Default MongoDB configuration */
	DefaultMongoConfiguration("default-mongodb-configuration"),

	/** Alternate MongoDB configuration */
	AlternateMongoConfiguration("alternate-mongodb-configuration");

	/** Event code */
	private String localName;

	private MongoDbElements(String localName) {
	    this.localName = localName;
	}

	public static MongoDbElements getByLocalName(String localName) {
	    for (MongoDbElements value : MongoDbElements.values()) {
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