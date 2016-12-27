package com.sitewhere.spring.handler;

/**
 * Enumerates elements used by batch operations parser.
 * 
 * @author Derek
 */
public interface IBatchOperationsParser {

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** Default batch operation manager */
	DefaultBatchOperationManager("default-batch-operation-manager"),

	/** Batch operation manager reference */
	BatchOperationManager("batch-operation-manager");

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