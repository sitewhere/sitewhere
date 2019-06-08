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

if (execution.command.name == 'serialPrintln') {
    def message = execution.parameters['message']
    logger.info "Attempting serial println with message '" + message + "'"
    return (new String("{'command': 'print', 'message': '" + message + "'}")).getBytes()
}

return (new String("{'command': 'unknown'}")).getBytes()
