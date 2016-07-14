package com.sitewhere.rest.model.device.command;

import com.sitewhere.spi.device.command.DeviceMappingResult;
import com.sitewhere.spi.device.command.IDeviceMappingAckCommand;
import com.sitewhere.spi.device.command.SystemCommandType;

/**
 * Default implementation of {@link IDeviceMappingAckCommand}.
 * 
 * @author Derek
 */
public class DeviceMappingAckCommand extends SystemCommand implements IDeviceMappingAckCommand {

	/** Serial version UID */
	private static final long serialVersionUID = 459571414041623952L;

	public DeviceMappingAckCommand() {
		super(SystemCommandType.DeviceMappingAck);
	}

	/** Device mapping result */
	private DeviceMappingResult result;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.command.IDeviceMappingAckCommand#getResult()
	 */
	public DeviceMappingResult getResult() {
		return result;
	}

	public void setResult(DeviceMappingResult result) {
		this.result = result;
	}
}