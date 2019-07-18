package com.sitewhere.configuration.instance.rdb;

public class RDBConfiguration {
    /** Default hostname for relational database */
    private static final String DEFAULT_HOSTNAME = "localhost";

    /** Default url for relational database */
    private static final String DEFAULT_URL = "jdbc:postgresql://114.116.1.182:5432/tenant1";

    /** Default database name */
    private static final String DEFAULT_DATABASE_NAME = "sitewhere";

    /** Default authentication database name */
    private static final String DEFAULT_AUTH_DATABASE_NAME = "admin";

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

    /** Hostname used to access the relational datastore */
    private String hostname = DEFAULT_HOSTNAME;

    /** URL used to access the relational datastore */
    private String url = DEFAULT_URL;

    /** Username used for authentication */
    private String username;

    /** Password used for authentication */
    private String password;

    /** Replica set name (blank or null for none) */
    private String replicaSetName;

    /** Indicates if replication should be auto-configured */
    private boolean autoConfigureReplication = true;

    /** Database that holds sitewhere collections */
    private String databaseName = DEFAULT_DATABASE_NAME;

    /** Database that holds user credentials */
    private String authDatabaseName = DEFAULT_AUTH_DATABASE_NAME;

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

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
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

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getAuthDatabaseName() {
        return authDatabaseName;
    }

    public void setAuthDatabaseName(String authDatabaseName) {
        this.authDatabaseName = authDatabaseName;
    }
}
