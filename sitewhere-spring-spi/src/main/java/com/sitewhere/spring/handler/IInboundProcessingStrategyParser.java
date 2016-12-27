package com.sitewhere.spring.handler;

/**
 * Enumerates elements used by inbound processing strategy parser.
 * 
 * @author Derek
 */
public interface IInboundProcessingStrategyParser {

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** Blocking queue inbound processing strategy */
	BlockingQueueInboundProcessingStrategy("blocking-queue-inbound-processing-strategy"),

	/** Default inbound processing strategy */
	DefaultInboundProcessingStrategy("default-inbound-processing-strategy");

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