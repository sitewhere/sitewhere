/*
 * HBaseGrantedAuthority.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.user;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.common.MarshalUtils;
import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IGrantedAuthoritySearchCriteria;
import com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest;

/**
 * HBase specifics for dealing with SiteWhere granted authorities.
 * 
 * @author Derek
 */
public class HBaseGrantedAuthority {

	/**
	 * Create a new granted authority.
	 * 
	 * @param hbase
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static GrantedAuthority createGrantedAuthority(ISiteWhereHBaseClient hbase,
			IGrantedAuthorityCreateRequest request) throws SiteWhereException {
		GrantedAuthority existing = getGrantedAuthorityByName(hbase, request.getAuthority());
		if (existing != null) {
			throw new SiteWhereSystemException(ErrorCode.DuplicateAuthority, ErrorLevel.ERROR,
					HttpServletResponse.SC_CONFLICT);
		}

		// Create the new granted authority and store it.
		GrantedAuthority auth = SiteWherePersistence.grantedAuthorityCreateLogic(request);
		byte[] primary = getGrantedAuthorityRowKey(request.getAuthority());
		byte[] json = MarshalUtils.marshalJson(auth);

		HTableInterface users = null;
		try {
			users = hbase.getTableInterface(ISiteWhereHBase.USERS_TABLE_NAME);
			Put put = new Put(primary);
			put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT, json);
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
	 * @param hbase
	 * @param name
	 * @return
	 * @throws SiteWhereException
	 */
	public static GrantedAuthority getGrantedAuthorityByName(ISiteWhereHBaseClient hbase, String name)
			throws SiteWhereException {
		byte[] rowkey = getGrantedAuthorityRowKey(name);

		HTableInterface users = null;
		try {
			users = hbase.getTableInterface(ISiteWhereHBase.USERS_TABLE_NAME);
			Get get = new Get(rowkey);
			get.addColumn(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT);
			Result result = users.get(get);
			if (result.size() == 0) {
				return null;
			}
			if (result.size() > 1) {
				throw new SiteWhereException("Expected one JSON entry for granted authority and found: "
						+ result.size());
			}
			return MarshalUtils.unmarshalJson(result.value(), GrantedAuthority.class);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to load granted authority by name.", e);
		} finally {
			HBaseUtils.closeCleanly(users);
		}
	}

	/**
	 * List granted authorities that match the given criteria.
	 * 
	 * @param hbase
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static List<IGrantedAuthority> listGrantedAuthorities(ISiteWhereHBaseClient hbase,
			IGrantedAuthoritySearchCriteria criteria) throws SiteWhereException {
		HTableInterface users = null;
		ResultScanner scanner = null;
		try {
			users = hbase.getTableInterface(ISiteWhereHBase.USERS_TABLE_NAME);
			Scan scan = new Scan();
			scan.setStartRow(new byte[] { UserRecordType.GrantedAuthority.getType() });
			scan.setStopRow(new byte[] { UserRecordType.End.getType() });
			scanner = users.getScanner(scan);

			ArrayList<IGrantedAuthority> matches = new ArrayList<IGrantedAuthority>();
			for (Result result : scanner) {
				boolean shouldAdd = true;
				Map<byte[], byte[]> row = result.getFamilyMap(ISiteWhereHBase.FAMILY_ID);
				byte[] json = null;
				for (byte[] qualifier : row.keySet()) {
					if (Bytes.equals(ISiteWhereHBase.JSON_CONTENT, qualifier)) {
						json = row.get(qualifier);
					}
				}
				if ((shouldAdd) && (json != null)) {
					matches.add(MarshalUtils.unmarshalJson(json, GrantedAuthority.class));
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
	 * @param username
	 * @return
	 */
	public static byte[] getGrantedAuthorityRowKey(String name) {
		byte[] gaBytes = Bytes.toBytes(name);
		ByteBuffer buffer = ByteBuffer.allocate(1 + gaBytes.length);
		buffer.put(UserRecordType.GrantedAuthority.getType());
		buffer.put(gaBytes);
		return buffer.array();
	}
}