/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rdb;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

public class FlywayConfig {
    @Bean
    public Boolean tenantsFlyway(String tenantName, DataSource dataSource){
        String schema = tenantName;
        Flyway flyway = new Flyway();
        flyway.setLocations("db/migrations/tenants");
        flyway.setDataSource(dataSource);
        flyway.setSchemas(schema);
        int migrate = flyway.migrate();
        return (migrate > 0);
    }

}
