Integer planeCount = 30

// ########### //
// Create Site //
// ########### //
def site = deviceBuilder.newSite '9d0f4ddd-3d9c-480e-ab5b-5b1da0efcbd8', 'Air Traffic Example'
site.withDescription '''Example project that emulates an air traffic monitoring system. The system tracks 
many plane assets that have associated monitoring devices which send events for plane locations and various 
other KPIs.'''
site.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/airport/airport.gif'
site.openStreetMap 39.798122, -98.7223078, 5
site = deviceBuilder.persist site
logger.info "[Create Site] ${site.name}"

// Load lists of all tracker and plane assets.
def trackers = assetBuilder.allAssetsInModule 'at-devices'
def planes = assetBuilder.allAssetsInModule 'at-planes'

def randomId = { java.util.UUID.randomUUID().toString() }
def randomItem = { items ->
	items.get((int)(Math.random() * items.size()))
}

// Create specification for each type of tracker asset.
def allSpecs = []
trackers.each { tracker ->
	def spec = deviceBuilder.newSpecification randomId(), tracker.name + '  Specification', 'at-devices', tracker.id
	spec = deviceBuilder.persist spec
	logger.info "[Create Specification] ${spec.name}"
	allSpecs << spec
}

// Create tracker device for each plane.
def allTrackerDevices = []
(1..planeCount).each {
	def randomSpec = randomItem(allSpecs)
	def device = deviceBuilder.newDevice site.token, randomSpec.token, randomId() withComment "Air traffic tracker device ${it}."
	device = deviceBuilder.persist device
	logger.info "[Create Device] ${device.hardwareId}"
	allTrackerDevices << device
}

// Assign each tracker device to a plane of a random type.
allTrackerDevices.each { device ->
	def randomPlane = randomItem(planes)
	def assn = deviceBuilder.newAssignment device.hardwareId, 'at-planes', randomPlane.id
	assn.metadata('flightNumber', 'SW' + ((int) Math.floor(Math.random() * 10000)))
	assn = deviceBuilder.persist assn
	logger.info "[Create Assignment] ${assn.token}"
}
