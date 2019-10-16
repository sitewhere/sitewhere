/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rdb.multitenancy;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
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
		DataSource dataSource = this.dataSourcesDvdRental.get(tenantIdentifier);
		return dataSource;
	}

	@Override
	public Connection getConnection(String tenantIdentifier) throws SQLException {
		Connection connection = super.getConnection(tenantIdentifier);
		connection.setSchema(tenantIdentifier);
		return connection;
	}
}