#include <pb.h>
#include <pb_decode.h>
#include <pb_encode.h>
#include <sitewhere.h>
#include <sitewhere-arduino.pb.h>

#include <SPI.h>
#include <Ethernet.h>
#include <PubSubClient.h>

// Update these with values suitable for your network.
byte mac[]  = { 0xDE, 0xED, 0xBA, 0xFE, 0xFE, 0xED };
byte mqtt[] = { 192, 168, 1, 68 };

/** Callback function header */
void callback(char* topic, byte* payload, unsigned int length);

/** Connectivity */
EthernetClient ethClient;
PubSubClient mqttClient(mqtt, 1883, callback, ethClient);

/** Message buffer */
uint8_t buffer[300];

/** Keeps up with whether we have registered */
bool registered = false;

/** Timestamp for last event */
unsigned long lastEvent = millis();

/** MQTT client name */
static char* clientName = "arduinoClient";

/** Unique hardware id for this device */
static char* hardwareId = "123-ARDUINO-TEST";

/** Device specification token for hardware configuration */
static char* specificationToken = "417b36a8-21ef-4196-a8fe-cc756f994d0b";

/** Outbound MQTT topic */
static char* outbound = "SiteWhere/input/protobuf";

/** Inbound custom command topic */
static char* command = "SiteWhere/commands/123-ARDUINO-TEST";

/** Inbound system command topic */
static char* system = "SiteWhere/system/123-ARDUINO-TEST";

/** Handle MQTT subscription responses */
void callback(char* topic, byte* payload, unsigned int length) {
  if (strcmp(topic, system) == 0) {
    handleSystemCommand(payload, length);
  } else if (strcmp(topic, command) == 0) {
    // No custom commands in this example.
  }
}

/** Handle a system command */
static void handleSystemCommand(byte* payload, unsigned int length) {
  Device_Header header;
  pb_istream_t stream = pb_istream_from_buffer(payload, length);

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
}

/** Set up processing */
void setup() {
  Serial.begin(9600);
  Serial.println(F("Starting up ethernet..."));
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
}

/** Main MQTT processing loop */
void loop() {
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
}