<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="Assignment Emulator" />
<c:set var="sitewhere_section" value="sites" />
<c:set var="use_map_includes" value="true" />
<c:set var="use_mqtt" value="true" />
<%@ include file="../includes/top.inc"%>

<style>
	.emulator-map {
		height: 400px; 
		margin-top: 10px; 
		border: 1px solid #cccccc;
		cursor: crosshair;
	}
	
	#mc-measurements .k-grid-content {
		min-height: 150px;
	}
</style>

<!-- Title Bar -->
<div class="sw-title-bar content k-header" style="margin-bottom: -1px;">
	<h1 class="ellipsis" data-i18n="assignments.emulator.title"></h1>
	<div class="sw-title-bar-right">
		<a id="btn-assignment-detail" class="btn" href="detail.html?token=<c:out value="${assignment.token}"/>" data-i18n="assignments.emulator.AssignmentDetails">
			<i class="icon-circle-arrow-left sw-button-icon"></i></a>
	</div>
</div>

<!-- Detail panel for selected assignment -->
<div id="assignment-details" style="line-height: normal;"></div>

<!-- Tab panel -->
<div id="tabs">
	<ul>
		<li class="k-state-active" data-i18n="public.Emulator"></li>
		<li>MQTT</li>
	</ul>
	<div>
		<div class="k-header sw-button-bar">
			<div class="sw-button-bar-title" data-i18n="assignments.emulator.DeviceAssignmentEmulator"></div>
			<div>
				<a id="btn-refresh-locations" class="btn" href="javascript:void(0)" data-i18n="assignments.emulator.RefreshLocations">
					<i class="icon-refresh sw-button-icon"></i></a>
				<div class="btn-group">
					<a class="btn" href="javascript:void(0)" data-i18n="public.Create">
						<i class="icon-plus sw-button-icon"></i></a>
					<button class="btn dropdown-toggle" data-toggle="dropdown">
					<span class="caret"></span>
					</button>
					<ul class="dropdown-menu" style="text-align: left;">
						<li><a tabindex="-1" href="javascript:void(0)" onclick="mcOpen()" data-i18n="public.Measurements"></a></li>
						<li><a tabindex="-1" href="javascript:void(0)" onclick="acOpen()" data-i18n="public.Alert"></a></li>
					</ul>
				</div>			
				<a id="mqtt-btn-connect" class="btn btn-primary" href="javascript:void(0)" data-i18n="assignments.emulator.Connect">
					<i class="icon-bolt sw-button-icon"></i></a>
			</div>
		</div>
		<div id="emulator-map" class="emulator-map"></div>
	</div>
	<div>
		<div class="k-header sw-button-bar">
			<div class="sw-button-bar-title" data-i18n="assignments.emulator.MQTTInformation"></div>
			<div>
				<a id="mqtt-btn-test-connect" class="btn" href="javascript:void(0)" data-i18n="assignments.emulator.TestConnection">
					<i class="icon-bolt sw-button-icon"></i></a>
			</div>
		</div>
		<div>
			<div id="mqtt-tabs">
				<ul>
					<li class="k-state-active" data-i18n="public.Settings"></li>
					<li data-i18n="assignments.emulator.LastMessage"></li>
				</ul>
				<div>
					<form class="form-horizontal" style="padding-top: 20px; display: inline-block; vertical-align: top">
						<div class="control-group">
							<label class="control-label" for="mqtt-host-name" data-i18n="assignments.emulator.MQTTHostName"></label>
							<div class="controls">
								<input type="text" id="mqtt-host-name" value="<%= request.getServerName() %>"
									class="input-large" title="Host name">
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="mqtt-port" data-i18n="assignments.emulator.MQTTWebsocketPort"></label>
							<div class="controls">
								<input id="mqtt-port" type="number" value="61623" min="0" step="1" class="input-large"
									title="Port" onkeyup="this.value=this.value.replace(/[^\d]/,'')"/>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="mqtt-client-id" data-i18n="assignments.emulator.ClientID"></label>
							<div class="controls">
								<input type="text" id="mqtt-client-id" value="SiteWhereWeb"
									class="input-large">
							</div>
						</div>
					</form>
					<form class="form-horizontal" style="padding-top: 20px; display: inline-block; vertical-align: top">
						<div class="control-group">
							<label class="control-label" for="mqtt-username" data-i18n="public.Username"></label>
							<div class="controls">
								<input type="text" id="mqtt-username" class="input-large" value="admin">
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="mqtt-password" data-i18n="public.Password"></label>
							<div class="controls">
								<input type="password" id="mqtt-password" class="input-large" value="password">
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="mqtt-topic" data-i18n="assignments.emulator.Topic"></label>
							<div class="controls">
								<input type="text" id="mqtt-topic" class="input-large" value="SiteWhere/input/jsonbatch">
							</div>
						</div>
					</form>
				</div>
				<div>
					<div id="mqtt-last-message">
						<div style="padding: 25px; font-size: 14pt; text-align: center;" data-i18n="assignments.emulator.jsonmessage">
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<!-- Dialog for creating a new location -->
<div id="lc-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 data-i18n="assignments.emulator.CreateLocation"></h3>
	</div>
	<div class="modal-body">
		<div id="lc-tabs" style="clear: both;">
			<ul>
				<li class="k-state-active" data-i18n="public.Location"></li>
				<li data-i18n="public.Metadata"></li>
			</ul>
			<div>
				<form class="form-horizontal" style="padding-top: 20px">
					<div class="control-group">
						<label class="control-label" for="lc-lat" data-i18n="public.Latitude"></label>
						<div class="controls">
							<input type="number" id="lc-lat" class="input-large">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="lc-lng" data-i18n="public.Longitude"></label>
						<div class="controls">
							<input type="number" id="lc-lng" class="input-large">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="lc-elevation" data-i18n="public.Elevation"></label>
						<div class="controls">
							<input type="number" id="lc-elevation" class="input-large" value="0">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="lc-event-date" data-i18n="public.EventDate"></label>
						<div class="controls">
							<select id="lc-date-type" class="input-xlarge" 
								style="margin-bottom: 10px; width: 300px;"/>
							<input id="lc-event-date" class="input-large">
						</div>
					</div>
				</form>
			</div>
			<div>
				<div id="lc-metadata">
					<jsp:include page="../includes/metadata.jsp" flush="true">
					    <jsp:param name="uid" value="lc"/>
					</jsp:include>
				</div>
            </div>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal" data-i18n="public.Cancel"></a> 
		<a id="lc-dialog-submit" href="javascript:void(0)" class="btn btn-primary" data-i18n="public.Create"></a>
	</div>
</div>

<!-- Dialog for creating a new measurement -->
<script type="text/x-kendo-tmpl" id="tpl-sw-mx-entry">
	<tr class="sw-list-entry">
		<td style="width: 205px">#:name#</td>
		<td style="width: 145px">#:value#</td>
		<td>
			<div style="text-align: right;">
				<i class="icon-remove sw-action-glyph sw-delete-glyph" title="Delete Measurement"
					onclick="mcDeleteMeasurement('#:name#')"></i>
			</div>
		</td>
	</tr>
</script>
<div id="mc-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 data-i18n="assignments.emulator.CreateMeasurements"></h3>
	</div>
	<div class="modal-body">
		<div id="mc-tabs" style="clear: both;">
			<ul>
				<li class="k-state-active" data-i18n="public.Measurements"></li>
				<li data-i18n="public.Metadata"></li>
			</ul>
			<div>
				<div class="sw-sublist-header">
					<div style="width: 205px;" data-i18n="public.Name"></div>
					<div style="width: 145px" data-i18n="public.Value"></div>
				</div>
				<table id="mc-measurements" class="sw-sublist-list" style="height: 150px;">
				</table>
				<div class="sw-sublist-add-new">
					<div class="sw-sublist-footer">
						<div style="width: 225px; margin-left: 3px;" data-i18n="public.Name"></div>
						<div style="width: 145px" data-i18n="public.Value"></div>
					</div>
					<input type="text" id="sw-mx-name" 
						style="width: 205px; margin-bottom: 0px; margin-right: 10px;" title="Measurement name">
					<input type="text" id="sw-mx-value" 
						style="width: 150px; margin-bottom: 0px; margin-right: 10px;" title="Measurement value">
					<a class="btn" href="javascript:void(0)" onclick="mcAddMeasurement()" data-i18n="public.Add">
						<i class="icon-plus sw-button-icon"></i></a>
					<div id="sw-mx-error" style="color: #f00; display: none;"></div>
				</div>	
				<form class="form-horizontal" style="padding-top: 20px">
					<div class="control-group">
						<label class="control-label" for="lc-event-date" data-i18n="public.EventDate"></label>
						<div class="controls">
							<select id="mc-date-type" class="input-xlarge" 
								style="margin-bottom: 10px; width: 300px;"/>
							<input id="mc-event-date" class="input-large">
						</div>
					</div>
				</form>
            </div>
			<div>
				<div id="mc-metadata">
					<jsp:include page="../includes/metadata.jsp" flush="true">
					    <jsp:param name="uid" value="mc"/>
					</jsp:include>
				</div>
            </div>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal" data-i18n="public.Cancel"></a> 
		<a id="mc-dialog-submit" href="javascript:void(0)" class="btn btn-primary" data-i18n="public.Create"></a>
	</div>
</div>

<!-- Dialog for creating a new location -->
<div id="ac-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 data-i18n="assignments.emulator.CreateAlert"></h3>
	</div>
	<div class="modal-body">
		<div id="ac-tabs" style="clear: both;">
			<ul>
				<li class="k-state-active" data-i18n="public.Alert"></li>
				<li data-i18n="public.Metadata"></li>
			</ul>
			<div>
				<form class="form-horizontal" style="padding-top: 20px">
					<div class="control-group">
						<label class="control-label" for="ac-type" data-i18n="assignments.emulator.AlertType"></label>
						<div class="controls">
							<input type="text" id="ac-type" class="input-xlarge">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="ac-message" data-i18n="public.Message"></label>
						<div class="controls">
							<textarea id="ac-message" class="input-xlarge" style="height: 120px;"></textarea>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="ac-event-date" data-i18n="public.EventDate"></label>
						<div class="controls">
							<select id="ac-date-type" class="input-xlarge" 
								style="margin-bottom: 10px; width: 300px;"/>
							<input id="ac-event-date" class="input-large">
						</div>
					</div>
				</form>
			</div>
			<div>
				<div id="ac-metadata">
					<jsp:include page="../includes/metadata.jsp" flush="true">
					    <jsp:param name="uid" value="ac"/>
					</jsp:include>
				</div>
            </div>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal" data-i18n="public.Cancel"></a> 
		<a id="ac-dialog-submit" href="javascript:void(0)" class="btn btn-primary" data-i18n="public.Create"></a>
	</div>
</div>

<%@ include file="../includes/templateAssignmentEntry.inc"%>

<%@ include file="../includes/commonFunctions.inc"%>

<script>
    /** Set sitewhere_title */
    sitewhere_i18next.sitewhere_title = "assignments.emulator.title";

	/** Assignment token */
	var token = '<c:out value="${assignment.token}"/>';
	
	/** Site token */
	var siteToken = '<c:out value="${assignment.siteToken}"/>';
	
	/** Device hardware id */
	var hardwareId = '<c:out value="${assignment.deviceHardwareId}"/>';

	/** Tab strip */
	var tabs;
	
	/** Tabs for MQTT */
	var mqttTabs;

	/** Provides external access to tabs */
	var lcTabs;
	
	/** Event date type */
	var lcDateType;
	
	/** Picker for event date */
	var lcDatePicker;

	/** Provides external access to tabs */
	var mcTabs;
	
	/** Measurements datasource */
	var mcMeasurementsDS;
	
	/** Event date type */
	var mcDateType;
	
	/** Picker for event date */
	var mcDatePicker;

	/** Provides external access to tabs */
	var acTabs;
	
	/** Event date type */
	var acDateType;
	
	/** Picker for event date */
	var acDatePicker;

	/** MQTT client */
	var client;
	
	/** Indicates if client is connected */
	var connected = false;
	
	/** Emulator map */
	var map;
	
	/** Indicates if we are testing the connection */
	var testingConnection = false;
	
	/** Shows text instructions for adding location */
	var tooltip;
	
	/** Layer that holds most recent location info */
	var locationsLayer;
	
	/** Last location recorded for the selected assignment */
	var lastLocation;
	
	/** Attempt to connect */
	function doConnect() {
		if (client && connected) {
			client.disconnect();
			delete client;
		}
		var host = $('#mqtt-host-name').val();
		var port = $('#mqtt-port').val();
		var clientId = $('#mqtt-client-id').val();
		var username = $('#mqtt-username').val();
		var password = $('#mqtt-password').val();
		
		client = new Messaging.Client(host, Number(port), clientId);
		client.onConnectionLost = onConnectionLost;
		client.onMessageArrived = onMessageArrived;
		client.connect({userName:username, password:password, onSuccess:onConnect, onFailure:onConnectFailed});
	}
	
	/** Stores connection values for next page load if localStorage is supported */
	function saveValues(host, port, clientId, username, password) {
		if (Modernizr.localStorage) {
			localStorage.swEmulatorMqttHost = host;
			localStorage.swEmulatorMqttPort = port;
			localStorage.swEmulatorMqttClientId = clientId;
			localStorage.swEmulatorMqttUsername = username;
			localStorage.swEmulatorMqttPassword = password;
		}
	}
	
	/** Called on successful connection */
	function onConnect() {
		if (testingConnection) {
			swAlert(i18next("assignments.Connected"), i18next("assignments.emulator.MQTTCS"));
		}
//		saveValues(host, port, clientId, username, password);
		showConnectedButton();
		testingConnection = false;
		connected = true;
	}
	
	/** Called if connection fails */
	function onConnectFailed() {
		swAlert(i18next("assignments.emulator.ConnectFailed"), i18next("assignments.emulator.MQTTCF"));
		showConnectButton();
		testingConnection = false;
		connected = false;
	}
	
	/** Called if connection is broken */
	function onConnectionLost(responseObject) {
		showConnectButton();
		connected = false;
	}
	
	/** Send a message to the given destination */
	function sendMessage(payload, destinationOverride) {
		if (checkConnected()) {
			var message = new Messaging.Message(payload);
			var destination = $('#mqtt-topic').val();
			if (destinationOverride) {
				destination = destinationOverride;
			}
			message.destinationName = destination;
			client.send(message);
			$('#mqtt-last-message').html("<pre><code>" + payload + "</code></pre>");
			hljs.highlightBlock(document.getElementById('mqtt-last-message').childNodes[0]);
		}
	}
	
	/** Called when a message is received */
	function onMessageArrived(message) {
	}
    
    /** Brings locations layer to front once zones are loaded */
    function onZonesLoaded() {
        initMap();
        hideTooltip();
    }
	
	/** Show the connect button */
	function showConnectButton() {
		$('#mqtt-btn-connect').removeClass('btn-sw-success').addClass('btn-primary').html('<i class="icon-bolt sw-button-icon"></i> '+i18next("assignments.emulator.Connect"));
		$('#mqtt-btn-connect').click(function(event) {
			event.preventDefault();
			doConnect();
		});
	}
	
	/** Hide the connect button */
	function showConnectedButton() {
		$('#mqtt-btn-connect').removeClass('btn-primary').addClass('btn-sw-success').html('<i class="icon-check sw-button-icon"></i> '+i18next("assignments.Connected"));
		$('#mqtt-btn-connect').unbind('click');
	}
	
	/** Initialize the map */
	function initMap() {
		// Create tooltip for help message.
		tooltip = new L.Tooltip(map);
		tooltip.updateContent({text: "Click map to add a new location"});
		
		// Hook up mouse events for tooltip.
		map.on('mousemove', onMouseMove);
		map.on('mouseout', onMouseOut);
		map.on('click', onMouseClick);
		
		// Create layer for locations
		locationsLayer = L.FeatureGroup.SiteWhere.assignmentLocations({
			siteWhereApi: '${pageContext.request.contextPath}/api/',
			assignmentToken: token,
			onLocationsLoaded: onLocationsUpdated,
		});
		map.addLayer(locationsLayer);
	}
	
	/** Refresh the location layer */
	function refreshLocations() {
		locationsLayer.refresh();
	}
	
	/** When locations are updated, re-recenter map on last location */
	function onLocationsUpdated() {
		locationsLayer.panToLastLocation(map);
	}
	
	/** Called when mouse moves over map */
	function onMouseMove(e) {
		tooltip.updatePosition(e.latlng);
	}
	
	/** Called when mouse moves out of map area */
	function onMouseOut(e) {
		hideTooltip();
	}
	
	/** Hide tooltip by moving it to southeast corner */
	function hideTooltip() {
		var se = map.getBounds().getSouthEast();
		tooltip.updatePosition(se);
	}
	
	/** Called when mouse is clicked over map */
	function onMouseClick(e) {
		lcOpen(e.latlng.lat, e.latlng.lng);
	}
	
	/** Create an option for the event date dropdown */
	function createDateOption(label, value) {
		var option = {};
		option.label = label;
		option.value = value;
		return option;
	}
	
	/** Create option for a delta based on current time and offset in minutes */
	function createDeltaOption(label, deltaInMinutes) {
		return createDateOption("Set to " + label + " after last location event", 
				"delta" + deltaInMinutes);
	}
	
	/** Create an option that accesses a date picker */
	function createDatePickerOption() {
		return createDateOption("Choose date/time for event", "picker");
	}
	
	/** Create an option that returns the current date/time */
	function createCurrentOption(datePicker) {
		return createDateOption("Use current date/time for event", "current");
	}
	
	/** Open the location dialog */
	function lcOpen(lat, lng) {
		lcTabs.select(0);
		if (checkConnected()) {
			$("#lc-lat").val(lat);
			$("#lc-lng").val(lng);
			$("#lc-elevation").val("0");
			lcDatePicker.value(new Date());
			lcMetadataDS.data(new Array());
			
			var dataSource = createOptionsDatasource();
			lcDateType.setDataSource(dataSource);
			lcDateTypeChanged();
			
			$('#lc-dialog').modal('show');
		}
	}
	
	/** Called when event date type dropdown selection changes */
	function lcDateTypeChanged() {
		var option = lcDateType.dataItem();
		$('#lc-dialog .k-datetimepicker').removeClass("hide");
		if (option.value != "picker") {
			$('#lc-dialog .k-datetimepicker').addClass("hide");
		}
	}
	
	/** Get date value based on dropdown setting */
	function calculateDateValue(dropdown, picker) {
		var option = dropdown.dataItem();
		if (option.value == "current") {
			return new Date();
		}
		if (option.value == "picker") {
			var result = picker.value();
			if (!result) {
				swAlert("Error", "The date entered is invalid.");
			}
			return result;
		}
		if (option.value.startsWith("delta")) {
			var nowMillis = (kendo.parseDate(lastLocation.eventDate)).getTime();
			var delta = Number(option.value.substr(5));
			var deltaMillis = delta * 60 * 1000;
			return new Date(nowMillis + deltaMillis);
		}
		return new Date();
	}
	
	/** Create datasource for event date options */
	function createOptionsDatasource() {
		var options = [];
		options.push(createCurrentOption());
		options.push(createDatePickerOption());
		if (lastLocation) {
			options.push(createDeltaOption("five minutes", 5));
			options.push(createDeltaOption("fifteen minutes", 15));
			options.push(createDeltaOption("one hour", 60));
		}
		var dataSource = new kendo.data.DataSource({
			data: options
		});
		return dataSource;
	}
	
	/** Submit location data via MQTT */
	function lcSubmit() {
		var lat = $("#lc-lat").val();
		var lng = $("#lc-lng").val();
		var elevation = $("#lc-elevation").val();
		var eventDate = calculateDateValue(lcDateType, lcDatePicker);
		if (!eventDate) {
			return;
		}
		var eventDateStr = asISO8601(eventDate);
		var batch = {"hardwareId": hardwareId};
		batch.locations = [{"latitude": lat, "longitude": lng, "elevation": elevation, 
			"eventDate": eventDateStr, "metadata": swMetadataAsLookup(lcMetadataDS.data())}];
		sendMessage(JSON.stringify(batch, null, "\t"));
    	$('#lc-dialog').modal('hide');
    	setTimeout(refreshLocations, 2000);
	}
	
	/** Open the measurements dialog */
	function mcOpen() {
		mcTabs.select(0);
		if (checkConnected()) {
			mcMeasurementsDS.data(new Array());
			mcMetadataDS.data(new Array());
			
			var dataSource = createOptionsDatasource();
			mcDateType.setDataSource(dataSource);
			mcDateTypeChanged();
			
			$('#mc-dialog').modal('show');
		}
	}
	
	/** Called when event date type dropdown selection changes */
	function mcDateTypeChanged() {
		var option = mcDateType.dataItem();
		$('#mc-dialog .k-datetimepicker').removeClass("hide");
		if (option.value != "picker") {
			$('#mc-dialog .k-datetimepicker').addClass("hide");
		}
	}
	
	/** Add a new measurement */
	function mcAddMeasurement() {
		// Reset error.
		$("#sw-mx-error").hide();
		var error = "";
		
		// Create measurement entry.
		var mx = {};
		mx.name = $("#sw-mx-name").val();
		mx.value = $("#sw-mx-value").val();
		
		// Check for empty.
		if (mx.name.length == 0) {
			error = "Name is required.";
		}
		var nameRegex = /^[\w-_\.]+$/;
		if (!nameRegex.test(mx.name)) {
			error = "Invalid measurement in name."
		}
		
		// Check for empty.
		if (mx.value.length == 0) {
			error = "Value is required.";
		}
		var valueRegex = /^-?\d+\.?\d*$/;
		if (!valueRegex.test(mx.value)) {
			error = "Invalid value."
		}
		
		// Check for already used.
		var data = mcMeasurementsDS.data();
		for (var index = 0, existing; existing = data[index]; index++) {
			if (mx.name == existing.name) {
				error = "Measurment name is already being used.";
				break;
			}
		}
		if (error.length > 0) {
			$("#sw-mx-error").html(error);
			$("#sw-mx-error").toggle();
		} else {
			mcMeasurementsDS.data().push(mx);
			$("#sw-mx-name").val("");
			$("#sw-mx-value").val("");
		}
	}

	/** Deletes a measurement by name */
	function mcDeleteMeasurement(name) {
		var data = mcMeasurementsDS.data();
		for (var index = 0, existing; existing = data[index]; index++) {
			if (existing.name == name) {
				mcMeasurementsDS.data().splice(index, 1);
				return;
			}
		}
	}
	
	/** Submit measurements data via MQTT */
	function mcSubmit() {
		var eventDate = calculateDateValue(mcDateType, mcDatePicker);
		if (!eventDate) {
			return;
		}
		var eventDateStr = asISO8601(eventDate);
		var batch = {"hardwareId": hardwareId};
		batch.measurements = [{"eventDate": eventDateStr, 
			"measurements": swMetadataAsLookup(mcMeasurementsDS.data()), 
			"metadata": swMetadataAsLookup(mcMetadataDS.data())}];
		sendMessage(JSON.stringify(batch, null, "\t"));
    	$('#mc-dialog').modal('hide');
	}
	
	/** Open the alert dialog */
	function acOpen() {
		acTabs.select(0);
		if (checkConnected()) {
			$("#ac-type").val("");
			$("#ac-message").val("");
			acDatePicker.value(new Date());
			acMetadataDS.data(new Array());
			
			var dataSource = createOptionsDatasource();
			acDateType.setDataSource(dataSource);
			acDateTypeChanged();
			
			$('#ac-dialog').modal('show');
		}
	}
	
	/** Called when event date type dropdown selection changes */
	function acDateTypeChanged() {
		var option = acDateType.dataItem();
		$('#ac-dialog .k-datetimepicker').removeClass("hide");
		if (option.value != "picker") {
			$('#ac-dialog .k-datetimepicker').addClass("hide");
		}
	}
	
	/** Submit alert data via MQTT */
	function acSubmit() {
		var type = $("#ac-type").val();
		var message = $("#ac-message").val();
		var eventDate = calculateDateValue(acDateType, acDatePicker);
		if (!eventDate) {
			return;
		}
		var eventDateStr = asISO8601(eventDate);
		var batch = {"hardwareId": hardwareId};
		batch.alerts = [{"type": type, "message": message, "eventDate": eventDateStr,
			"metadata": swMetadataAsLookup(acMetadataDS.data())}];
		sendMessage(JSON.stringify(batch, null, "\t"));
    	$('#ac-dialog').modal('hide');
	}
	
	/** Make sure client is connected and warn if not */
	function checkConnected() {
		if (!connected) {
			swAlert(i18next("assignments.emulator.NotConnected"), i18next("assignments.emulator.MQTTNC"));
		}
		return connected;
	}
	
	/** Loads information for the selected assignment */
	function loadAssignment() {
		$.getJSON("${pageContext.request.contextPath}/api/assignments/" + token, 
			loadGetSuccess, loadGetFailed);
	}
    
    /** Called on successful assignment load request */
    function loadGetSuccess(data, status, jqXHR) {
		var template = kendo.template($("#tpl-assignment-entry").html());
		parseAssignmentData(data);
		data.inDetailView = true;
		$('#assignment-details').html(template(data));
    }
    
	/** Handle error on getting assignment data */
	function loadGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load assignment data.");
	}
	
	/** Called when 'release assignment' is clicked */
	function onReleaseAssignment(e, token) {
		var event = e || window.event;
		event.stopPropagation();
		swReleaseAssignment(token, onReleaseAssignmentComplete);
	}
	
	/** Called after successful release assignment */
	function onReleaseAssignmentComplete() {
		loadAssignment();
	}
	
	/** Called when 'missing assignment' is clicked */
	function onMissingAssignment(e, token) {
		var event = e || window.event;
		event.stopPropagation();
		swAssignmentMissing(token, onMissingAssignmentComplete);
	}
	
	/** Called after successful missing assignment */
	function onMissingAssignmentComplete() {
		loadAssignment();
	}
	
	$(document).ready(function() {
		
		/** Handle browsers without websocket support */
		if (!Modernizr.websockets) {
	        loadAssignment();
			$("#tabs").html('<div style="padding: 25px; font-size: 14pt; text-align: center;">' +
				'Your browser does not support websockets, which is required by the emulator. ' +
				'Download the latest version of your browser to enable this feature.' +
				'</div>');
			return;
		}
		
		/** Create the tab strip */
		tabs = $("#tabs").kendoTabStrip({
			animation: false
		}).data("kendoTabStrip");
		
		/** Create the MQTT tab strip */
		mqttTabs = $("#mqtt-tabs").kendoTabStrip({
			animation: false
		}).data("kendoTabStrip");
		
		/** Create tab strip */
		lcTabs = $("#lc-tabs").kendoTabStrip({
			animation: false
		}).data("kendoTabStrip");

    	// Create DropDownList for location event date type.
    	lcDateType = $("#lc-date-type").kendoDropDownList({
    		dataTextField: "label",
    		dataValueField: "value",
    	    index: 0,
    	    change: lcDateTypeChanged
    	}).data("kendoDropDownList");
		
        lcDatePicker = $("#lc-event-date").kendoDateTimePicker({
            value:new Date()
        }).data("kendoDateTimePicker");
		
        /** Handle location create dialog submit */
		$('#lc-dialog-submit').click(function(event) {
			lcSubmit();
		});
		
		/** Create tab strip */
		mcTabs = $("#mc-tabs").kendoTabStrip({
			animation: false
		}).data("kendoTabStrip");
		
		/** Local source for metadata entries */
		mcMeasurementsDS = swMetadataDatasource();
		
		/** Grid for measurements */
		$("#mc-measurements").kendoListView({
			dataSource : mcMeasurementsDS,
			template : kendo.template($("#tpl-sw-mx-entry").html())
		});

    	// Create DropDownList for measurements event date type.
    	mcDateType = $("#mc-date-type").kendoDropDownList({
    		dataTextField: "label",
    		dataValueField: "value",
    	    index: 0,
    	    change: mcDateTypeChanged
    	}).data("kendoDropDownList");
		
        mcDatePicker = $("#mc-event-date").kendoDateTimePicker({
            value:new Date()
        }).data("kendoDateTimePicker");
		
        /** Handle location create dialog submit */
		$('#mc-dialog-submit').click(function(event) {
			mcSubmit();
		});
		
		/** Create tab strip */
		acTabs = $("#ac-tabs").kendoTabStrip({
			animation: false
		}).data("kendoTabStrip");

    	// Create DropDownList for alert event date type.
    	acDateType = $("#ac-date-type").kendoDropDownList({
    		dataTextField: "label",
    		dataValueField: "value",
    	    index: 0,
    	    change: acDateTypeChanged
    	}).data("kendoDropDownList");
		
        acDatePicker = $("#ac-event-date").kendoDateTimePicker({
            value:new Date()
        }).data("kendoDateTimePicker");
		
        /** Handle location create dialog submit */
		$('#ac-dialog-submit').click(function(event) {
			acSubmit();
		});
		
        /** Handle dialog submit */
		$('#mqtt-btn-test-connect').click(function(event) {
			testingConnection = true;
			event.preventDefault();
			doConnect();
		});
		
        /** Handle 'refresh locations' button */
		$('#btn-refresh-locations').click(function(event) {
			refreshLocations();
		});
		
        /** Start in disconnected mode */
        showConnectButton();
       
        /** Create emulator map */
		map = L.Map.siteWhere('emulator-map', {
			siteWhereApi: '${pageContext.request.contextPath}/api/',
		    siteToken: siteToken,
		    onZonesLoaded: onZonesLoaded,
		});

        loadAssignment();
	});
</script>

<%@ include file="../includes/bottom.inc"%>