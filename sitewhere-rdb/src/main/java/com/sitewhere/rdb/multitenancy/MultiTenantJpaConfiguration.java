/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rdb.multitenancy;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import com.sitewhere.rdb.entities.Area;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * The configuration of multi tenant in Jpa
 *
 * Simeon Chen
 */
@Configuration
@EnableConfigurationProperties({  JpaProperties.class })
@EnableJpaRepositories(basePackages = { "com.sitewhere.rdb.repositories" }, transactionManagerRef = "txManager")
@EnableTransactionManagement
public class MultiTenantJpaConfiguration {

    @Autowired
    private JpaProperties jpaProperties;

    @Bean(name = "rdbDataSources")
    public Map<String, DataSource> rdbDataSources() {
        Map<String, DataSource> result = new HashMap<>();
        for (MultiTenantProperties.DataSourceProperties dsProperties : MultiTenantProperties.DATA_SOURCES_PROPS) {
            DataSourceBuilder factory = DataSourceBuilder
                    .create()
                    .url(dsProperties.getUrl())
                    .username(dsProperties.getUsername())
                    .password(dsProperties.getPassword())
                    .driverClassName(dsProperties.getDriverClassName());
            DataSource build = factory.build();
            result.put(dsProperties.getTenantId(), build);
        }
        return result;
    }

    @Bean
    public MultiTenantConnectionProvider multiTenantConnectionProvider() {
        // Autowires rdbDataSources
        return new DataSourceMultiTenantConnectionProviderImpl();
    }

    @Bean
    public CurrentTenantIdentifierResolver currentTenantIdentifierResolver() {
        return new TenantIdentifierResolverImpl();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(MultiTenantConnectionProvider multiTenantConnectionProvider,
                                                                           CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {
        Map<String, Object> hibernateProps = new LinkedHashMap<>();
        hibernateProps.putAll(this.jpaProperties.getProperties());
        hibernateProps.put(Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
        hibernateProps.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
        hibernateProps.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver);
        hibernateProps.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQL94Dialect");
        hibernateProps.put(Environment.SHOW_SQL,"true");
        hibernateProps.put(Environment.FORMAT_SQL, "true");
        // No dataSource is set to resulting entityManagerFactoryBean
        LocalContainerEntityManagerFactoryBean result = new LocalContainerEntityManagerFactoryBean();
        result.setPackagesToScan(new String[] { Area.class.getPackage().getName() });
        result.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        result.setJpaPropertyMap(hibernateProps);
        return result;
    }

    @Bean
    public EntityManagerFactory entityManagerFactory(LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
        return entityManagerFactoryBean.getObject();
    }

    @Bean
    public PlatformTransactionManager txManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}