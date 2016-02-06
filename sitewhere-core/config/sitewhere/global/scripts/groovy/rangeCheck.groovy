logger.info "Handling event processing in Groovy script"

if (event.hasMeasurement('fuel.level')) {
	logger.info "Got fuel level. Creating a new location."
	def newloc = eventsBuilder.newLocation 33.75, -84.39 withElevation 10 on(new Date()) trackState()
	eventsBuilder.forSameAssignmentAs event persist newloc
}
