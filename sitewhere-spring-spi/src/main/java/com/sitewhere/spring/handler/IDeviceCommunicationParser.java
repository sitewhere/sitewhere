package com.sitewhere.spring.handler;

/**
 * Enumerates elements used by asset management parser.
 * 
 * @author Derek
 */
public interface IDeviceCommunicationParser {

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** Event sources list */
	EventSources("event-sources"),

	/** Device services */
	DeviceServices("device-services"),

	/** Batch operations */
	BatchOperations("batch-operations"),

	/** Command routing configuration */
	CommandRouting("command-routing"),

	/** Command destinations list */
	CommandDestinations("command-destinations");

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