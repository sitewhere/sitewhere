package com.sitewhere.spi.device.request;

import com.sitewhere.spi.common.IMetadataProvider;

/**
 * Information needed to create a device status.
 * 
 * @author Derek
 */
public interface IDeviceStatusCreateRequest extends IMetadataProvider {

    /**
     * Get the unique status code.
     * 
     * @return
     */
    public String getCode();

    /**
     * Get token for the parent specification.
     * 
     * @return
     */
    public String getSpecificationToken();

    /**
     * Name displayed in user interface.
     * 
     * @return
     */
    public String getName();

    /**
     * Background color for user interface.
     * 
     * @return
     */
    public String getBackgroundColor();

    /**
     * Foreground color for user interface.
     * 
     * @return
     */
    public String getForegroundColor();

    /**
     * Border color for user interface.
     * 
     * @return
     */
    public String getBorderColor();

    /**
     * Icon for user interface.
     * 
     * @return
     */
    public String getIcon();
}