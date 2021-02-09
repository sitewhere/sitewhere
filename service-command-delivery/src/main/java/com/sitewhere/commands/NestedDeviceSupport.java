/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.commands;

import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.util.DeviceUtils;

import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;

/**
 * Provides support logic for handling interactions with nested devices.
 */
public class NestedDeviceSupport {

    /**
     * Perform common logic for locating device nesting information.
     * 
     * @param target
     * @return
     * @throws SiteWhereException
     */
    public static NestedDeviceInformation calculateNestedDeviceInformation(IDevice target, SiteWhereTenant tenant)
	    throws SiteWhereException {
	NestedDeviceInformation nested = new NestedDeviceInformation();

	// No parent set. Treat target device as gateway.
	if (target.getParentDeviceId() == null) {
	    nested.setGateway(target);
	    return nested;
	}

	// Resolve parent and verify it exists.
	IDevice parent = getDeviceManagement().getDevice(target.getParentDeviceId());
	if (parent == null) {
	    throw new SiteWhereException("Parent device reference points to device that does not exist.");
	}

	// Parent should contain a mapping entry for the target device.
	IDeviceElementMapping mapping = DeviceUtils.findMappingFor(parent, target.getToken());

	// Fall back to target as gateway if no mapping exists. This should not
	// happen.
	if (mapping == null) {
	    nested.setGateway(target);
	    return nested;
	}

	nested.setGateway(parent);
	nested.setNested(target);
	nested.setPath(mapping.getDeviceElementSchemaPath());
	return nested;
    }

    /**
     * Holds fields passed for addressing nested devices via a gateway.
     */
    public static class NestedDeviceInformation implements IDeviceNestingContext {

	/** Primary hardware id */
	private IDevice gateway;

	/** Nested hardware id */
	private IDevice nested;

	/** Path to nested device */
	private String path;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceNestingContext#getGateway()
	 */
	public IDevice getGateway() {
	    return gateway;
	}

	public void setGateway(IDevice gateway) {
	    this.gateway = gateway;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceNestingContext#getNested()
	 */
	public IDevice getNested() {
	    return nested;
	}

	public void setNested(IDevice nested) {
	    this.nested = nested;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceNestingContext#getPath()
	 */
	public String getPath() {
	    return path;
	}

	public void setPath(String path) {
	    this.path = path;
	}
    }

    private static IDeviceManagement getDeviceManagement() {
	return null;
    }
}