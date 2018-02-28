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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoClientException;
import com.mongodb.MongoCommandException;
import com.mongodb.MongoTimeoutException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
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
    private static Log LOGGER = LogFactory.getLog(MongoPersistence.class);

    /**
     * Common handler for creating new objects. Assures that errors are handled in a
     * consistent way.
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
	} catch (MongoWriteException e) {
	    ErrorCategory category = e.getError().getCategory();
	    if (ErrorCategory.DUPLICATE_KEY == category) {
		throw new ResourceExistsException(ifDuplicate);
	    }
	    throw new SiteWhereException("Error during MongoDB insert.", e);
	} catch (MongoClientException e) {
	    throw handleClientException(e);
	}
    }

    /**
     * Common handler for updating existing objects. Assures that errors are handled
     * in a consistent way.
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
	} catch (MongoClientException e) {
	    throw handleClientException(e);
	}
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
	    LOGGER.debug("Executing search query " + query.toJson() + " with sort " + sort.toJson() + " on collection "
		    + collection.getNamespace());
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
	} catch (MongoClientException e) {
	    throw handleClientException(e);
	}
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
	} catch (MongoClientException e) {
	    throw handleClientException(e);
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
     * Common handler for MongoDB client exceptions.
     * 
     * @param e
     * @throws SiteWhereException
     */
    public static SiteWhereException handleClientException(MongoClientException e) {
	if (e instanceof MongoTimeoutException) {
	    return new SiteWhereException("Connection to MongoDB lost.", e);
	}
	return new SiteWhereException("Exception in MongoDB client.", e);
    }
}