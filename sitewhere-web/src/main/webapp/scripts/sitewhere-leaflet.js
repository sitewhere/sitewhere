/*
 * SiteWhere extensions for Leaflet maps.
 */
L.Map.SiteWhere = L.Map.extend({
	
	statics: {
		MAP_TYPE_MAPQUEST: "mapquest",
		MAP_TYPE_GEOSERVER: "geoserver",
	},

	options: {
		siteWhereApi: 'http://localhost:8080/sitewhere/api/',
		siteToken: null,
		tenantAuthToken: null,
		showZones: true,
		onZonesLoaded: null,
	},
	
	/** Initialize components */
	initialize: function(id, options) {
		L.setOptions(this, options);
		L.Map.prototype.initialize.call(this, id, options);
        
		// Error if no site token specified.
		if (!this.options.siteToken) {
			this._handleNoSiteToken();
		} else if (!this.options.tenantAuthToken) {
			this._handleNoTenantAuthToken();
		} else {
			this.refresh();
		}
	},
	
	/** Refresh site information */
	refresh: function() {
		var self = this;
		var url = this.options.siteWhereApi + 'sites/' + this.options.siteToken + 
			'?tenantAuthToken=' + this.options.tenantAuthToken;
		L.SiteWhere.Util.getJSON(url, 
				function(site, status, jqXHR) { self._onSiteLoaded(site); }, 
				function(jqXHR, textStatus, errorThrown) { self._onSiteFailed(jqXHR, textStatus, errorThrown); }
		);
	},
	
	/** Called when site data has been loaded successfully */
	_onSiteLoaded: function(site) {
		var mapInfo = site.map.metadata;
		var latitude = (mapInfo.centerLatitude ? mapInfo.centerLatitude : 39.9853);
		var longitude = (mapInfo.centerLongitude ? mapInfo.centerLongitude : -104.6688);
		var zoomLevel = (mapInfo.zoomLevel ? mapInfo.zoomLevel : 10);
		L.Map.prototype.setView.call(this, [latitude, longitude], zoomLevel);
		this._loadMapTileLayer(site, mapInfo);
		if (this.options.showZones) {
			var zones = L.FeatureGroup.SiteWhere.zones({
				siteWhereApi: this.options.siteWhereApi,
				siteToken: this.options.siteToken,
				tenantAuthToken: this.options.tenantAuthToken,
				onZonesLoaded: this.options.onZonesLoaded,
			});
			this.addLayer(zones);
		}
	},
	
	/** Loads a TileLayer based on map type and metadata associated with site */
	_loadMapTileLayer: function(site, mapInfo) {
		if (site.map.type == L.Map.SiteWhere.MAP_TYPE_MAPQUEST) {
			var mapquestUrl = 'http://{s}.mqcdn.com/tiles/1.0.0/osm/{z}/{x}/{y}.png';
			var subDomains = ['otile1','otile2','otile3','otile4'];
			var mapquestAttrib = 'MapQuest data';
			var mapquest = new L.TileLayer(mapquestUrl, {maxZoom: 18, attribution: mapquestAttrib, subdomains: subDomains});		
			mapquest.addTo(this);
		} else if (site.map.type == L.Map.SiteWhere.MAP_TYPE_GEOSERVER) {
			var gsBaseUrl = (mapInfo.geoserverBaseUrl ? mapInfo.geoserverBaseUrl : "http://localhost:8080/geoserver/");
			var gsRelativeUrl = "geoserver/gwc/service/gmaps?layers=";
			var gsLayerName = (mapInfo.geoserverLayerName ? mapInfo.geoserverLayerName : "tiger:tiger_roads");
			var gsParams = "&zoom={z}&x={x}&y={y}&format=image/png";
			var gsUrl = gsBaseUrl + gsRelativeUrl + gsLayerName + gsParams;
			var geoserver = new L.TileLayer(gsUrl, {maxZoom: 18});		
			geoserver.addTo(this);
		}
	},
	
	/** Called when site data load fails */
	_onSiteFailed: function(jqXHR, textStatus, errorThrown) {
		alert('Site load failed! ' + errorThrown);
	},
	
	/** Handle error condition if no site token was specified */
	_handleNoSiteToken: function() {
		alert('No site token.');
	},
	
	/** Handle error condition if no tenant authentication token was specified */
	_handleNoTenantAuthToken: function() {
		alert('No tenant authentication token.');
	},
});

L.Map.siteWhere = function (id, options) {
    return new L.Map.SiteWhere(id, options);
};

/*
 * Container for SiteWhere feature groups.
 */
L.FeatureGroup.SiteWhere = {};

/*
 * Feature group for SiteWhere zones.
 */
L.FeatureGroup.SiteWhere.Zones = L.FeatureGroup.extend({

	options: {
		siteWhereApi: 'http://localhost:8080/sitewhere/api/',
		siteToken: null,
		tenantAuthToken: null,
		onZonesLoaded: null,
		zoneTokenToSkip: null,
	},
	
	initialize: function(options) {
		L.setOptions(this, options);
		L.FeatureGroup.prototype.initialize.call(this);
        
		// Error if no site token specified.
		if (!this.options.siteToken) {
			this._handleNoSiteToken();
		} else if (!this.options.tenantAuthToken) {
			this._handleNoTenantAuthToken();
		} else {
			this.refresh();
		}
	},
	
	/** Refresh zones information */
	refresh: function() {
		var self = this;
		var url = this.options.siteWhereApi + 'sites/' + this.options.siteToken + 
			'/zones?tenantAuthToken=' + this.options.tenantAuthToken;
		L.SiteWhere.Util.getJSON(url, 
				function(zones, status, jqXHR) { self._onZonesLoaded(zones); }, 
				function(jqXHR, textStatus, errorThrown) { self._onZonesFailed(jqXHR, textStatus, errorThrown); }
		);
	},
	
	/** Called when zones data has been loaded successfully */
	_onZonesLoaded: function(zones) {
		var zone, results = zones.results;
		var polygon;
		
		// Add newest last.
		results.reverse();
		
		// Add a polygon layer for each zone.
		for (var zoneIndex = 0; zoneIndex < results.length; zoneIndex++) {
			zone = results[zoneIndex];
			if (zone.token != this.options.zoneTokenToSkip) {
				polygon = this._createPolygonForZone(zone);
				this.addLayer(polygon);
			}
		}
		
		// Callback for actions taken after zones are loaded.
		if (this.options.onZonesLoaded != null) {
			this.options.onZonesLoaded();
		}
	},
	
	/** Create a polygon layer based on zone information */
	_createPolygonForZone: function(zone) {
		var coords = zone.coordinates;
		var latLngs = [];
		for (var coordIndex = 0; coordIndex < coords.length; coordIndex++) {
			coordinate = coords[coordIndex];
			latLngs.push(new L.LatLng(coordinate.latitude, coordinate.longitude));
		}
		var polygon = new L.Polygon(latLngs, {
			"color": zone.borderColor, "opacity": 1, weight: 3,
			"fillColor": zone.fillColor, "fillOpacity": zone.opacity,
			"clickable": false});
		return polygon;
	},
	
	/** Called when zones data load fails */
	_onZonesFailed: function(jqXHR, textStatus, errorThrown) {
		alert('Zones load failed! ' + errorThrown);
	},
	
	/** Handle error condition if no site token was specified */
	_handleNoSiteToken: function() {
		alert('No site token.');
	},
	
	/** Handle error condition if no tenant authentication token was specified */
	_handleNoTenantAuthToken: function() {
		alert('No tenant authentication token.');
	},
});

L.FeatureGroup.SiteWhere.zones = function (options) {
	return new L.FeatureGroup.SiteWhere.Zones(options);
};


/*
 * Feature group for recent locations associated with an assignment.
 */
L.FeatureGroup.SiteWhere.AssignmentLocations = L.FeatureGroup.extend({

	options: {
		// Data options.
		siteWhereApi: 'http://localhost:8080/sitewhere/api/',
		assignmentToken: null,
		tenantAuthToken: null,
		maxResults: 30,
		
		// Line rendering options (see L.Path).
		showLine: true,
		lineOptions: {
            stroke: true,
            color: '#005599',
            weight: 5,
            opacity: 0.5,
		},
		
		// Marker rendering options.
		showMarkers: true,
		
		// Event callbacks.
		onLocationsLoaded: null,
		onError: null,
	},
	
	initialize: function(options) {
		L.setOptions(this, options);
		L.FeatureGroup.prototype.initialize.call(this);
        
		// Error if no assignment token specified.
		if (!this.options.assignmentToken) {
			this._handleNoAssignmentToken();
		} else if (!this.options.tenantAuthToken) {
			this._handleNoTenantAuthToken();
		} else {
			this.refresh();
		}
	},
	
	/** Refresh locations information */
	refresh: function() {
		var self = this;
		var url = this.options.siteWhereApi + 'assignments/' + this.options.assignmentToken + 
			'/locations?tenantAuthToken=' + this.options.tenantAuthToken;
		L.SiteWhere.Util.getJSON(url, 
			function(locations, status, jqXHR) { 
				self._onLocationsLoaded(locations); }, 
			function(jqXHR, textStatus, errorThrown) { 
				self._onLocationsFailed(jqXHR, textStatus, errorThrown); }
		);
	},
	
	/** Pan map to most recent location */
	panToLastLocation: function(map) {
		if (this.lastLocation) {
    		map.panTo(this.lastLocation);
		}
	},
	
	/** LatLng for last location in list */
	lastLocation: null,
	
	/** Called when location data has been loaded successfully */
	_onLocationsLoaded: function(locations) {
    	this.clearLayers();
    	this.lastLocation = null;
    	
		var location, results = locations.results;
		var marker;
		
		// Add newest last.
		results.reverse();
		
		// Add a marker for each location.
    	var latLngs = [];
    	var latLng;
		for (var locIndex = 0; locIndex < results.length; locIndex++) {
			location = results[locIndex];
			if (this.options.showMarkers) {
				marker = this._createMarkerForLocation(location);
				this.addLayer(marker);
			}
			latLng = new L.LatLng(location.latitude, location.longitude);
    		latLngs.push(latLng);
    		this.lastLocation = latLng;
		}
    	if ((latLngs.length > 0) && (this.options.showLine)) {
    		this._createLineForLocations(this, latLngs);
    	}
		
		// Callback for actions taken after locations are loaded.
		if (this.options.onLocationsLoaded != null) {
			this.options.onLocationsLoaded();
		}
	},
	
	/** Create a marker for the given location */
	_createMarkerForLocation: function(location) {
		return L.marker([location.latitude, location.longitude]).bindPopup(location.assetName);
	},
	
	/** Create a line that connects the locations */
	_createLineForLocations: function(layer, latLngs) {
		var line = L.polyline(latLngs, this.options.lineOptions);
		layer.addLayer(line);	
	},
	
	/** Called when location data load fails */
	_onLocationsFailed: function(jqXHR, textStatus, errorThrown) {
		this._handleError('Locations load failed. ' + errorThrown);
	},
	
	/** Handle error condition if no assignment token was specified */
	_handleNoAssignmentToken: function() {
		this._handleError('No assignment token specified.');
	},
	
	/** Handle error condition if no tenant authentication token was specified */
	_handleNoTenantAuthToken: function() {
		alert('No tenant authentication token.');
	},
	
	/** Handle error in processing */
	_handleError: function(message) {
		if (onError != null) {
			onError(message);
		}
	}
});

L.FeatureGroup.SiteWhere.assignmentLocations = function (options) {
	return new L.FeatureGroup.SiteWhere.AssignmentLocations(options);
};

/*
 * Container for SiteWhere classes.
 */
L.SiteWhere = {};

/*
 * SiteWhere utility functions.
 */
L.SiteWhere.Util = L.Class.extend({
	
	statics: {
		
		/** Make a JSONP GET request */
		getJSON: function(url, onSuccess, onFail) {
			return jQuery.ajax({
				'type' : 'GET',
				'dataType': 'jsonp',
				'url' : url,
				'contentType' : 'application/json',
				'success' : onSuccess,
				'error' : onFail
			});
		},
	},
})