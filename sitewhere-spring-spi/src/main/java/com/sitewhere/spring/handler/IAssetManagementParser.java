package com.sitewhere.spring.handler;

/**
 * Enumerates elements used by asset management parser.
 * 
 * @author Derek
 */
public interface IAssetManagementParser {

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** References an asset module defined as a Spring bean */
	AssetModuleReference("asset-module"),

	/** Asset module that pulls data from WSO2 Identity Server */
	Wso2IdentityAssetModule("wso2-identity-asset-module"),

	/** Configures a filesystem device asset module */
	FilesystemDeviceAssetModule("filesystem-device-asset-module"),

	/** Configures a filesystem hardware asset module */
	FilesystemHardwareAssetModule("filesystem-hardware-asset-module"),

	/** Configures a filesystem person asset module */
	FilesystemPersonAssetModule("filesystem-person-asset-module"),

	/** Configures a filesystem location asset module */
	FilesystemLocationAssetModule("filesystem-location-asset-module");

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