import com.sitewhere.spi.device.event.*;
import com.sitewhere.rest.model.device.event.request.*;

// Get assignment token from event.
def assignment = event.getData(0)

logger.info("The person for this assignment may be unconscious! " + assignment)

// Build request for creating a new alert.
def alert = new DeviceAlertCreateRequest()
alert.setLevel(AlertLevel.valueOf("Warning"))
alert.setType('possibly.unconscious')
alert.setMessage('Owner may have fallen or be unconscious!')
alert.setEventDate(new java.util.Date())

// Create new device alert using device management API.
devices.addDeviceAlert(assignment, alert);