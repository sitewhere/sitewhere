/*
 * HBaseUser.java 
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
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.hbase.client.Delete;
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
import com.sitewhere.rest.model.user.GrantedAuthoritySearchCriteria;
import com.sitewhere.rest.model.user.User;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.IUserSearchCriteria;
import com.sitewhere.spi.user.request.IUserCreateRequest;

/**
 * HBase specifics for dealing with SiteWhere users.
 * 
 * @author Derek
 */
public class HBaseUser {

	/**
	 * Create a new device.
	 * 
	 * @param hbase
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static User createUser(ISiteWhereHBaseClient hbase, IUserCreateRequest request)
			throws SiteWhereException {
		User existing = getUserByUsername(hbase, request.getUsername());
		if (existing != null) {
			throw new SiteWhereSystemException(ErrorCode.DuplicateUser, ErrorLevel.ERROR,
					HttpServletResponse.SC_CONFLICT);
		}

		// Create the new user and store it.
		User user = SiteWherePersistence.userCreateLogic(request);
		byte[] primary = getUserRowKey(request.getUsername());
		byte[] json = MarshalUtils.marshalJson(user);

		HTableInterface users = null;
		try {
			users = hbase.getTableInterface(ISiteWhereHBase.USERS_TABLE_NAME);
			Put put = new Put(primary);
			put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT, json);
			users.put(put);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to set JSON for user.", e);
		} finally {
			HBaseUtils.closeCleanly(users);
		}

		return user;
	}

	/**
	 * Update an existing user.
	 * 
	 * @param hbase
	 * @param username
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static User updateUser(ISiteWhereHBaseClient hbase, String username, IUserCreateRequest request)
			throws SiteWhereException {
		User updated = getUserByUsername(hbase, username);
		if (updated == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidUsername, ErrorLevel.ERROR);
		}
		SiteWherePersistence.userUpdateLogic(request, updated);

		byte[] primary = getUserRowKey(username);
		byte[] json = MarshalUtils.marshalJson(updated);

		HTableInterface users = null;
		try {
			users = hbase.getTableInterface(ISiteWhereHBase.USERS_TABLE_NAME);
			Put put = new Put(primary);
			put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT, json);
			users.put(put);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to set JSON for user.", e);
		} finally {
			HBaseUtils.closeCleanly(users);
		}
		return updated;
	}

	/**
	 * Delete an existing user.
	 * 
	 * @param hbase
	 * @param username
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	public static User deleteUser(ISiteWhereHBaseClient hbase, String username, boolean force)
			throws SiteWhereException {
		User existing = getUserByUsername(hbase, username);
		if (existing == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidUsername, ErrorLevel.ERROR);
		}
		existing.setDeleted(true);
		byte[] primary = getUserRowKey(username);
		if (force) {
			HTableInterface users = null;
			try {
				users = hbase.getTableInterface(ISiteWhereHBase.USERS_TABLE_NAME);
				Delete delete = new Delete(primary);
				users.delete(delete);
			} catch (IOException e) {
				throw new SiteWhereException("Unable to delete user.", e);
			} finally {
				HBaseUtils.closeCleanly(users);
			}
		} else {
			byte[] marker = { (byte) 0x01 };
			SiteWherePersistence.setUpdatedEntityMetadata(existing);
			byte[] updated = MarshalUtils.marshalJson(existing);
			HTableInterface users = null;
			try {
				users = hbase.getTableInterface(ISiteWhereHBase.USERS_TABLE_NAME);
				Put put = new Put(primary);
				put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT, updated);
				put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.DELETED, marker);
				users.put(put);
			} catch (IOException e) {
				throw new SiteWhereException("Unable to set deleted flag for user.", e);
			} finally {
				HBaseUtils.closeCleanly(users);
			}
		}
		return existing;
	}

	/**
	 * Get a user by unique username. Returns null if not found.
	 * 
	 * @param hbase
	 * @param username
	 * @return
	 * @throws SiteWhereException
	 */
	public static User getUserByUsername(ISiteWhereHBaseClient hbase, String username)
			throws SiteWhereException {
		byte[] rowkey = getUserRowKey(username);

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
				throw new SiteWhereException("Expected one JSON entry for site and found: " + result.size());
			}
			return MarshalUtils.unmarshalJson(result.value(), User.class);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to load user by username.", e);
		} finally {
			HBaseUtils.closeCleanly(users);
		}
	}

	/**
	 * Authenticate a username password combination.
	 * 
	 * @param hbase
	 * @param username
	 * @param password
	 * @return
	 * @throws SiteWhereException
	 */
	public static User authenticate(ISiteWhereHBaseClient hbase, String username, String password)
			throws SiteWhereException {
		if (password == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidPassword, ErrorLevel.ERROR,
					HttpServletResponse.SC_BAD_REQUEST);
		}
		User existing = getUserByUsername(hbase, username);
		if (existing == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidUsername, ErrorLevel.ERROR,
					HttpServletResponse.SC_UNAUTHORIZED);
		}
		String inPassword = SiteWherePersistence.encodePassoword(password);
		if (!existing.getHashedPassword().equals(inPassword)) {
			throw new SiteWhereSystemException(ErrorCode.InvalidPassword, ErrorLevel.ERROR,
					HttpServletResponse.SC_UNAUTHORIZED);
		}

		// Update last login date.
		existing.setLastLogin(new Date());
		byte[] primary = getUserRowKey(username);
		byte[] json = MarshalUtils.marshalJson(existing);

		HTableInterface users = null;
		try {
			users = hbase.getTableInterface(ISiteWhereHBase.USERS_TABLE_NAME);
			Put put = new Put(primary);
			put.add(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.JSON_CONTENT, json);
			users.put(put);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to set deleted flag for user.", e);
		} finally {
			HBaseUtils.closeCleanly(users);
		}
		return existing;
	}

	/**
	 * List users that match certain criteria.
	 * 
	 * @param hbase
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static List<IUser> listUsers(ISiteWhereHBaseClient hbase, IUserSearchCriteria criteria)
			throws SiteWhereException {

		HTableInterface users = null;
		ResultScanner scanner = null;
		try {
			users = hbase.getTableInterface(ISiteWhereHBase.USERS_TABLE_NAME);
			Scan scan = new Scan();
			scan.setStartRow(new byte[] { UserRecordType.User.getType() });
			scan.setStopRow(new byte[] { UserRecordType.GrantedAuthority.getType() });
			scanner = users.getScanner(scan);

			ArrayList<IUser> matches = new ArrayList<IUser>();
			for (Result result : scanner) {
				boolean shouldAdd = true;
				Map<byte[], byte[]> row = result.getFamilyMap(ISiteWhereHBase.FAMILY_ID);
				byte[] json = null;
				for (byte[] qualifier : row.keySet()) {
					if ((Bytes.equals(ISiteWhereHBase.DELETED, qualifier)) && (!criteria.isIncludeDeleted())) {
						shouldAdd = false;
					}
					if (Bytes.equals(ISiteWhereHBase.JSON_CONTENT, qualifier)) {
						json = row.get(qualifier);
					}
				}
				if ((shouldAdd) && (json != null)) {
					matches.add(MarshalUtils.unmarshalJson(json, User.class));
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
	 * Get the list of granted authorities for a user.
	 * 
	 * @param hbase
	 * @param username
	 * @return
	 * @throws SiteWhereException
	 */
	public static List<IGrantedAuthority> getGrantedAuthorities(ISiteWhereHBaseClient hbase, String username)
			throws SiteWhereException {
		IUser user = getUserByUsername(hbase, username);
		List<String> userAuths = user.getAuthorities();
		List<IGrantedAuthority> all =
				HBaseGrantedAuthority.listGrantedAuthorities(hbase, new GrantedAuthoritySearchCriteria());
		List<IGrantedAuthority> matched = new ArrayList<IGrantedAuthority>();
		for (IGrantedAuthority auth : all) {
			if (userAuths.contains(auth.getAuthority())) {
				matched.add(auth);
			}
		}
		return matched;
	}

	/**
	 * Get row key for a user.
	 * 
	 * @param username
	 * @return
	 */
	public static byte[] getUserRowKey(String username) {
		byte[] userBytes = Bytes.toBytes(username);
		ByteBuffer buffer = ByteBuffer.allocate(1 + userBytes.length);
		buffer.put(UserRecordType.User.getType());
		buffer.put(userBytes);
		return buffer.array();
	}
}
