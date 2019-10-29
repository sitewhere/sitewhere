/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.instance.rdb;

public class RDBConfiguration {

    /** Default url for relational database */
    private static final String DEFAULT_URL = "jdbc:postgresql://sitewhere-postgresql-headless.default.svc.cluster.local/tenant1";

    /** Default parameter for formatting Hibernate SQL */
    private static final String DEFAULT_FORMATSQL = "true";

    /** Default parameter for displaying Hibernate SQL */
    private static final String DEFAULT_SHOWSQL = "true";

    /** Default Hibernate dialect */
    private static final String DEFAULT_DIALECT = "org.hibernate.dialect.PostgreSQL94Dialect";

    /** Default Hibernate dll_auto */
    private static final String HBM2DDLAUTO = "update";

    /** Default database driver class */
    private static final String DEFAULT_DRIVER = "org.postgresql.Driver";

    /** Format Hibernate SQL */
    private String formatSql = DEFAULT_FORMATSQL;

    /** Display Hibernate SQL */
    private String showSql = DEFAULT_SHOWSQL;

    /** Hibernate dialect */
    private String dialect = DEFAULT_DIALECT;

    /** Hibernate hbm2ddl auto */
    private String hbm2ddlAuto = HBM2DDLAUTO;

    /** Driver class for relational databases */
    private String driver = DEFAULT_DRIVER;

    /** URL used to access the relational datastore */
    private String url = DEFAULT_URL;

    /** Username used for authentication */
    private String username = "sitewhere";

    /** Password used for authentication */
    private String password = "password";

    /** Replica set name (blank or null for none) */
    private String replicaSetName;

    /** Indicates if replication should be auto-configured */
    private boolean autoConfigureReplication = true;

    public String getFormatSql() {
        return formatSql;
    }

    public void setFormatSql(String formatSql) {
        this.formatSql = formatSql;
    }

    public String getShowSql() {
        return showSql;
    }

    public void setShowSql(String showSql) {
        this.showSql = showSql;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getHbm2ddlAuto() {
        return hbm2ddlAuto;
    }

    public void setHbm2ddlAuto(String hbm2ddlAuto) {
        this.hbm2ddlAuto = hbm2ddlAuto;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getReplicaSetName() {
        return replicaSetName;
    }

    public void setReplicaSetName(String replicaSetName) {
        this.replicaSetName = replicaSetName;
    }

    public boolean isAutoConfigureReplication() {
        return autoConfigureReplication;
    }

    public void setAutoConfigureReplication(boolean autoConfigureReplication) {
        this.autoConfigureReplication = autoConfigureReplication;
    }
}
