package com.sitewhere.rdb;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitewhere.configuration.instance.rdb.RDBConfiguration;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableTransactionManagement
@ComponentScan({ "com.sitewhere.rdb" })
public class PersistenceConfig {

    private static final Logger log = LoggerFactory.getLogger(PersistenceConfig.class);

    @Autowired
    private Environment env;

    private RDBConfiguration config;

    @Autowired
    public PersistenceConfig(ApplicationArguments args) {
        String jsonStr = args.getSourceArgs()[0];
        ObjectMapper mapper = new ObjectMapper();
        try {
            config = mapper.readValue(jsonStr, RDBConfiguration.class);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
    }

    @Bean
    public DataSource dataSource() {
        final HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(config.getDriver());
        dataSource.setJdbcUrl(config.getUrl());
        dataSource.setUsername(config.getUsername());
        dataSource.setPassword(config.getPassword());
        return dataSource;
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) throws URISyntaxException {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new  LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(new String[] { "com.sitewhere.rdb.entities" });
        em.setJpaProperties(hibernateProperties());
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        return em;
    }

    /**
     * Generate properties for hibernate
     *
     * @return
     */
    private final Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", config.getHbm2ddlAuto());
        hibernateProperties.setProperty("hibernate.dialect", config.getDialect());
        hibernateProperties.setProperty("hibernate.show_sql", config.getShowSql());
        hibernateProperties.setProperty("hibernate.format_sql", config.getFormatSql());
//		hibernateProperties.setProperty("hibernate.connection.autocommit","true");
//		hibernateProperties.setProperty("hibernate.connection.provider_disables_autocommit","false");
        hibernateProperties.setProperty("hibernate.jdbc.lob.non_contextual_creation", "true");
        // Envers properties
        hibernateProperties.setProperty("org.hibernate.envers.audit_table_suffix", "_audit_log");
        return hibernateProperties;
    }
}
