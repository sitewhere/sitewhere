package com.sitewhere.rdb.multitenancy;

import com.sitewhere.configuration.instance.rdb.RDBConfiguration;
import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.AbstractMultiTenantConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *  MultiTenancyConnectionProvider implementation
 *
 *  Simeon Chen
 */
@Component
public class MapMultiTenantConnectionProviderImpl extends AbstractMultiTenantConnectionProvider {

    private final Map<String, ConnectionProvider> connectionProviderMap = new HashMap<>();

    @Override
    protected ConnectionProvider getAnyConnectionProvider() {
        return connectionProviderMap.values()
                .iterator()
                .next();
    }

    @Override
    protected ConnectionProvider selectConnectionProvider(String tenantIdentifier) {
        return connectionProviderMap.get(tenantIdentifier);
    }

    private void initConnectionProviderForTenant(String tenantId, Properties props) {
        DriverManagerConnectionProviderImpl connectionProvider = new DriverManagerConnectionProviderImpl();
        connectionProvider.configure(props);
        this.connectionProviderMap.put(tenantId, connectionProvider);
    }

    /**
     * Register a new tenant connection provider
     *
     * @param tenantId
     * @param config
     */
    public void registerTenantConnectionProvider(String tenantId, RDBConfiguration config) {
        Properties props = new Properties();
        props.put("hibernate.connection.driver_class",config.getDriver());
        props.put("hibernate.connection.url",config.getUrl());
        props.put("hibernate.connection.username",config.getUsername());
        props.put("hibernate.connection.password",config.getPassword());
        props.put("hibernate.hbm2ddl.auto", config.getHbm2ddlAuto());
        props.put("hibernate.dialect", config.getDialect());
        props.put("hibernate.jdbc.lob.non_contextual_creation", "true");

        initConnectionProviderForTenant(tenantId, props);
    }
}
