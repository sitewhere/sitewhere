import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.util.*;
import com.vividsolutions.jts.algorithm.*;

// ########### //
// Common Code //
// ########### //
Integer planeCount = 30
Integer devicesPerSite = 30
Integer mxPerAssignment = 50
Integer locationsPerAssignment = 40
Integer minTemp = 80
Integer warnTemp = 160
Integer errorTemp = 180
Integer criticalTemp = 200
Integer maxTemp = 220

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
def airPortCustomerType = deviceBuilder.newCustomerType 'airline', 'Airline Company'
airPortCustomerType.withDescription 'A company that manages one or more airplanes.' withIcon 'plane'
airPortCustomerType = deviceBuilder.persist airPortCustomerType
logger.info "[Create Customer Type] ${airPortCustomerType.name}"

// ############ //
// Create Areas //
// ############ //

def customers = []

// American Airlines.
def aaCustomer = deviceBuilder.newCustomer airPortCustomerType.token, null, 'american', 'American Airlines, Inc.'
aaCustomer.withDescription 'American Airlines, Inc. is is a major United States airline headquartered in Fort Worth, Texas, within the Dallas-Fort Worth metroplex.'
aaCustomer.withImageUrl 'https://upload.wikimedia.org/wikipedia/commons/f/f2/N124AA_LAX_%2826109389861%29.jpg'
aaCustomer = deviceBuilder.persist aaCustomer
customers << aaCustomer
logger.info "[Create Customer] ${aaCustomer.name}"

// Southwest Airlines Co.
def swCustomer = deviceBuilder.newCustomer airPortCustomerType.token, null, 'southwest', 'Southwest Airlines Co.'
swCustomer.withDescription 'Southwest Airlines Co. is a major United States airline headquartered in Dallas, Texas, and is the world’s largest low-cost carrier.'
swCustomer.withImageUrl 'https://upload.wikimedia.org/wikipedia/commons/6/6a/N8675A.jpg'
swCustomer = deviceBuilder.persist swCustomer
customers << swCustomer
logger.info "[Create Customer] ${swCustomer.name}"

// ################################# //
// Create Area Types, Areas and Zone //
// ################################# //

// AirPort type.
def airPortType = deviceBuilder.newAreaType 'aiport-type', 'Air Port Type'
airPortType.withDescription 'Example Air Port.' withIcon 'plane'
airPortType = deviceBuilder.persist airPortType
logger.info "[Create Area Type] ${airPortType.name}"

// AirPort.
def airPort = deviceBuilder.newArea airPortType.token, null, 'airport', 'Air Traffic Example'
airPort.withDescription '''Example project that emulates an air traffic monitoring system. The system tracks 
many plane assets that have associated monitoring devices which send events for plane locations and various 
other KPIs.'''
airPort.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/airport/airport.gif'
airPort.coord(39.798122, -98.7223078)
airPort = deviceBuilder.persist airPort
logger.info "[Create Area] ${airPort.name}"


// Create zone associated with airport.

def zone = deviceBuilder.newZone 'workarea', 'Work Area', airPort
zone.withBorderColor('#017112') withFillColor('#1db32e') withOpacity(0.4)
zone.coord(39.798122, -98.7223078)
zone.coord(39.798123, -98.7223078)
zone.coord(39.798123, -98.7223079)
zone.coord(39.798122, -98.7223079)
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
heavyEquipmentItems << assnChoice('Equipment Tracker', 'airport', '923483933-SERIAL-NUMBER-416F')
heavyEquipmentItems << assnChoice('Equipment Tracker', 'airport', '298383493-SERIAL-NUMBER-430F')
heavyEquipmentItems << assnChoice('Equipment Tracker', 'airport', '593434849-SERIAL-NUMBER-D5K2')
heavyEquipmentItems << assnChoice('Equipment Tracker', 'airport', '345438345-SERIAL-NUMBER-D5K2')
heavyEquipmentItems << assnChoice('Equipment Tracker', 'airport', '847234833-SERIAL-NUMBER-320EL')

// Lists for personnel device types and available assignment options.
def personnel = []
def personnelItems = []
personnelItems << assnChoice('Personnel Tracker', 'airport', 'derek.adams@sitewhere.com')
personnelItems << assnChoice('Personnel Tracker', 'airport', 'bryan.rank@sitewhere.com')
personnelItems << assnChoice('Personnel Tracker', 'airport', 'martin.weber@sitewhere.com')


// Create specification for each type of tracker asset.

def addDeviceType = { type -> 
	type = deviceBuilder.persist type
	allDeviceTypes << type; 
	logger.info "[Create Device Type] ${type.name}"
	return type;
}

def addCommand = { type, command ->
	command = deviceBuilder.persist command
	logger.info "[Create Command] ${type.name} ${command.name}"
	return command;
}

// Air Traffic Decice Type
def atDevice = deviceBuilder.newDeviceType 'airtraffic-device', 'Air Traffic Device'
atDevice.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/airport/fmz2000.jpg'
atDevice.withDescription 'This thin, lightweight Air Traffic Device display along with the same familiar interface.'
atDevice.metadata 'manufacturer', 'Honeywell'
atDevice = addDeviceType atDevice

def atDevice_bgcolor = deviceBuilder.newCommand atDevice.token, randomId(), 'http://at-device/example', 'changeBackground' withDescription 'Change background color of application.' withStringParameter('color', true)
addCommand atDevice, atDevice_bgcolor
personnel << atDevice

// Air Traffic Decice Type
def atPlane = deviceBuilder.newDeviceType 'airtraffic-plane', 'Air Traffic Plane'
atPlane.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/airport/a330-200.jpg'
atPlane.withDescription 'An airplane or aeroplane (informally plane) is a powered, fixed-wing aircraft that is propelled forward by thrust from a jet engine, propeller or rocket engine. Airplanes come in a variety of sizes, shapes, and wing configurations.'
atPlane = addDeviceType atPlane
heavyEquipment << atPlane

// Add common commands.
allDeviceTypes.each { type ->
	def ping = deviceBuilder.newCommand type.token, randomId(), ns, 'ping' withDescription 'Send a ping request to the device to verify it can be reached.'
	addCommand type, ping
	def testEvents = deviceBuilder.newCommand type.token, randomId(), ns, 'testEvents' withDescription 'Send a ping request to the device to verify it can be reached.'
	addCommand type, testEvents
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
		
	def mxRequests = []
	def alertRequests = []
	
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
		
		def fuellevel = eventBuilder.newMeasurements() measurement('fuel.level', fuel) on(new Date(current)) trackState()
		mxRequests << fuellevel
		mxCount++;
		
		def engtemp = eventBuilder.newMeasurements() measurement('engine.temperature', temp) on(new Date(current)) trackState()
		mxRequests << engtemp
		mxCount++;
		
		if (temp > warnTemp) {
			def alert = eventBuilder.newAlert 'engine.overheat', 'Engine temperature is at top of operating range.' on(new Date(current)) warning() trackState()
			if (temp > errorTemp) {
				alert = eventBuilder.newAlert 'engine.overheat', 'Engine temperature is at a dangerous level.' on(new Date(current)) error() trackState()
			}
			if (temp > criticalTemp) {
				alert = eventBuilder.newAlert 'engine.overheat', 'Engine shut down due to critical temperature of ' + temp + ' degrees' on(new Date(current)) critical() trackState()
			}
			alertRequests << alert
			alertCount++;
		}
		
		current += (long) (Math.random() * 30000.0);
	}
			
	eventBuilder.forAssignment assn.token persistMeasurements mxRequests
	def alerts = eventBuilder.forAssignment assn.token persistAlerts alertRequests
			
	// Create alarms for critical alerts.
	alerts.each { alert ->
		if (alert.level.name() == 'Critical') {
			def alarm = deviceBuilder.newDeviceAlarm assn.token, alert.message 
			alarm.withTriggeringEventId alert.id
			deviceBuilder.persist alarm
		}
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
	
	def locationRequests = []

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
				locationRequests << newloc

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
	eventBuilder.forAssignment assn.token persistLocations locationRequests
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

def heavyGroup = deviceBuilder.newGroup randomId(), 'Heavy Transport' withRole('heavy-transport') withRole('heavy')
heavyGroup.withDescription 'Heavy transport aircraft.'
heavyGroup.withImageUrl 'https://upload.wikimedia.org/wikipedia/commons/a/a1/C-130J_135th_AS_Maryland_ANG_in_flight.jpg'
heavyGroup = addGroup heavyGroup

def personalGroup = deviceBuilder.newGroup randomId(), 'Personal Transport' withRole('personal-transport') withRole('personal')
personalGroup.withDescription 'Personal transport aircraft.'
personalGroup.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/airport/a330-200.jpg'
personalGroup = addGroup personalGroup

// ############################## //
// Create Devices and Assignments //
// ############################## //

// Create the requested number of devices and assignments per site.
def heavyElements = []
def personElements = []

devicesPerSite.times {
	def type = randomItem(allDeviceTypes);
	
	def assnInfo
	if (type in personnel) {
		assnInfo = randomItem(personnelItems);
	} else {
		assnInfo = randomItem(heavyEquipmentItems);
	}
	
	// Create a device.
	def device = deviceBuilder.newDevice type.token, randomDeviceToken(type) withComment "${assnInfo.title} based on ${type.name}."
	device = deviceBuilder.persist device
	logger.info "[Create Device] ${device.token}"

	def customer
	customer = randomItem(customers);

	// Create an assignment based on device type.
	def assn = deviceBuilder.newAssignment device.token, customer.token, assnInfo.areaToken, assnInfo.assetToken
	assn = deviceBuilder.persist assn
	logger.info "[Create Assignment] ${assn.token}"
	
	def element = deviceBuilder.newGroupElement device.token;
	if (type in personnel) {
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
deviceBuilder.persist personalGroup, personElements
