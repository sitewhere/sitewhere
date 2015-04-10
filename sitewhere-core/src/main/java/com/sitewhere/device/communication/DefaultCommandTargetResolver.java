/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.communication.ICommandTargetResolver;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Uses information in an {@link IDeviceCommandInvocation} to determine a list of target
 * {@link IDeviceAssignment} objects. This implementation returns the
 * {@link IDeviceAssignment} associated with the invocation.
 * 
 * @author Derek
 */
public class DefaultCommandTargetResolver extends LifecycleComponent implements ICommandTargetResolver {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DefaultCommandTargetResolver.class);

	public DefaultCommandTargetResolver() {
		super(LifecycleComponentType.CommandTargetResolver);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.ICommandTargetResolver#resolveTargets(com
	 * .sitewhere.spi.device.event.IDeviceCommandInvocation)
	 */
	@Override
	public List<IDeviceAssignment> resolveTargets(IDeviceCommandInvocation invocation)
			throws SiteWhereException {
		LOGGER.debug("Resolving target for invocation.");
		IDeviceAssignment assignment =
				SiteWhere.getServer().getDeviceManagement().getDeviceAssignmentByToken(
						invocation.getDeviceAssignmentToken());
		List<IDeviceAssignment> results = new ArrayList<IDeviceAssignment>();
		results.add(assignment);
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		LOGGER.info("Started command target resolver.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		LOGGER.info("Stopped command target resolver");
	}
}