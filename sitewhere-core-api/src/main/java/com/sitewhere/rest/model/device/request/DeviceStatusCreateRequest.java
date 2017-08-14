package com.sitewhere.rest.model.device.request;

import com.sitewhere.spi.device.request.IDeviceStatusCreateRequest;

/**
 * Model object implementing {@link IDeviceStatusCreateRequest}.
 * 
 * @author Derek
 */
public class DeviceStatusCreateRequest implements IDeviceStatusCreateRequest {

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
     * @see
     * com.sitewhere.spi.device.request.IDeviceStatusCreateRequest#getCode()
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
     * @see com.sitewhere.spi.device.request.IDeviceStatusCreateRequest#
     * getSpecificationToken()
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
     * @see
     * com.sitewhere.spi.device.request.IDeviceStatusCreateRequest#getName()
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
     * @see com.sitewhere.spi.device.request.IDeviceStatusCreateRequest#
     * getBackgroundColor()
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
     * @see com.sitewhere.spi.device.request.IDeviceStatusCreateRequest#
     * getForegroundColor()
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
     * @see
     * com.sitewhere.spi.device.request.IDeviceStatusCreateRequest#getIcon()
     */
    public String getIcon() {
	return icon;
    }

    public void setIcon(String icon) {
	this.icon = icon;
    }
}