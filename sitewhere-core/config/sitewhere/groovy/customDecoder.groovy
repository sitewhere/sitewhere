// Sanity-check payload.
def parts = payload.split(",");
if (parts.length < 2) {
  logger.error("Invalid parameters")
  return;
}

// Parse type and hardware id.
def type = parts[0]
def hwid = parts[1]

// Create object to hold decoded event data.
def decoded = new com.sitewhere.rest.model.device.provisioning.DecodedDeviceEventRequest()
decoded.setHardwareId(hwid);

// Handle location event in the form LOC,HWID,LAT,LONG
if ("LOC".equals(type)) {
  if (parts.length < 4) {
    logger.error("Invalid location parameters")
    return
  }
  
  // Create a location object from the parsed data and added it to the list of decoded events.
  def location = new com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest()
  location.setLatitude(Double.parseDouble(parts[2]))
  location.setLongitude(Double.parseDouble(parts[3]))
  location.setElevation(0.0)
  location.setEventDate(new java.util.Date())
  decoded.setRequest(location)
  
  events.add(decoded);
}