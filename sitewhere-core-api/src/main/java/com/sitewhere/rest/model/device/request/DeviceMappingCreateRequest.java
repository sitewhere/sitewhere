package com.sitewhere.rest.model.device.request;

import com.sitewhere.spi.device.event.request.IDeviceMappingCreateRequest;

/**
 * Request for mapping a device to a path on a composite device.
 * 
 * @author Derek
 */
public class DeviceMappingCreateRequest implements IDeviceMappingCreateRequest {

    /** Hardware id of composite device containing mapping */
    private String compositeDeviceHardwareId;

    /** Path device will be mapped to */
    private String mappingPath;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.request.IDeviceMappingCreateRequest#
     * getCompositeDeviceHardwareId()
     */
    public String getCompositeDeviceHardwareId() {
	return compositeDeviceHardwareId;
    }

    public void setCompositeDeviceHardwareId(String compositeDeviceHardwareId) {
	this.compositeDeviceHardwareId = compositeDeviceHardwareId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.request.IDeviceMappingCreateRequest#
     * getMappingPath()
     */
    public String getMappingPath() {
	return mappingPath;
    }

    public void setMappingPath(String mappingPath) {
	this.mappingPath = mappingPath;
    }
}