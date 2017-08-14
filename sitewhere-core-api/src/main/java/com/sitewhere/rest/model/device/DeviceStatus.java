package com.sitewhere.rest.model.device;

import com.sitewhere.spi.device.IDeviceStatus;

/**
 * Model implementation of {@link IDeviceStatus}.
 * 
 * @author Derek
 */
public class DeviceStatus implements IDeviceStatus {

    /** Status code */
    private String code;

    /** Specification token */
    private String specificationToken;

    /** Display name */
    private String name;

    /** Background color */
    private String backgroundColor;

    /** Foreground color */
    private String foregroundColor;

    /** Icon */
    private String icon;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceStatus#getCode()
     */
    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceStatus#getSpecificationToken()
     */
    public String getSpecificationToken() {
	return specificationToken;
    }

    public void setSpecificationToken(String specificationToken) {
	this.specificationToken = specificationToken;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceStatus#getName()
     */
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceStatus#getBackgroundColor()
     */
    public String getBackgroundColor() {
	return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
	this.backgroundColor = backgroundColor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceStatus#getForegroundColor()
     */
    public String getForegroundColor() {
	return foregroundColor;
    }

    public void setForegroundColor(String foregroundColor) {
	this.foregroundColor = foregroundColor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceStatus#getIcon()
     */
    public String getIcon() {
	return icon;
    }

    public void setIcon(String icon) {
	this.icon = icon;
    }
}