/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hazelcast.timeseries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.listener.EntryEvictedListener;
import com.sitewhere.hazelcast.timeseries.EventBucket.Scope;
import com.sitewhere.rest.model.device.event.DeviceEvent;
import com.sitewhere.rest.model.search.Pager;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.ITenant;

/**
 * Manages mapping of time-series event data into Hazelcast maps.
 * 
 * @author Derek
 */
public class TimeSeriesManager {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(TimeSeriesManager.class);

	/** Map name for device event buckets */
	private static final String MAP_BUCKETS = "com.sitewhere.events.DeviceEventBucket";

	/** Map name for device event data */
	private static final String MAP_EVENTS = "com.sitewhere.events.DeviceEvent";

	/** Number of milliseconds covered by a bucket */
	private static final int BUCKET_INTERVAL = 60 * 1000;

	/** Number of chunks that can be queued before blocking */
	private static final int MAX_BUFFERED_EVENTS = 100000;

	/** Number of milliseconds to wait between persistence cycles */
	private static final int PERSISTENCE_INTERVAL = 1000;

	/** Tenant */
	private ITenant tenant;

	/** Handle to Hazelcast instance */
	private HazelcastInstance hazelcast;

	/** Number of seconds before events expire */
	private long expirationInSec;

	/** Map that stores event buckets */
	private IMap<Long, EventBucket> bucketsMap;

	/** Map that stores device events */
	private IMap<String, DeviceEvent> eventsMap;

	/** Used to manage persistence in a separate thread */
	private ExecutorService executor;

	/** Lower bound bucket value */
	private long lowerBound;

	/** Queue of events to be processed */
	private BlockingQueue<DeviceEvent> events = new ArrayBlockingQueue<DeviceEvent>(MAX_BUFFERED_EVENTS);

	public TimeSeriesManager(ITenant tenant, HazelcastInstance hazelcast, long expirationInSec) {
		this.tenant = tenant;
		this.hazelcast = hazelcast;
		this.expirationInSec = expirationInSec;

		this.bucketsMap = getHazelcast().getMap(MAP_BUCKETS + ":" + tenant.getId());
		this.eventsMap = getHazelcast().getMap(MAP_EVENTS + ":" + tenant.getId());

		// Use evictions to keep up with lower bound on buckets to search.
		bucketsMap.addEntryListener(new EntryEvictedListener<Long, EventBucket>() {

			@Override
			public void entryEvicted(EntryEvent<Long, EventBucket> event) {
				lowerBound = event.getKey();
			}
		}, false);

		this.lowerBound = (System.currentTimeMillis() - (expirationInSec * 1000)) / BUCKET_INTERVAL;
	}

	/**
	 * Start processing.
	 */
	public void start() {
		executor = Executors.newSingleThreadExecutor();
		executor.execute(new Persistence());
	}

	/**
	 * Stop processing.
	 */
	public void stop() {
		if (executor != null) {
			executor.shutdownNow();
		}
	}

	/**
	 * Add an event.
	 * 
	 * @param event
	 * @throws SiteWhereException
	 */
	public void addEvent(DeviceEvent event) throws SiteWhereException {
		try {
			events.put(event);
		} catch (InterruptedException e) {
			throw new SiteWhereException("Event processing blocked.", e);
		}
	}

	/**
	 * Get an event by unique id.
	 * 
	 * @param id
	 * @return
	 */
	public DeviceEvent getEventById(String id) {
		return getEventsMap().get(id);
	}

	/**
	 * Perform a search based on the given criteria.
	 * 
	 * @param scope
	 * @param token
	 * @param type
	 * @param criteria
	 * @param clazz
	 * @return
	 * @throws SiteWhereException
	 */
	public <T extends IDeviceEvent> ISearchResults<T> search(Scope scope, String token, DeviceEventType type,
			IDateRangeSearchCriteria criteria, Class<T> clazz) throws SiteWhereException {
		List<T> all = getEventsForScope(scope, token, type, clazz);
		Pager<T> pager = new Pager<>(criteria);
		for (T event : all) {
			if ((criteria.getStartDate() != null) && (criteria.getStartDate().after(event.getEventDate()))) {
				continue;
			}
			if ((criteria.getEndDate() != null) && (criteria.getEndDate().before(event.getEventDate()))) {
				continue;
			}
			pager.process(event);
		}
		return new SearchResults<T>(pager.getResults(), pager.getTotal());
	}

	/**
	 * Loop through buckets to find matching events.
	 * 
	 * @param scope
	 * @param token
	 * @param type
	 * @param clazz
	 * @return
	 * @throws SiteWhereException
	 */
	@SuppressWarnings({ "unchecked" })
	public <T extends IDeviceEvent> List<T> getEventsForScope(Scope scope, String token, DeviceEventType type,
			Class<T> clazz) throws SiteWhereException {
		long current = System.currentTimeMillis() / BUCKET_INTERVAL;
		List<T> matches = new ArrayList<T>();

		while (true) {
			EventBucket bucket = getBucketsMap().get(current);
			if (bucket != null) {
				List<String> eventIds = bucket.getEventsForScope(scope, token, type);
				for (String eventId : eventIds) {
					T event = (T) getEventById(eventId);
					if (event != null) {
						matches.add(event);
					}
				}
			}
			current--;
			if (current < lowerBound) {
				break;
			}
		}
		Collections.sort(matches, new Comparator<IDeviceEvent>() {

			@Override
			public int compare(IDeviceEvent a, IDeviceEvent b) {
				return b.getEventDate().compareTo(a.getEventDate());
			}
		});
		return matches;
	}

	/** Persists events from the queue at a given interval */
	private class Persistence implements Runnable {

		@Override
		public void run() {
			List<DeviceEvent> toProcess = new ArrayList<DeviceEvent>();

			while (true) {
				toProcess.clear();
				events.drainTo(toProcess);

				Map<Long, List<DeviceEvent>> buckets = new HashMap<Long, List<DeviceEvent>>();
				for (DeviceEvent event : toProcess) {
					getEventsMap().put(event.getId(), event, expirationInSec, TimeUnit.SECONDS);

					long bucket = event.getEventDate().getTime() / BUCKET_INTERVAL;
					List<DeviceEvent> bucketEvents = buckets.get(bucket);
					if (bucketEvents == null) {
						bucketEvents = new ArrayList<DeviceEvent>();
						buckets.put(bucket, bucketEvents);
					}
					bucketEvents.add(event);
				}
				for (Long key : buckets.keySet()) {
					try {
						getBucketsMap().lock(key);
						EventBucket bucket = getBucketsMap().get(key);
						if (bucket == null) {
							bucket = new EventBucket(key);
						}
						List<DeviceEvent> toAdd = buckets.get(key);
						for (DeviceEvent event : toAdd) {
							bucket.addEvent(event);
						}
						getBucketsMap().put(key, bucket, expirationInSec, TimeUnit.SECONDS);

						// Back up lower bound as necessary.
						if (key < lowerBound) {
							lowerBound = key;
						}
					} finally {
						getBucketsMap().unlock(key);
					}
				}

				try {
					Thread.sleep(PERSISTENCE_INTERVAL);
				} catch (InterruptedException e) {
					LOGGER.info("Persistence thread interrupted.");
					return;
				}
			}
		}
	}

	public ITenant getTenant() {
		return tenant;
	}

	public void setTenant(ITenant tenant) {
		this.tenant = tenant;
	}

	public HazelcastInstance getHazelcast() {
		return hazelcast;
	}

	public void setHazelcast(HazelcastInstance hazelcast) {
		this.hazelcast = hazelcast;
	}

	public IMap<Long, EventBucket> getBucketsMap() {
		return bucketsMap;
	}

	public void setBucketsMap(IMap<Long, EventBucket> bucketsMap) {
		this.bucketsMap = bucketsMap;
	}

	public IMap<String, DeviceEvent> getEventsMap() {
		return eventsMap;
	}

	public void setEventsMap(IMap<String, DeviceEvent> eventsMap) {
		this.eventsMap = eventsMap;
	}
}