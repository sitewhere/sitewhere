package com.sitewhere.spi.device.command;

import java.io.Serializable;

/**
 * Sends information about a pending device mapping operation to an interested
 * device.
 * 
 * @author Derek
 */
public interface IDeviceMappingAckCommand extends ISystemCommand, Serializable {

    /**
     * Indicates result of device mapping operation.
     * 
     * @return
     */
    public DeviceMappingResult getResult();
}