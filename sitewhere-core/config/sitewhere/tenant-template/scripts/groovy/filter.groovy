import com.sitewhere.spi.device.event.*;

// Allow alerts from any device.
if (event.eventType == DeviceEventType.valueOf("Alert")) {
	return true;
}

// Allow devices in a comma-delimited list.
def ogDetailDevices = targetAssignment?.metadata['og-device-detail']?.split(',');
for (hwid in ogDetailDevices) {
	if (eventDevice.hardwareId.equals(hwid)) {
		logger.info('Event in devices include list. Allowing');
		return true;
	}
}
return false;