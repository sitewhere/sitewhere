/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication.debug;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sitewhere.core.DataUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IDecodedDeviceRequest;
import com.sitewhere.spi.device.communication.IDeviceEventDecoder;

/**
 * Implementation of {@link IDeviceEventDecoder} that logs the event payload but does not
 * actually produce any events. This is useful for debugging when implementing decoders
 * for hardware sending human-readable commands across the wire.
 * 
 * @author Derek
 */
public class PayloadLoggerEventDecoder implements IDeviceEventDecoder<byte[]> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(PayloadLoggerEventDecoder.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.IDeviceEventDecoder#decode(java.lang.Object)
	 */
	@Override
	public List<IDecodedDeviceRequest<?>> decode(byte[] payload) throws SiteWhereException {
		LOGGER.info("=== EVENT DATA BEGIN ===");
		LOGGER.info(new String(payload));
		LOGGER.info("(hex) " + DataUtils.bytesToHex(payload));
		LOGGER.info("=== EVENT DATA END ===");
		return new ArrayList<IDecodedDeviceRequest<?>>();
	}
}