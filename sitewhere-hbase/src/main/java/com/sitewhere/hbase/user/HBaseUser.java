/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
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
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.encoder.PayloadMarshalerResolver;
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
	 * @param context
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static User createUser(IHBaseContext context, IUserCreateRequest request) throws SiteWhereException {
		User existing = getUserByUsername(context, request.getUsername());
		if (existing != null) {
			throw new SiteWhereSystemException(ErrorCode.DuplicateUser, ErrorLevel.ERROR,
					HttpServletResponse.SC_CONFLICT);
		}

		// Create the new user and store it.
		User user = SiteWherePersistence.userCreateLogic(request);
		byte[] primary = getUserRowKey(request.getUsername());
		byte[] payload = context.getPayloadMarshaler().encodeUser(user);

		Table users = null;
		try {
			users = getUsersTableInterface(context);
			Put put = new Put(primary);
			HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
			users.put(put);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to set JSON for user.", e);
		} finally {
			HBaseUtils.closeCleanly(users);
		}

		return user;
	}

	/**
	 * Import a user (including hashed password).
	 * 
	 * @param context
	 * @param imported
	 * @param overwrite
	 * @return
	 * @throws SiteWhereException
	 */
	public static User importUser(IHBaseContext context, IUser imported, boolean overwrite) throws SiteWhereException {
		if (!overwrite) {
			User existing = getUserByUsername(context, imported.getUsername());
			if (existing != null) {
				throw new SiteWhereSystemException(ErrorCode.DuplicateUser, ErrorLevel.ERROR,
						HttpServletResponse.SC_CONFLICT);
			}
		}

		User user = User.copy(imported);
		byte[] primary = getUserRowKey(imported.getUsername());
		byte[] payload = context.getPayloadMarshaler().encodeUser(user);

		Table users = null;
		try {
			users = getUsersTableInterface(context);
			Put put = new Put(primary);
			HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
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
	 * @param context
	 * @param username
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static User updateUser(IHBaseContext context, String username, IUserCreateRequest request)
			throws SiteWhereException {
		User updated = getUserByUsername(context, username);
		if (updated == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidUsername, ErrorLevel.ERROR);
		}
		SiteWherePersistence.userUpdateLogic(request, updated);

		byte[] primary = getUserRowKey(username);
		byte[] payload = context.getPayloadMarshaler().encodeUser(updated);

		Table users = null;
		try {
			users = getUsersTableInterface(context);
			Put put = new Put(primary);
			HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
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
	 * @param context
	 * @param username
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	public static User deleteUser(IHBaseContext context, String username, boolean force) throws SiteWhereException {
		User existing = getUserByUsername(context, username);
		if (existing == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidUsername, ErrorLevel.ERROR);
		}
		existing.setDeleted(true);
		byte[] primary = getUserRowKey(username);
		if (force) {
			Table users = null;
			try {
				users = getUsersTableInterface(context);
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
			byte[] payload = context.getPayloadMarshaler().encodeUser(existing);
			Table users = null;
			try {
				users = getUsersTableInterface(context);
				Put put = new Put(primary);
				HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
				put.addColumn(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.DELETED, marker);
				users.put(put);
			} catch (IOException e) {
				throw new SiteWhereException("Unable to set deleted flag for user.", e);
			} finally {
				HBaseUtils.closeCleanly(users);
			}
		}
		SiteWherePersistence.userDeleteLogic(username);
		return existing;
	}

	/**
	 * Get a user by unique username. Returns null if not found.
	 * 
	 * @param context
	 * @param username
	 * @return
	 * @throws SiteWhereException
	 */
	public static User getUserByUsername(IHBaseContext context, String username) throws SiteWhereException {
		byte[] rowkey = getUserRowKey(username);

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

			return PayloadMarshalerResolver.getInstance().getMarshaler(type).decodeUser(payload);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to load user by username.", e);
		} finally {
			HBaseUtils.closeCleanly(users);
		}
	}

	/**
	 * Authenticate a username password combination.
	 * 
	 * @param context
	 * @param username
	 * @param password
	 * @return
	 * @throws SiteWhereException
	 */
	public static User authenticate(IHBaseContext context, String username, String password) throws SiteWhereException {
		if (password == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidPassword, ErrorLevel.ERROR,
					HttpServletResponse.SC_BAD_REQUEST);
		}
		User existing = getUserByUsername(context, username);
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
		byte[] payload = context.getPayloadMarshaler().encodeUser(existing);

		Table users = null;
		try {
			users = getUsersTableInterface(context);
			Put put = new Put(primary);
			HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
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
	 * @param context
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static List<IUser> listUsers(IHBaseContext context, IUserSearchCriteria criteria) throws SiteWhereException {

		Table users = null;
		ResultScanner scanner = null;
		try {
			users = getUsersTableInterface(context);
			Scan scan = new Scan();
			scan.setStartRow(new byte[] { UserRecordType.User.getType() });
			scan.setStopRow(new byte[] { UserRecordType.GrantedAuthority.getType() });
			scanner = users.getScanner(scan);

			ArrayList<IUser> matches = new ArrayList<IUser>();
			for (Result result : scanner) {
				boolean shouldAdd = true;
				Map<byte[], byte[]> row = result.getFamilyMap(ISiteWhereHBase.FAMILY_ID);

				byte[] payloadType = null;
				byte[] payload = null;
				for (byte[] qualifier : row.keySet()) {
					if ((Bytes.equals(ISiteWhereHBase.DELETED, qualifier)) && (!criteria.isIncludeDeleted())) {
						shouldAdd = false;
					}
					if (Bytes.equals(ISiteWhereHBase.PAYLOAD_TYPE, qualifier)) {
						payloadType = row.get(qualifier);
					}
					if (Bytes.equals(ISiteWhereHBase.PAYLOAD, qualifier)) {
						payload = row.get(qualifier);
					}
				}
				if ((shouldAdd) && (payloadType != null) && (payload != null)) {
					matches.add(PayloadMarshalerResolver.getInstance().getMarshaler(payloadType).decodeUser(payload));
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
	 * @param context
	 * @param username
	 * @return
	 * @throws SiteWhereException
	 */
	public static List<IGrantedAuthority> getGrantedAuthorities(IHBaseContext context, String username)
			throws SiteWhereException {
		IUser user = getUserByUsername(context, username);
		List<String> userAuths = user.getAuthorities();
		List<IGrantedAuthority> all = HBaseGrantedAuthority.listGrantedAuthorities(context,
				new GrantedAuthoritySearchCriteria());
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