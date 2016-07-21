package com.sitewhere.hbase.tenant;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.hbase.device.DeleteRecord;
import com.sitewhere.hbase.encoder.PayloadMarshalerResolver;
import com.sitewhere.rest.model.search.Pager;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.tenant.TenantGroupElement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.tenant.ITenantGroupElement;
import com.sitewhere.spi.tenant.request.ITenantGroupElementCreateRequest;

public class HBaseTenantGroupElement {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(HBaseTenantGroupElement.class);

	/** Length of element index info (subset of 8 byte long) */
	public static final int INDEX_LENGTH = 4;

	/** Column qualifier for element identifier (tenant id) */
	public static final byte[] ELEMENT_IDENTIFIER = Bytes.toBytes("i");

	/**
	 * Create one or more tenant group elements.
	 * 
	 * @param context
	 * @param groupToken
	 * @param requests
	 * @return
	 * @throws SiteWhereException
	 */
	public static List<ITenantGroupElement> createTenantGroupElements(IHBaseContext context, String groupId,
			List<ITenantGroupElementCreateRequest> requests) throws SiteWhereException {
		byte[] groupKey = HBaseTenantGroup.KEY_BUILDER.buildPrimaryKey(context, groupId);
		List<ITenantGroupElement> results = new ArrayList<ITenantGroupElement>();
		for (ITenantGroupElementCreateRequest request : requests) {
			Long eid = HBaseTenantGroup.allocateNextElementId(context, groupKey);
			results.add(HBaseTenantGroupElement.createTenantGroupElement(context, groupId, eid, request));
		}
		return results;
	}

	/**
	 * Create a new tenant group element.
	 * 
	 * @param context
	 * @param groupToken
	 * @param index
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public static ITenantGroupElement createTenantGroupElement(IHBaseContext context, String groupToken, Long index,
			ITenantGroupElementCreateRequest request) throws SiteWhereException {
		byte[] elementKey = getElementRowKey(context, groupToken, index);

		TenantGroupElement element = SiteWherePersistence.tenantGroupElementCreateLogic(groupToken, request);

		byte[] payload = context.getPayloadMarshaler().encodeTenantGroupElement(element);

		HTableInterface users = null;
		try {
			users = getUserTableInterface(context);
			Put put = new Put(elementKey);
			HBaseUtils.addPayloadFields(context.getPayloadMarshaler().getEncoding(), put, payload);
			put.add(ISiteWhereHBase.FAMILY_ID, ELEMENT_IDENTIFIER, request.getTenantId().getBytes());
			users.put(put);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to create tenant group element.", e);
		} finally {
			HBaseUtils.closeCleanly(users);
		}

		return element;
	}

	/**
	 * Remove the given tenant group elements.
	 * 
	 * @param context
	 * @param groupId
	 * @param elements
	 * @return
	 * @throws SiteWhereException
	 */
	public static List<ITenantGroupElement> removeTenantGroupElements(IHBaseContext context, String groupId,
			List<ITenantGroupElementCreateRequest> elements) throws SiteWhereException {
		List<byte[]> tenantIds = new ArrayList<byte[]>();
		for (ITenantGroupElementCreateRequest request : elements) {
			tenantIds.add(request.getTenantId().getBytes());
		}
		return deleteElements(context, groupId, tenantIds);
	}

	/**
	 * Handles logic for finding and deleting tenant group elements.
	 * 
	 * @param context
	 * @param groupId
	 * @param tenantIds
	 * @return
	 * @throws SiteWhereException
	 */
	protected static List<ITenantGroupElement> deleteElements(IHBaseContext context, String groupId,
			List<byte[]> tenantIds) throws SiteWhereException {
		HTableInterface table = null;
		ResultScanner scanner = null;
		try {
			table = getUserTableInterface(context);
			byte[] primary = HBaseTenantGroup.KEY_BUILDER.buildSubkey(context, groupId,
					TenantGroupSubtype.TenantGroupElement.getType());
			byte[] after = HBaseTenantGroup.KEY_BUILDER.buildSubkey(context, groupId,
					(byte) (TenantGroupSubtype.TenantGroupElement.getType() + 1));
			Scan scan = new Scan();
			scan.setStartRow(primary);
			scan.setStopRow(after);
			scanner = table.getScanner(scan);

			List<DeleteRecord> matches = new ArrayList<DeleteRecord>();
			for (Result result : scanner) {
				byte[] row = result.getRow();

				boolean shouldAdd = false;
				byte[] type = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
				byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
				byte[] tenantId = result.getValue(ISiteWhereHBase.FAMILY_ID, ELEMENT_IDENTIFIER);
				if (tenantId != null) {
					for (byte[] toDelete : tenantIds) {
						if (Bytes.equals(toDelete, tenantId)) {
							shouldAdd = true;
							break;
						}
					}
				}
				if ((shouldAdd) && (type != null) && (payload != null)) {
					matches.add(new DeleteRecord(row, type, payload));
				}
			}
			List<ITenantGroupElement> results = new ArrayList<ITenantGroupElement>();
			for (DeleteRecord dr : matches) {
				try {
					Delete delete = new Delete(dr.getRowkey());
					table.delete(delete);
					results.add(PayloadMarshalerResolver.getInstance().getMarshaler(dr.getPayloadType())
							.decodeTenantGroupElement(dr.getPayload()));
				} catch (IOException e) {
					LOGGER.warn("Tenant group element delete failed for key: " + dr.getRowkey());
				}
			}
			return results;
		} catch (IOException e) {
			throw new SiteWhereException("Error scanning tenant group element rows.", e);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
			HBaseUtils.closeCleanly(table);
		}
	}

	/**
	 * Deletes all elements for a tenant group. TODO: There is probably a much
	 * more efficient method of deleting the records than calling a delete for
	 * each.
	 * 
	 * @param context
	 * @param groupId
	 * @throws SiteWhereException
	 */
	public static void deleteElements(IHBaseContext context, String groupId) throws SiteWhereException {
		HTableInterface table = null;
		ResultScanner scanner = null;
		try {
			table = getUserTableInterface(context);
			byte[] primary = HBaseTenantGroup.KEY_BUILDER.buildSubkey(context, groupId,
					TenantGroupSubtype.TenantGroupElement.getType());
			byte[] after = HBaseTenantGroup.KEY_BUILDER.buildSubkey(context, groupId,
					(byte) (TenantGroupSubtype.TenantGroupElement.getType() + 1));
			Scan scan = new Scan();
			scan.setStartRow(primary);
			scan.setStopRow(after);
			scanner = table.getScanner(scan);

			List<DeleteRecord> matches = new ArrayList<DeleteRecord>();
			for (Result result : scanner) {
				byte[] row = result.getRow();
				byte[] type = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
				byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
				if ((type != null) && (payload != null)) {
					matches.add(new DeleteRecord(row, type, payload));
				}
			}
			for (DeleteRecord dr : matches) {
				try {
					Delete delete = new Delete(dr.getRowkey());
					table.delete(delete);
				} catch (IOException e) {
					LOGGER.warn("Tenant group element delete failed for key: " + dr.getRowkey());
				}
			}
		} catch (IOException e) {
			throw new SiteWhereException("Error scanning tenant group element rows.", e);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
			HBaseUtils.closeCleanly(table);
		}
	}

	/**
	 * Get paged results for listing tenant group elements. TODO: This is not
	 * optimized! Getting the correct record count requires a full scan of all
	 * elements in the group.
	 * 
	 * @param context
	 * @param groupId
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public static SearchResults<ITenantGroupElement> listTenantGroupElements(IHBaseContext context, String groupId,
			ISearchCriteria criteria) throws SiteWhereException {
		HTableInterface table = null;
		ResultScanner scanner = null;
		try {
			table = getUserTableInterface(context);
			byte[] primary = HBaseTenantGroup.KEY_BUILDER.buildSubkey(context, groupId,
					TenantGroupSubtype.TenantGroupElement.getType());
			byte[] after = HBaseTenantGroup.KEY_BUILDER.buildSubkey(context, groupId,
					(byte) (TenantGroupSubtype.TenantGroupElement.getType() + 1));
			Scan scan = new Scan();
			scan.setStartRow(primary);
			scan.setStopRow(after);
			scanner = table.getScanner(scan);

			Pager<ITenantGroupElement> pager = new Pager<ITenantGroupElement>(criteria);
			for (Result result : scanner) {
				byte[] type = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD_TYPE);
				byte[] payload = result.getValue(ISiteWhereHBase.FAMILY_ID, ISiteWhereHBase.PAYLOAD);
				if ((type != null) && (payload != null)) {
					pager.process(PayloadMarshalerResolver.getInstance().getMarshaler(type)
							.decodeTenantGroupElement(payload));
				}
			}
			return new SearchResults<ITenantGroupElement>(pager.getResults());
		} catch (IOException e) {
			throw new SiteWhereException("Error scanning tenant group element rows.", e);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
			HBaseUtils.closeCleanly(table);
		}
	}

	/**
	 * Get key for a group element.
	 * 
	 * @param context
	 * @param groupId
	 * @param elementId
	 * @return
	 * @throws SiteWhereException
	 */
	public static byte[] getElementRowKey(IHBaseContext context, String groupId, Long elementId)
			throws SiteWhereException {
		byte[] baserow = HBaseTenantGroup.KEY_BUILDER.buildSubkey(context, groupId,
				TenantGroupSubtype.TenantGroupElement.getType());
		byte[] eidBytes = getTruncatedIdentifier(elementId);
		ByteBuffer buffer = ByteBuffer.allocate(baserow.length + eidBytes.length);
		buffer.put(baserow);
		buffer.put(eidBytes);
		return buffer.array();
	}

	/**
	 * Truncate element id value to expected length. This will be a subset of
	 * the full 8-bit long value.
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] getTruncatedIdentifier(Long value) {
		byte[] bytes = Bytes.toBytes(value);
		byte[] result = new byte[INDEX_LENGTH];
		System.arraycopy(bytes, bytes.length - INDEX_LENGTH, result, 0, INDEX_LENGTH);
		return result;
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