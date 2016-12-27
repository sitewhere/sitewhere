package com.sitewhere.spring.handler;

/**
 * Enumerates elements used by event processing parser.
 * 
 * @author Derek
 */
public interface IEventProcessingParser {

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** Inbound processing strategy */
	InboundProcessingStrategy("inbound-processing-strategy"),

	/** Outbound processing strategy */
	OutboundProcessingStrategy("outbound-processing-strategy"),

	/** Inbound processing chain */
	InboundProcessingChain("inbound-processing-chain"),

	/** Outbound processing chain */
	OutboundProcessingChain("outbound-processing-chain");

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