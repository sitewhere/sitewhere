<style>
.sw-map {
	height: 290px;
	width: 500px;
	border: 1px solid #dddddd;
}

.sw-name-label {
	display: inline-block;
	padding-right: 5px;
}
</style>

<!-- Dialog for creating a device -->
<div id="zc-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 id="zc-dialog-title" data-i18n="includes.zoneCreateDialog.CreateZone"></h3>
	</div>
	<div class="modal-body">
		<div id="zc-tabs" style="clear: both;">
			<ul>
				<li class="k-state-active" data-i18n="includes.zoneCreateDialog.ZoneSettings"></li>
				<li data-i18n="public.Metadata"></li>
			</ul>
			<div>
				<div id="zc-map" class="sw-map"></div>
				<div style="padding-top: 3px; width: 500px;">
					<label for="zc-zone-name" class="sw-name-label"><font data-i18n="includes.zoneCreateDialog.ZoneName"></font>:</label>
					<input id="zc-zone-name" type="text" style="padding: 0px 5px; margin: 0; width: 227px;"/>
					<label for="zc-border-picker" class="sw-name-label" style="margin-left: 10px;"><font data-i18n="includes.zoneCreateDialog.Border"></font>:</label>
					<input type="text" id="zc-border-picker"/>
					<label for="zc-fill-picker" class="sw-name-label" style="margin-left: 10px;"><font data-i18n="includes.zoneCreateDialog.Fill"></font>:</label>
					<input type="text" id="zc-fill-picker"/>
				</div>
			</div>
			<div>
				<div id="zc-metadata">
					<jsp:include page="../includes/metadata.jsp" flush="true">
					    <jsp:param name="uid" value="zc"/>
					</jsp:include>
				</div>
            </div>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal" data-i18n="public.Cancel"></a> 
		<a id="zc-dialog-submit" href="javascript:void(0)" class="btn btn-primary" data-i18n="public.Create"></a>
	</div>
</div>

<!-- Script support for zone create dialog -->
<script>
	/** Site that provides map metadata */
	var zcSite;
	
	/** Zone being edited if in update mode */
	var zcZone;
	
	/** Function called when dialog is submitted */
	var zcSubmitCallback;

	/** Provides external access to tabs */
	var zcTabs;
	
	/** Map reference */
	var zcMap;
	
	/** Leaflet.draw control */
	var zcDrawControl;
	
	/** Layer for existing zones */
	var zcZonesLayer;
	
	/** Layer for new zone */
	var zcNewZoneLayer;
	
	/** Feature group that holds edited layer */
	var zcEditFeatureGroup;

	$(document).ready(function() {
		
		/** Create tab strip */
		zcTabs = $("#zc-tabs").kendoTabStrip({
			animation: false
		}).data("kendoTabStrip");
		
        $("#zc-border-picker").spectrum({
            color: "#000",
            showButtons: false,
            change: function(color) {
            	zcUpdateDrawControls();
            }
        });
        $("#zc-fill-picker").spectrum({
            color: "#f00",
            showAlpha: true,
            showButtons: false,
            change: function(color) {
            	zcUpdateDrawControls();
            }
        });
		
        /** Handle create dialog submit */
		$('#zc-dialog-submit').click(function(event) {
			event.preventDefault();
			var name = $("#zc-zone-name").val();
			if (!(name && name.length > 0)) {
				swAlert("Enter Name", "Zone name must be specified.");
				return;
			}
			if (!zcNewZoneLayer) {
				swAlert(i18next("includes.zoneCreateDialog.CreateZone"), i18next("includes.zoneCreateDialog.NZHBDUTMTTCTZC"));
				return;
			}
			var latLongs = zcNewZoneLayer.getLatLngs();
			var coords = [];
			for (var index = 0; index < latLongs.length; index++) {
				coords.push({"latitude": latLongs[index].lat, "longitude": latLongs[index].lng, "elevation": 0})
			}
			var zoneData = {
				"borderColor": $("#zc-border-picker").spectrum("get").toHexString(), 
				"fillColor": $("#zc-fill-picker").spectrum("get").toHexString(), 
				"opacity": $("#zc-fill-picker").spectrum("get").alpha,
				"name": $("#zc-zone-name").val(),
				"coordinates": coords,
				"metadata": swMetadataAsLookup(zcMetadataDS.data()),
			}
			if (zcZone) {
				$.putJSON("${pageContext.request.contextPath}/api/zones/" + zcZone.token,
					zoneData, onCreateSuccess, onCreateFail);
			} else {
				$.postJSON("${pageContext.request.contextPath}/api/sites/" + zcSite.token + "/zones", 
					zoneData, onCreateSuccess, onCreateFail);
			}
		});
        
        /** Called on successful create/update */
        function onCreateSuccess() {
        	$('#zc-dialog').modal('hide');
			if (zcSubmitCallback != null) {
				zcSubmitCallback();
			}
        }
        
		/** Handle failed call to create zone */
		function onCreateFail(jqXHR, textStatus, errorThrown) {
			handleError(jqXHR, i18next("includes.zoneCreateDialog.UTCZ"));
		}
        
		/** Handle failed call to update zone */
		function onUpdateFail(jqXHR, textStatus, errorThrown) {
			handleError(jqXHR, "Unable to update zone.");
		}
    });
	
	/** Validate everything */
	function zcValidate() {
		$.validity.setup({ outputMode:"label" });
		$.validity.start();
      
		var result = $.validity.end();
		return result.valid;
	}
	
	/** Updates draw colors from color pickers */
	function zcUpdateDrawControls() {
		if (zcDrawControl) {
			zcDrawControl.removeFrom(zcMap);
		}
		zcDrawControl = null;
		var borderColor = $("#zc-border-picker").spectrum("get").toHexString();
		var fillColor = $("#zc-fill-picker").spectrum("get").toHexString();
		var fillAlpha = $("#zc-fill-picker").spectrum("get").alpha;
		if (zcZone) {
			zcDrawControl = swEnableMapEditing(zcMap, zcEditFeatureGroup);
		} else {
			zcDrawControl = swEnableMapDrawing(zcMap, borderColor, fillColor, fillAlpha);
		}
		
		if (zcNewZoneLayer) {
			zcNewZoneLayer.setStyle({color: borderColor, opacity: 1, fillColor: fillColor, fillOpacity: fillAlpha});
		}
	}
	
	/** Reset dialog state */
	function zcReset() {
		zcNewZoneLayer = null;
		zcDrawControl = null;
		zcZone = null;
		
		// Reset form and metadata.
		$('#zc-zone-name').val("");
		zcMetadataDS.data(new Array());
    	
    	// Select first tab.
		zcTabs.select(0);
		$.validity.clear();
	}
	
	/** Open the create dialog */
	function zcOpen(site, callback) {
		zcReset();
		
		// Store site info.
		zcSite = site;
		
		// Set dialog title
		$('#zc-dialog-title').html(i18next("includes.zoneCreateDialog.CreateZone"));
		$('#zc-dialog-submit').html(i18next("public.Create"));
		
		// Creates map if not already created, then inits from site settings.
		if (zcMap) {
			zcMap.remove();
		}
		zcMap = L.map('zc-map');
		zcUpdateDrawControls();
		zcMap.on('draw:drawstart', function (e) {
			if (zcNewZoneLayer) {
				zcMap.removeLayer(zcNewZoneLayer);
			}
		});		
		zcMap.on('draw:created', function (e) {
			zcNewZoneLayer = e.layer;
			zcMap.addLayer(zcNewZoneLayer);
		});		
		swInitMapForSite(zcMap, zcSite);
		
		// Function called on submit.
		zcSubmitCallback = callback;
		
		$('#zc-dialog').modal('show');
		zcMap.invalidateSize();
	}
	
	/** Open zone update dialog */
	function zuOpen(site, zoneToken, callback) {
		zcReset();
		
		// Store site info.
		zcSite = site;
		
		// Set dialog title
		$('#zc-dialog-title').html(i18next("includes.EditZone"));
		$('#zc-dialog-submit').html(i18next("public.Save"));
		
		// Creates map if not already created, then inits from site settings.
		if (zcMap) {
			zcMap.remove();
		}
		zcMap = L.map('zc-map');
		swInitMapForSite(zcMap, zcSite, zoneToken);
		
		// Function called on submit.
		zcSubmitCallback = callback;
		
		// Asyncronously load zones and add layer to map.
		$.getJSON("/sitewhere/api/zones/" + zoneToken, zuOpenGetSuccess, 
				function(jqXHR, textStatus, errorThrown) { handleError(jqXHR, "Unable to get zone for update."); }
		);
	}
	
	/** Called after zone has been loaded for update */
	function zuOpenGetSuccess(zone) {
		zcZone = zone;
		
		zcEditFeatureGroup = new L.FeatureGroup();
		zcNewZoneLayer = swCreatePolygonForZone(zone);
		zcEditFeatureGroup.addLayer(zcNewZoneLayer);		
		zcMap.addLayer(zcEditFeatureGroup);
		zcEditFeatureGroup.bringToFront();
		
		zcDrawControl = swEnableMapEditing(zcMap, zcEditFeatureGroup);
		
		$('#zc-zone-name').val(zone.name);
		$("#zc-border-picker").spectrum("set", tinycolor(zone.borderColor));
		var fillColor = tinycolor(zone.fillColor).toHsv();
		fillColor.a = zone.opacity;
		$("#zc-fill-picker").spectrum("set", fillColor);
		zcMetadataDS.data(swLookupAsMetadata(zone.metadata));
		
		$('#zc-dialog').modal('show');
		zcMap.invalidateSize();
		
		var bounds = zcNewZoneLayer.getBounds();
		zcMap.fitBounds(bounds, {padding: [0, 0]});
	}
</script>