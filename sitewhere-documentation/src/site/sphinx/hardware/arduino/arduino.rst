=========================================
 SiteWhere Arduino Integration Libraries
=========================================
SiteWhere provides libraries that support many devices from the popular 
`Arduino <http://www.arduino.cc/>`_ platform, including full round-trip
MQTT support for devices such as the `Mega 2560 <http://arduino.cc/en/Main/arduinoBoardMega2560>`_.
Other Arduino devices such as the `Uno <http://arduino.cc/en/Main/arduinoBoardUno>`_ 
have very limited memory and can only support device events via the REST APIs. This document
will provide a comprehensive example of developing applications with full 
round-trip capabilities.

---------------------------
 Required Arduino Hardware
---------------------------
In order to interact with SiteWhere, a few pieces of hardware are required. They include:

+----------------------+-----------------------------------------------------------------------+
| Hardware             | Description                                                           |
+======================+=======================================================================+
| Arduino Board        | SiteWhere supports round-trip MQTT interactions on                    |
|                      | devices such as the Mega 2560 that offer plenty of                    |
|                      | memory for both programming and SRAM. On smaller                      |
|                      | devices such as the Uno, the combination of networking                |
|                      | libraries, MQTT support, and SiteWhere code stretch                   |
|                      | the devices to their limits before user code comes                    |
|                      | into play. These devices are not recommended.                         |
+----------------------+-----------------------------------------------------------------------+
| Network Hardware     | Any network hardware that supports the Arduino                        |
|                      | `Client <http://arduino.cc/en/Reference/ClientConstructor>`_          |
|                      | interface should should work with the libraries. Components           |
|                      | that implement the interface include the                              |
|                      | `Ethernet Shield <http://arduino.cc/en/Main/ArduinoEthernetShield>`_  |
|                      | and `WiFi Shield <http://arduino.cc/en/Main/ArduinoWiFiShield>`_.     |
+----------------------+-----------------------------------------------------------------------+
| Maker Kit            | A kit that includes wires, sensors, actuators, and other              |
|                      | components is needed for building anything more than the most         |
|                      | basic projects. Many companies offer                                  |
|                      | `starter kits <http://arduino.cc/en/Main/ArduinoStarterKit>`_         |
|                      | with most of the commonly used components.                            |
+----------------------+-----------------------------------------------------------------------+

---------------------------------------
 Preparing the Development Environment
---------------------------------------
Arduino offers an open source development environment that makes it easy to build and deploy
code to Arduino devices. SiteWhere offers libraries that plug in to the development environment,
simplifying the process of connecting to and interacting with a remote SiteWhere instance.

Download the Arduino IDE
------------------------
The Arduino development environnment may be downloaded from the following URL:

	http://arduino.cc/en/main/software
	
Generally, the beta versions (1.5.x) are stable and offer compatibility with the latest devices.

Download the SiteWhere Libraries
--------------------------------
The SiteWhere libraries are written in C and may be plugged directly into the Arduino development
environment. They include examples for some of the common tasks in connecting devices to SiteWhere.
The libraries are available on GitHub in both source and packaged forms at:

	https://github.com/reveal-technologies/sitewhere-arduino
	
The latest stable code is always available in the *master* branch. The libraries are available in
zip archive format for easy installation into the Arduino IDE. Download both the
*SiteWhere.zip* and *PubSubClient.zip* archives from the root of the repository. The PubSubClient
library is an open source MQTT library for Arduino available here:

	https://github.com/knolleary/pubsubclient
	
The SiteWhere version is identical to the version above except that the MQTT maximum packet size
has been increased from 128 bytes to 512 bytes to accomodate larger binary messages.

Installing the Libraries
------------------------
Follow the `library installation <http://arduino.cc/en/Guide/Libraries>`_ instuctions provided 
with the IDE to install both the SiteWhere and PubSubClient libraries. If the libraries
were installed successfully, you can click **File > Examples** and you will see entries at the
bottom of the list for SiteWhere and PubSubClient.

Assembling the Hardware
-----------------------
The hardware shown below is typical of an Arduino/SiteWhere setup. It includes and Arduino Mega 2560
board with an Ethernet shield attached for network communications. 

.. image:: /_static/images/hardware/arduino/arduino-boards.png
   :width: 100%
   :alt: Arduino Hardware
   :align: left

--------------------------------
 Starting with a Simple Example
--------------------------------
Much of the core code for a SiteWhere/Arduino project is the same regardless of whether you are
creating a simple project or something more advanced. The code below provides an example of 
configuring the various parameters for your device and how it connects to SiteWhere. It 
demonstrates connecting to SiteWhere via an MQTT broker, sending and processing registration, 
then sending a simple event at a given interval. Rather than copy/pasting the code, you can open
it from the examples menu in the IDE. Click **File > Examples > SiteWhere > BasicEvents** to 
open it in an editor:

.. literalinclude:: BasicEvents.ino
   :language: c

TCP/IP Settings
---------------
The default SiteWhere messaging configuration sends encoded messages over MQTT, which
requires TCP/IP as a transport. The example code demonstrates configuring an Ethernet shield
to connect to the remote MQTT broker. The settings for MAC address and MQTT broker IP provide
everything required since we are using DHCP to get an IP address for the Arduino device.

.. code-block:: c

	byte mac[]  = { 0xDE, 0xED, 0xBA, 0xFE, 0xFE, 0xED };
	byte mqtt[] = { 192, 168, 1, 68 };

If using a non-standard MQTT port, you will need to update the number in the following line from
the default value of 1883:

.. code-block:: c

	PubSubClient mqttClient(mqtt, 1883, callback, ethClient);

Device Identification
---------------------
Each device communicating with SiteWhere needs a unique identifier referred to as a *hardware id*.
Also, SiteWhere needs to understand the type of hardware it is communicating with, which is 
indicated by a *specification token* passed on registration. Device specifications may be created
via the administrative console or via REST services. When a device self-registers, SiteWhere
uses the hardware id and device specification to create a new device instance in the system.

.. code-block:: c

	/** Unique hardware id for this device */
	static char* hardwareId = "123-ARDUINO-TEST";

	/** Device specification token for hardware configuration */
	static char* specificationToken = "417b36a8-21ef-4196-a8fe-cc756f994d0b";

MQTT Topic Names
----------------
SiteWhere is configured with a default strategy for routing MQTT traffic to deliver events
from devices and send commands to devices. The default setup has a single inbound topic for
all device events since identifying information is passed in the event packets themselves. For outbound
command traffic, SiteWhere uses two separate topics per device; one for system command traffic such
as registration acknowledgement and another for custom commands that may be defined on a per-specification
basis. **NOTE: This is not a hard-coded configuration.** It is very easy to create your own strategy
for how inbound and outbound traffic are handled. For instance, you could have a single topic per
device and pass an indicator to differentiate between system and custom messages. The constants below
indicate the topic names to be used for communicating with SiteWhere using the default strategy:

.. code-block:: c

	/** Outbound MQTT topic */
	static char* outbound = "SiteWhere/input/protobuf";

	/** Inbound custom command topic */
	static char* command = "SiteWhere/commands/123-ARDUINO-TEST";

	/** Inbound system command topic */
	static char* system = "SiteWhere/system/123-ARDUINO-TEST";

Connecting and Registering
--------------------------
The code below provides an example of getting an IP address from DHCP, connecting to MQTT, then registering
the device with SiteWhere. If you are using hardware other than the standard Ethernet shield, you will 
need to update the connection code to work with the specifics of your hardware. The general pattern is:

1) Get a network connection (e.g. get an IP address from DHCP).
2) Connect to MQTT broker (taken care of by *connect()* method in PubSubClient).
3) Subscribe to MQTT topics for receiving commands from SiteWhere.
4) Send a regsitration request to either regsiter or confirm registration before sending data.

.. code-block:: c

  if (Ethernet.begin(mac) == 0) {
    Serial.println(F("Unable to get ethernet address."));
    return;
  }
  Serial.println(F("Connected to ethernet."));
  if (mqttClient.connect(clientName)) {
    Serial.println(F("Connected to MQTT."));
    
    // Subscribe to command topics.
    mqttClient.subscribe(command);
    mqttClient.subscribe(system);

    // Register device with SiteWhere.
    unsigned int len = 0;
    if (len = sw_register(hardwareId, specificationToken, buffer, sizeof(buffer), NULL)) {
      mqttClient.publish(outbound, buffer, len);
    }
    Serial.println("Sent registration.");
  } else {
    Serial.println(F("Unable to connect to MQTT."));
  }

Responding to Registration
--------------------------
After handling device registration, SiteWhere sends a *registration acknowledgement (ack)* back
to the device, indicating the registration status. If no device with the given hardware id
existed before the current registration, SiteWhere will create one and indicate this was a new reqistration.
Otherwise, SiteWhere will indicate that an existing registration is being used. If there are
errors in the registration process, SiteWhere sends an error indicator and an explanation of
what failed. The code below parses a registration response:

.. code-block:: c

  // Read header to find what type of command follows.
  if (pb_decode_delimited(&stream, Device_Header_fields, &header)) {
  
    // Handle a registration acknowledgement.
    if (header.command == Device_Command_REGISTER_ACK) {
      Device_RegistrationAck ack;
      if (pb_decode_delimited(&stream, Device_RegistrationAck_fields, &ack)) {
        if (ack.state == Device_RegistrationAckState_NEW_REGISTRATION) {
          Serial.println(F("Registered new device."));
          registered = true;
        } else if (ack.state == Device_RegistrationAckState_ALREADY_REGISTERED) {
          Serial.println(F("Device was already registered."));
          registered = true;
        } else if (ack.state == Device_RegistrationAckState_REGISTRATION_ERROR) {
          Serial.println(F("Error registering device."));
        }
      }
    }
  } else {
    Serial.println(F("Unable to decode system command."));
  }

Note that this code is using the default SiteWhere Google Protocol Buffers command structure based on the 
`proto <https://github.com/reveal-technologies/sitewhere/blob/master/sitewhere-protobuf/proto/sitewhere.proto>`_
that acts as an agreement between the device and Sitewhere server. Other message formats can be used 
by adjusting the server-side configuration.

Processing Events
-----------------
The primary intent of this example is to send a mocked event to SiteWhere at a given interval. Now that
the device is connected and registered, the SiteWhere event APIs can be used to easily send data to the
server. The event processing takes place in the main processing *loop()* function and should be guarded
by a flag that only allows interactions once registration has completed:

.. code-block:: c

  mqttClient.loop();
  
  /** Only send events after registered and at most every five seconds */
  if ((registered) && ((millis() - lastEvent) > 5000)) {
    unsigned int len = 0;
    if (len = sw_alert(hardwareId, "arduino.alive", "The Arduino is alive!", NULL, buffer, sizeof(buffer), NULL)) {
      mqttClient.publish(outbound, buffer, len);
      Serial.println(F("Sent alert."));
    }
    lastEvent = millis();
  }

The first statement calls the logic on the PubSubClient that will handle inbound processing callbacks for 
commands from SiteWhere. The rest of the code verifies that the device is registered, limits code execution
to a given time interval, then sends a mocked alert event to SiteWhere. The alert will be recorded to the device
that was previously registered based on hardware id. In addition to the *sw_alert()* function, there are 
others for sending locations, measurements, and other data to SiteWhere. See the
`library header files <https://github.com/reveal-technologies/sitewhere-arduino/blob/master/SiteWhere/sitewhere.h>`_ 
for a complete list.
