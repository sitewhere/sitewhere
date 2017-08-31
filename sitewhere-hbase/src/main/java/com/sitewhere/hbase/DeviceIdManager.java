/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase;

import com.sitewhere.hbase.uid.UniqueIdCounterMap;
import com.sitewhere.hbase.uid.UniqueIdType;
import com.sitewhere.hbase.uid.UuidRowKeyMap;
import com.sitewhere.spi.SiteWhereException;

/**
 * Singleton that keeps up with ids for entities.
 * 
 * @author Derek
 */
public class DeviceIdManager implements IDeviceIdManager {

    /** Manager for site tokens */
    private UniqueIdCounterMap siteKeys;

    /** Manager for device ids */
    private UniqueIdCounterMap deviceKeys;

    /** Manager for device specification tokens */
    private UniqueIdCounterMap specificationKeys;

    /** Manager for device group tokens */
    private UniqueIdCounterMap deviceGroupKeys;

    /** Manager for batch operation tokens */
    private UniqueIdCounterMap batchOperationKeys;

    /** Manager for command tokens */
    private UuidRowKeyMap commandKeys;

    /** Manager for zone ids */
    private UuidRowKeyMap zoneKeys;

    /** Manager for device assignment ids */
    private UuidRowKeyMap assignmentKeys;

    /**
     * Load key managers from HBase.
     * 
     * @param context
     * @throws SiteWhereException
     */
    public void load(IHBaseContext context) throws SiteWhereException {
	siteKeys = new UniqueIdCounterMap(context, UniqueIdType.SiteKey.getIndicator(),
		UniqueIdType.SiteValue.getIndicator());
	siteKeys.refresh();

	deviceKeys = new UniqueIdCounterMap(context, UniqueIdType.DeviceKey.getIndicator(),
		UniqueIdType.DeviceValue.getIndicator());
	deviceKeys.refresh();

	specificationKeys = new UniqueIdCounterMap(context, UniqueIdType.SpecificationKey.getIndicator(),
		UniqueIdType.SpecificationValue.getIndicator());
	specificationKeys.refresh();

	deviceGroupKeys = new UniqueIdCounterMap(context, UniqueIdType.DeviceGroupKey.getIndicator(),
		UniqueIdType.DeviceGroupValue.getIndicator());
	deviceGroupKeys.refresh();

	batchOperationKeys = new UniqueIdCounterMap(context, UniqueIdType.BatchOperationKey.getIndicator(),
		UniqueIdType.BatchOperationValue.getIndicator());
	batchOperationKeys.refresh();

	commandKeys = new UuidRowKeyMap(context, UniqueIdType.CommandKey.getIndicator(),
		UniqueIdType.CommandValue.getIndicator());
	commandKeys.refresh();

	zoneKeys = new UuidRowKeyMap(context, UniqueIdType.ZoneKey.getIndicator(),
		UniqueIdType.ZoneValue.getIndicator());
	zoneKeys.refresh();

	assignmentKeys = new UuidRowKeyMap(context, UniqueIdType.DeviceAssignmentKey.getIndicator(),
		UniqueIdType.DeviceAssignmentValue.getIndicator());
	assignmentKeys.refresh();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.device.IDeviceIdManager#getSiteKeys()
     */
    public UniqueIdCounterMap getSiteKeys() {
	return siteKeys;
    }

    protected void setSiteKeys(UniqueIdCounterMap siteKeys) {
	this.siteKeys = siteKeys;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.device.IDeviceIdManager#getDeviceKeys()
     */
    public UniqueIdCounterMap getDeviceKeys() {
	return deviceKeys;
    }

    protected void setDeviceKeys(UniqueIdCounterMap deviceKeys) {
	this.deviceKeys = deviceKeys;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.device.IDeviceIdManager#getSpecificationKeys()
     */
    public UniqueIdCounterMap getSpecificationKeys() {
	return specificationKeys;
    }

    protected void setSpecificationKeys(UniqueIdCounterMap specificationKeys) {
	this.specificationKeys = specificationKeys;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.device.IDeviceIdManager#getCommandKeys()
     */
    public UuidRowKeyMap getCommandKeys() {
	return commandKeys;
    }

    public void setCommandKeys(UuidRowKeyMap commandKeys) {
	this.commandKeys = commandKeys;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.device.IDeviceIdManager#getDeviceGroupKeys()
     */
    public UniqueIdCounterMap getDeviceGroupKeys() {
	return deviceGroupKeys;
    }

    public void setDeviceGroupKeys(UniqueIdCounterMap deviceGroupKeys) {
	this.deviceGroupKeys = deviceGroupKeys;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.device.IDeviceIdManager#getBatchOperationKeys()
     */
    public UniqueIdCounterMap getBatchOperationKeys() {
	return batchOperationKeys;
    }

    public void setBatchOperationKeys(UniqueIdCounterMap batchOperationKeys) {
	this.batchOperationKeys = batchOperationKeys;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.device.IDeviceIdManager#getZoneKeys()
     */
    public UuidRowKeyMap getZoneKeys() {
	return zoneKeys;
    }

    protected void setZoneKeys(UuidRowKeyMap zoneKeys) {
	this.zoneKeys = zoneKeys;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hbase.device.IDeviceIdManager#getAssignmentKeys()
     */
    public UuidRowKeyMap getAssignmentKeys() {
	return assignmentKeys;
    }

    protected void setAssignmentKeys(UuidRowKeyMap assignmentKeys) {
	this.assignmentKeys = assignmentKeys;
    }
}