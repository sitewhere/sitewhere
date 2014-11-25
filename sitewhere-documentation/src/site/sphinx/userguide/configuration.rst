====================
System Configuration
====================
SiteWhere uses a hierarchy of `Spring <http://projects.spring.io/spring-framework/>`_ XML files as
its configuration mechanism. When the SiteWhere server starts, one of the first steps is to bootstrap
the core system components by loading the *conf/sitewhere/sitewhere-server.xml* file.
Versions of SiteWhere prior to 0.9.7 only used the generic Spring beans schema for defining the core
list of beans needed to configure the server. Starting with version 0.9.7 SiteWhere has added a custom
XML schema that is more succinct and expressive. The schema provides shortcuts for many common 
configuration options while still allowing the user to extend the core architecture with custom
component implementations.

.. contents:: Contents
   :local:

--------------------------
Configuration Fundamentals
--------------------------
A valid SiteWhere configuration is based on a standard Spring beans XML file with an embedded section
that uses a schema specific to SiteWhere. The XML below is a partial configuration file illustrating some
of the key features. 

Notice the schema declarations and enclosing *<beans>* element at the top of the file. These are standard for a 
`Spring beans <http://docs.spring.io/spring-framework/docs/current/spring-framework-reference/html/beans.html>`_ 
configuration file. There is an *http://www.sitewhere.com/schema/sitewhere* namespace declared and 
pointed to the SiteWhere schema for the 0.9.7 release. Often a new SiteWhere release will add 
features to the schema, so it is important to point to the schema
for the version of SiteWhere being run on the server.

The *<sw:configuraton>* section contains all of the schema-based SiteWhere configuration elements. If a
schema-aware editor such as Eclipse is being used, the editor will provide syntax completion based on the 
SiteWhere schema. The SiteWhere schema contains many of the most often used building blocks for setting up
a SiteWhere server. It also allows for the introduction of user-defined component implementations. For example,
in the configuration below, the *<sw:outbound-event-processor>* contains a *ref* attribute that points to an
external Spring bean. By implementing components that conform to SiteWhere interfaces and plugging them in via
Spring beans, the behavior of the system may be customized to add new behaviors. In this case, the system has 
been configured to broadcast all processed events via Hazelcast.

.. code-block:: xml

	<?xml version="1.0" encoding="UTF-8"?>
	<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xmlns:context="http://www.springframework.org/schema/context" xmlns:sw="http://www.sitewhere.com/schema/sitewhere"
		xsi:schemaLocation="
	           http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
	           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.1.xsd
	           http://www.springframework.org/schema/security http://www.springframework.org/schema/security/spring-security-3.0.xsd
	           http://www.sitewhere.com/schema/sitewhere http://www.sitewhere.org/schema/sitewhere/0.9.7/sitewhere.xsd">
	           
		<!-- Load property values for substitution -->
		<context:property-placeholder location="file:${CATALINA_BASE}/conf/sitewhere/sitewhere.properties"/>
		
		<!-- SiteWhere schema-based configuration -->
		<sw:configuration>
			
			<sw:outbound-processing-chain>
				
				<!-- Reference to Spring bean declared later in the file -->
				<sw:outbound-event-processor ref="hazelcastDeviceEventProcessor"/>
	
			</sw:outbound-processing-chain>
	
		</sw:configuration>
	
		<!-- Provides access to a local Hazelcast instance for SiteWhere -->
		<bean id="hazelcastConfig" class="com.sitewhere.hazelcast.SiteWhereHazelcastConfiguration">
			<property name="configFileName" value="hazelcast.xml"/>
		</bean>
		
	 	<!-- Broadcasts SiteWhere state over Hazelcast -->
		<bean id="hazelcastDeviceEventProcessor" class="com.sitewhere.hazelcast.HazelcastEventProcessor">
			<property name="configuration" ref="hazelcastConfig"/>
		</bean>
	
	</beans>

Moving Sensitive Data Outside the Configuration
-----------------------------------------------
SiteWhere configuration files often contain login credentials or other information that should not
be shared with other users. Also, there are situations where settings for a system are 
environment-specific (production vs. staging vs. development) and maintaining a separate configuration 
for each creates extra work. Using Spring
`property placeholders <http://docs.spring.io/spring-framework/docs/current/spring-framework-reference/html/xsd-config.html#xsd-config-body-schemas-context-pphc>`_
allows sensitive data to be moved into an external properties file and injected at runtime.
In the following example, the hostname and port for the MongoDB datastore would be loaded from
the **sitewhere.properties** file in the same directory as the main configuration file.

.. code-block:: xml
   :emphasize-lines: 1, 14
   
   <context:property-placeholder location="file:${CATALINA_BASE}/conf/sitewhere/sitewhere.properties" ignore-resource-not-found="true"/>

   <!-- ########################### -->
   <!-- # SITEWHERE CONFIGURATION # -->
   <!-- ########################### -->
   <sw:configuration>
      
      <!-- ########################### -->
      <!-- # DATASTORE CONFIGURATION # -->
      <!-- ########################### -->
      <sw:datastore>
      
         <!-- Default MongoDB Datastore -->
         <sw:mongo-datastore hostname="${mongo.host}" port="${mongo.port}" databaseName="sitewhere"/>
 
The properties file would contain values for the placeholders as shown below:

.. code-block:: properties

   # SiteWhere configuration properties.
   mongo.host=localhost
   mongo.port=1234

-----------------------
Datastore Configuration
-----------------------
SiteWhere can use either `MongoDB <http://www.mongodb.org/>`_ or `Apache HBase <https://hbase.apache.org/>`_ for 
underlying data storage. For small installations where extreme scalability is not needed, it is much quicker and 
easier to use MongoDB. For true "big data" applications, the HBase backend is the better choice. 

Configuring a MongoDB Datastore
-------------------------------
To use MongoDB as the backing datastore, edit the SiteWhere configuration *<sw:datastore>* section
and uncomment the *<sw:mongo-datastore>* element while leaving the *<sw:hbase-datastore>* element
commented as shown below:

.. code-block:: xml
   :emphasize-lines: 4, 7-9

	<sw:datastore>
	
		<!-- Default MongoDB Datastore -->
		<sw:mongo-datastore hostname="localhost" port="27017" databaseName="sitewhere"/>
	
		<!-- Default HBase Datastore -->
		<!--  
		<sw:hbase-datastore quorum="localhost"/>
		-->

Note that the default settings assume a local MongoDB instance running on the default port and using a database
named **sitewhere**.

Attributes for <mongo-datastore>
********************************
The following attributes may be specified for the *<sw:mongo-datastore>* element.
      
+----------------------+----------+--------------------------------------------------+
| Attribute            | Required | Description                                      |
+======================+==========+==================================================+
| hostname             | optional | Server hostname for MongoDB instance.            |
|                      |          | Defaults to *localhost*.                         |
+----------------------+----------+--------------------------------------------------+
| port                 | optional | Server port for MongoDB instance.                |
|                      |          | Defaults to *27017*.                             |
+----------------------+----------+--------------------------------------------------+
| databaseName         | optional | MongoDB database name for SiteWhere storage.     |
|                      |          | Defaults to *sitewhere*.                         |
+----------------------+----------+--------------------------------------------------+

Configuring an HBase Datastore
------------------------------
To use Apache HBase as the backing datastore, edit the SiteWhere configuration  *<sw:datastore>* section 
and uncomment the *<sw:hbase-datastore>* element while leaving the *<sw:mongo-datastore>* element
commented as shown below:

.. code-block:: xml
   :emphasize-lines: 4-6, 9

	<sw:datastore>
	
		<!-- Default MongoDB Datastore -->
		<!--  
		<sw:mongo-datastore hostname="localhost" port="27017" databaseName="sitewhere"/>
		-->
	
		<!-- Default HBase Datastore -->
		<sw:hbase-datastore quorum="localhost"/>

Note that you will need to update the quorum address so that SiteWhere can locate your HBase cluster.

Attributes for <hbase-datastore>
********************************
The following attributes may be specified for the *<sw:hbase-datastore>* element.
      
+----------------------+----------+--------------------------------------------------+
| Attribute            | Required | Description                                      |
+======================+==========+==================================================+
| quorum               | required | Server hostname for HBase ZooKeeper quorum.      |
+----------------------+----------+--------------------------------------------------+

Populating Sample Data
----------------------
In both MongoDB and HBase installations, SiteWhere will automatically create the underlying database if it does 
not already exist. After that, each time that SiteWhere server starts up, it will check whether there is data 
in the database and, if data initializers are configured, will prompt to populate 
the database with sample data (for non-console startup, there are properties on the 
model initializers in the configuration file that allow you to specify whether 
to populate the sample data automatically). SiteWhere provides initializers that will
create sample data for both the user and device models. They can be configured by adding
the *<sw:default-device-model-initializer/>* and/or *<sw:default-user-model-initializer/>*
elements to the *<sw:datastore>* section as shown below:

.. code-block:: xml
   :emphasize-lines: 7, 10

		<sw:datastore>
		
			<!-- Default MongoDB Datastore -->
			<sw:mongo-datastore hostname="localhost" port="27017" databaseName="sitewhere"/>
			
			<!-- Initializes device model with sample data if datastore is empty -->
			<sw:default-device-model-initializer/>
			
			<!-- Initializes user model with sample data if datastore is empty -->
			<sw:default-user-model-initializer/>
 
It is usually a good choice to allow the user model to be populated since a valid user and permissions 
are required to log in to the management application. Populating the sample device data gives a nice 
starting point for understanding SiteWhere in the context of a real application.

Device Management Cache Providers
---------------------------------
Many elements of the device data model do not change often and can benefit from a caching implementation.
SiteWhere offers a service provider interface 
`IDeviceManagementCacheProvider <../apidocs/com/sitewhere/spi/device/IDeviceManagementCacheProvider.html>`_
which may be implemented to provide caching capabilities that use an external cache provider.
SiteWhere offers a default device management cache implementation based on `Ehcache <http://ehcache.org/>`_
which can be configured as shown below:

.. code-block:: xml
   :emphasize-lines: 7

	<sw:datastore>
	
		<!-- Default MongoDB Datastore -->
		<sw:mongo-datastore hostname="localhost" port="27017" databaseName="sitewhere"/>
		
		<!-- Improves performance by using EHCache to store device management entities -->
		<sw:ehcache-device-management-cache/>

Note that removing the cache will result in noticeably slower performance since the underlying
service provider implementations will load all data from the datastore.

-------------------
Device Provisioning
-------------------
In SiteWhere, the term **provisioning** refers to the subsystem that communicates with devices.
On the inbound side, device data is brought in to the system via **event sources**. The inbound 
data is converted into SiteWhere events and passed in to the **inbound processing chain** by 
the **inbound processing strategy**. On the outbound side (as part of the outbound processing 
chain) commands are sent to external devices via **command destinations**. An **outbound 
command router** makes the choice of which command destination will be used to deliver the 
command payload.

Event Sources
-------------
Event sources are responsible for bringing data into SiteWhere. All event sources implement the
`IInboundEventSource <../apidocs/com/sitewhere/spi/device/provisioning/IInboundEventSource.html>`_
interface and are composed of one or more **event receivers** (implementing 
`IInboundEventReceiver <../apidocs/com/sitewhere/spi/device/provisioning/IInboundEventReceiver.html>`_) 
and a single **event decoder** (implementing 
`IDeviceEventDecoder <../apidocs/com/sitewhere/spi/device/provisioning/IDeviceEventDecoder.html>`_).
Event receivers take care of dealing with protocols for gathering data. The data is then processed
by the event decoder in order to create SiteWhere events which provide a common representation of
the device data so it can be processed by the inbound processing chain.

MQTT Event Source
*****************
Since consuming MQTT data is common in IoT applications, SiteWhere includes a component that 
streamlines the process. In the example below, an event source is configured to listen for messages
on the given topic, then use the **protobufEventDecoder** (declared externally as a Spring bean) to
decode the message payload into SiteWhere events.

.. code-block:: xml
   :emphasize-lines: 7-10

	<sw:provisioning>
	
		<!-- Inbound event sources -->
		<sw:event-sources>

			<!-- Event source for protobuf messages over MQTT -->
			<sw:mqtt-event-source hostname="localhost" port="1883"
				topic="SiteWhere/input/protobuf">
				<sw:decoder ref="protobufEventDecoder"/>
			</sw:mqtt-event-source>
         
Inbound Processing Strategy
---------------------------
The inbound processing strategy is responsible for moving events from event sources into the
inbound processing chain. It is responsible for handling threading and reliably delivering
events for processing. An inbound processing strategy must implement the 
`IInboundProcessingStrategy <../apidocs/com/sitewhere/spi/device/provisioning/IInboundProcessingStrategy.html>`_
interface.

Default Inbound Processing Strategy
***********************************
The default inbound processing strategy for SiteWhere CE uses a bounded queue to hold events
being delivered from event sources. It creates a thread pool that consumes the queue to 
deliver events to the inbound processing chain. If events are delivered faster than the thread
pool can process them, the queue will eventually start blocking the event receiver threads.
Increasing the number of threads for event processing takes load from the queue but increases
processing load on the core system. SiteWhere CE does not persist the inbound queue, so shutting 
down the server may result in data loss. SiteWhere EE offers a more advanced inbound processing
strategy implementation with persistent queues and transactional semantics.

.. code-block:: xml
   :emphasize-lines: 5-6

   <sw:provisioning>
   
         <!-- Inbound Processing Strategy -->
         <sw:inbound-processing-strategy>
            <sw:default-inbound-processing-strategy
               numEventProcessorThreads="150" enableMonitoring="true" monitoringIntervalSec="1"/>
         </sw:inbound-processing-strategy>

Attributes for <default-inbound-processing-strategy>
****************************************************
The following attributes may be specified for the *<sw:default-inbound-processing-strategy>* element.
      
+--------------------------+----------+----------------------------------------------------+
| Attribute                | Required | Description                                        |
+==========================+==========+====================================================+
| numEventProcessorThreads | optional | Number of threads used to process incoming events. |
|                          |          | Defaults to *100*.                                 |
+--------------------------+----------+----------------------------------------------------+
| enableMonitoring         | optional | Enables monitoring of event processing in the log. |
|                          |          | Defaults to *false*.                               |
+--------------------------+----------+----------------------------------------------------+
| monitoringIntervalSec    | optional | Interval (in seconds) at which monitoring messages |
|                          |          | are posted. Defaults to *5*.                       |
+--------------------------+----------+----------------------------------------------------+

Command Destinations
--------------------
Command destinations are responsible for delivering commands to devices. All command destinations implement the
`ICommandDestination <../apidocs/com/sitewhere/spi/device/provisioning/ICommandDestination.html>`_
interface and are composed of a **command encoder** (implementing 
`ICommandExecutionEncoder <../apidocs/com/sitewhere/spi/device/provisioning/ICommandExecutionEncoder.html>`_),
a **parameter extractor** (implementing
`ICommandDeliveryParameterExtractor <../apidocs/com/sitewhere/spi/device/provisioning/ICommandDeliveryParameterExtractor.html>`_),
and a **delivery provider** (implementing 
`ICommandDeliveryProvider <../apidocs/com/sitewhere/spi/device/provisioning/ICommandDeliveryProvider.html>`_).
The command encoder is used to convert the command payload into a format understood by the device. The parameter
extractor pulls information needed for delivering the message to the delivery provider (e.g. for an SMS provider,
the extractor may pull the SMS phone number for the device from device metadata). The delivery provider takes 
the encoded payload and extracted parameters, then delivers the message to the device.

MQTT Command Destination
************************
For devices that listen on an MQTT topic for commands, the *<sw:mqtt-command-destination>* element can 
be used to easily configure a destination. An encoder and parameter extractor should be configured
based on the expected command format and location of MQTT routing information. The 
*<sw:hardware-id-topic-extractor>* element configures the MQTT topics for delivery based
on an expression that includes the hardware id of the device to be addressed. In cases where this
is not appropriate, a custom parameter extractor can be injected instead.

.. code-block:: xml
   :emphasize-lines: 7-12

		<sw:provisioning>
					
			<!-- Outbound command destinations -->
			<sw:command-destinations>

				<!-- Delivers commands via MQTT -->
				<sw:mqtt-command-destination destinationId="default"
					hostname="localhost" port="1883">
					<sw:encoder ref="protobufExecutionEncoder"/>
					<sw:hardware-id-topic-extractor commandTopicExpr="SiteWhere/commands/%s"
						systemTopicExpr="SiteWhere/system/%s"/>
				</sw:mqtt-command-destination>

Twilio Command Destination
**************************
For devices that receive commands via SMS messages, the *<sw:twilio-command-destination>* may be used to
deliver the command via the `Twilio <https://www.twilio.com/>`_ online service. To use the service you will
need to create a Twilio account and pay for the outbound SMS service (including a phone number that
messages will be sent from).

.. code-block:: xml
   :emphasize-lines: 7-12

		<sw:provisioning>
					
			<!-- Outbound command destinations -->
			<sw:command-destinations>

				<!-- Delivers commands via Twilio SMS messages -->
				<sw:twilio-command-destination destinationId="laipac"
					accountSid="${twilio.account.sid}" authToken="${twilio.auth.token}" 
					fromPhoneNumber="${twilio.from.phone.number}">
					<sw:encoder ref="laipacExecutionEncoder"/>
					<sw:parameter-extractor ref="laipacExtractor"/>
				</sw:twilio-command-destination>
				
The account SID, auth token, and sending phone number are all pieces of data related to the Twilio account.
The parameter extractor implementation should be one that supplies parameters of type 
SmsParameters which is used by the delivery provider to determine the SMS phone number 
to deliver the command to.

------------------------
Inbound Processing Chain
------------------------
After data has been converted into SiteWhere device events by event sources, the default provisioning 
implementation (DefaultDeviceProvisioning.html)
queues up events to be processed by the **inbound processing chain**. The chain is a series of
**inbound event processors** (implementing 
`IInboundEventProcessor <../apidocs/com/sitewhere/spi/device/event/processor/IInboundEventProcessor.html>`_)
that each handle the inbound events in series. New inbound event processors can be added to the chain to augment
the existing functionality. For instance, a metrics processor could keep count of events processed per second. 

**Since REST calls (or other calls that directly invoke the device management APIs) do not enter the system via event sources, 
they are not processed by the inbound processing chain.**


Default Event Storage Processor
-------------------------------
By default, an instance of *<sw:default-event-storage-processor/>* is configured in the chain. This processor
takes care of persisting device events via the device management service provider interfaces. If this 
processor is removed, events will not be stored and devices will not be registered. The default configuration
is shown below:

.. code-block:: xml
   :emphasize-lines: 6

		<sw:provisioning>
					
			<sw:inbound-processing-chain>
				
				<!-- Store events and delegate to registration manager -->
				<sw:default-event-storage-processor/>
	
			</sw:inbound-processing-chain>

-------------------------
Outbound Processing Chain
-------------------------
In the default provisioning implementation, each time an event is saved via the device management 
service provider interfaces, the outbound event processing chain is invoked. In the same way the 
inbound processing chain acts on unsaved inbound event data, the oubound processing chain acts on 
data that has been successfully persisted to the datastore. Each **outbound event processor** (implementing 
`IOutboundEventProcessor <../apidocs/com/sitewhere/spi/device/event/processor/IOutboundEventProcessor.html>`_)
is executed in series. New outbound event processors can be added to the chain to augment existing
functionality. For instance, SiteWhere has an event processor for sending all outbound events to
Hazelcast subscribers, allowing external clients to act on the events.

**REST calls (or other calls that directly invoke the device management APIs) are processed by the
outbound processing chain in the same manner as events from event sources.**

Provisioning Event Processor
----------------------------
By default, an instance of *<sw:provisioning-event-processor/>* is configured in the outbound chain. This
processor hands off device command invocations to the provisioning subsystem for processing. If this 
processor is removed, device command invocations will be persisted, but will never be processed. The
default configuration is shown below:

.. code-block:: xml
   :emphasize-lines: 6

		<sw:provisioning>
					
			<sw:outbound-processing-chain>
			
				<!-- Routes commands for provisioning -->
				<sw:provisioning-event-processor/>
				
				<!-- Send outbound device events over Hazelcast -->
				<sw:outbound-event-processor ref="hazelcastDeviceEventProcessor"/>
	
			</sw:outbound-processing-chain>

This example also shows the addition of a custom outbound event processor which references a Spring bean
defined elsewhere in the configuration. Events will be passed to the custom processor after they have
been processed by the provisioning processor.

Broadcasting Events via Hazelcast
---------------------------------
SiteWhere has support for broadcasting events over `Hazelcast <http://hazelcast.com/>`_ topics, making it
easy to share events with external agents. To enable Hazelcast broadcasting, declare the following beans
in the configuration file anywhere outside of the *<sw:configuration>* block:

.. code-block:: xml
   
		<!-- Provides access to a local Hazelcast instance for SiteWhere -->
		<bean id="hazelcastConfig" class="com.sitewhere.hazelcast.SiteWhereHazelcastConfiguration">
			<property name="configFileName" value="hazelcast.xml"/>
		</bean>
		
		<!-- Broadcasts SiteWhere state over Hazelcast -->
		<bean id="hazelcastDeviceEventProcessor" class="com.sitewhere.hazelcast.HazelcastEventProcessor">
			<property name="configuration" ref="hazelcastConfig"/>
		</bean>

Note that the Hazelcast event processor references a **hazelcast.xml** configuration file. This file
(located in the same directory as the primary configuration file) may be used to configure Hazelcast options.
Once the beans have been declared, they may be referenced as part of the outbound processing chain to
enable broadcasting of events.

.. code-block:: xml
   :emphasize-lines: 7
   
		<sw:outbound-processing-chain>
		
			<!-- Routes commands for provisioning -->
			<sw:provisioning-event-processor/>
			
			<!-- Send outbound device events over Hazelcast -->
			<sw:outbound-event-processor ref="hazelcastDeviceEventProcessor"/>

		</sw:outbound-processing-chain>

To consume events from the Hazelcast topics, listen on the topic names as defined in 
`ISiteWhereHazelcast <../apidocs/com/sitewhere/spi/server/hazelcast/ISiteWhereHazelcast.html>`_.

Sending Events to Apache Solr
-----------------------------
SiteWhere supports forwarding events to `Apache Solr <http://lucene.apache.org/solr/>`_ to leverage
the sophisticated search and analytics features it provides. The Solr outbound event processor uses
the `Solrj <https://cwiki.apache.org/confluence/display/solr/Using+SolrJ>`_ library to send each
outbound event to a Solr instance. The events are stored using a custom SiteWhere document schema,
allowing event data to be indexed based on its type. For instance, location events are stored with
geospatial indexes to allow efficient location searches. To enable the Solr event processor, the 
following beans must be added to the configuration file anywhere outside of the *<sw:configuration>* block:

.. code-block:: xml
   
		<!-- Provides connectivity to Solr for components that need it -->
		<bean id="solrConfig" class="com.sitewhere.solr.SiteWhereSolrConfiguration">
			<property name="solrServerUrl" value="http://localhost:8983/solr/SiteWhere"/>
		</bean>
			
		<!-- Indexes SiteWhere events in Solr -->
		<bean id="solrDeviceEventProcessor" class="com.sitewhere.solr.SolrDeviceEventProcessor">
			<property name="solr" ref="solrConfig"/>
		</bean>

The **solrServerUrl** parameter needs to point to the Solr core being used for SiteWhere data. To
add the bean to the outbound processing chain, reference it as shown below:

.. code-block:: xml
   :emphasize-lines: 7
   
		<sw:outbound-processing-chain>
		
			<!-- Routes commands for provisioning -->
			<sw:provisioning-event-processor/>
			
			<!-- Send outbound device events to Solr -->
			<sw:outbound-event-processor ref="solrDeviceEventProcessor"/>

		</sw:outbound-processing-chain>

Note that on system startup, the event processor attempts to ping the Solr server to verify the 
settings are correct. If the ping fails, server startup will fail.