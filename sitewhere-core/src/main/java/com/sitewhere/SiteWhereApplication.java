/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastJpaDependencyAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;
import org.springframework.boot.autoconfigure.velocity.VelocityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import com.sitewhere.core.Boilerplate;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.spi.ServerStartupException;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.ISiteWhereApplication;

/**
 * Root Spring Boot application that loads all other SiteWhere artifacts.
 * 
 * @author Derek
 */
@Configuration
@EnableAutoConfiguration(exclude = { ActiveMQAutoConfiguration.class, DataSourceAutoConfiguration.class,
	DataSourceTransactionManagerAutoConfiguration.class, ErrorMvcAutoConfiguration.class,
	GsonAutoConfiguration.class, HazelcastAutoConfiguration.class, HazelcastJpaDependencyAutoConfiguration.class,
	JmxAutoConfiguration.class, MongoAutoConfiguration.class, SolrAutoConfiguration.class,
	VelocityAutoConfiguration.class })
public class SiteWhereApplication implements ISiteWhereApplication {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    public SiteWhereApplication() {
	try {
	    LifecycleProgressMonitor monitor = new LifecycleProgressMonitor();
	    SiteWhere.start(this, monitor);
	    LOGGER.info("Server started successfully.");
	    SiteWhere.getServer().logState();
	} catch (ServerStartupException e) {
	    SiteWhere.getServer().setServerStartupError(e);
	    List<String> messages = new ArrayList<String>();
	    messages.add("!!!! SiteWhere Server Failed to Start !!!!");
	    messages.add("");
	    messages.add("Component: " + e.getDescription());
	    messages.add("Error: " + e.getComponent().getLifecycleError().getMessage());
	    String message = Boilerplate.boilerplate(messages, '*', 60);
	    LOGGER.info("\n" + message + "\n");
	} catch (SiteWhereException e) {
	    LOGGER.error("Exception on server startup.", e);
	    List<String> messages = new ArrayList<String>();
	    messages.add("!!!! SiteWhere Server Failed to Start !!!!");
	    messages.add("");
	    messages.add("Error: " + e.getMessage());
	    String message = Boilerplate.boilerplate(messages, '*', 60);
	    LOGGER.info("\n" + message + "\n");
	} catch (Throwable e) {
	    LOGGER.error("Unhandled exception in server startup.", e);
	    List<String> messages = new ArrayList<String>();
	    messages.add("!!!! Unhandled Exception !!!!");
	    messages.add("");
	    messages.add("Error: " + e.getMessage());
	    String message = Boilerplate.boilerplate(messages, '*', 60);
	    LOGGER.info("\n" + message + "\n");
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ISiteWhereApplication#getServerClass()
     */
    @Override
    public Class<? extends SiteWhereServer> getServerClass() throws SiteWhereException {
	return SiteWhereServer.class;
    }

    public static void main(String[] args) {
	SpringApplication.run(SiteWhereApplication.class, args);
    }
}