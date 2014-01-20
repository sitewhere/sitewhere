/*
 * DefaultDeviceEventProcessorChain.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.device.event.processor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;

import com.sitewhere.spi.device.event.processor.IDeviceEventProcessor;
import com.sitewhere.spi.device.event.processor.IDeviceEventProcessorChain;

/**
 * Default implementation of {@link IDeviceEventProcessorChain} interface.
 * 
 * @author Derek
 */
public class DefaultDeviceEventProcessorChain implements IDeviceEventProcessorChain, InitializingBean {

	/** List of event processors */
	private List<IDeviceEventProcessor> processors = new ArrayList<IDeviceEventProcessor>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		for (IDeviceEventProcessor processor : getProcessors()) {
			processor.start();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IDeviceEventProcessorChain#getProcessors()
	 */
	@Override
	public List<IDeviceEventProcessor> getProcessors() {
		return processors;
	}

	public void setProcessors(List<IDeviceEventProcessor> processors) {
		this.processors = processors;
	}
}