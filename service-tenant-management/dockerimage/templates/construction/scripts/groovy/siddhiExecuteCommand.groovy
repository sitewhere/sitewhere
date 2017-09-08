// Get assignment token from event.
def assignment = event.getData(0)

logger.info("About to execute command to turn on all lights for: " + assignment)

Map<String, String> parameters = new HashMap<String, String>();
parameters.put("itemName", "Lights");
parameters.put("command", "ON");
actions.sendCommand(assignment, "sendOnOffCommand", parameters);