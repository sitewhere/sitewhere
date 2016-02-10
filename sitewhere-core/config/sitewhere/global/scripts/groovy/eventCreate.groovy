// ######################## //
// Event Generation Example //
// ######################## //
logger.debug "Handling event processing in Groovy script"

// Only execute logic if event contains measurements with fuel level.
if (event.hasMeasurement('fuel.level')) {
	logger.info "Got fuel level. Creating a new location."
	
	// Create a new location event. It will be stored and processed like other events.
	def newloc = eventBuilder.newLocation 33.75, -84.39 withElevation 10 on(new Date()) trackState()
	eventBuilder.forSameAssignmentAs event persist newloc
}
