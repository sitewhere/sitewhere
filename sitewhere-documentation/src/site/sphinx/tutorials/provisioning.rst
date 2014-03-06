=================================
 SiteWhere Provisioning Tutorial
=================================
Provisioning is one of the central features provided by SiteWhere. The provisioning engine allows
SiteWhere to interact with connected devices using a powerful and highly configurable set of
components. This tutorial will explain the basics of how SiteWhere provisioning works including
the concept of device specifications, the encoding and delivery of commands, how command responses
are handled, and an example of creating a customized device agent that responds to commands.

---------------------
Device Specifications
---------------------
Device specifications contain information that pertains to a class of device hardware. For 
instance, if you have 5 different configurations for a given hardware platform, it makes sense to
have a different specification for each. The specification defines some high level attributes such
as the hardware asset type, but can also be customized with metadata and commands that are specific
to a given configuration. In the SiteWhere admin UI, there is a **Specifications** tab at the top
of the page that allows you to view the existing specifications and create new ones:

.. image:: /_static/images/tutorials/provisioning/specifications.png
   :width: 100%
   :alt: Specifications List
   :align: left

Clicking on the green arrow beside a specification navigates to the detail page for that specification
as shown below:

.. image:: /_static/images/tutorials/provisioning/specification-detail.png
   :width: 100%
   :alt: Specifications List
   :align: left
   
The banner at the top of the page indicates the type of asset with its associated name, description, 
and thumbnail image. There is also a **Token** field that contains a UUID which uniquely identifies
the device specification. This value is used by devices on registration to indicate the type of 
hardware so that SiteWhere can properly interact with them.

---------------
Device Commands
---------------
Below the basic specification information is a list of *Device Commands*. Commands specify how SiteWhere
can interact with the underlying hardware. A device command has a unique name, a namespace, and zero
or more parameters. Device commands are intended to work in much the same way as calling
an RPC method in a programming language. The command name and a list of strongly-typed parameters are 
sent to the device which interprets them and executes the corresponding logic. Each parameter has a name
(which must be unique within the command), a type, and a flag indicating whether it is required. An example
of the SiteWhere UI for editing commands is shown below:

.. image:: /_static/images/tutorials/provisioning/command-parameters.png
   :width: 100%
   :alt: Editing a Device Command
   :align: left

Parameter datatypes have been chosen based on the ones offered by `Google Protocol Buffers 
<https://developers.google.com/protocol-buffers/>`_. A full list of available datatypes and an
explanation of what they represent can be found in the protocol buffer documentation for 
`scalar types <https://developers.google.com/protocol-buffers/docs/proto#scalar>`_.
Protocol buffers provide a prototyping language based on **.proto** files which can be used to generate
bindings for most common languages. Rather than re-invent the wheel, SiteWhere uses protobuf as 
the default encoding mechanism when sending and receiving messages from connected devices. 

-------------------------------
Protocol Buffer Code Generation
-------------------------------
Clicking on the **Code Generation** tab on the specification detail page shows the protocol 
buffer definition that can be used to send the current commands. You can view a syntax-highlighted
version of the *.proto* in the admin UI or download it to your local machine using the **Download**
button at the top of the page. An example of the page is shown below:

.. image:: /_static/images/tutorials/provisioning/protobuf-code-generation.png
   :width: 100%
   :alt: Google Protocol Buffers Code Generation
   :align: left

As mentioned above, protocol buffers have been chosen as the default encoding mechanism for sending
messages between SiteWhere and connected devices. The encoding (as well as most other provisioning
components) is pluggable, so you are not forced to use protocol buffers. Taking a look in the main
configuration file found at **${SITEWHERE_HOME}/conf/sitewhere/sitewhere-server.xml**, browse down 
and you will find a section for configuring provisioning. It will look similar to the one below:

.. code-block:: xml

	<!-- ####################### -->
	<!-- # DEVICE PROVISIONING # -->
	<!-- ####################### -->
	
	<!-- OUTBOUND PROCESSING -->
	
	<!-- Encodes device commands using Google Protocol Buffers -->
	<bean id="protobufExecutionEncoder" class="com.sitewhere.device.provisioning.protobuf.ProtobufExecutionEncoder"/>
	
	<!-- Delivery provider that publishes commands to MQTT topics -->
	<bean id="mqttDeliveryProvider" class="com.sitewhere.device.provisioning.mqtt.MqttCommandDeliveryProvider">
		<property name="hostname" value="localhost"/>
		<property name="port" value="1883"/>
		<property name="commandTopicPrefix" value="SiteWhere/commands/"/>
		<property name="systemTopicPrefix" value="SiteWhere/system/"/>
	</bean>
	
	<!-- GOOGLE PROTOCOL BUFFER INBOUND PROCESSING -->
	
	<!-- Device event receiver that monitors an MQTT topic for events (protobuf format) -->
	<bean id="protobufMqttEventReceiver" class="com.sitewhere.device.provisioning.mqtt.MqttDeviceEventReceiver">
		<property name="hostname" value="localhost"/>
		<property name="port" value="1883"/>
		<property name="topic" value="SiteWhere/input/protobuf"/>
	</bean>
	
	<!-- Decodes device event messages using Google Protocol Buffers -->
	<bean id="protobufEventDecoder" class="com.sitewhere.device.provisioning.protobuf.ProtobufDeviceEventDecoder"/>
	
	<!-- Device event processor for protobuf messages over MQTT -->
	<bean id="protbufEventProcessor" class="com.sitewhere.device.provisioning.DefaultDeviceEventProcessor">
		<property name="deviceEventDecoder" ref="protobufEventDecoder"/>
		<property name="deviceEventReceivers">
			<list>
				<ref bean="protobufMqttEventReceiver"/>
			</list>
		</property>
	</bean>
	
	<!-- JSON BATCH INBOUND PROCESSING -->
	
	<!-- Device event receiver that monitors an MQTT topic for events (JSON batch format) -->
	<bean id="jsonMqttEventReceiver" class="com.sitewhere.device.provisioning.mqtt.MqttDeviceEventReceiver">
		<property name="hostname" value="localhost"/>
		<property name="port" value="1883"/>
		<property name="topic" value="SiteWhere/input/jsonbatch"/>
	</bean>
	
	<!-- Decodes device event messages using Jackson/JSON -->
	<bean id="jsonEventDecoder" class="com.sitewhere.device.provisioning.decoders.JsonBatchEventDecoder"/>
	
	<!-- Device event processor for protobuf messages over MQTT -->
	<bean id="jsonEventProcessor" class="com.sitewhere.device.provisioning.DefaultDeviceEventProcessor">
		<property name="deviceEventDecoder" ref="jsonEventDecoder"/>
		<property name="deviceEventReceivers">
			<list>
				<ref bean="jsonMqttEventReceiver"/>
			</list>
		</property>
	</bean>
	
	<!-- REGISTRATION -->
	
	<!-- Manages registration of new devices in the system -->
	<bean id="protobufRegistrationManager" class="com.sitewhere.device.provisioning.protobuf.ProtobufRegistrationManager">
		<property name="allowNewDevices" value="true"/>
		<property name="autoAssignSite" value="true"/>
	</bean>

	<!-- Configures device provisioning -->
	<bean id="deviceProvisioning" class="com.sitewhere.device.provisioning.DefaultDeviceProvisioning">
		<property name="commandExecutionEncoder" ref="protobufExecutionEncoder"/>
		<property name="commandDeliveryProvider" ref="mqttDeliveryProvider"/>
		<property name="registrationManager" ref="protobufRegistrationManager"/>
		<property name="deviceEventProcessors">
			<list>
				<ref bean="protbufEventProcessor"/>
				<ref bean="jsonEventProcessor"/>
			</list>
		</property>
	</bean>

This block of configuration elements configures how SiteWhere communicates with connected devices. In the
default configuration shown above, outbound device commands are encoded using protocol buffers, then 
delivered to a device via MQTT. Inbound events/responses from devices are delivered via two channels. One
channel is pulled from an inbound MQTT topic and decoded via protocol buffers. The other channel is
pulled from another inbound MQTT topic and decoded as JSON which is expected in a pre-determined format.
As you can see, it is easy enough to substitute your own custom encoding methods or transports. SiteWhere 
provides core service provider interfaces(SPIs) for all of the major provisioning components. For the rest
of the tutorial, we will assume that you are using the default configuration.

-----------------
Client Processing
-----------------
Each connected device needs to be able to receive commands from SiteWhere and react to them. As mentioned
earlier, SiteWhere allows you to use any transport or encoding you wish, but defaults to protocol buffers 
over MQTT. Using the **.proto** file generated by SiteWhere, you could manually generate bindings for any
supported language, but it's much easier to start with a reasonable scaffolding and just make the changes
needed to implement the commands you need. SiteWhere offers a base Java agent project that provides a nice
starting point for creating a custom Java client. Agents in other languages will be supported soon, but
it's not hard to develop one from scratch considering much of the code is generated for you. First, take
a look at the following project on GitHub:

	https://github.com/reveal-technologies/sitewhere-tools
	
This project contains a module called **sitewhere-java-agent** which provides the scaffolding for a custom
Java agent that can be executed from the command line of any platform that supports Java. In our example, we
will be using a Raspberry Pi to communicate with SiteWhere in the cloud.

----------------------------
Download and Build the Agent
----------------------------
Before moving ahead, verify that you have the required development tools installed. You will need Eclipse 
and Maven installed as detailed in the `development guide <../development.html>`_. Import the GitHub 
project for **sitewhere-tools** into Eclipse, then build it in Maven to make sure that the dependencies 
are downloaded. If the build is successful, a jar named **sitewhere-java-agent-0.9.4.jar** should be 
generated in the **sitewhere-java-agent/target** directory.

By default, the agent uses the **.proto** generated based on the **Raspberrry Pi Default** spec 
included in the SiteWhere sample data. To test the jar,  copy it to the device you want to test from 
(this could be your local desktop or a device such as the Raspberry Pi). The jar 
contains everything needed to run the client, so it can be executed directly
from the command line. It expects a file named **config.properties** to be located in the same directory
as the jar. The propertes file contains the settings for the device hardware id, device specification token,
and details about the location of your cloud instance of SiteWhere.

The default config.properties looks below:

.. code-block:: properties

	mqtt.hostname=sitewhere-aws
	command.processor.classname=com.example.ExampleCommandProcessor
	device.hardware.id=123-TEST-439829343897429
	device.specification.token=7dfd6d63-5e8d-4380-be04-fc5c73801dfb

The default settings assume you are using the sample data included with SiteWhere and are running SiteWhere 
in the cloud as shown in the EC2 guide.

If you have not already started a local or cloud instance of SiteWhere, do so now. Details on starting a
cloud instance can be found `here <../cloud/amazon_ec2.html>`_.

-----------------
Running the Agent
-----------------
To run the agent, execute the following from the command line:

	java -jar sitewhere-java-agent-0.9.4.jar
	
Assuming the settings in **config.properties** are correct, the agent should connect to the remote
SiteWhere instance via MQTT and register with the server. A response should be echoed to the console
to indicate whether the device was already registered or not:

.. image:: /_static/images/tutorials/provisioning/agent-startup.png
   :width: 70%
   :alt: SiteWhere Agent Startup
   :align: center
   
-------------------------
Invoking a Device Command
-------------------------
Now that the agent is running, go back to the SiteWhere admin UI, click on the **Sites** tab at the top
of the page. Click the green arrow to navigate into the site detail page. There should be a new 
**Unassociated Device** entry in the assignments list as shown below. This is the device that was dynamically registered
when the agent connected.

.. image:: /_static/images/tutorials/provisioning/device-registered.png
   :width: 100%
   :alt: Device Registered
   :align: left

Click on the green arrow at the right of the entry for the unassociated device, which will navigate to the
assignment detail page. Click on the **Command Invocations** tab at the top. Click the **Invoke Command**
button to open the dialog and choose **helloWorld** from the dropdown of commands available.

.. image:: /_static/images/tutorials/provisioning/invoke-command.png
   :width: 100%
   :alt: Invoke a Command
   :align: left

Enter a greeting and check the checkbox for the *loud* parameter (which uppercases the response), then click
the **Invoke** button to send the command to the device. You should see a new command invocation appear in
the list for the assignment and you should see output in the agent console to indicate the command was invoked.

-------------------------
Command Response Handling
-------------------------
When a command is executed, the receiving device may issue a response to the command. This may be in the
form of one of the standard device events -- a location, alert, or measurements -- or it can be an
acknowledgement (*ack*). In the case of an ack, a message of the device's choosing is sent back to SiteWhere.
In any of the events, there is an optional *originator* field that may be passed. Commands coming in 
from SiteWhere pass the originating event id as part of the payload. If a response event passes that id in 
the originator field, the response will be associated with the original command invocation. If you look
at the list of events for a device in the **Command Invocations** tab, there is an *info* icon at the 
right edge of each entry. Clicking on that icon will show details on the command that was invoked:

.. image:: /_static/images/tutorials/provisioning/invocation-details.png
   :width: 100%
   :alt: Invocation Details
   :align: left

Clicking on the **Responses** tab shows any responses that have come in that were associated with the
invocation:

.. image:: /_static/images/tutorials/provisioning/responses.png
   :width: 100%
   :alt: Invocation Responses
   :align: left


