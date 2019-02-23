// Available variable bindings:
// ----------------------------
// context: (IDeviceEventContext) Context information associated with device event.
// event: (IDeviceEvent) Current device event being processed by connector.
// logger: (Logger) Supports output to system logs.
// ----------------------------
// return value (boolean): Return true to filter event.
// ----------------------------

logger.info 'Processing Groovy event filter ...'
return true
