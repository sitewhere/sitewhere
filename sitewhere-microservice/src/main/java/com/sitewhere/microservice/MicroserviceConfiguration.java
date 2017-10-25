/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sitewhere.microservice.instance.InstanceSettings;
import com.sitewhere.microservice.kafka.KafkaTopicNaming;
import com.sitewhere.microservice.security.SystemUser;
import com.sitewhere.microservice.security.TokenManagement;
import com.sitewhere.microservice.spi.IMicroservice;
import com.sitewhere.microservice.spi.configuration.IZookeeperManager;
import com.sitewhere.microservice.spi.instance.IInstanceSettings;
import com.sitewhere.microservice.spi.kafka.IKafkaTopicNaming;
import com.sitewhere.microservice.spi.security.ISystemUser;
import com.sitewhere.microservice.spi.security.ITokenManagement;
import com.sitewhere.microservice.zookeeper.ZookeeperManager;

import brave.Tracing;
import brave.opentracing.BraveTracer;
import io.opentracing.Tracer;
import zipkin.Span;
import zipkin.reporter.AsyncReporter;
import zipkin.reporter.okhttp3.OkHttpSender;

@Configuration
public class MicroserviceConfiguration {

    @Bean
    public IInstanceSettings instanceSettings() {
	return new InstanceSettings();
    }

    @Bean
    public IZookeeperManager zookeeperManager() {
	return new ZookeeperManager();
    }

    @Bean
    IKafkaTopicNaming kafkaTopicNaming() {
	return new KafkaTopicNaming();
    }

    @Bean
    public ITokenManagement tokenManagement() {
	return new TokenManagement();
    }

    @Bean
    public ISystemUser systemUser() {
	return new SystemUser();
    }

    @Bean
    @Autowired
    public Tracer tracer(IInstanceSettings instanceSettings, IMicroservice microservice) {
	OkHttpSender okHttpSender = OkHttpSender
		.create("http://" + instanceSettings.getTracerServer() + "/api/v1/spans");
	AsyncReporter<Span> reporter = AsyncReporter.builder(okHttpSender).build();
	Tracing braveTracer = Tracing.newBuilder().localServiceName(microservice.getIdentifier()).reporter(reporter)
		.build();
	return BraveTracer.create(braveTracer);
    }
}