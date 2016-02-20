import com.sitewhere.spi.device.event.*;

//
// Example of using Groovy script as outbound processor filter.
//

// Only allow alerts!
if (event.eventType == DeviceEventType.valueOf("Alert")) {
	return true;
}
return false;