package com.sitewhere.hbase.tenant;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.uid.UniqueIdCounterMap;
import com.sitewhere.hbase.uid.UniqueIdCounterMapRowKeyBuilder;
import com.sitewhere.hbase.user.UserRecordType;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.tenant.TenantGroup;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.common.IFilter;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.tenant.ITenantGroup;
import com.sitewhere.spi.tenant.request.ITenantGroupCreateRequest;

/**
 * HBase specifics for dealing with SiteWhere tenant groups.
 * 
 * @author Derek
 */
public class HBaseTenantGroup {

	/** Length of group identifier (subset of 8 byte long) */
	public static final int IDENTIFIER_LENGTH = 4;

	/** Column qualifier for group entry counter */
	public static final byte[] ENTRY_COUNTER = Bytes.toBytes("entryctr");

	/** Used to look up row keys from tokens */
	public static UniqueIdCounterMapRowKeyBuilder KEY_BUILDER = new UniqueIdCounterMapRowKeyBuilder() {

		@Override
		public UniqueIdCounterMap getMap(IHBaseContext context) {
			return context.getUserIdManager().getTenantGroupKeys();
		}

		@Override
		public byte getTypeIdentifier() {
			return UserRecordType.TenantGroup.getType();
		}

		@Override
		public byte getPrimaryIdentifier() {
			return TenantGroupSubtype.TenantGroup.getType();
		}

		@Override
		public int getKeyIdLength() {
			return 4;
		}

		@Override
		public void throwInvalidKey() throws SiteWhereException {
			throw new SiteWhereSystemException(ErrorCode.InvalidTenantGroupId, ErrorLevel.ERROR);
		}
	};

	/**
	 * Create a tenant group.
	 * 
	 * @param context
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static ITenantGroup createTenantGroup(IHBaseContext context, ITenantGroupCreateRequest request)
			throws SiteWhereException {
		String groupId = null;
		if (request.getId() != null) {
			groupId = KEY_BUILDER.getMap(context).useExistingId(request.getId());
		} else {
			groupId = KEY_BUILDER.getMap(context).createUniqueId();
		}

		// Use common logic so all backend implementations work the same.
		TenantGroup group = SiteWherePersistence.tenantGroupCreateLogic(request);

		Map<byte[], byte[]> qualifiers = new HashMap<byte[], byte[]>();
		byte[] zero = Bytes.toBytes((long) 0);
		qualifiers.put(ENTRY_COUNTER, zero);
		return HBaseUtils.createOrUpdate(context, context.getPayloadMarshaler(), ISiteWhereHBase.USERS_TABLE_NAME,
				group, groupId, KEY_BUILDER, qualifiers);
	}

	/**
	 * Update tenant group information.
	 * 
	 * @param context
	 * @param token
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static ITenantGroup updateTenantGroup(IHBaseContext context, String token, ITenantGroupCreateRequest request)
			throws SiteWhereException {
		TenantGroup updated = assertTenantGroup(context, token);
		SiteWherePersistence.tenantGroupUpdateLogic(request, updated);
		return HBaseUtils.put(context, context.getPayloadMarshaler(), ISiteWhereHBase.USERS_TABLE_NAME, updated, token,
				KEY_BUILDER);
	}

	/**
	 * Get a {@link TenantGroup} by unique token. *
	 * 
	 * @param context
	 * @param groupId
	 * @return
	 * @throws SiteWhereException
	 */
	public static TenantGroup getTenantGroupById(IHBaseContext context, String groupId) throws SiteWhereException {
		return HBaseUtils.get(context, ISiteWhereHBase.USERS_TABLE_NAME, groupId, KEY_BUILDER, TenantGroup.class);
	}

	/**
	 * Get paged {@link ITenantGroup} results based on the given search
	 * criteria.
	 * 
	 * @param context
	 * @param includeDeleted
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<ITenantGroup> listTenantGroups(IHBaseContext context, boolean includeDeleted,
			ISearchCriteria criteria) throws SiteWhereException {
		Comparator<TenantGroup> comparator = new Comparator<TenantGroup>() {

			public int compare(TenantGroup a, TenantGroup b) {
				return -1 * (a.getCreatedDate().compareTo(b.getCreatedDate()));
			}

		};
		IFilter<TenantGroup> filter = new IFilter<TenantGroup>() {

			public boolean isExcluded(TenantGroup item) {
				return false;
			}
		};
		return HBaseUtils.getFilteredList(context, ISiteWhereHBase.USERS_TABLE_NAME, KEY_BUILDER, includeDeleted,
				ITenantGroup.class, TenantGroup.class, filter, criteria, comparator);
	}

	/**
	 * Allocates the next available tenant group element id.
	 * 
	 * @param context
	 * @param primary
	 * @return
	 * @throws SiteWhereException
	 */
	public static Long allocateNextElementId(IHBaseContext context, byte[] primary) throws SiteWhereException {
		HTableInterface users = null;
		try {
			users = getUserTableInterface(context);
			Increment increment = new Increment(primary);
			increment.addColumn(ISiteWhereHBase.FAMILY_ID, ENTRY_COUNTER, 1);
			Result result = users.increment(increment);
			return Bytes.toLong(result.value());
		} catch (IOException e) {
			throw new SiteWhereException("Unable to allocate next group element id.", e);
		} finally {
			HBaseUtils.closeCleanly(users);
		}
	}

	/**
	 * Delete an existing device group.
	 * 
	 * @param context
	 * @param groupId
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	public static ITenantGroup deleteTenantGroup(IHBaseContext context, String groupId, boolean force)
			throws SiteWhereException {
		// If actually deleting group, delete all group elements.
		if (force) {
			HBaseTenantGroupElement.deleteElements(context, groupId);
		}
		return HBaseUtils.delete(context, context.getPayloadMarshaler(), ISiteWhereHBase.USERS_TABLE_NAME, groupId,
				force, KEY_BUILDER, TenantGroup.class);
	}

	/**
	 * Get a {@link TenantGroup} by unique id or throw an exception if token is
	 * not valid.
	 * 
	 * @param context
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	public static TenantGroup assertTenantGroup(IHBaseContext context, String groupId) throws SiteWhereException {
		TenantGroup existing = getTenantGroupById(context, groupId);
		if (existing == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidTenantGroupId, ErrorLevel.ERROR);
		}
		return existing;
	}

	/**
	 * Get the unique device identifier based on the long value associated with
	 * the device group UUID. This will be a subset of the full 8-bit long
	 * value.
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] getTruncatedIdentifier(Long value) {
		byte[] bytes = Bytes.toBytes(value);
		byte[] result = new byte[IDENTIFIER_LENGTH];
		System.arraycopy(bytes, bytes.length - IDENTIFIER_LENGTH, result, 0, IDENTIFIER_LENGTH);
		return result;
	}

	/**
	 * Get row key for a tenant group with the given internal id.
	 * 
	 * @param groupId
	 * @return
	 */
	public static byte[] getPrimaryRowKey(Long groupId) {
		ByteBuffer buffer = ByteBuffer.allocate(IDENTIFIER_LENGTH + 2);
		buffer.put(UserRecordType.TenantGroup.getType());
		buffer.put(getTruncatedIdentifier(groupId));
		buffer.put(TenantGroupSubtype.TenantGroup.getType());
		return buffer.array();
	}

	/**
	 * Get user table based on context.
	 * 
	 * @param context
	 * @return
	 * @throws SiteWhereException
	 */
	protected static HTableInterface getUserTableInterface(IHBaseContext context) throws SiteWhereException {
		return context.getClient().getTableInterface(context.getTenant(), ISiteWhereHBase.USERS_TABLE_NAME);
	}
}