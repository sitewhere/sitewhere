/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.sitewhere.core.device;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitewhere.rest.model.common.Location;
import com.sitewhere.rest.model.device.DeviceAssignmentHistoryEntry;
import com.sitewhere.rest.model.device.InterpolatedAssignmentHistory;
import com.sitewhere.spi.device.IDeviceLocation;

/**
 * Builds an interpolated history based on location results.
 * 
 * @author Derek Adams
 */
public class InterpolatedHistoryBuilder {

	/** Map of locations by device assignment */
	private Map<String, List<IDeviceLocation>> locationsByAssignment = new HashMap<String, List<IDeviceLocation>>();

	/** Minimum slot value */
	private Long minSlot;

	/** Maximum slot value */
	private Long maxSlot;

	/** Build history entries for all assignments */
	public List<InterpolatedAssignmentHistory> build(List<IDeviceLocation> matches) {
		prepareData(matches);
		List<InterpolatedAssignmentHistory> results = new ArrayList<InterpolatedAssignmentHistory>();
		for (String assignmentToken : locationsByAssignment.keySet()) {
			InterpolatedAssignmentHistory history = createEntryFor(assignmentToken);
			results.add(history);
		}
		return results;
	}

	/** Create a history entry for an assignment */
	protected InterpolatedAssignmentHistory createEntryFor(String assignmentToken) {
		InterpolatedAssignmentHistory history = new InterpolatedAssignmentHistory();
		history.setDeviceAssignmentToken(assignmentToken);
		history.setSlots(createEmptySlots());
		List<IDeviceLocation> locations = locationsByAssignment.get(assignmentToken);
		if (locations != null) {
			Collections.sort(locations);
			IDeviceLocation last = null;
			for (IDeviceLocation location : locations) {
				if (last == null) {
					last = location;
					continue;
				}
				interpolate(history, last, location);
				last = location;
			}
		}
		return history;
	}

	/** Fill in slots with interpolated information */
	protected void interpolate(InterpolatedAssignmentHistory history, IDeviceLocation last,
			IDeviceLocation location) {
		long lastSlot = last.getEventDate().getTime();
		long currSlot = location.getEventDate().getTime();
		double lastLat = last.getLatitude();
		double currLat = location.getLatitude();
		double lastLong = last.getLongitude();
		double currLong = location.getLongitude();
		int numSlots = ((int) (currSlot - lastSlot) / (60 * 1000));
		double latDelta = (currLat - lastLat) / numSlots;
		double longDelta = (currLong - lastLong) / numSlots;
		for (int i = 0; i < numSlots; i++) {
			long slot = (lastSlot + (i * (60 * 1000)));
			double lat = (lastLat + (i * latDelta));
			double lon = (lastLong + (i * longDelta));
			setSlotValue(history, slot, lat, lon);
		}
	}

	/** Set value into one of the history slots */
	protected void setSlotValue(InterpolatedAssignmentHistory history, long slot, double lat, double lon) {
		for (DeviceAssignmentHistoryEntry entry : history.getSlots()) {
			if (entry.getTimeSlot() == slot) {
				Location location = new Location();
				location.setLatitude(lat);
				location.setLongitude(lon);
				entry.setLocation(location);
				return;
			}
		}
		System.out.println("Invalid slot value");
	}

	/** Create empty list of all slot values */
	protected List<DeviceAssignmentHistoryEntry> createEmptySlots() {
		List<DeviceAssignmentHistoryEntry> entries = new ArrayList<DeviceAssignmentHistoryEntry>();
		long slot = minSlot;
		while (slot < maxSlot) {
			DeviceAssignmentHistoryEntry entry = new DeviceAssignmentHistoryEntry();
			entry.setTimeSlot(slot);
			entries.add(entry);
			slot += (60 * 1000);
		}
		return entries;
	}

	/** Prepare data */
	protected void prepareData(List<IDeviceLocation> locations) {
		locationsByAssignment.clear();
		for (IDeviceLocation location : locations) {
			List<IDeviceLocation> match = locationsByAssignment.get(location.getDeviceAssignmentToken());
			if (match == null) {
				match = new ArrayList<IDeviceLocation>();
				locationsByAssignment.put(location.getDeviceAssignmentToken(), match);
			}
			roundToTheMinute(location.getEventDate());
			long locSlot = location.getEventDate().getTime();
			if ((minSlot == null) || (minSlot > locSlot)) {
				minSlot = locSlot;
			}
			if ((maxSlot == null) || (maxSlot < locSlot)) {
				maxSlot = locSlot;
			}
			match.add(location);
		}
	}

	/** Gets rid of everything below the minute on a date */
	protected void roundToTheMinute(Date input) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(input);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		input.setTime(cal.getTimeInMillis());
	}
}