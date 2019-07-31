package com.sitewhere.rdb.multitenancy;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.util.Map;

/**
 * The provider with dynamic configuration of tenants
 *
 * Simeon Chen
 */
public class DvdRentalDataSourceMultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

	private static final long serialVersionUID = 1L;

	@Autowired
	private Map<String, DataSource> dataSourcesDvdRental;

	@Override
	protected DataSource selectAnyDataSource() {
		DataSource selected = dataSourcesDvdRental.get(DvdRentalTenantContext.getTenantId());
		return selected;
	}

	@Override
	protected DataSource selectDataSource(String tenantIdentifier) {
		return this.dataSourcesDvdRental.get(tenantIdentifier);
	}
}