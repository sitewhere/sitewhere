/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.test;

import org.junit.Test;

import com.sitewhere.microservice.util.MarshalUtils;

import io.sitewhere.k8s.crd.instance.SiteWhereInstance;

public class ResourceMarshalTest {

    @Test
    public void unmarshaInstanceResource() throws Exception {
	String input = "{\"apiVersion\":\"sitewhere.io/v1alpha3\",\"kind\":\"SiteWhereInstance\",\"metadata\":{\"creationTimestamp\":\"2019-12-13T18:25:55Z\",\"generation\":2,\"name\":\"sitewhere1\",\"resourceVersion\":\"1432412\",\"selfLink\":\"/apis/sitewhere.io/v1alpha3/instances/sitewhere1/status\",\"uid\":\"0046c89b-1dd6-11ea-9f6c-00155d380173\"},\"spec\":{\"configuration\":{\"persistenceConfigurations\":{\"cassandraConfigurations\":{\"default\":{\"contactPoints\":\"${cassandra.contact.points:cassandra}\",\"keyspace\":\"${cassandra.keyspace:tenant_[[tenant.id]]}\"}},\"influxDbConfigurations\":{\"default\":{\"databaseName\":\"${influxdb.database:[[tenant.id]]}\",\"hostname\":\"${influxdb.host:influxdb}\",\"port\":\"${influxdb.port:8086}\"}},\"rdbConfigurations\":{\"default\":{\"databaseName\":\"${mongodb.tenant.prefix:tenant-[[tenant.id]]}\",\"hostname\":\"${mongodb.host:mongodb}\",\"port\":\"${mongodb.port:27017}\"}}}},\"configurationTemplate\":\"instance-config-default\",\"datasetTemplate\":\"instance-dataset-default\",\"instanceNamespace\":\"sitewhere\"},\"status\":{\"tenantManagementBootstrapState\":\"Bootstrapping\",\"userManagementBootstrapState\":\"NotBootstrapped\"}}";
	SiteWhereInstance instance = MarshalUtils.unmarshalJson(input.getBytes(), SiteWhereInstance.class);
	System.out.println(MarshalUtils.marshalJsonAsPrettyString(instance));
    }
}
