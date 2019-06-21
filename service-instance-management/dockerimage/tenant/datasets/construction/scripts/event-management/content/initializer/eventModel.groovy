import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.util.*;
import com.vividsolutions.jts.algorithm.*;

// ########### //
// Common Code //
// ########### //
Integer mxPerAssignment = 50
Integer locationsPerAssignment = 40
Integer minTemp = 80
Integer warnTemp = 160
Integer errorTemp = 180
Integer criticalTemp = 200
Integer maxTemp = 220

def randomId = { java.util.UUID.randomUUID().toString() }
def randomItem = { items ->
	items.get((int)(Math.random() * items.size()))
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

// ############################# //
// Create Events for Assignments //
// ############################# //

// Start events two hours before current.
Date start = new Date(System.currentTimeMillis() - (2 * 60 * 60 * 1000));
createMeasurements assn, start
createLocations assn, start
