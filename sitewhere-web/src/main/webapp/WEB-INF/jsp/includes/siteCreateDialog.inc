<style>
.sc-map {
	height: 290px;
	width: 525px;
	border: 1px solid #cccccc;
}
</style>

<!-- Dialog for create/update -->
<div id="sc-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 id="sc-dialog-header" data-i18n="includes.siteCreateDialog.CreateSite"></h3>
	</div>
	<div class="modal-body">
		<div id="sc-tabs">
			<ul>
				<li class="k-state-active" >&nbsp;<font data-i18n="includes.siteCreateDialog.SiteDetails"></font></li>
				<li >&nbsp;<font data-i18n="includes.siteCreateDialog.MapInformation"></font></li>
				<li >&nbsp;<font data-i18n="public.Metadata"></font></li>
			</ul>
			<div>
				<form id="general-form" class="form-horizontal" style="padding-top: 20px;">
					<div class="control-group">
						<label class="control-label" for="gen-site-name" data-i18n="includes.siteCreateDialog.SiteName"></label>
						<div class="controls">
							<input type="text" id="gen-site-name" class="input-xlarge" title="Site name">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="gen-site-desc" data-i18n="includes.siteCreateDialog.SiteDescription"></label>
						<div class="controls">
							<textarea id="gen-site-desc" name="siteDesc" class="input-xlarge" style="height: 120px;"></textarea>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="gen-site-image-url" data-i18n="includes.siteCreateDialog.ImageURL"></label>
						<div class="controls">
							<input type="text" id="gen-site-image-url" name="siteImageUrl" class="input-xlarge">
						</div>
					</div>
				</form>
			</div>
			<div>
				<form class="form-horizontal" style="padding-top: 15px;">
					<div class="control-group" style="border-bottom: 1px solid #eeeeee; padding-bottom: 10px;">
						<label class="control-label" for="sc-map-type" data-i18n="includes.siteCreateDialog.MapType"></label>
						<div class="controls">
							<input id="sc-map-type"/>
						</div>
					</div>
				</form>
				<div id="map-type-forms">
					<form id="mapquest" class="form-horizontal">
						<div class="control-group">
							<label class="control-label" for="mq-center-latitude" data-i18n="includes.siteCreateDialog.CenterLatitude"></label>
							<div class="controls">
								<input type="text" id="mq-center-latitude" class="input-large">
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="mq-center-longitude" data-i18n="includes.siteCreateDialog.CenterLongitude"></label>
							<div class="controls">
								<input type="text" id="mq-center-longitude" class="input-large">
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="mq-zoom-level" data-i18n="includes.siteCreateDialog.ZoomLevel"></label>
							<div class="controls">
								<input type="text" id="mq-zoom-level" class="input-large">
							</div>
						</div>
						<div class="control-group">
							<label class="control-label"></label>
							<div class="controls">
								<a href="javascript:void(0)" onclick="scShowMap()" data-i18n="includes.siteCreateDialog.CLAZOM">
								</a>
							</div>
						</div>
					</form>
					<form id="geoserver" class="form-horizontal hide">
						<div class="control-group">
							<label class="control-label" for="gs-base-url">GeoServer Base URL</label>
							<div class="controls">
								<input type="text" id="gs-base-url" title="GeoServer base url" class="input-xlarge">
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="gs-layer-name">GeoServer Layer</label>
							<div class="controls">
								<input type="text" id="gs-layer-name" title="Layer name" class="input-xlarge">
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="gs-center-latitude" data-i18n="includes.siteCreateDialog.CenterLatitude"></label>
							<div class="controls">
								<input type="text" id="gs-center-latitude" class="input-large">
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="gs-center-longitude" data-i18n="includes.siteCreateDialog.CenterLongitude"></label>
							<div class="controls">
								<input type="text" id="gs-center-longitude" class="input-large">
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="gs-zoom-level" data-i18n="includes.siteCreateDialog.ZoomLevel"></label>
							<div class="controls">
								<input type="text" id="gs-zoom-level" class="input-large">
							</div>
						</div>
						<div class="control-group">
							<label class="control-label"></label>
							<div class="controls">
								<a href="javascript:void(0)" onclick="scShowMap()" data-i18n="includes.siteCreateDialog.CLAZOM">
								</a>
							</div>
						</div>
					</form>
				</div>
			</div>
			<div>
				<div id="sc-metadata">
					<jsp:include page="../includes/metadata.jsp" flush="true">
					    <jsp:param name="uid" value="sc"/>
					</jsp:include>
				</div>
            </div>
		</div>
		<div id="sc-map-content" class="hide">
			<div id="sc-map" class="sc-map"></div>
		</div>
	</div>
	<input id="site-token" type="hidden" value="" />
	<div class="modal-footer">
		<div id="sc-buttons">
			<a href="javascript:void(0)" class="btn" data-dismiss="modal" data-i18n="public.Cancel"></a> 
			<a id="sc-dialog-submit" href="javascript:void(0)" class="btn btn-primary" data-i18n="public.Create"></a>
		</div>
		<div id="sc-map-buttons" class="hide">
			<a href="javascript:void(0)" class="btn" onclick="scHideMap(false)" data-i18n="public.Cancel"></a> 
			<a href="javascript:void(0)" class="btn btn-primary" onclick="scHideMap(true)" data-i18n="includes.siteCreateDialog.UseTheseSettings"></a>
		</div>
	</div>
</div>

<!-- Script support for site create/update dialog -->
<script>
	/** Function called when dialog is submitted */
	var scSubmitCallback;
	
	/** Pointer to tabs instance */
	var scTabs;
	
	/** Reference for sites datasource */
	var scSitesDS;
	
	/** Available map types shown in dropdown */
    var scMapTypes = [
		{ text: "MapQuest World Map", value: "mapquest" },
		{ text: "GeoServer Layer", value: "geoserver" },
	];
	
	/** Map reference */
	var scMap;

	$(document).ready(function() {
		
		/** Create the tab strip */
		scTabs = $("#sc-tabs").kendoTabStrip({
			animation: false
		}).data("kendoTabStrip");

    	// create DropDownList from input HTML element
    	$("#sc-map-type").kendoDropDownList({
    		dataTextField: "text",
    		dataValueField: "value",
    	    dataSource: scMapTypes,
    	    index: 0,
    	    change: scMapTypeChanged
    	});
		
        /** Handle dialog submit */
		$('#sc-dialog-submit').click(function(event) {
			event.preventDefault();
			if (!scValidate()) {
				return;
			}
			var siteToken = $('#site-token').val();
			var siteData = scCreateSiteData();
			if (siteToken == "") {
				$.postJSON("${pageContext.request.contextPath}/api/sites", 
						siteData, onSuccess, onCreateFail);
			} else {
				$.putJSON("${pageContext.request.contextPath}/api/sites/" + siteToken, 
						siteData, onSuccess, onUpdateFail);
			}
		});
        
        /** Called on successful create/update */
        function onSuccess() {
        	$('#sc-dialog').modal('hide');
			if (scSubmitCallback != null) {
				scSubmitCallback();
			}
        }
        
		/** Handle failed call to create site */
		function onCreateFail(jqXHR, textStatus, errorThrown) {
			handleError(jqXHR, "Unable to create site.");
		}
        
		/** Handle failed call to update site */
		function onUpdateFail(jqXHR, textStatus, errorThrown) {
			handleError(jqXHR, "Unable to update site.");
		}
    });
    
    /** Create site data structure based on form input */
    function scCreateSiteData() {
		var mapMetadata = scBuildMapMetadata();
		var siteData = {
			"name": $('#gen-site-name').val(), 
			"description": $('#gen-site-desc').val(), 
			"imageUrl": $('#gen-site-image-url').val(), 
			"metadata": swMetadataAsLookup(scMetadataDS.data()),
			"map": {
				"type": $('#sc-map-type').val(), 
				"metadata": mapMetadata,
			}
		}
		return siteData;
    }
	
	/** Validate everything */
	function scValidate() {
		$.validity.setup({ outputMode:"label" });
		$.validity.start();

        /** Validate main form */
		$("#gen-site-name").require();
        
		var selectedMapType = $("#sc-map-type").val();
		
        /** Validate geoserver form */
		if (selectedMapType == "geoserver") {
			$("#gs-base-url").require();
			$("#gs-layer-name").require();
		}
      
		var result = $.validity.end();
		return result.valid;
	}

	/** Open the site create dialog */
	function scOpen(e, callback) {
		scClearDialog();
		$('#sc-dialog-header').html(i18next("includes.siteCreateDialog.CreateSite"));
		$('#sc-dialog-submit').html(i18next("public.Create"));
		$('#site-token').val("");
		scMetadataDS.data(new Array());
		scTabs.select(0);
		
		// Function called on submit.
		scSubmitCallback = callback;
		
		$.validity.clear();
		$('#sc-dialog').modal('show');
	}
	
	/** Show map for current info */
	function scShowMap() {
		$('#sc-tabs').addClass("hide");
		$('#sc-map-content').removeClass("hide");
		$('#sc-buttons').addClass("hide");
		$('#sc-map-buttons').removeClass("hide");
		
		var siteData = scCreateSiteData();
		
		// Creates map if not already created, then inits from site settings.
		if (scMap) {
			scMap.remove();
		}
		scMap = L.map('sc-map');
		swInitMapForSite(scMap, siteData);
	}
	
	/** Show map for current info */
	function scHideMap(useSettings) {
		$('#sc-tabs').removeClass("hide");
		$('#sc-map-content').addClass("hide");
		$('#sc-buttons').removeClass("hide");
		$('#sc-map-buttons').addClass("hide");
		
		if (useSettings) {
			scUpdateFormFromMap();
		}
	}
	
	/** Called to load the given site and open it for edit */
	function suOpen(siteToken, callback) {
		// Function called on submit.
		scSubmitCallback = callback;
		
		// Get site for edit.
		$.getJSON("${pageContext.request.contextPath}/api/sites/" + siteToken, 
				suSiteGetSuccess, suSiteGetFailed);
	}
    
    /** Called on successful site load request */
    function suSiteGetSuccess(data, status, jqXHR) {
		scClearDialog();
		$('#sc-dialog-header').html(i18next("public.EditSite"));
		$('#sc-dialog-submit').html(i18next("public.Save"));
		$('#gen-site-name').val(data.name);
		$('#gen-site-desc').val(data.description);
		$('#gen-site-image-url').val(data.imageUrl);
		$('#site-token').val(data.token);
		scMetadataDS.data(swLookupAsMetadata(data.metadata));
		scSelectMapType(data.map.type);
		scLoadMapFormFromMetadata(data.map);
		scTabs.select(0);
		
		$.validity.clear();
		$('#sc-dialog').modal('show');
    }
    
	/** Handle error on getting site */
	function suSiteGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load site for edit.");
	}
    
    /** Based on map type, load fields into proper form */
    function scLoadMapFormFromMetadata(map) {
    	var lookup = map.metadata;
    	
    	if (map.type == "mapquest") {
    		$('#mq-center-latitude').val(scGetMetadataValue(lookup, 'centerLatitude'));
    		$('#mq-center-longitude').val(scGetMetadataValue(lookup, 'centerLongitude'));
    		$('#mq-zoom-level').val(scGetMetadataValue(lookup, 'zoomLevel'));
    	} else if (map.type == "geoserver") {
    		$('#gs-base-url').val(scGetMetadataValue(lookup, 'geoserverBaseUrl'));
    		$('#gs-layer-name').val(scGetMetadataValue(lookup, 'geoserverLayerName'));
    		$('#gs-center-latitude').val(scGetMetadataValue(lookup, 'centerLatitude'));
    		$('#gs-center-longitude').val(scGetMetadataValue(lookup, 'centerLongitude'));
    		$('#gs-zoom-level').val(scGetMetadataValue(lookup, 'zoomLevel'));
    	}
    }
    
    /** Pull data from map form and populate metadata */
    function scBuildMapMetadata() {
    	var metadata = {};
		var mapType = $("#sc-map-type").val();
		
		if (mapType == "mapquest") {
			scAddMapMetadataEntry(metadata, 'centerLatitude', $('#mq-center-latitude').val());
			scAddMapMetadataEntry(metadata, 'centerLongitude', $('#mq-center-longitude').val());
			scAddMapMetadataEntry(metadata, 'zoomLevel', $('#mq-zoom-level').val());
		} else if (mapType == "geoserver") {
			scAddMapMetadataEntry(metadata, 'geoserverBaseUrl', $('#gs-base-url').val());
			scAddMapMetadataEntry(metadata, 'geoserverLayerName', $('#gs-layer-name').val());
			scAddMapMetadataEntry(metadata, 'centerLatitude', $('#gs-center-latitude').val());
			scAddMapMetadataEntry(metadata, 'centerLongitude', $('#gs-center-longitude').val());
			scAddMapMetadataEntry(metadata, 'zoomLevel', $('#gs-zoom-level').val());
		}
		return metadata;
    }
    
    /** Update existing form data from map settings */
    function scUpdateFormFromMap() {
    	var metadata = scBuildMapMetadata();
		var center = scMap.getCenter();
		var zoom = scMap.getZoom();
		scAddMapMetadataEntry(metadata, 'centerLatitude', center.lat);
		scAddMapMetadataEntry(metadata, 'centerLongitude', center.lng);
		scAddMapMetadataEntry(metadata, 'zoomLevel', zoom);
		var map = {};
		map.type = scGetMapType();
		map.metadata = metadata;
		scLoadMapFormFromMetadata(map);
    }
    
    /** Add a single entry into the map metadata */
    function scAddMapMetadataEntry(metadata, name, value) {
    	metadata[name] = value;
    }
    
    /** Gets the value associated with a given field name */
    function scGetMetadataValue(lookup, field) {
    	if (lookup && lookup[field]) {
    		return lookup[field];
    	}
    	return "";
    }
    
    /** Clear all dialog fields */
    function scClearDialog() {
    	$('#general-form')[0].reset();
    	$('#mapquest')[0].reset();
    	$('#geoserver')[0].reset();
    }
	
	/** Select the given map type and show its associated panel */
	function scSelectMapType(type) {
		var dropdownlist = $("#sc-map-type").data("kendoDropDownList");
		dropdownlist.value(type);
		scMapTypeChanged();
	}
	
	/** Get the currently selected map type */
	function scGetMapType() {
		return $("#sc-map-type").val();
	}
	
	/** Called when map type dropdown value changes */
	function scMapTypeChanged() {
		var selectedMapType = scGetMapType();
		$("#map-type-forms").children().each(function(i) {
			if (selectedMapType == $(this).attr("id")) {
				$(this).removeClass("hide");
			} else if (!$(this).hasClass("hide")) {
				$(this).addClass("hide");
			}
		});
	}
</script>