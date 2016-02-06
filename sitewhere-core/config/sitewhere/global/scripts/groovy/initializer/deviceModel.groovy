// ########### //
// Common Code //
// ########### //
Integer devicesPerSite = 50
Integer mxPerAssignment = 75
Integer minTemp = 80
Integer warnTemp = 160
Integer errorTemp = 180
Integer criticalTemp = 190
Integer maxTemp = 200

def randomId = { java.util.UUID.randomUUID().toString() }
def randomItem = { items ->
	items.get((int)(Math.random() * items.size()))
}

// ########### //
// Create Site //
// ########### //
def site = deviceBuilder.newSite 'bb105f8d-3150-41f5-b9d1-db04965668d3', 'Construction Site'

site.withDescription '''A construction site with many high-value assets that should not be taken offsite. 
The system provides location tracking for the assets and notifies administrators if any of the assets move 
outside of the general site area or into areas where they are not allowed.'''

site.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/construction.jpg'
site.mapquestMap 34.10469794977326, -84.23966646194458, 15
site = deviceBuilder.persist site
logger.info "[Create Site] ${site.name}"

// ############################ //
// Create Device Specifications //
// ############################ //
def module = 'fs-devices' // Asset module with device assets.
def ns = 'http://sitewhere/common' // Namespace used for commands.
def allSpecifications = []

// Data structure for assignment information.
def assnChoice = { title, assetModuleId, assetId ->
	return ['title': title, 'module': assetModuleId, 'asset': assetId];
}

// Lists for heavy equipment specifications and available assignment options.
def heavyEquipment = []
def heavyEquipmentItems = []
heavyEquipmentItems << assnChoice('Equipment Tracker', 'fs-hardware', '300')
heavyEquipmentItems << assnChoice('Equipment Tracker', 'fs-hardware', '301')
heavyEquipmentItems << assnChoice('Equipment Tracker', 'fs-hardware', '302')
heavyEquipmentItems << assnChoice('Equipment Tracker', 'fs-hardware', '303')
heavyEquipmentItems << assnChoice('Equipment Tracker', 'fs-hardware', '304')

// Lists for personnel specifications and available assignment options.
def personnel = []
def personnelItems = []
personnelItems << assnChoice('Personnel Tracker', 'fs-persons', '1')
personnelItems << assnChoice('Personnel Tracker', 'fs-persons', '2')
personnelItems << assnChoice('Personnel Tracker', 'fs-persons', '3')

// Lists for tool specifications and available assignment options.
def tools = []
def toolsItems = []
toolsItems << assnChoice('Tool Tracker', 'fs-locations', '1')
toolsItems << assnChoice('Tool Tracker', 'fs-locations', '2')

def addSpecification = { spec -> 
	spec = deviceBuilder.persist spec
	allSpecifications << spec; 
	logger.info "[Create Specification] ${spec.name}"
	return spec;
}
def addCommand = { spec, command ->
	command = deviceBuilder.persist spec, command
	logger.info "[Create Command] ${spec.name} ${command.name}"
	return command;
}

// Android specification.
def android = deviceBuilder.newSpecification 'd2604433-e4eb-419b-97c7-88efe9b2cd41', 'Android Tablet', module, '173'
android = addSpecification android
def android_bgcolor = deviceBuilder.newCommand randomId(), 'http://android/example', 'changeBackground' withDescription 'Change background color of application.' withStringParameter('color', true)
addCommand android, android_bgcolor
personnel << android

// Arduino high memory specification.
def arduinohm = deviceBuilder.newSpecification '417b36a8-21ef-4196-a8fe-cc756f994d0b', 'Arduino High Memory', module, '181'
arduinohm = addSpecification arduinohm
def arduinohm_serial = deviceBuilder.newCommand randomId(), 'http://arduino/example', 'serialPrintln' withDescription 'Print a message to the serial output.' withStringParameter('message', true)
addCommand arduinohm, arduinohm_serial
tools << arduinohm

// Raspberry Pi specification.
def rpi = deviceBuilder.newSpecification '7dfd6d63-5e8d-4380-be04-fc5c73801dfb', 'Raspberry Pi', module, '174'
rpi = addSpecification rpi
def rpi_hello = deviceBuilder.newCommand randomId(), 'http://raspberrypi/example', 'helloWorld' withDescription 'Request a hello world response from device.' withStringParameter('greeting', true) withBooleanParameter('loud', true)
addCommand rpi, rpi_hello
tools << rpi

// Meitrack specification.
def meitrack = deviceBuilder.newSpecification '82043707-9e3d-441f-bdcc-33cf0f4f7260', 'MeiTrack GPS', module, '175'
meitrack = addSpecification meitrack
heavyEquipment << meitrack

// Gateway default specification.
def gateway = deviceBuilder.newSpecification '75126a52-0607-4cca-b995-df40e73a707b', 'Gateway Default', module, '176'
gateway = addSpecification gateway
tools << gateway

// OpenHAB specification.
def openhab = deviceBuilder.newSpecification '5a95f3f2-96f0-47f9-b98d-f5c081d01948', 'openHAB', module, '190'
openhab = addSpecification openhab
def openhab_onoff = deviceBuilder.newCommand randomId(), ns, 'sendOnOffCommand' withDescription 'Send on/off command to an openHAB item.' withStringParameter('itemName', true) withStringParameter('command', true)
addCommand openhab, openhab_onoff
def openhab_openclose = deviceBuilder.newCommand randomId(), ns, 'sendOpenCloseCommand' withDescription 'Send open/close command to an openHAB item.' withStringParameter('itemName', true) withStringParameter('command', true)
addCommand openhab, openhab_openclose
tools << openhab

// Node-RED specification.
def nodered = deviceBuilder.newSpecification '964e7613-dab3-4fb3-8919-266a91370884', 'Node-RED', module, '195'
nodered = addSpecification nodered
tools << nodered

// Laipac specification.
def laipac = deviceBuilder.newSpecification 'fc0f3d8d-c6e6-4fd2-b7d6-6f21bcf3a910', 'Laipac Health Bracelet', module, '300'
laipac = addSpecification laipac
personnel << laipac

// iPhone specification.
def iphone = deviceBuilder.newSpecification '9f2426bd-46de-49d6-833e-385784a9dc1a', 'Apple iPhone', module, '400'
iphone = addSpecification iphone
personnel << iphone

// iPad specification.
def ipad = deviceBuilder.newSpecification 'cfe831ea-20ca-47f6-b0d7-809d26e45b5b', 'Apple iPad', module, '405'
ipad = addSpecification ipad
personnel << ipad

// Add common commands.
allSpecifications.each { spec ->
	if (spec != openhab) {
		def ping = deviceBuilder.newCommand randomId(), ns, 'ping' withDescription 'Send a ping request to the device to verify it can be reached.'
		addCommand spec, ping
		def testEvents = deviceBuilder.newCommand randomId(), ns, 'testEvents' withDescription 'Send a ping request to the device to verify it can be reached.'
		addCommand spec, testEvents
	}
}

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
		
		def newmx = eventBuilder.newMeasurements() measurement('engine.temperature', temp) measurement('fuel.level', fuel) trackState()
		eventBuilder.forAssignment assn.token persist newmx
		
		if (temp > warnTemp) {
			def alert = eventBuilder.newAlert 'engine.overheat', 'Engine temperature is at top of operating range.' warning() trackState()
			if (temp > errorTemp) {
				alert = eventBuilder.newAlert 'engine.overheat', 'Engine temperature is at a dangerous level.' error() trackState()
			} else if (temp > criticalTemp) {
				alert = eventBuilder.newAlert 'engine.overheat', 'Engine temperature critical. Shutting down.' critical() trackState()
			}
			eventBuilder.forAssignment assn.token persist alert
			
			alertCount++;
		}
		
		mxCount++;
		current += (long) (Math.random() * 30000.0);
	}
	logger.info "[Create Events] ${mxCount} measurements. ${alertCount} alerts."
}

// Create the requested number of devices and assignments per site.
devicesPerSite.times {
	def spec = randomItem(allSpecifications);
	if (Math.random() > 0.75) {
		spec = meitrack
	}
	
	def assnInfo
	if (spec in tools) {
		assnInfo = randomItem(toolsItems);
	} else if (spec in personnel) {
		assnInfo = randomItem(personnelItems);
	} else {
		assnInfo = randomItem(heavyEquipmentItems);
	}
	
	// Create a device.
	def device = deviceBuilder.newDevice site.token, spec.token, randomId() withComment "${assnInfo.title}. Device generated by model initializer."
	device = deviceBuilder.persist device
	logger.info "[Create Device] ${device.hardwareId}"
	
	// Create an assignment based on specification type.
	def assn = deviceBuilder.newAssignment device.hardwareId, assnInfo.module, assnInfo.asset
	assn = deviceBuilder.persist assn
	logger.info "[Create Assignment] ${assn.token}"
	
	// Start events two hours before current.
	Date start = new Date(System.currentTimeMillis() - (2 * 60 * 60 * 1000));
	createMeasurements assn, start
}
