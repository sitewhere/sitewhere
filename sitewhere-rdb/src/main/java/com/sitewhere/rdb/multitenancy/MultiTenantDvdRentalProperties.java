package com.sitewhere.rdb.multitenancy;

import java.util.List;
import java.util.Vector;

import com.sitewhere.configuration.instance.rdb.RDBConfiguration;

/**
 * Tenant's database properties
 *
 * Simeon Chen
 */
public class MultiTenantDvdRentalProperties {

	public static Vector<DataSourceProperties> DATA_SOURCES_PROPS = new Vector<>();

	public static void ADD_NEW_DATASOURCE(RDBConfiguration config, String tenantId) {
		// Delete old datasource if the tenantId exist
		for(DataSourceProperties ds : DATA_SOURCES_PROPS) {
			if(ds.getTenantId().equals(tenantId)) {
				DATA_SOURCES_PROPS.remove(ds);
				break;
			}
		}
		// Insert new datasource with tenantId
		DataSourceProperties ds = new DataSourceProperties();
		ds.setTenantId(tenantId);
		ds.setUrl(config.getUrl());
		ds.setUsername(config.getUsername());
		ds.setPassword(config.getPassword());
		ds.setDriverClassName(config.getDriver());
		DATA_SOURCES_PROPS.add(ds);
	}


	private List<DataSourceProperties> dataSourcesProps;

	public List<DataSourceProperties> getDataSources() {
		return this.dataSourcesProps;
	}

	public void setDataSources(List<DataSourceProperties> dataSourcesProps) {
		this.dataSourcesProps = dataSourcesProps;
	}

	public static class DataSourceProperties extends org.springframework.boot.autoconfigure.jdbc.DataSourceProperties {

		private String tenantId;

		public String getTenantId() {
			return tenantId;
		}

		public void setTenantId(String tenantId) {
			this.tenantId = tenantId;
		}
	}
}