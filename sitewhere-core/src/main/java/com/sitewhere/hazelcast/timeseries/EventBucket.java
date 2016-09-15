/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hazelcast.timeseries;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitewhere.rest.model.device.event.DeviceEvent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.DeviceEventType;

/**
 * Bucket that contains event references for a given chunk of time.
 * 
 * @author Derek
 */
public class EventBucket implements Serializable {

    /** Serial version UID */
    private static final long serialVersionUID = 4364382406533386001L;

    /** Time-based key for bucket */
    private long bucketKey;

    /** Map of events by assignment */
    private Map<String, List<EventEntry>> eventsByAssignment = new HashMap<String, List<EventEntry>>();

    /** Map of events by site */
    private Map<String, List<EventEntry>> eventsBySite = new HashMap<String, List<EventEntry>>();

    public EventBucket(long bucketKey) {
	this.bucketKey = bucketKey;
    }

    /**
     * Adds an event to the bucket.
     * 
     * @param event
     */
    public void addEvent(DeviceEvent event) {
	EventEntry entry = new EventEntry(event);
	List<EventEntry> assnEntries = getEventsByAssignment().get(event.getDeviceAssignmentToken());
	if (assnEntries == null) {
	    assnEntries = new ArrayList<EventEntry>();
	    getEventsByAssignment().put(event.getDeviceAssignmentToken(), assnEntries);
	}
	assnEntries.add(entry);

	List<EventEntry> siteEntries = getEventsBySite().get(event.getSiteToken());
	if (siteEntries == null) {
	    siteEntries = new ArrayList<EventEntry>();
	    getEventsBySite().put(event.getSiteToken(), siteEntries);
	}
	siteEntries.add(entry);
    }

    /**
     * Get events for the given scope that match the criteria.
     * 
     * @param scope
     * @param token
     * @param type
     * @return
     * @throws SiteWhereException
     */
    public List<String> getEventsForScope(Scope scope, String token, DeviceEventType type) throws SiteWhereException {
	switch (scope) {
	case Assignment: {
	    List<EventEntry> entries = getEventsByAssignment().get(token);
	    return getMatchingEvents(entries, type);
	}
	case Site: {
	    List<EventEntry> entries = getEventsBySite().get(token);
	    return getMatchingEvents(entries, type);
	}
	}
	throw new SiteWhereException("Unknown scope: " + scope.name());
    }

    /**
     * Get events that match the given criteria.
     * 
     * @param entries
     * @param type
     * @return
     */
    protected List<String> getMatchingEvents(List<EventEntry> entries, DeviceEventType type) {
	List<String> results = new ArrayList<String>();
	if (entries == null) {
	    return results;
	}
	for (EventEntry entry : entries) {
	    if ((type == null) || (entry.getType() == type)) {
		results.add(entry.getEventId());
	    }
	}
	return results;
    }

    public long getBucketKey() {
	return bucketKey;
    }

    protected void setBucketKey(long bucketKey) {
	this.bucketKey = bucketKey;
    }

    protected Map<String, List<EventEntry>> getEventsByAssignment() {
	return eventsByAssignment;
    }

    protected void setEventsByAssignment(Map<String, List<EventEntry>> eventsByAssignment) {
	this.eventsByAssignment = eventsByAssignment;
    }

    protected Map<String, List<EventEntry>> getEventsBySite() {
	return eventsBySite;
    }

    protected void setEventsBySite(Map<String, List<EventEntry>> eventsBySite) {
	this.eventsBySite = eventsBySite;
    }

    /** Scope used for searching buckets */
    public static enum Scope {
	Assignment, Site;
    }

    public static class EventEntry implements Serializable {

	/** Serial version UID */
	private static final long serialVersionUID = -7792505276753005067L;

	private String eventId;

	private DeviceEventType type;

	public EventEntry(DeviceEvent event) {
	    this.eventId = event.getId();
	    this.type = event.getEventType();
	}

	public String getEventId() {
	    return eventId;
	}

	public void setEventId(String eventId) {
	    this.eventId = eventId;
	}

	public DeviceEventType getType() {
	    return type;
	}

	public void setType(DeviceEventType type) {
	    this.type = type;
	}
    }
}