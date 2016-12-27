package com.sitewhere.spring.handler;

/**
 * Enumerates elements used by command routing parser.
 * 
 * @author Derek
 */
public interface ICommandRoutingParser {

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** Command router reference */
	CommandRouter("command-router"),

	/** Specification command router */
	SpecificationMappingRouter("specification-mapping-router");

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