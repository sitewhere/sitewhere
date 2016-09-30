import com.sitewhere.common.*;
import com.sitewhere.rest.model.device.communication.*;
import com.sitewhere.rest.model.device.event.request.*;

def type = metadata["eventType"]
def hwid = metadata["hardwareId"]

// Parse device registration message.
if (DeviceRequest.Type.RegisterDevice.name().equals(type)) {
	def registration = MarshalUtils.unmarshalJson(payload, DeviceRegistrationRequest.class)
	DecodedDeviceRequest<DeviceRegistrationRequest> request = new DecodedDeviceRequest<DeviceRegistrationRequest>()
	request.setHardwareId(registration.getHardwareId())
	request.setRequest(registration)
	events.add(request)
	logger.info("Added device registration request.")
}

// Parse device measurements message.
else if (DeviceRequest.Type.DeviceMeasurements.name().equals(type)) {
	def mxs = MarshalUtils.unmarshalJson(payload, DeviceMeasurementsCreateRequest.class)
	DecodedDeviceRequest<DeviceMeasurementsCreateRequest> request = new DecodedDeviceRequest<DeviceMeasurementsCreateRequest>()
	request.setHardwareId(hwid)
	request.setRequest(mxs)
	events.add(request)
	logger.info("Added device measurements request for ${hwid}.")
}

// Parse device alert message.
else if (DeviceRequest.Type.DeviceAlert.name().equals(type)) {
	def alert = MarshalUtils.unmarshalJson(payload, DeviceAlertCreateRequest.class)
	DecodedDeviceRequest<DeviceAlertCreateRequest> request = new DecodedDeviceRequest<DeviceAlertCreateRequest>()
	request.setHardwareId(hwid)
	request.setRequest(alert)
	events.add(request)
	logger.info("Added device alert request for ${hwid}.")
}

// Parse device location message.
else if (DeviceRequest.Type.DeviceLocation.name().equals(type)) {
	def location = MarshalUtils.unmarshalJson(payload, DeviceLocationCreateRequest.class)
	DecodedDeviceRequest<DeviceLocationCreateRequest> request = new DecodedDeviceRequest<DeviceLocationCreateRequest>()
	request.setHardwareId(hwid)
	request.setRequest(location)
	events.add(request)
	logger.info("Added device location request for ${hwid}.")
}

// Parse device acknowledge message.
else if (DeviceRequest.Type.Acknowledge.name().equals(type)) {
	def ack = MarshalUtils.unmarshalJson(payload, DeviceCommandResponseCreateRequest.class)
	DecodedDeviceRequest<DeviceCommandResponseCreateRequest> request = new DecodedDeviceRequest<DeviceCommandResponseCreateRequest>()
	request.setHardwareId(hwid)
	request.setRequest(ack)
	events.add(request)
	logger.info("Added device acknowledge request for ${hwid}.")
}
