package com.sitewhere.spring.handler;

/**
 * Enumerates elements used by device services parser.
 * 
 * @author Derek
 */
public interface IDeviceServicesParser {

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** Default registration manager */
	DefaultRegistrationManager("default-registration-manager"),

	/** Registration manager reference */
	RegistrationManager("registration-manager"),

	/** Symbol generator manager reference */
	SymbolGeneratorManager("symbol-generator-manager"),

	/** Default presence manager */
	DefaultPresenceManager("default-presence-manager");

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

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum SymbolGenerators {

	/** QR-Code symbol generator */
	QRCodeSymbolGenerator("qr-code-symbol-generator");

	/** Event code */
	private String localName;

	private SymbolGenerators(String localName) {
	    this.localName = localName;
	}

	public static SymbolGenerators getByLocalName(String localName) {
	    for (SymbolGenerators value : SymbolGenerators.values()) {
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