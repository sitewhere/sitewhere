<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="View Device" />
<c:set var="sitewhere_section" value="devices" />
<%@ include file="../includes/top.inc"%>

<style>
.event-pager {
	margin-top: 10px;
}
</style>

<!-- Title Bar -->
<div class="sw-title-bar content k-header" style="margin-bottom: -1px;">
	<h1 class="ellipsis" data-i18n="" data-i18n="devices.detail.title"></h1>
	<div class="sw-title-bar-right">
		<a id="btn-edit-device" class="btn" href="javascript:void(0)" data-i18n="public.EditDevice">
			<i class="icon-pencil sw-button-icon"></i></a>
	</div>
</div>

<!-- Detail panel for selected device -->
<div id="device-details" style="line-height: normal;"></div>

<!-- Tab panel -->
<div id="tabs">
	<ul>
		<li class="k-state-active">&nbsp;<font data-i18n="devices.detail.AssignmentHistory"></font></li>
<c:choose>
	<c:when test="${specification.containerPolicy == 'Composite'}">
		<li>&nbsp;<font data-i18n="public.Composition"></font></li>
	</c:when>
</c:choose>
	</ul>
	<div>
		<div class="k-header sw-button-bar">
			<div class="sw-button-bar-title" data-i18n="devices.detail.DeviceAssignmentHistory"></div>
			<div>
				<a id="btn-assign-device" class="btn hide" href="javascript:void(0)" data-i18n="devices.detail.AssignDevice">
					<i class="icon-tag sw-button-icon"></i></a>
				<a id="btn-filter-assignments" class="btn" href="javascript:void(0)" data-i18n="public.FilterResults">
					<i class="icon-search sw-button-icon"></i></a>
				<a id="btn-refresh-assignments" class="btn" href="javascript:void(0)" data-i18n="public.Refresh">
					<i class="icon-refresh sw-button-icon"></i></a>
			</div>
		</div>
		<div id="assignments" class="sw-assignment-list"></div>
		<div id="assignments-pager" class="k-pager-wrap"></div>
	</div>
<c:choose>
	<c:when test="${specification.containerPolicy == 'Composite'}">
		<div>
			<div id="sw-composition-section" style="margin-top: 10px;"></div>
		</div>
	</c:when>
</c:choose>
</div>

<form id="view-assignment-detail" method="get" action="../assignments/detail.html">
	<input id="detail-assignment-token" name="token" type="hidden"/>
</form>

<%@ include file="../includes/deviceUpdateDialog.inc"%>	
<%@ include file="../includes/deviceSearchDialog.inc"%>	
<%@ include file="../includes/assetTemplates.inc"%>	
<%@ include file="../includes/assignmentCreateDialog.inc"%>	
<%@ include file="../includes/assignmentUpdateDialog.inc"%>
<%@ include file="../includes/templateDeviceDetailHeader.inc"%>
<%@ include file="../includes/templateDeviceEntry.inc"%>
<%@ include file="../includes/templateDeviceEntrySmall.inc"%>
<%@ include file="../includes/templateAssignmentEntry.inc"%>
<%@ include file="../includes/commonFunctions.inc"%>

<script>
    /** Set sitewhere_title */
    sitewhere_i18next.sitewhere_title = "devices.detail.title";

	var hardwareId = '<c:out value="${device.hardwareId}"/>';

	/** Datasource for assignments */
	var assignmentsDS;
	
	/** Maps device element schema paths to device information */
	var mappings;
	
	
	/** Called when 'delete assignment' is clicked */
	function onDeleteAssignment(e, token) {
		var event = e || window.event;
		event.stopPropagation();
		swAssignmentDelete(token, onDeleteAssignmentComplete);
	}
	
	/** Called after successful delete assignment */
	function onDeleteAssignmentComplete() {
		assignmentsDS.read();
	}
	
	/** Called when 'edit assignment' is clicked */
	function onEditAssignment(e, token) {
		var event = e || window.event;
		event.stopPropagation();
		auOpen(token, onEditAssignmentComplete);
	}
	
	/** Called after successful edit assignment */
	function onEditAssignmentComplete() {
		assignmentsDS.read();
	}
	
	/** Called when 'view assignment' is clicked */
	function onViewAssignment(e, token) {
		var event = e || window.event;
		event.stopPropagation();
		$('#detail-assignment-token').val(token);
		$('#view-assignment-detail').submit();
	}
	
	/** Called when 'release assignment' is clicked */
	function onReleaseAssignment(e, token) {
		var event = e || window.event;
		event.stopPropagation();
		swReleaseAssignment(token, onReleaseAssignmentComplete);
	}
	
	/** Called after successful release assignment */
	function onReleaseAssignmentComplete() {
		loadDevice();
		assignmentsDS.read();
	}
	
	/** Called when 'missing assignment' is clicked */
	function onMissingAssignment(e, token) {
		var event = e || window.event;
		event.stopPropagation();
		swAssignmentMissing(token, onMissingAssignmentComplete);
	}
	
	/** Called after successful missing assignment */
	function onMissingAssignmentComplete() {
		loadDevice();
		assignmentsDS.read();
	}
	
	/** Called on succesfull device assignment */
	function onAssignmentAdded() {
		loadDevice();
		assignmentsDS.read();
	}
	
	$(document).ready(function() {
		
		
		/** Create AJAX datasource for assignments list */
		assignmentsDS = new kendo.data.DataSource({
			transport : {
				read : {
					url : "${pageContext.request.contextPath}/api/devices/" + hardwareId + 
								"/assignments?includeAsset=true&includeDevice=true",
					dataType : "json",
				}
			},
			schema : {
				data: "results",
				total: "numResults",
				parse:function (response) {
				    $.each(response.results, function (index, item) {
				    	parseAssignmentData(item);
				    });
				    return response;
				}
			},
            serverPaging: true,
            serverSorting: true,
            pageSize: 15,
		});
		
		/** Create the assignments list */
		$("#assignments").kendoListView({
			dataSource : assignmentsDS,
			template : kendo.template($("#tpl-assignment-entry").html())
		});
		
	    $("#assignments-pager").kendoPager({
	        dataSource: assignmentsDS
	    });
		
	    $("#btn-refresh-assignments").click(function() {
	    	assignmentsDS.read();
	    });
		
	    $("#btn-edit-device").click(function() {
	    	duOpen(hardwareId, onDeviceEditSuccess);
	    });
		
	    $("#btn-assign-device").click(function() {
	    	acOpen(null, hardwareId, onAssignmentAdded);
	    });
		
		/** Create the tab strip */
		tabs = $("#tabs").kendoTabStrip({
			animation: false
		}).data("kendoTabStrip");
		
		loadDevice();
	});
	
	/** Loads information for the selected device */
	function loadDevice() {
		$.getJSON("${pageContext.request.contextPath}/api/devices/" + hardwareId + "?includeNested=true", 
			loadGetSuccess, loadGetFailed);
	}
    
    /** Called on successful device load request */
    function loadGetSuccess(data, status, jqXHR) {
		var template = kendo.template($("#tpl-device-detail-header").html());
		parseDeviceData(data);
		data.inDetailView = true;
		$('#device-details').html(template(data));
		
		// Only show the 'assign device' button if not already assigned.
		if (!data.assignment) {
		    $("#btn-assign-device").show();
		} else {
		    $("#btn-assign-device").hide();
		}
		
		// Update device element mappings information.
		refreshDeviceElementMappings(data);
    }
    
	/** Handle error on getting device data */
	function loadGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load device data.");
	}
	
	/** Called after device edit to update banner */
	function onDeviceEditSuccess() {
		loadDevice();
	}
	
	/** Add new mapping for the given path */
	function addMapping(path) {
		dvsOpen({"path": path}, onMappingDeviceChosen);
	}
	
	/** Called after user chooses a device for mapping */
	function onMappingDeviceChosen(data, target) {
		var mapping = {
			"deviceElementSchemaPath": data.path, 
			"hardwareId": target, 
		}
		$.postJSON("${pageContext.request.contextPath}/api/devices/" + hardwareId + "/mappings", 
				mapping, onMappingCreateSuccess, onMappingCreateFail);
	}
    
    /** Called on successful call to create mapping */
    function onMappingCreateSuccess() {
    	loadDevice();
    }
    
	/** Handle failed call to create mapping */
	function onMappingCreateFail(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to create mapping.");
	}
	
	/** Delete an existing device element mapping */
	function deleteMapping(path) {
		swConfirm("Delete Device Element Mapping", "Are you sure that you want to delete the device element mapping?", function(result) {
			if (result) {
				$.deleteJSON("${pageContext.request.contextPath}/api/devices/" + hardwareId + "/mappings?path=" + path, 
						onMappingDeleteSuccess, onMappingDeleteFail);
			}
		});
	}
    
    /** Called on successful call to delete mapping */
    function onMappingDeleteSuccess() {
    	loadDevice();
    }
    
	/** Handle failed call to delete mapping */
	function onMappingDeleteFail(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to delete mapping.");
	}
	
	/** Load HTML for device element mappings */
	function refreshDeviceElementMappings(device) {
		mappings = swGetDeviceSlotPathMap(device);
		var schema = device.specification.deviceElementSchema;
		if (!schema) {
			return;
		}
    	var shtml = getUnitHtml(schema, "");
    	$('#sw-composition-section').html(shtml);
	}
	
	/** Create HTML for a device unit */
	function getUnitHtml(unit, context) {
    	var uhtml = "";
		var slength = unit.deviceSlots.length;
   		if (slength > 0) {
   			uhtml += "<div class='sw-device-slot-container'>";
   			uhtml += "<div class='sw-device-slot-header'><i class='icon-link sw-button-icon'></i> Device Slots</div>";
    		for (var i = 0; i < slength; i++) {
    			uhtml += getSlotHtml(unit.deviceSlots[i], context);
    		}
       		uhtml += "</div>";
   		}
		var ulength = unit.deviceUnits.length;
   		for (var i = 0; i < ulength; i++) {
       		var relContext = context + "/" + unit.deviceUnits[i].path;
       		uhtml += "<div class='sw-device-unit-container sw-list-entry'>";
       		uhtml += getUnitHeaderHtml(unit.deviceUnits[i], relContext);
   			uhtml += getUnitHtml(unit.deviceUnits[i], relContext);
       		uhtml += "</div>";
   		}
    	return uhtml;
	}
	
	/** Create HTML for device unit header bar */
	function getUnitHeaderHtml(unit, relContext) {
		var uhtml = "<div class='sw-device-unit-header'><i class='icon-folder-close sw-button-icon'></i>" + 
			unit.name + " (<span class='sw-device-unit-path'>" + relContext + "</span>)</div>";
		return uhtml;
	}
	
	/** Create HTML for a device slot */
	function getSlotHtml(slot, context) {
		var relContext = context + "/" + slot.path;
		var mapping = mappings[relContext];
    	var shtml;
    	if (mapping) {
        	shtml = "<div class='sw-device-slot' style='border: 2px solid #006; background-color: #ddf;'>" + 
        		"<i class='icon-link sw-button-icon' style='padding-right: 5px'></i>" + 
    			slot.name + " (<span class='sw-device-slot-path'>" + relContext + "</span>)";
        	shtml += "<div class='sw-device-slot-buttons'>" + 
        		"<i class='icon-link sw-button-icon' style='color: #030; margin-right: 1px;'></i>" +      	
				"<a class='sw-device-slot-mapped-device' href='${pageContext.request.contextPath}/admin/devices/detail.html?hardwareId=" + 
				mapping.hardwareId + "'>" + mapping.specification.assetName + "</a>" +
    			"<i class='icon-remove sw-button-icon sw-action-glyph sw-delete-glyph' style='margin-top: -2px;' " + 
				"onclick=\"deleteMapping('" + relContext + "');\" title='Delete Device Element Mapping'></i>" +
				"</div></div>";
    	} else {
        	shtml = "<div class='sw-device-slot'><i class='icon-link sw-button-icon' style='padding-right: 5px'></i>" + 
    			slot.name + " (<span class='sw-device-slot-path'>" + relContext + "</span>)";
        	shtml += "<div class='sw-device-slot-buttons'>" +
    			"<i class='icon-plus sw-button-icon sw-action-glyph sw-view-glyph' " + 
				"onclick=\"addMapping('" + relContext + "');\" title='Add Device Element Mapping'></i>" +
				"</div></div>";
    	}
		return shtml;
	}
</script>

<%@ include file="../includes/bottom.inc"%>