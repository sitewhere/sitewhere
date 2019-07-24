package com.sitewhere.rdb;

import java.net.URISyntaxException;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import com.sitewhere.rdb.multitenancy.MapMultiTenantConnectionProviderImpl;
import com.sitewhere.rdb.multitenancy.CurrentTenantIdentifierResolverImpl;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.*;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Persistence configuration for Relational database
 *
 * Simeon Chen
 */
@Configuration
@EnableTransactionManagement
@ComponentScan({ "com.sitewhere.rdb" })
public class PersistenceConfig {

    private static final Logger log = LoggerFactory.getLogger(PersistenceConfig.class);

    @Bean(name = "transactionManager")
    public PlatformTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) throws URISyntaxException {
        log.info("Initialize transactionManager");
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, MapMultiTenantConnectionProviderImpl multiTenantConnectionProvider, CurrentTenantIdentifierResolverImpl currentTenantIdentifierResolver) {
        LocalContainerEntityManagerFactoryBean em = new  LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan(new String[] { "com.sitewhere.rdb.entities" });
        em.setJpaProperties(hibernateProperties(multiTenantConnectionProvider,currentTenantIdentifierResolver ));
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        return em;
    }

    /**
     * Generate properties for hibernate
     *
     * @return
     */
    private final Properties hibernateProperties(MapMultiTenantConnectionProviderImpl multiTenantConnectionProviderImpl, CurrentTenantIdentifierResolverImpl currentTenantIdentifierResolver) {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.jdbc.lob.non_contextual_creation", "true");
        // Envers properties
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL94Dialect");
        hibernateProperties.setProperty("org.hibernate.envers.audit_table_suffix", "_audit_log");
        hibernateProperties.put(Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
        hibernateProperties.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProviderImpl);
        hibernateProperties.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver);
        hibernateProperties.put(Environment.FORMAT_SQL, "true");
        return hibernateProperties;
    }
}
