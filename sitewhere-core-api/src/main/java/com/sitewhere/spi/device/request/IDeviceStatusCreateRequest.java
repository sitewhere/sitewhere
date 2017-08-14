package com.sitewhere.spi.device.request;

/**
 * Information needed to create a device status.
 * 
 * @author Derek
 */
public interface IDeviceStatusCreateRequest {

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
     * Icon for user interface.
     * 
     * @return
     */
    public String getIcon();
}