// Available variable bindings:
// ----------------------------
// execution: (IDeviceCommandExecution) Command execution information.
// nesting: (IDeviceNestingContext) Device information including nesting details.
// assignment: (IDeviceAssignment) Assignment associated with invocation event.
// logger: (Logger) Supports output to system logs.
// ----------------------------
// return value (byte[]): Binary output sent to transport.
// ----------------------------

logger.info 'Building command execution...'
return (new String("output")).getBytes()
