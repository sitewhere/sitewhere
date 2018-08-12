import com.sitewhere.rest.model.device.communication.*;
import com.sitewhere.rest.model.device.event.request.*;
import com.sitewhere.spi.device.event.request.*;

// Sanity-check payload.
def message = new String(payload);
def parts = message.split(",");
if (parts.length < 2) {
  logger.error("Invalid parameters")
  return;
}

// Parse type and hardware id.
def type = parts[0]
def hwid = parts[1]

// Handle location event in the form LOC,HWID,LAT,LONG
if ("LOC".equals(type)) {
  if (parts.length < 4) {
    logger.error("Invalid location parameters")
    return
  }
  
  def decoded = new DecodedDeviceRequest<IDeviceLocationCreateRequest>()
  decoded.setHardwareId(hwid);
  
  // Create a location object from the parsed data and added it to the list of decoded events.
  def location = new DeviceLocationCreateRequest()
  location.setLatitude(Double.parseDouble(parts[2]))
  location.setLongitude(Double.parseDouble(parts[3]))
  location.setElevation(0.0)
  location.setEventDate(new java.util.Date())
  decoded.setRequest(location)
  
  events.add(decoded);
}