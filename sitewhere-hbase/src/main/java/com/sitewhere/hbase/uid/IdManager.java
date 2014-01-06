/*
 * IdManager.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
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

	/** Manager for site ids */
	private UnqiueIdCounterMap siteKeys;

	/** Manager for device ids */
	private UnqiueIdCounterMap deviceKeys;

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
		siteKeys = new UnqiueIdCounterMap(hbase, UniqueIdType.SiteKey, UniqueIdType.SiteValue);
		siteKeys.refresh();
		deviceKeys = new UnqiueIdCounterMap(hbase, UniqueIdType.DeviceKey, UniqueIdType.DeviceValue);
		deviceKeys.refresh();
		zoneKeys = new UuidRowKeyMap(hbase, UniqueIdType.ZoneKey, UniqueIdType.ZoneValue);
		zoneKeys.refresh();
		assignmentKeys =
				new UuidRowKeyMap(hbase, UniqueIdType.DeviceAssignmentKey, UniqueIdType.DeviceAssignmentValue);
		assignmentKeys.refresh();
	}

	public UnqiueIdCounterMap getSiteKeys() {
		return siteKeys;
	}

	protected void setSiteKeys(UnqiueIdCounterMap siteKeys) {
		this.siteKeys = siteKeys;
	}

	public UnqiueIdCounterMap getDeviceKeys() {
		return deviceKeys;
	}

	public void setDeviceKeys(UnqiueIdCounterMap deviceKeys) {
		this.deviceKeys = deviceKeys;
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

	public void setAssignmentKeys(UuidRowKeyMap assignmentKeys) {
		this.assignmentKeys = assignmentKeys;
	}
}