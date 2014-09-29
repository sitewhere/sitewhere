/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.uid;

import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.spi.SiteWhereException;

/**
 * Singleton that keeps up with ids for entities.
 * 
 * @author Derek
 */
public class IdManager {

	/** Singleton instance */
	private static IdManager SINGLETON;

	/** Manager for site tokens */
	private UniqueIdCounterMap siteKeys;

	/** Manager for device ids */
	private UniqueIdCounterMap deviceKeys;

	/** Manager for device specification tokens */
	private UniqueIdCounterMap specificationKeys;

	/** Manager for device group tokens */
	private UniqueIdCounterMap deviceGroupKeys;

	/** Manager for command tokens */
	private UuidRowKeyMap commandKeys;

	/** Manager for zone ids */
	private UuidRowKeyMap zoneKeys;

	/** Manager for device assignment ids */
	private UuidRowKeyMap assignmentKeys;

	private IdManager() {
	}

	public static IdManager getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new IdManager();
		}
		return SINGLETON;
	}

	/**
	 * Load key managers from HBase.
	 * 
	 * @param hbase
	 * @throws SiteWhereException
	 */
	public void load(ISiteWhereHBaseClient hbase) throws SiteWhereException {
		siteKeys = new UniqueIdCounterMap(hbase, UniqueIdType.SiteKey, UniqueIdType.SiteValue);
		siteKeys.refresh();

		deviceKeys = new UniqueIdCounterMap(hbase, UniqueIdType.DeviceKey, UniqueIdType.DeviceValue);
		deviceKeys.refresh();

		specificationKeys =
				new UniqueIdCounterMap(hbase, UniqueIdType.SpecificationKey, UniqueIdType.SpecificationValue);
		specificationKeys.refresh();

		deviceGroupKeys =
				new UniqueIdCounterMap(hbase, UniqueIdType.DeviceGroupKey, UniqueIdType.DeviceGroupValue);
		deviceGroupKeys.refresh();

		commandKeys = new UuidRowKeyMap(hbase, UniqueIdType.CommandKey, UniqueIdType.CommandValue);
		commandKeys.refresh();

		zoneKeys = new UuidRowKeyMap(hbase, UniqueIdType.ZoneKey, UniqueIdType.ZoneValue);
		zoneKeys.refresh();

		assignmentKeys =
				new UuidRowKeyMap(hbase, UniqueIdType.DeviceAssignmentKey, UniqueIdType.DeviceAssignmentValue);
		assignmentKeys.refresh();
	}

	public UniqueIdCounterMap getSiteKeys() {
		return siteKeys;
	}

	protected void setSiteKeys(UniqueIdCounterMap siteKeys) {
		this.siteKeys = siteKeys;
	}

	public UniqueIdCounterMap getDeviceKeys() {
		return deviceKeys;
	}

	protected void setDeviceKeys(UniqueIdCounterMap deviceKeys) {
		this.deviceKeys = deviceKeys;
	}

	public UniqueIdCounterMap getSpecificationKeys() {
		return specificationKeys;
	}

	protected void setSpecificationKeys(UniqueIdCounterMap specificationKeys) {
		this.specificationKeys = specificationKeys;
	}

	public UuidRowKeyMap getCommandKeys() {
		return commandKeys;
	}

	public void setCommandKeys(UuidRowKeyMap commandKeys) {
		this.commandKeys = commandKeys;
	}

	public UniqueIdCounterMap getDeviceGroupKeys() {
		return deviceGroupKeys;
	}

	public void setDeviceGroupKeys(UniqueIdCounterMap deviceGroupKeys) {
		this.deviceGroupKeys = deviceGroupKeys;
	}

	public UuidRowKeyMap getZoneKeys() {
		return zoneKeys;
	}

	protected void setZoneKeys(UuidRowKeyMap zoneKeys) {
		this.zoneKeys = zoneKeys;
	}

	public UuidRowKeyMap getAssignmentKeys() {
		return assignmentKeys;
	}

	protected void setAssignmentKeys(UuidRowKeyMap assignmentKeys) {
		this.assignmentKeys = assignmentKeys;
	}
}