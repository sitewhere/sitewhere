package com.sitewhere.spring.handler;

/**
 * Enumerates elements used by outbound processing strategy parser.
 * 
 * @author Derek
 */
public interface IOutboundProcessingStrategyParser {

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** Blocking queue outbound processing strategy */
	BlockingQueueOutboundProcessingStrategy("blocking-queue-outbound-processing-strategy"),

	/** Default outbound processing strategy */
	DefaultOutboundProcessingStrategy("default-outbound-processing-strategy");

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