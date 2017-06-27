package com.sitewhere.device.marshaling;

import com.sitewhere.rest.model.asset.HardwareAsset;

/**
 * Used to show broken link if referenced asset is deleted.
 * 
 * @author Derek
 */
public class InvalidAsset extends HardwareAsset {

    /** Serial version UID */
    private static final long serialVersionUID = 1383739852322979924L;

    public InvalidAsset() {
	super();
	setId("invalid");
	setAssetCategoryId("invalid");
	setName("Missing Asset");
	setDescription("Referenced asset was not found.");
	setImageUrl("https://s3.amazonaws.com/sitewhere-demo/broken-link.png");
	setSku("invalid");
    }
}
