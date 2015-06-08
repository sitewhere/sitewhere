============================
Using SiteWhere with openHAB
============================
SiteWhere supports integration with the popular `openHAB <http://www.openhab.org/>`_ open
home automation project. The SiteWhere openHAB plugin (available on the
`downloads <http://www.sitewhere.org/downloads>`_ page) allows openHAB data to 
be stored to SiteWhere as if the openHAB system was a composite device. 
SiteWhere (beginning with version 1.0.4) includes a device specification 
for an openHAB virtual device, allowing commands to be sent back to openHAB as the
result of SiteWhere processing.

Installing the Plugin
---------------------
Begin by installing openHAB as detailed in their `getting started <http://www.openhab.org/gettingstarted.html>`_
guide. Install the runtime, addons, and demo application. Start the system and open the demo application
to verify that the installation was successful.

Download the `SiteWhere openHAB Addon <https://s3.amazonaws.com/sitewhere-openhab/org.openhab.persistence.sitewhere-1.7.0-SNAPSHOT.jar>`_
and copy it into the openHAB addons folder. In the openHAB **configurations** folder, create a new folder named **sitewhere**.
Copy the contents of the `sitewhere.json <https://s3.amazonaws.com/sitewhere-openhab/sitewhere.json>`_ file into the
**sitewhere** folder. This file is used to configure the SiteWhere addon and will look something like the
content below:

.. code-block:: json

   {
      "connection": {
         "restApiUrl": "http://localhost:9090/sitewhere/api/",
         "restApiUsername": "admin",
         "restApiPassword": "password",
         "mqttHost": "localhost",
         "mqttPort": 1883
      },
      "defaultHardwareId": "123-OPENHAB-234908324",
      "mappings": [
         { "itemName": "yyy", "hardwareId": "123" },
         { "itemName": "zzz", "hardwareId": "456" }
      ]
   } 
   
The **connection** section contains information on connecting to SiteWhere. Note that both SiteWhere and openHAB
by default run on port 8080, so the port will need to be changed if both are running on the same machine. To
change the port for SiteWhere, open the **conf/server.xml** file and look for the following:

.. code-block:: xml

    <Connector port="8080" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="8443" />

Change the port to another value such as 9090. Make sure that the **restApiUrl** in the openHAB configuration
points to the correct port. The openHAB addon uses the SiteWhere REST APIs for some operations, but also uses 
an MQTT connection to send a receive data. 

The MQTT values should be configured to point to the MQTT broker SiteWhere is connected to. All data sent between
openHAB and SiteWhere is sent across the MQTT connections.

The **defaultHardwareId** value provides an association between the openHAB instance and a SiteWhere device.
Once connected, if no device exists in SiteWhere with the given hardware id, a new openHAB virtual device will
be registered under that id. All data sent from the openHAB instance will be recorded under the virtual
device. If more than one openHAB instance is connecting to SiteWhere, different hardware ids should be used for
each instance. SiteWhere can scale to support thousands or even millions of openHAB instances running
concurrently.

The **mappings** section is not currently used, but future versions of the addon will allow items to be 
mapped into devices within the top-level openHAB composite device. In the meantime, all data for an 
openHAB instance shows up under a single SiteWhere device with extra metadata in the events to indicate
which item generated the event.

In order for openHAB to send all events to SiteWhere, add a file named **sitewhere.persist** in the
**configurations/persistence** folder. This file is in the standard openHAB persistence format. The
configuration below will send all events to SiteWhere:

.. code-block:: text

   Strategies {
      everyHour   : "0 0 * * * ?"
      everyDay    : "0 0 0 * * ?"
   
      // if no strategy is specified for an item entry below, the default list will be used
      default = everyChange
   }
   
   /* 
    * Each line in this section defines for which item(s) which strategy(ies) should be applied.
    * You can list single items, use "*" for all items or "groupitem*" for all members of a group
    * item (excl. the group item itself).
    */
   Items {
      * : strategy = everyChange, everyDay, restoreOnStartup
   }


SiteWhere Configuration
-----------------------
If using the default configuration file provided with SiteWhere, no changes are needed to
connect to openHAB. The openHAB addon uses the 
`SiteWhere Java agent <https://github.com/sitewhere/sitewhere-tools/tree/master/sitewhere-java-agent>`_
to interact with SiteWhere over MQTT. If not using the default SiteWhere configuration, the section below
should be copied into the *<sw:event-sources>* block. 

.. code-block:: xml

   <!-- Event source for protobuf messages over MQTT -->
   <sw:mqtt-event-source sourceId="protobuf" hostname="localhost"
      port="1883" topic="SiteWhere/input/protobuf">
      <sw:protobuf-event-decoder/>
   </sw:mqtt-event-source>

In order for SiteWhere to be able to send commands to openHAB, a command destination needs to be
configured to send output back over MQTT. The default configuration uses the following:

.. code-block:: xml

   <!-- Device command routing -->
   <sw:command-routing>
      <sw:specification-mapping-router defaultDestination="default">
         <sw:mapping specification="d2604433-e4eb-419b-97c7-88efe9b2cd41"
            destination="hybrid"/>
         <sw:mapping specification="7dfd6d63-5e8d-4380-be04-fc5c73801dfb"
            destination="hybrid"/>
         <sw:mapping specification="5a95f3f2-96f0-47f9-b98d-f5c081d01948"
            destination="hybrid"/>
      </sw:specification-mapping-router>
   </sw:command-routing>
   
   <!-- Outbound command destinations -->
   <sw:command-destinations>
   
      <!-- Delivers commands via MQTT -->
      <sw:mqtt-command-destination destinationId="default"
         hostname="localhost" port="1883">
         <sw:protobuf-command-encoder/>
         <sw:hardware-id-topic-extractor commandTopicExpr="SiteWhere/commands/%s"
            systemTopicExpr="SiteWhere/system/%s"/>
      </sw:mqtt-command-destination>
   
      <!-- Used for devices that expect hybrid protobuf/Java invocations -->
      <sw:mqtt-command-destination destinationId="hybrid"
         hostname="localhost" port="1883">
         <sw:java-protobuf-hybrid-encoder/>
         <sw:hardware-id-topic-extractor commandTopicExpr="SiteWhere/commands/%s"
            systemTopicExpr="SiteWhere/system/%s"/>
      </sw:mqtt-command-destination>
   
   </sw:command-destinations>

Based on the default data loaded the first time SiteWhere starts, there will be a specification with
UUID **5a95f3f2-96f0-47f9-b98d-f5c081d01948** for an openHAB virtual device. Note that the above
configuration routes commands for that specification over MQTT as expected by the openHAB addon.

Testing the Installation
------------------------
Once the **sitewhere.json** configuration has been set up, start the openHAB server. Messages in
the openHAB log should indicate that the agent was able to connect with SiteWhere as shown below:

.. code-block:: text

   2015-06-08 11:24:49.091 [INFO ] [.s.internal.SiteWhereActivator] - SiteWhere persistence bundle has been started.
   2015-06-08 11:24:49.260 [INFO ] [.SiteWhereConfigurationManager] - Loaded SiteWhere configuration file from: C:\OpenHAB\openHAB-1.6.2\configurations\sitewhere\sitewhere.json
   2015-06-08 11:24:49.858 [INFO ] [.i.SiteWherePersistenceService] - Connected to SiteWhere 1.0.4 CE
   2015-06-08 11:24:49.863 [WARN ] [com.sitewhere.agent.Agent     ] - Using default outbound SiteWhere MQTT topic: SiteWhere/input/protobuf
   2015-06-08 11:24:49.863 [WARN ] [com.sitewhere.agent.Agent     ] - Using default inbound SiteWhere MQTT topic: SiteWhere/system/123-OPENHAB-234908324
   2015-06-08 11:24:49.868 [WARN ] [com.sitewhere.agent.Agent     ] - Using default inbound command MQTT topic: SiteWhere/commands/123-OPENHAB-234908324
   2015-06-08 11:24:50.763 [INFO ] [.p.s.i.OpenHabCommandProcessor] - SiteWhere created a new registration for openHAB device.

After a few seconds, values for the various demo items should start to be persisted into SiteWhere.
Open the SiteWhere administrative console, click on the default site, then click on the green arrow next
to the **openHAB Virtual Device** at the top of the list. Click on the **Measurements** tab, and
data for openHAB items should appear in the list as shown below:

.. image:: /_static/images/tutorials/openhab/events.png
   :width: 100%
   :alt: openHAB Events
   :align: left

Sending Commands to openHAB Items
---------------------------------
SiteWhere can also send data to openHAB to affect its managed items. For instance, to turn on the 
light switch in the first floor bathroom ceiling, we can send a command via the SiteWhere 
administrative interface. From the assignment page for the device, click on the
**Command Invocations** tab. Click the **Invoke Command** button and leave the dropdown
with **sendOnOffCommand** chosen. Set **ItemName** to **Light_FF_Bath_Ceiling** and
the **command** to **ON**, then click **Invoke**. 

.. image:: /_static/images/tutorials/openhab/command.png
   :width: 100%
   :alt: openHAB Events
   :align: left

An entry will appear in the openHAB log to indicate the light switch state has changed, and 
the user interface for the demo will reflect the light switch has been turned on.

Using Complex Event Processing with openHAB
-------------------------------------------
SiteWhere integrates with `Siddhi <https://github.com/wso2/siddhi>`_ for complex event processing and may be easily 
configured to integrate the functionality with openHAB. For instance, consider a use case where the light
switch in the child's bedroom can be used as an SOS device. If the light switch is flipped more than three times
within a ten second period, all of the lights in the house should come on. The logic can be configured by
adding the following XML to the SiteWhere configuration:

.. code-block:: xml

   <sw:siddhi-event-processor>
               
      <sw:siddhi-query
         selector="from every e1 = AlertStream[type == 'openhab.onoff:Light_FF_Child_Ceiling' and message == 'ON'] -> e2 = AlertStream[type == 'openhab.onoff:Light_FF_Child_Ceiling' and message == 'ON' and e1.assignment == assignment] -> e3 = AlertStream[type == 'openhab.onoff:Light_FF_Child_Ceiling' and message == 'ON' and e2.assignment == assignment] within 10 sec select e1.assignment insert into SOS">
         <sw:groovy-stream-processor scriptPath="siddhiCreateAlert.groovy"
            stream="SOS"/>
      </sw:siddhi-query>
   
   </sw:siddhi-event-processor>

The logic watches the **AlertStream** which is a real time stream of alerts processed by SiteWhere. The logic triggers on
alerts with type **openhab.onoff:Light_FF_Child_Ceiling** which is the indicator for alerts from the light switch in 
the child's bedroom. The event matches if the alert message is ON. The first match is followed by two more matches with
the added stipulation that the alerts originate from the same device assignment. Otherwise, with multiple openHAB 
instances, children in three different houses could flip the light switch and trigger an alert. If the trigger condition
is met, a new event is sent to the **SOS** stream. The *<sw:groovy-stream-processor>* allows a 
`Groovy <http://www.groovy-lang.org/>`_ script to be executed to process the SOS events. It looks for a script
named **siddhiCreateAlert.groovy** in the **sitewhere/conf/sitewhere/groovy** folder. The script below will
create a new SiteWhere alert as the result of the SOS event:

.. code-block:: java

   import com.sitewhere.spi.device.event.*;
   import com.sitewhere.rest.model.device.event.request.*;
   
   // Get assignment token from event.
   def assignment = event.getData(0)
   
   logger.info("SOS detected in child's bedroom!")
   
   // Build request for creating a new alert.
   def alert = new DeviceAlertCreateRequest()
   alert.setSource(AlertSource.valueOf("System"))
   alert.setLevel(AlertLevel.valueOf("Warning"))
   alert.setType('SOS')
   alert.setMessage("SOS detected in child's bedroom!")
   alert.setEventDate(new java.util.Date())
   
   // Create new device alert using device management API.
   devices.addDeviceAlert(assignment, alert);

Note the the Groovy script can be changed while the SiteWhere server is running and it will be 
recompiled on the fly. Bring up the openHAB demo interface and flip the switch in the child's
bedroom off and back to on three times in less than 10 seconds. A system generated alert of
type SOS will show up in the alerts list.

Rather than creating an alert, the system can also take action based on the SOS event and
fire an event back to openHAB. In this case, all lights in the home should be turned on.
Replace the content of the above script with the following content to create the command:

.. code-block:: java

   // Get assignment token from event.
   def assignment = event.getData(0)
   
   logger.info("About to execute command to turn on all lights for: " + assignment)
   
   Map<String, String> parameters = new HashMap<String, String>();
   parameters.put("itemName", "Lights");
   parameters.put("command", "ON");
   actions.sendCommand(assignment, "sendOnOffCommand", parameters);

Now go back to the openHAB interface and flip the switch three times to trigger the event
again. Note that SiteWhere sends a command and all of the lights in the home are turned on.

Next Steps
----------
Now that openHAB data can be pulled into SiteWhere and SiteWhere can fire events back into openHAB,
there are many other possibilities that open up. For instance:

1. Events from openHAB can be indexed in Apache Solr for analytics. 
2. Events can be fired into Mule and sent to Salesforce or other cloud providers.
3. Events can trigger SMS messages using SiteWhere/Twilio integration.
4. Events can be fired onto Azure EventHub for CEP and Machine Learning applications.

The list above only scratches the surface of what can be done. Play around with combining the
building blocks SiteWhere provides and take your IoT applications to the next level!
