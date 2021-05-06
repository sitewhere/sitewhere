/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.instance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.influx.InfluxDbAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JndiDataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.context.annotation.Import;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.MicroserviceApplication;
import com.sitewhere.web.configuration.RestConfiguration;
import com.sitewhere.web.configuration.WebSecurityConfiguration;

/**
 * Spring Boot application for web/REST microservice.
 */
@Import(value = { RestConfiguration.class, WebSecurityConfiguration.class })
@SpringBootApplication(exclude = { AopAutoConfiguration.class, CacheAutoConfiguration.class,
	CassandraAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
	FlywayAutoConfiguration.class, InfluxDbAutoConfiguration.class, JndiDataSourceAutoConfiguration.class,
	JtaAutoConfiguration.class, KafkaAutoConfiguration.class, WebFluxAutoConfiguration.class })
public class InstanceManagementApplication extends MicroserviceApplication<IInstanceManagementMicroservice> {

    @Autowired
    private IInstanceManagementMicroservice microservice;

    public static void main(String[] args) {
	SpringApplication.run(InstanceManagementApplication.class, args);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroserviceApplication#getMicroservice()
     */
    @Override
    public IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}