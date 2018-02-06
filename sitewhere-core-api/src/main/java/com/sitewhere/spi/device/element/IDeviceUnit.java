/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.element;

import java.util.List;

/**
 * Used to group related {@link IDeviceSlot} elements into logical units.
 * 
 * @author Derek
 */
public interface IDeviceUnit extends IDeviceElement {

    /**
     * Get list of slots associated with the unit.
     * 
     * @return
     */
    public List<IDeviceSlot> getDeviceSlots();

    /**
     * Get list of subordinate units associated with the unit.
     * 
     * @return
     */
    public List<IDeviceUnit> getDeviceUnits();
}