// Available variable bindings:
// ----------------------------
// context: (IDeviceEventContext) Context information associated with device event.
// event: (IDeviceEvent) Current device event being processed by connector.
// assignment: (IDeviceAssignment) Assignment associated with invocation event.
// device: (IDevice) Information about device related to event.
// logger: (Logger) Supports output to system logs.
// ----------------------------
// return value (byte[]): Binary array containing payload content.
// ----------------------------

String payload = String.format("{ 'deviceToken': '%s' }", device.getToken())
logger.info String.format("HTTP connector using payload:: %s", payload)
return payload.getBytes("UTF-8")
