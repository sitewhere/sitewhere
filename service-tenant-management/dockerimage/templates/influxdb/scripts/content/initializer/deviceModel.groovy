import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.util.*;
import com.vividsolutions.jts.algorithm.*;

// ########### //
// Common Code //
// ########### //
Integer devicesPerSite = 30
Integer mxPerAssignment = 50
Integer locationsPerAssignment = 40
Integer minTemp = 80
Integer warnTemp = 160
Integer errorTemp = 180
Integer criticalTemp = 190
Integer maxTemp = 200

def randomId = { java.util.UUID.randomUUID().toString() }
def randomDeviceToken = { type ->
	'' + ((int)(Math.random() * 100000)) + '-' + type.token.toUpperCase() + '-' + ((int)(Math.random() * 10000000))
}
def randomItem = { items ->
	items.get((int)(Math.random() * items.size()))
}

// ################################### //
// Create Customer Types and Customers //
// ################################### //

// Construction company type.
def companyCustomerType = deviceBuilder.newCustomerType 'construction', 'Construction Company'
companyCustomerType.withDescription 'A company that manages one or more construction areas.' withIcon 'fa-building'
companyCustomerType = deviceBuilder.persist companyCustomerType
logger.info "[Create Customer Type] ${companyCustomerType.name}"

// Subcontractor company type.
def subCustomerType = deviceBuilder.newCustomerType 'subcontractor', 'Subcontractor'
subCustomerType.withDescription 'A subcontractor that works for a company.' withIcon 'fa-truck'
subCustomerType = deviceBuilder.persist subCustomerType
logger.info "[Create Customer Type] ${subCustomerType.name}"

// Acme Construction Company.
def acmeCustomer = deviceBuilder.newCustomer companyCustomerType.token, null, 'acme', 'ACME Construction Company'
acmeCustomer.withDescription 'ACME construction company manages many subcontractors and construction sites.'
acmeCustomer.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/construction.jpg'
acmeCustomer = deviceBuilder.persist acmeCustomer
logger.info "[Create Customer] ${acmeCustomer.name}"

// Subcontractor A.
def subACustomer = deviceBuilder.newCustomer subCustomerType.token, 'acme', 'subA', 'Subcontractor A'
subACustomer.withDescription 'Subcontractor A manages multiple construction sites.'
subACustomer.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/construction.jpg'
subACustomer = deviceBuilder.persist subACustomer
logger.info "[Create Customer] ${subACustomer.name}"

// Subcontractor B.
def subBCustomer = deviceBuilder.newCustomer subCustomerType.token, 'acme', 'subB', 'Subcontractor B'
subBCustomer.withDescription 'Subcontractor B manages multiple construction sites.'
subBCustomer.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/construction.jpg'
subBCustomer = deviceBuilder.persist subBCustomer
logger.info "[Create Customer] ${subBCustomer.name}"

// ################################# //
// Create Area Types, Areas and Zone //
// ################################# //

// Construction area type.
def constAreaType = deviceBuilder.newAreaType 'construction', 'Construction Area'
constAreaType.withDescription 'A construction area.' withIcon 'fa-truck'
constAreaType = deviceBuilder.persist constAreaType
logger.info "[Create Area Type] ${constAreaType.name}"

// Region type.
def regionType = deviceBuilder.newAreaType 'region', 'Region'
regionType.withDescription 'Subsection of the United States.' withIcon 'fa-map'
regionType.withContainedAreaType constAreaType.token
regionType = deviceBuilder.persist regionType
logger.info "[Create Area Type] ${regionType.name}"

// Southeast region.
def seRegion = deviceBuilder.newArea regionType.token, null, 'southeast', 'Southeast Region'
seRegion.withDescription 'Region including the southeastern portion of the United States.'
seRegion.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/areas/region-se.jpg'
seRegion.coord(34.10260138703638, -84.24412965774536) coord(34.101837372446774, -84.24243450164795)
seRegion.coord(34.101517550337825, -84.24091100692749) coord(34.10154953265732, -84.23856675624847)
seRegion.coord(34.10153176473365, -84.23575580120087) coord(34.10409030732968, -84.23689305782318)
seRegion.coord(34.104996439280704, -84.23700034618376) coord(34.10606246444614, -84.23700034618376)
seRegion.coord(34.107691680235604, -84.23690915107727)
seRegion = deviceBuilder.persist seRegion
logger.info "[Create Area] ${seRegion.name}"

// Peachtree construction site.
def ptreeSite = deviceBuilder.newArea constAreaType.token, seRegion.token, 'peachtree', 'Peachtree Construction Site'
ptreeSite.withDescription '''A construction site with many high-value assets that should not be taken offsite. 
The system provides location tracking for the assets and notifies administrators if any of the assets move 
outside of the general site area or into areas where they are not allowed.'''
ptreeSite.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/construction.jpg'
ptreeSite.coord(34.10260138703638, -84.24412965774536) coord(34.101837372446774, -84.24243450164795)
ptreeSite.coord(34.101517550337825, -84.24091100692749) coord(34.10154953265732, -84.23856675624847)
ptreeSite.coord(34.10153176473365, -84.23575580120087) coord(34.10409030732968, -84.23689305782318)
ptreeSite.coord(34.104996439280704, -84.23700034618376) coord(34.10606246444614, -84.23700034618376)
ptreeSite.coord(34.107691680235604, -84.23690915107727)
ptreeSite = deviceBuilder.persist ptreeSite
logger.info "[Create Area] ${ptreeSite.name}"

// Create zone associated with construction site.
def zone = deviceBuilder.newZone 'workarea', 'Work Area', ptreeSite
zone.withBorderColor('#017112') withFillColor('#1db32e') withOpacity(0.4)
zone.coord(34.10260138703638, -84.24412965774536) coord(34.101837372446774, -84.24243450164795)
zone.coord(34.101517550337825, -84.24091100692749) coord(34.10154953265732, -84.23856675624847)
zone.coord(34.10153176473365, -84.23575580120087) coord(34.10409030732968, -84.23689305782318)
zone.coord(34.104996439280704, -84.23700034618376) coord(34.10606246444614, -84.23700034618376)
zone.coord(34.107691680235604, -84.23690915107727)
zone = deviceBuilder.persist zone

// ################### //
// Create Device Types //
// ################### //

def ns = 'http://sitewhere/common' // Namespace used for commands.
def allDeviceTypes = []

// Data structure for assignment information.
def assnChoice = { title, areaToken, assetToken ->
	return ['title': title, 'areaToken': areaToken, 'assetToken': assetToken];
}

// Lists for heavy equipment device types and available assignment options.
def heavyEquipment = []
def heavyEquipmentItems = []
heavyEquipmentItems << assnChoice('Equipment Tracker', 'peachtree', '923483933-SERIAL-NUMBER-416F')
heavyEquipmentItems << assnChoice('Equipment Tracker', 'peachtree', '298383493-SERIAL-NUMBER-430F')
heavyEquipmentItems << assnChoice('Equipment Tracker', 'peachtree', '593434849-SERIAL-NUMBER-D5K2')
heavyEquipmentItems << assnChoice('Equipment Tracker', 'peachtree', '345438345-SERIAL-NUMBER-D5K2')
heavyEquipmentItems << assnChoice('Equipment Tracker', 'peachtree', '847234833-SERIAL-NUMBER-320EL')
heavyEquipmentItems << assnChoice('Equipment Tracker', 'peachtree', '349544949-SERIAL-NUMBER-324E')

// Lists for personnel device types and available assignment options.
def personnel = []
def personnelItems = []
personnelItems << assnChoice('Personnel Tracker', 'peachtree', 'derek.adams@sitewhere.com')
personnelItems << assnChoice('Personnel Tracker', 'peachtree', 'bryan.rank@sitewhere.com')
personnelItems << assnChoice('Personnel Tracker', 'peachtree', 'martin.weber@sitewhere.com')

// Lists for sensor types and available assignment options.
def sensors = []
def sensorsItems = []
sensorsItems << assnChoice('Sensor', 'peachtree', '342349343-SERIAL-NUMBER-EKA4')
sensorsItems << assnChoice('Sensor', 'peachtree', '623947324-SERIAL-NUMBER-EKB4')
sensorsItems << assnChoice('Sensor', 'peachtree', '392455494-SERIAL-NUMBER-T301W')
sensorsItems << assnChoice('Sensor', 'peachtree', '734539339-SERIAL-NUMBER-TS1')
sensorsItems << assnChoice('Sensor', 'peachtree', '193835744-SERIAL-NUMBER-TS1')
sensorsItems << assnChoice('Sensor', 'peachtree', '398434398-SERIAL-NUMBER-HS1')

def addDeviceType = { type -> 
	type = deviceBuilder.persist type
	allDeviceTypes << type; 
	logger.info "[Create Device Type] ${type.name}"
	return type;
}
def addCommand = { type, command ->
	command = deviceBuilder.persist type, command
	logger.info "[Create Command] ${type.name} ${command.name}"
	return command;
}

// Android device type.
def android = deviceBuilder.newDeviceType 'galaxytab3', 'Galaxy Tab 3'
android.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/misc/android-logo.png'
android.withDescription 'This thin, lightweight Android tablet features a 7-inch touch display along with the same familiar interface as other Samsung Galaxy devices, making it easy to use. Use it to quickly browse the web, watch movies, read e-books, or download apps from Google Play.'
android.metadata 'manufacturer', 'Samsung' metadata 'cpu', '1.2ghz' metadata 'memory', '1gb'
android = addDeviceType android
def android_bgcolor = deviceBuilder.newCommand randomId(), 'http://android/example', 'changeBackground' withDescription 'Change background color of application.' withStringParameter('color', true)
addCommand android, android_bgcolor
personnel << android

// Arduino UNO.
def uno = deviceBuilder.newDeviceType 'uno', 'Arduino UNO'
uno.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/arduino/uno.jpg'
uno.withDescription 'The Arduino Uno is a microcontroller board based on the ATmega328.'
uno.metadata 'manufacturer', 'Arduino'
uno = addDeviceType uno
sensors << uno

// Arduino high memory device type.
def arduino = deviceBuilder.newDeviceType 'mega2560', 'Arduino Mega 2560'
arduino.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/arduino/mega2560.jpg'
arduino.withDescription 'The Arduino Mega 2560 is a microcontroller board based on the ATmega2560.'
arduino.metadata 'manufacturer', 'Arduino'
arduino = addDeviceType arduino
def arduinohm_serial = deviceBuilder.newCommand randomId(), 'http://arduino/example', 'serialPrintln' withDescription 'Print a message to the serial output.' withStringParameter('message', true)
addCommand arduino, arduinohm_serial
sensors << arduino

// Raspberry Pi device type.
def rpi = deviceBuilder.newDeviceType 'raspberrypi', 'Raspberry Pi'
rpi.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/misc/raspberry-pi.jpg'
rpi.withDescription 'The Raspberry Pi is a credit-card-sized single-board computer developed in the UK by the Raspberry Pi Foundation with the intention of promoting the teaching of basic computer science in schools.'
rpi.metadata 'manufacturer', 'Raspberry Pi Foundation' metadata 'weight', '1.000' metadata 'memory', '2kb'
rpi = addDeviceType rpi
def rpi_hello = deviceBuilder.newCommand randomId(), 'http://raspberrypi/example', 'helloWorld' withDescription 'Request a hello world response from device.' withStringParameter('greeting', true) withBooleanParameter('loud', true)
addCommand rpi, rpi_hello
sensors << rpi

// Meitrack device type.
def meitrack = deviceBuilder.newDeviceType 'mt90', 'MeiTrack MT90'
meitrack.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/meitrack/mt90.jpg'
meitrack.withDescription 'MT90 is a waterproof GPS personal tracker suitable for lone workers, kids, aged, pet, assets, vehicle and fleet management.'
meitrack.metadata 'manufacturer', 'MeiTrack' metadata 'weight', '1.000' metadata 'memory', '8kb'
meitrack = addDeviceType meitrack
heavyEquipment << meitrack

// Gateway default device type.
def gateway = deviceBuilder.newDeviceType 'gateway', 'Gateway Default'
gateway.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/misc/gateway.gif'
gateway.withDescription 'Sample gateway for testing nested device configurations.'
gateway.metadata 'manufacturer', 'Advantech'
def schema = gateway.makeComposite() newSchema() addSlot 'Gateway Port 1', 'gw1'
def dbus = schema.addUnit 'Default Bus', 'default'
dbus.addUnit 'PCI Bus', 'pci' addSlot 'PCI Device 1', 'pci1' addSlot 'PCI Device 2', 'pci2'
dbus.addUnit 'Serial Ports', 'serial' addSlot 'COM Port 1', 'com1' addSlot 'COM Port 2', 'com2'
schema.addUnit 'High Voltage Bus 1', 'hv1' addSlot 'HV Slot 1', 'slot1' addSlot 'HV Slot 2', 'slot2'
gateway = addDeviceType gateway
sensors << gateway

// OpenHAB device type.
def openhab = deviceBuilder.newDeviceType 'openhab', 'openHAB'
openhab.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/gateway/openhab.png'
openhab.withDescription 'This is a virual device type for testing openHAB functionality.'
openhab.metadata 'manufacturer', 'openHAB'
openhab = addDeviceType openhab
def openhab_onoff = deviceBuilder.newCommand randomId(), ns, 'sendOnOffCommand' withDescription 'Send on/off command to an openHAB item.' withStringParameter('itemName', true) withStringParameter('command', true)
addCommand openhab, openhab_onoff
def openhab_openclose = deviceBuilder.newCommand randomId(), ns, 'sendOpenCloseCommand' withDescription 'Send open/close command to an openHAB item.' withStringParameter('itemName', true) withStringParameter('command', true)
addCommand openhab, openhab_openclose
sensors << openhab

// Node-RED device type.
def nodered = deviceBuilder.newDeviceType 'nodered', 'Node-RED'
nodered.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/gateway/node-red.png'
nodered.withDescription 'This is a virual device type for testing Node-RED functionality.'
nodered.metadata 'manufacturer', 'Node-RED'
nodered = addDeviceType nodered
sensors << nodered

// Laipac device type.
def laipac = deviceBuilder.newDeviceType 'laipac-S911', 'S911 Bracelet Locator HC'
laipac.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/laipac/laipac-s-911bl.png'
laipac.withDescription 'S911 Bracelet Locator HC (Healthcare) can be used to provide location of the patients, physicians, nurses, and police on duty and assist patients unable to communicate due to issues of injury, health or age.'
laipac.metadata 'manufacturer', 'Laipac'
laipac = addDeviceType laipac
personnel << laipac

// iPhone device type.
def iphone = deviceBuilder.newDeviceType 'iphone6s', 'Apple iPhone 6S'
iphone.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/misc/iphone6.jpg'
iphone.withDescription "The only thing that's changed is everything. The moment you use iPhone 6s, you know you've never felt anything like it. With just a single press, 3D Touch lets you do more than ever. Live Photos brings your memories to life in a powerfully vivid way. And that's just the beginning. Take a deeper look at iPhone 6s, and you'll find innovation on every level."
iphone.metadata 'manufacturer', 'Apple'
iphone = addDeviceType iphone
personnel << iphone

// iPad device type.
def ipad = deviceBuilder.newDeviceType 'ipad', 'Apple iPad'
ipad.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/misc/ipad.jpg'
ipad.withDescription "Even with its 12.9-inch Retina display, the largest and most capable iPad ever is only 6.9mm thin and weighs just 1.57 lbs. It has a powerful A9X chip with 64-bit desktop-class architecture, four speaker audio, advanced iSight and FaceTime HD cameras, Wi-Fi and LTE connectivity, iCloud, the breakthrough Touch ID fingerprint sensor, and up to 10 hours of battery life."
ipad.metadata 'manufacturer', 'Apple'
ipad = addDeviceType ipad
personnel << ipad

// Add common commands.
allDeviceTypes.each { type ->
	if (type != android) {
		def ping = deviceBuilder.newCommand randomId(), ns, 'ping' withDescription 'Send a ping request to the device to verify it can be reached.'
		addCommand type, ping
		def testEvents = deviceBuilder.newCommand randomId(), ns, 'testEvents' withDescription 'Send a ping request to the device to verify it can be reached.'
		addCommand type, testEvents
	}
}

// #################### //
// Create Device Events //
// #################### //

// Closure for creating measurement and alert events.
def createMeasurements = { assn, start ->
	long current = start.time - (long) (Math.random() * 60000.0);
	double temp = minTemp
	double fuel = 100
	double delta = 4
	double mult = 6
	
	int mxCount = 0
	int alertCount = 0
	mxPerAssignment.times {
		// Simulate temperature changes.
		temp = temp + (delta + ((Math.random() * mult * 2) - mult));
		temp = Math.round(temp * 100.0) / 100.0;
		if ((temp > maxTemp) || (temp < minTemp)) {
			delta = -delta
		}

		// Simulate fuel changes.
		fuel -= (Math.random() * 2);
		fuel = Math.round(fuel * 100.0) / 100.0;
		if (fuel < 0) {
			fuel = 0
		}
		
		def newmx = eventBuilder.newMeasurements() measurement('engine.temperature', temp) measurement('fuel.level', fuel) on(new Date(current)) trackState()
		eventBuilder.forAssignment assn.token persist newmx
		
		if (temp > warnTemp) {
			def alert = eventBuilder.newAlert 'engine.overheat', 'Engine temperature is at top of operating range.' on(new Date(current)) warning() trackState()
			if (temp > errorTemp) {
				alert = eventBuilder.newAlert 'engine.overheat', 'Engine temperature is at a dangerous level.' on(new Date(current)) error() trackState()
			} else if (temp > criticalTemp) {
				alert = eventBuilder.newAlert 'engine.overheat', 'Engine temperature critical. Shutting down.' on(new Date(current)) critical() trackState()
			}
			eventBuilder.forAssignment assn.token persist alert
			
			alertCount++;
		}
		
		mxCount++;
		current += (long) (Math.random() * 30000.0);
	}
	logger.info "[Create Events] ${mxCount} measurements. ${alertCount} alerts."
}

// Closure for creating location events.
def createLocations = { assn, startDate ->
	long current = startDate.time - (long) (Math.random() * 60000.0);
	Polygon polyZone = com.sitewhere.geospatial.GeoUtils.createPolygonForLocations(zone.bounds);
	Point centroid = polyZone.getCentroid();

	// Calculate length of steps between locations based on bounding circle.
	MinimumBoundingCircle circle = new MinimumBoundingCircle(polyZone);
	double step = circle.radius / 10;

	double cx = centroid.x;
	double cy = centroid.y;
	double deltaX = (Math.sqrt(Math.random()) * step * 2) - step;
	double deltaY = (Math.sqrt(Math.random()) * step * 2) - step;

	// Used to rotate deltas to turn path and stay inside polygon.
	AffineTransformation xform = new AffineTransformation();
	xform.rotate(Math.toRadians(22.5));

	int locCount = 0
	GeometryFactory factory = new GeometryFactory();
	locationsPerAssignment.times {
		boolean foundNext = false;

		// Add a little randomness to path.
		double waver = ((Math.random() * 20) - 10.0);
		AffineTransformation waverXform = new AffineTransformation();
		waverXform.rotate(Math.toRadians(waver));
		Coordinate waverDelta = new Coordinate(deltaX, deltaY);
		waverXform.transform(waverDelta, waverDelta);
		deltaX = waverDelta.x;
		deltaY = waverDelta.y;

		while (!foundNext) {
			Coordinate start = new Coordinate(cx, cy);
			Coordinate end = new Coordinate(cx + deltaX, cy + deltaY);
			Coordinate[] lineCoords = [start, end];
			LineString line = factory.createLineString(lineCoords);
			if (polyZone.contains(line)) {
				def newloc = eventBuilder.newLocation end.y, end.x on(new Date(current)) trackState()
				eventBuilder.forAssignment assn.token persist newloc

				cx = cx + deltaX;
				cy = cy + deltaY;
				foundNext = true;
			} else {
				// Rotate deltas and try again.
				Coordinate delta = new Coordinate(deltaX, deltaY);
				xform.transform(delta, delta);
				deltaX = delta.x;
				deltaY = delta.y;
			}
		}
		locCount++
		current += (long) (Math.random() * 30000.0);
	}
	logger.info "[Create Events] ${locCount} locations."
}

// #################### //
// Create Device Groups //
// #################### //
def addGroup = { group ->
	group = deviceBuilder.persist group
	logger.info "[Create Group] ${group.name}"
	return group;
}

def heavyGroup = deviceBuilder.newGroup randomId(), 'Heavy Equipment Tracking' withRole('heavy-equipment-tracking') withRole('tracking')
heavyGroup.withDescription 'Device group that contains devices for tracking location of heavy equipment.'
heavyGroup.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/cat/cat-416f.jpg'
heavyGroup = addGroup heavyGroup

def personGroup = deviceBuilder.newGroup randomId(), 'Personnel Tracking' withRole('personnel-tracking') withRole('tracking')
personGroup.withDescription 'Device group that contains devices for tracking location of people.'
personGroup.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/sitewhere-symbol.png'
personGroup = addGroup personGroup

def sensorGroup = deviceBuilder.newGroup randomId(), 'Sensors' withRole('monitoring') withRole('data-gathering')
sensorGroup.withDescription 'Device group that contains sensors for tracking environmental conditions.'
sensorGroup.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/ekahau/ekahau-a4-tag.jpg'
sensorGroup = addGroup sensorGroup

// ############################## //
// Create Devices and Assignments //
// ############################## //

// Create the requested number of devices and assignments per site.
def heavyElements = []
def personElements = []
def sensorElements = []
devicesPerSite.times {
	def type = randomItem(allDeviceTypes);
	
	def assnInfo
	if (type in sensors) {
		assnInfo = randomItem(sensorsItems);
	} else if (type in personnel) {
		assnInfo = randomItem(personnelItems);
	} else {
		assnInfo = randomItem(heavyEquipmentItems);
	}
	
	// Create a device.
	def device = deviceBuilder.newDevice type.token, randomDeviceToken(type) withComment "${assnInfo.title} based on ${type.name}."
	device = deviceBuilder.persist device
	logger.info "[Create Device] ${device.token}"
	
	// Create an assignment based on device type.
	def assn = deviceBuilder.newAssignment device.token, acmeCustomer.token, assnInfo.areaToken, assnInfo.assetToken
	assn = deviceBuilder.persist assn
	logger.info "[Create Assignment] ${assn.token}"
	
	def element = deviceBuilder.newGroupElement device.token;
	if (type in sensors) {
		sensorElements << element
	} else if (type in personnel) {
		personElements << element
	} else {
		heavyElements << element
	}
	
	// Start events two hours before current.
	Date start = new Date(System.currentTimeMillis() - (2 * 60 * 60 * 1000));
	createMeasurements assn, start
	createLocations assn, start
}
deviceBuilder.persist heavyGroup, heavyElements
deviceBuilder.persist personGroup, personElements
deviceBuilder.persist sensorGroup, sensorElements
