/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.uid;

/**
 * Row type indicators for the UID table.
 * 
 * @author Derek
 */
public enum UniqueIdType {

    /** First byte for rows that act as counters */
    CounterPlaceholder((byte) 0x00),

    /** Key and value for site UUIDs */
    SiteKey((byte) 0x01), SiteValue((byte) 0x02),

    /** Key and value for device UUIDs */
    DeviceKey((byte) 0x03), DeviceValue((byte) 0x04),

    /** Key and value for zone UUIDs */
    ZoneKey((byte) 0x05), ZoneValue((byte) 0x06),

    /** Key and value for assignment UUIDs */
    DeviceAssignmentKey((byte) 0x07), DeviceAssignmentValue((byte) 0x08),

    /** Key and value for specification UUIDs */
    SpecificationKey((byte) 0x09), SpecificationValue((byte) 0x0a),

    /** Key and value for specification UUIDs */
    CommandKey((byte) 0x0b), CommandValue((byte) 0x0c),

    /** Key and value for device group UUIDs */
    DeviceGroupKey((byte) 0x0d), DeviceGroupValue((byte) 0x0e),

    /** Key and value for batch operation UUIDs */
    BatchOperationKey((byte) 0x10), BatchOperationValue((byte) 0x11),

    /** Key and value for tenant ids */
    TenantKey((byte) 0x11), TenantValue((byte) 0x12),

    /** Key and value for tenant group ids */
    TenantGroupKey((byte) 0x13), TenantGroupValue((byte) 0x14),

    /** Key and value for asset UUIDs */
    AssetKey((byte) 0x01), AssetValue((byte) 0x02),

    /** Key and value for schedule UUIDs */
    ScheduleKey((byte) 0x01), ScheduleValue((byte) 0x02),

    /** Key and value for scheduled job UUIDs */
    ScheduledJobKey((byte) 0x03), ScheduledJobValue((byte) 0x04);

    /** Type indicator */
    private byte indicator;

    /**
     * Create a unique id type with the given byte value.
     * 
     * @param value
     */
    private UniqueIdType(byte indicator) {
	this.indicator = indicator;
    }

    /**
     * Get the UID type indicator.
     * 
     * @return
     */
    public byte getIndicator() {
	return indicator;
    }
}