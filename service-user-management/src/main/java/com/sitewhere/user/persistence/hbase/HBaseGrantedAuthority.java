/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.user.persistence.hbase;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.encoder.PayloadMarshalerResolver;
import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IGrantedAuthoritySearchCriteria;
import com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest;
import com.sitewhere.user.persistence.UserManagementPersistence;

/**
 * HBase specifics for dealing with SiteWhere granted authorities.
 * 
 * @author Derek
 */
public class HBaseGrantedAuthority {

    /**
     * Create a new granted authority.
     * 
     * @param context
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static GrantedAuthority createGrantedAuthority(IHBaseContext context, IGrantedAuthorityCreateRequest request)
	    throws SiteWhereException {
	GrantedAuthority existing = getGrantedAuthorityByName(context, request.getAuthority());
	if (existing != null) {
	    throw new SiteWhereSystemException(ErrorCode.DuplicateAuthority, ErrorLevel.ERROR);
	}

	// Create the new granted authority and store it.
	GrantedAuthority auth = UserManagementPersistence.grantedAuthorityCreateLogic(request);
	byte[] primary = getGrantedAuthorityRowKey(request.getAuthority());
	byte[] payload = context.getPayloadMarshaler().encodeGrantedAuthority(auth);

	Table users = null;
	try {
	    users = getUsersTableInterface(context);
	    Put put = new Put(primary);
	    HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
	    users.put(put);
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to create granted authority.", e);
	} finally {
	    HBaseUtils.closeCleanly(users);
	}

	return auth;
    }

    /**
     * Get a granted authority by unique name.
     * 
     * @param context
     * @param name
     * @return
     * @throws SiteWhereException
     */
    public static GrantedAuthority getGrantedAuthorityByName(IHBaseContext context, String name)
	    throws SiteWhereException {
	byte[] rowkey = getGrantedAuthorityRowKey(name);

	Table users = null;
	try {
	    users = getUsersTableInterface(context);
	    Get get = new Get(rowkey);
	    HBaseUtils.addPayloadFields(get);
	    Result result = users.get(get);

	    byte[] type = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
	    byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
	    if ((type == null) || (payload == null)) {
		return null;
	    }

	    return PayloadMarshalerResolver.getInstance().getMarshaler(type).decodeGrantedAuthority(payload);
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to load granted authority by name.", e);
	} finally {
	    HBaseUtils.closeCleanly(users);
	}
    }

    /**
     * List granted authorities that match the given criteria.
     * 
     * @param context
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static List<IGrantedAuthority> listGrantedAuthorities(IHBaseContext context,
	    IGrantedAuthoritySearchCriteria criteria) throws SiteWhereException {
	Table users = null;
	ResultScanner scanner = null;
	try {
	    users = getUsersTableInterface(context);
	    Scan scan = new Scan();
	    scan.setStartRow(new byte[] { UserRecordType.GrantedAuthority.getType() });
	    scan.setStopRow(new byte[] { (byte) (UserRecordType.GrantedAuthority.getType() + 1) });
	    scanner = users.getScanner(scan);

	    ArrayList<IGrantedAuthority> matches = new ArrayList<IGrantedAuthority>();
	    for (Result result : scanner) {
		boolean shouldAdd = true;
		Map<byte[], byte[]> row = result.getFamilyMap(ISiteWhereHBase.FAMILY_ID);

		byte[] payloadType = null;
		byte[] payload = null;
		for (byte[] qualifier : row.keySet()) {
		    if (Bytes.equals(ISiteWhereHBase.PAYLOAD_TYPE, qualifier)) {
			payloadType = row.get(qualifier);
		    }
		    if (Bytes.equals(ISiteWhereHBase.PAYLOAD, qualifier)) {
			payload = row.get(qualifier);
		    }
		}
		if ((shouldAdd) && (payloadType != null) && (payload != null)) {
		    matches.add(PayloadMarshalerResolver.getInstance().getMarshaler(payloadType)
			    .decodeGrantedAuthority(payload));
		}
	    }
	    return matches;
	} catch (IOException e) {
	    throw new SiteWhereException("Error scanning user rows.", e);
	} finally {
	    if (scanner != null) {
		scanner.close();
	    }
	    HBaseUtils.closeCleanly(users);
	}
    }

    /**
     * Get row key for a granted authority.
     * 
     * @param name
     * @return
     */
    public static byte[] getGrantedAuthorityRowKey(String name) {
	byte[] gaBytes = Bytes.toBytes(name);
	ByteBuffer buffer = ByteBuffer.allocate(1 + gaBytes.length);
	buffer.put(UserRecordType.GrantedAuthority.getType());
	buffer.put(gaBytes);
	return buffer.array();
    }

    /**
     * Get users table based on context.
     * 
     * @param context
     * @return
     * @throws SiteWhereException
     */
    protected static Table getUsersTableInterface(IHBaseContext context) throws SiteWhereException {
	return context.getClient().getTableInterface(ISiteWhereHBase.USERS_TABLE_NAME);
    }
}