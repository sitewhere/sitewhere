package com.sitewhere.spi.device.event.request;

/**
 * Information needed to map a device to a slot on a composite device.
 * 
 * @author Derek
 */
public interface IDeviceMappingCreateRequest {

	/**
	 * Get hardware id of composite device that will add device mapping.
	 * 
	 * @return
	 */
	public String getCompositeDeviceHardwareId();

	/**
	 * Get path in device element schema to be mapped.
	 * 
	 * @return
	 */
	public String getMappingPath();
}