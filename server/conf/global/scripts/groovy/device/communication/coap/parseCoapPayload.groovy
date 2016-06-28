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
	logger.info("Added device measurements request.")
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
