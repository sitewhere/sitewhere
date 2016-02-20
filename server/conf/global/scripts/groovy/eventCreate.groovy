// ######################## //
// Event Generation Example //
// ######################## //
logger.debug "Handling event processing in Groovy script"

// Only execute logic if event contains measurements with fuel level.
if (event.hasMeasurement('fuel.level')) {
	Double fuelLevel = event.getMeasurement('fuel.level');
	Double mpg = event.getMeasurement('fuel.mpg');
	
	// Create a new location event.
	logger.info "Received fuel level. Marking location."
	def newloc = eventBuilder.newLocation 33.75, -84.39 withElevation 10 trackState()
	eventBuilder.forSameAssignmentAs event persist newloc
	
	// Create a new alert event if fuel level < 10.
	if (fuelLevel < 10) {
		logger.info "Fuel level less than 10. Generating alert."
		def newAlert = eventBuilder.newAlert 'fuel.low', 'Fuel level is low!' warning() trackState()
		eventBuilder.forSameAssignmentAs event persist newAlert
	}
	
	if (mpg != null) {
		logger.info "Found MPG. Calculating distance until empty."
		def newMx = eventBuilder.newMeasurements() measurement('estimated.dte', mpg * fuelLevel) trackState()
		eventBuilder.forSameAssignmentAs event persist newMx
	}
}
