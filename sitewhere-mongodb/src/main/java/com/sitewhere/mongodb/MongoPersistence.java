/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoCommandException;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.sitewhere.mongodb.device.IDeviceEventBuffer;
import com.sitewhere.mongodb.device.MongoDeviceAlert;
import com.sitewhere.mongodb.device.MongoDeviceCommandInvocation;
import com.sitewhere.mongodb.device.MongoDeviceCommandResponse;
import com.sitewhere.mongodb.device.MongoDeviceEvent;
import com.sitewhere.mongodb.device.MongoDeviceLocation;
import com.sitewhere.mongodb.device.MongoDeviceMeasurement;
import com.sitewhere.mongodb.device.MongoDeviceMeasurements;
import com.sitewhere.mongodb.device.MongoDeviceStateChange;
import com.sitewhere.mongodb.device.MongoDeviceStreamData;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ResourceExistsException;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchCriteria;

/**
 * Common handlers for persisting Mongo data.
 * 
 * @author Derek
 */
public class MongoPersistence {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Default lookup */
    private static IMongoConverterLookup LOOKUP = new MongoConverters();

    /**
     * Common handler for creating new objects. Assures that errors are handled
     * in a consistent way.
     * 
     * @param collection
     * @param object
     * @throws SiteWhereException
     */
    public static void insert(MongoCollection<Document> collection, Document object, ErrorCode ifDuplicate)
	    throws SiteWhereException {
	try {
	    long start = System.currentTimeMillis();
	    collection.insertOne(object);
	    LOGGER.debug("Insert took " + (System.currentTimeMillis() - start) + " ms.");
	} catch (MongoCommandException e) {
	    throw new SiteWhereException("Error during MongoDB insert.", e);
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Connection to MongoDB lost.", e);
	} catch (DuplicateKeyException e) {
	    throw new ResourceExistsException(ifDuplicate);
	}
    }

    /**
     * Insert an event, taking into account whether the device management
     * implementation in configured for bulk operations.
     * 
     * @param collection
     * @param object
     * @param bulk
     * @param buffer
     * @throws SiteWhereException
     */
    public static void insertEvent(MongoCollection<Document> collection, Document object, boolean bulk,
	    IDeviceEventBuffer buffer) throws SiteWhereException {
	try {
	    if (bulk) {
		buffer.add(object);
	    } else {
		collection.insertOne(object);
	    }
	} catch (MongoCommandException e) {
	    throw new SiteWhereException("Error during MongoDB insert.", e);
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Connection to MongoDB lost.", e);
	}
    }

    /**
     * Common handler for updating existing objects. Assures that errors are
     * handled in a consistent way.
     * 
     * @param collection
     * @param object
     * @throws SiteWhereException
     */
    public static void update(MongoCollection<Document> collection, Document query, Document object)
	    throws SiteWhereException {
	try {
	    long start = System.currentTimeMillis();
	    collection.updateOne(query, new Document("$set", object));
	    LOGGER.debug("Update took " + (System.currentTimeMillis() - start) + " ms.");
	} catch (MongoCommandException e) {
	    throw new SiteWhereException("Error during MongoDB update.", e);
	}
    }

    /**
     * Common handler for deleting objects. Assures that errors are handled in a
     * consistent way.
     * 
     * @param collection
     * @param object
     * @return
     * @throws SiteWhereException
     */
    public static DeleteResult delete(MongoCollection<Document> collection, Document object) throws SiteWhereException {
	try {
	    long start = System.currentTimeMillis();
	    DeleteResult result = collection.deleteOne(object);
	    LOGGER.debug("Delete took " + (System.currentTimeMillis() - start) + " ms.");
	    return result;
	} catch (MongoCommandException e) {
	    throw new SiteWhereException("Error during MongoDB delete.", e);
	}
    }

    /**
     * Perform a get using the default lookup.
     * 
     * @param id
     * @param api
     * @param collection
     * @return
     * @throws SiteWhereException
     */
    public static <T> T get(String id, Class<T> api, MongoCollection<Document> collection) throws SiteWhereException {
	return get(id, api, collection, LOOKUP);
    }

    /**
     * Get a single entity by unique id.
     * 
     * @param id
     * @param api
     * @param collection
     * @param lookup
     * @return
     * @throws SiteWhereException
     */
    public static <T> T get(String id, Class<T> api, MongoCollection<Document> collection, IMongoConverterLookup lookup)
	    throws SiteWhereException {
	try {
	    Document searchById = new Document("_id", new ObjectId(id));
	    long start = System.currentTimeMillis();
	    FindIterable<Document> found = collection.find(searchById);
	    LOGGER.debug("Get took " + (System.currentTimeMillis() - start) + " ms.");
	    if (found != null) {
		MongoConverter<T> converter = lookup.getConverterFor(api);
		return converter.convert(found.first());
	    }
	    return null;
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Connection to MongoDB lost.", e);
	}
    }

    /**
     * Search using the default lookup.
     * 
     * @param api
     * @param collection
     * @param query
     * @param sort
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static <T> SearchResults<T> search(Class<T> api, MongoCollection<Document> collection, Document query,
	    Document sort, ISearchCriteria criteria) throws SiteWhereException {
	return search(api, collection, query, sort, criteria, LOOKUP);
    }

    /**
     * Search the given collection using the provided query and sort. Return the
     * paged seaerch results.
     * 
     * @param api
     * @param collection
     * @param query
     * @param sort
     * @param criteria
     * @param lookup
     * @return
     * @throws SiteWhereException
     */
    public static <T> SearchResults<T> search(Class<T> api, MongoCollection<Document> collection, Document query,
	    Document sort, ISearchCriteria criteria, IMongoConverterLookup lookup) throws SiteWhereException {
	try {
	    FindIterable<Document> found;
	    long start = System.currentTimeMillis();
	    if (criteria.getPageSize() == 0) {
		found = collection.find(query).sort(sort);
	    } else {
		int offset = Math.max(0, criteria.getPageNumber() - 1) * criteria.getPageSize();
		found = collection.find(query).skip(offset).limit(criteria.getPageSize()).sort(sort);
	    }
	    MongoCursor<Document> cursor = found.iterator();
	    LOGGER.debug("Search took " + (System.currentTimeMillis() - start) + " ms.");

	    List<T> matches = new ArrayList<T>();
	    SearchResults<T> results = new SearchResults<T>(matches);
	    MongoConverter<T> converter = lookup.getConverterFor(api);
	    try {
		results.setNumResults(collection.count(query));
		while (cursor.hasNext()) {
		    Document match = cursor.next();
		    matches.add(converter.convert(match));
		}
	    } finally {
		cursor.close();
	    }
	    return results;
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Connection to MongoDB lost.", e);
	}
    }

    /**
     * Search using the default lookup.
     * 
     * @param api
     * @param collection
     * @param query
     * @param sort
     * @return
     * @throws SiteWhereException
     */
    public static <T> SearchResults<T> search(Class<T> api, MongoCollection<Document> collection, Document query,
	    Document sort) throws SiteWhereException {
	return search(api, collection, query, sort, LOOKUP);
    }

    /**
     * Search the given collection using the provided query and sort.
     * 
     * @param api
     * @param collection
     * @param query
     * @param sort
     * @param lookup
     * @return
     * @throws SiteWhereException
     */
    public static <T> SearchResults<T> search(Class<T> api, MongoCollection<Document> collection, Document query,
	    Document sort, IMongoConverterLookup lookup) throws SiteWhereException {
	try {
	    long start = System.currentTimeMillis();
	    FindIterable<Document> found = collection.find(query).sort(sort);
	    MongoCursor<Document> cursor = found.iterator();
	    LOGGER.debug("Search took " + (System.currentTimeMillis() - start) + " ms.");

	    List<T> matches = new ArrayList<T>();
	    SearchResults<T> results = new SearchResults<T>(matches);
	    MongoConverter<T> converter = lookup.getConverterFor(api);
	    try {
		results.setNumResults(collection.count(query));
		while (cursor.hasNext()) {
		    Document match = cursor.next();
		    matches.add(converter.convert(match));
		}
	    } finally {
		cursor.close();
	    }
	    return results;
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Connection to MongoDB lost.", e);
	}
    }

    /**
     * List using the default lookup.
     * 
     * @param api
     * @param collection
     * @param query
     * @param sort
     * @return
     * @throws SiteWhereException
     */
    public static <T> List<T> list(Class<T> api, MongoCollection<Document> collection, Document query, Document sort)
	    throws SiteWhereException {
	return list(api, collection, query, sort, LOOKUP);
    }

    /**
     * List all items in the collection that match the qiven query.
     * 
     * @param api
     * @param collection
     * @param query
     * @param sort
     * @param lookup
     * @return
     * @throws SiteWhereException
     */
    public static <T> List<T> list(Class<T> api, MongoCollection<Document> collection, Document query, Document sort,
	    IMongoConverterLookup lookup) throws SiteWhereException {
	try {
	    long start = System.currentTimeMillis();
	    FindIterable<Document> found = collection.find(query);
	    MongoCursor<Document> cursor = found.iterator();
	    LOGGER.debug("List took " + (System.currentTimeMillis() - start) + " ms.");

	    List<T> matches = new ArrayList<T>();
	    MongoConverter<T> converter = lookup.getConverterFor(api);
	    try {
		while (cursor.hasNext()) {
		    Document match = cursor.next();
		    matches.add(converter.convert(match));
		}
	    } finally {
		cursor.close();
	    }
	    return matches;
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Connection to MongoDB lost.", e);
	}
    }

    /**
     * Appends filter criteria onto exiting query based on the given date range.
     * 
     * @param query
     * @param criteria
     */
    public static void addDateSearchCriteria(Document query, String dateField, IDateRangeSearchCriteria criteria) {
	if ((criteria.getStartDate() == null) && (criteria.getEndDate() == null)) {
	    return;
	}
	Document dateClause = new Document();
	if (criteria.getStartDate() != null) {
	    dateClause.append("$gte", criteria.getStartDate());
	}
	if (criteria.getEndDate() != null) {
	    dateClause.append("$lte", criteria.getEndDate());
	}
	query.put(dateField, dateClause);
    }

    /**
     * Given a {@link Document} that contains event information, unmarhal it to
     * the correct type.
     * 
     * @param found
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceEvent unmarshalEvent(Document found) throws SiteWhereException {
	String type = (String) found.get(MongoDeviceEvent.PROP_EVENT_TYPE);
	if (type == null) {
	    throw new SiteWhereException("Event matched but did not contain event type field.");
	}
	DeviceEventType eventType = DeviceEventType.valueOf(type);
	if (eventType == null) {
	    throw new SiteWhereException("Event type not recognized: " + type);
	}

	switch (eventType) {
	case Measurements: {
	    return MongoDeviceMeasurements.fromDocument(found, false);
	}
	case Measurement: {
	    return MongoDeviceMeasurement.fromDocument(found, false);
	}
	case Location: {
	    return MongoDeviceLocation.fromDocument(found, false);
	}
	case Alert: {
	    return MongoDeviceAlert.fromDocument(found, false);
	}
	case StreamData: {
	    return MongoDeviceStreamData.fromDocument(found, false);
	}
	case CommandInvocation: {
	    return MongoDeviceCommandInvocation.fromDocument(found);
	}
	case CommandResponse: {
	    return MongoDeviceCommandResponse.fromDocument(found);
	}
	case StateChange: {
	    return MongoDeviceStateChange.fromDocument(found);
	}
	default: {
	    throw new SiteWhereException("Event type not handled: " + type);
	}
	}
    }
}