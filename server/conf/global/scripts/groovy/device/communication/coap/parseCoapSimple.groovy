import com.sitewhere.common.*;
import com.sitewhere.rest.model.device.communication.*;
import com.sitewhere.rest.model.device.event.request.*;

def type = metadata["eventType"]
def hwid = metadata["hardwareId"]

// Parse device registration message in format "hwid=xxx,spec=yyy,site=zzz".
if (DeviceRequest.Type.RegisterDevice.name().equals(type)) {
	// Create registration object from parsed fields.
	def fields = parseFields(new String(payload))
	DeviceRegistrationRequest registration = new DeviceRegistrationRequest();
	registration.setHardwareId(fields.get("hwid"));
	registration.setSpecificationToken(fields.get("spec"));
	registration.setSiteToken(fields.get("site"));
	addMetadata(registration, fields);
	
	DecodedDeviceRequest<DeviceRegistrationRequest> request = new DecodedDeviceRequest<DeviceRegistrationRequest>()
	request.setHardwareId(registration.getHardwareId())
	request.setRequest(registration)
	events.add(request)
	logger.info("Added device measurements request.")
}

// Parse device measurements message in format "mx1=123.0,mx2=234.1,..."
else if (DeviceRequest.Type.DeviceMeasurements.name().equals(type)) {
	def fields = parseFields(new String(payload))
	DeviceMeasurementsCreateRequest mxs = new DeviceMeasurementsCreateRequest();
	for (String key : fields.keySet()) {
		mxs.addOrReplaceMeasurement(key, Double.parseDouble(fields.get(key)));
	}
	addMetadata(mxs, fields);
	
	DecodedDeviceRequest<DeviceMeasurementsCreateRequest> request = new DecodedDeviceRequest<DeviceMeasurementsCreateRequest>()
	request.setHardwareId(hwid)
	request.setRequest(mxs)
	events.add(request)
	logger.info("Added device measurements request for ${hwid}.")
}

// Parse device alert message in format "type=xxx,message=yyy"
else if (DeviceRequest.Type.DeviceAlert.name().equals(type)) {
	def fields = parseFields(new String(payload))
	DeviceAlertCreateRequest alert = new DeviceAlertCreateRequest();
	alert.setType(fields.get("type"));
	alert.setMessage(fields.get("message"));
	addMetadata(alert, fields);
	
	DecodedDeviceRequest<DeviceAlertCreateRequest> request = new DecodedDeviceRequest<DeviceAlertCreateRequest>()
	request.setHardwareId(hwid)
	request.setRequest(alert)
	events.add(request)
	logger.info("Added device alert request for ${hwid}.")
}

// Parse device ack message in format "orig=xxx,response=yyy"
else if (DeviceRequest.Type.Acknowledge.name().equals(type)) {
	def fields = parseFields(new String(payload))
	DeviceCommandResponseCreateRequest ack = new DeviceCommandResponseCreateRequest();
	ack.setOriginatingEventId(fields.get("orig"));
	ack.setResponse(fields.get("response"));
	addMetadata(ack, fields);
	
	DecodedDeviceRequest<DeviceCommandResponseCreateRequest> request = new DecodedDeviceRequest<DeviceCommandResponseCreateRequest>()
	request.setHardwareId(hwid)
	request.setRequest(ack)
	events.add(request)
	logger.info("Added device ack request for ${hwid}.")
}

// Parse device location message in format "lat=12.3,lon=23.4,ele=0.0"
else if (DeviceRequest.Type.DeviceLocation.name().equals(type)) {
	def fields = parseFields(new String(payload))
	DeviceLocationCreateRequest location = new DeviceLocationCreateRequest();
	location.setLatitude(Double.parseDouble(fields.get("lat")));
	location.setLongitude(Double.parseDouble(fields.get("lon")));
	location.setElevation(Double.parseDouble(fields.get("ele")));
	addMetadata(location, fields);
	
	DecodedDeviceRequest<DeviceLocationCreateRequest> request = new DecodedDeviceRequest<DeviceLocationCreateRequest>()
	request.setHardwareId(hwid)
	request.setRequest(location)
	events.add(request)
	logger.info("Added device location request for ${hwid}.")
}

// Parse fields in format "field1=x,field2=y" into a map indexed by field name.
Map<String, String> parseFields(String input) {
	Map<String, String> result = new HashMap<String, String>()
	String[] pairs = input.split(",")
	for (String pair : pairs) {
		String[] parts = pair.split("=")
		result.put(parts[0], parts[1])
	}
	return result
}

// Add metadata by extracting fields starting with 'm:'.
void addMetadata(DeviceEventCreateRequest request, Map<String, String> fields) {
	for (String key : fields.keySet()) {
		if (key.startsWith('m:')) {
		    if (request.getMetadata() == null) {
		    	request.setMetadata(new HashMap<String, String>());
		    }
			String metaKey = key.substring(2);
			String value = fields.get(key);
			request.getMetadata().put(metaKey, value);
		}
	}
}
