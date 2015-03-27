<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="View Site" />
<c:set var="sitewhere_section" value="sites" />
<c:set var="use_map_includes" value="true" />
<c:set var="use_color_picker_includes" value="true" />
<%@ include file="../includes/top.inc"%>

<style>
.sw-assignment-list {
	border: 0px;
}

.event-pager {
	margin-top: 10px;
}
</style>

<!-- Title Bar -->
<div class="sw-title-bar content k-header" style="margin-bottom: -1px;">
	<h1 class="ellipsis" data-i18n="sites.detail.title"></h1>
	<div class="sw-title-bar-right">
		<a id="btn-edit-site" class="btn" href="javascript:void(0)" data-i18n="public.EditSite">
			<i class="icon-edit sw-button-icon"></i></a>
	</div>
</div>

<!-- Detail panel for selected site -->
<div id="site-details" style="line-height: normal;"></div>

<!-- Tab panel -->
<div id="tabs">
	<ul>
		<li class="k-state-active" >&nbsp;<font data-i18n="sites.detail.Assignments"></font></li>
		<li >&nbsp;<font data-i18n="public.Locations"></font></li>
		<li >&nbsp;<font data-i18n="public.Measurements"></font></li>
		<li >&nbsp;<font data-i18n="public.Alerts"></font></li>
		<li >&nbsp;<font data-i18n="sites.detail.Zones"></font></li>
	</ul>
	<div>
		<div class="k-header sw-button-bar">
			<div class="sw-button-bar-title" data-i18n="sites.detail.DeviceAssignments"></div>
			<div>
				<a id="btn-filter-assignments" class="btn" href="javascript:void(0)" data-i18n="public.FilterResults">
					<i class="icon-search sw-button-icon"></i></a>
				<a id="btn-refresh-assignments" class="btn" href="javascript:void(0)" data-i18n="public.Refresh">
					<i class="icon-refresh sw-button-icon"></i></a>
			</div>
		</div>
		<div id="assignments" class="sw-assignment-list"></div>
		<div id="assignments-pager" class="k-pager-wrap"></div>
	</div>
	<div>
		<div class="k-header sw-button-bar">
			<div class="sw-button-bar-title" data-i18n="public.DeviceLocations"></div>
			<div>
				<a id="btn-filter-locations" class="btn" href="javascript:void(0)" data-i18n="public.FilterResults">
					<i class="icon-search sw-button-icon"></i></a>
				<a id="btn-refresh-locations" class="btn" href="javascript:void(0)" data-i18n="public.Refresh">
					<i class="icon-refresh sw-button-icon"></i></a>
			</div>
		</div>
		<table id="locations">
			<colgroup>
				<col style="width: 20%;"/>
				<col style="width: 37%;"/>
				<col style="width: 20%;"/>
				<col style="width: 20%;"/>
			</colgroup>
			<thead>
				<tr>
					<th data-i18n="public.Asset"></th>
					<th><font data-i18n="sites.detail.Location"></font> (Lat/Long/Elevation)</th>
					<th data-i18n="public.EventDate"></th>
					<th data-i18n="sites.detail.ReceivedDate"></th>
				</tr>
			</thead>
			<tbody>
				<tr><td colspan="4"></td></tr>
			</tbody>
		</table>
		<div id="locations-pager" class="k-pager-wrap event-pager"></div>
	</div>
	<div>
		<div class="k-header sw-button-bar">
			<div class="sw-button-bar-title" data-i18n="public.DeviceMeasurements"></div>
			<div>
				<a id="btn-filter-measurements" class="btn" href="javascript:void(0)" data-i18n="public.FilterResults">
					<i class="icon-search sw-button-icon"></i></a>
				<a id="btn-refresh-measurements" class="btn" href="javascript:void(0)" data-i18n="public.Refresh">
					<i class="icon-refresh sw-button-icon"></i></a>
			</div>
		</div>
		<table id="measurements">
			<colgroup>
				<col style="width: 20%;"/>
				<col style="width: 37%;"/>
				<col style="width: 20%;"/>
				<col style="width: 20%;"/>
			</colgroup>
			<thead>
				<tr>
					<th data-i18n="public.Asset"></th>
					<th data-i18n="public.Measurements"></th>
					<th data-i18n="public.EventDate"></th>
					<th data-i18n="sites.detail.ReceivedDate"></th>
				</tr>
			</thead>
			<tbody>
				<tr><td colspan="4"></td></tr>
			</tbody>
		</table>
		<div id="measurements-pager" class="k-pager-wrap event-pager"></div>
	</div>
	<div>
		<div class="k-header sw-button-bar">
			<div class="sw-button-bar-title" data-i18n="public.DeviceAlerts"></div>
			<div>
				<a id="btn-filter-alerts" class="btn" href="javascript:void(0)" data-i18n="public.FilterResults">
					<i class="icon-search sw-button-icon"></i></a>
				<a id="btn-refresh-alerts" class="btn" href="javascript:void(0)" data-i18n="public.Refresh">
					<i class="icon-refresh sw-button-icon"></i></a>
			</div>
		</div>
		<table id="alerts">
			<colgroup>
				<col style="width: 20%;"/>
				<col style="width: 10%;"/>
				<col style="width: 20%;"/>
				<col style="width: 10%;"/>
				<col style="width: 20%;"/>
				<col style="width: 20%;"/>
			</colgroup>
			<thead>
				<tr>
					<th data-i18n="public.Asset"></th>
					<th data-i18n="public.Type"></th>
					<th data-i18n="public.Message"></th>
					<th data-i18n="public.Source"></th>
					<th data-i18n="public.EventDate"></th>
					<th data-i18n="sites.detail.ReceivedDate"></th>
				</tr>
			</thead>
			<tbody>
				<tr><td colspan="6"></td></tr>
			</tbody>
		</table>
		<div id="alerts-pager" class="k-pager-wrap event-pager"></div>
	</div>
	<div>
		<div class="k-header sw-button-bar">
			<div class="sw-button-bar-title" data-i18n="sites.detail.Zones"></div>
			<div>
				<a id="btn-refresh-zones" class="btn" href="javascript:void(0)" data-i18n="public.Refresh">
					<i class="icon-refresh sw-button-icon"></i></a>
				<a id="btn-add-zone" class="btn" href="javascript:void(0)" data-i18n="sites.detail.AddNewZone">
					<i class="icon-plus sw-button-icon"></i></a>
			</div>
		</div>
		<table id="zones">
			<colgroup>
				<col style="width: 3%;"/>
				<col style="width: 23%;"/>
				<col style="width: 30%;"/>
				<col style="width: 15%;"/>
				<col style="width: 15%;"/>
				<col style="width: 50px;"/>
			</colgroup>
			<thead>
				<tr>
					<th></th>
					<th data-i18n="public.Name"></th>
					<th data-i18n="sites.detail.Token"></th>
					<th data-i18n="public.CreatedDate"></th>
					<th data-i18n="public.UpdatedDate"></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<tr><td colspan="6"></td></tr>
			</tbody>
		</table>
		<div id="zones-pager" class="k-pager-wrap event-pager"></div>
	</div>
</div>

<form id="view-assignment-detail" method="get" action="../assignments/detail.html">
	<input id="detail-assignment-token" name="token" type="hidden"/>
</form>

<%@ include file="../includes/siteCreateDialog.inc"%>
<%@ include file="../includes/zoneCreateDialog.inc"%>
<%@ include file="../includes/assignmentUpdateDialog.inc"%>
<%@ include file="../includes/templateSiteEntry.inc"%>
<%@ include file="../includes/templateAssignmentEntry.inc"%>
<%@ include file="../includes/templateSiteLocationEntry.inc"%>
<%@ include file="../includes/templateSiteMeasurementsEntry.inc"%>
<%@ include file="../includes/templateSiteAlertEntry.inc"%>
<%@ include file="../includes/templateZoneEntry.inc"%>
<%@ include file="../includes/commonFunctions.inc"%>

<script>
    /** Set sitewhere_title */
    sitewhere_i18next.sitewhere_title = "sites.detail.title";

	var siteToken = '<c:out value="${site.token}"/>';
	
	/** Current site */
	var site;

	/** Datasource for assignments */
	var assignmentsDS;
	
	/** Datasource for locations */
	var locationsDS;
	
	/** Datasource for measurements */
	var measurementsDS;
	
	/** Datasource for alerts */
	var alertsDS;
	
	/** Datasource for zones */
	var zonesDS;
	
	/** Reference to tab panel */
	var tabs;
	
	/** Size of pages from server */
	var pageSize = 100;
	
	/** Height of event grids */
	var gridHeight = 350;
	
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
		assignmentsDS.read();
	}
	
	/** Called when 'delete zone' is clicked */
	function onDeleteZone(e, token) {
		var event = e || window.event;
		event.stopPropagation();
		swZoneDelete(token, onDeleteZoneComplete);
	}
	
	/** Called after successful delete zone */
	function onDeleteZoneComplete() {
		zonesDS.read();
	}
	
	/** Called when 'edit zone' is clicked */
	function onEditZone(e, token) {
		var event = e || window.event;
		event.stopPropagation();
    	if (site) {
    		zuOpen(site, token, onZoneUpdateSuccess);
    	} else {
    		swAlert("Error", "Site has not been loaded.");
    	}
	}
	
	/** Called after successful edit zone */
	function onZoneUpdateSuccess() {
		zonesDS.read();
	}
	
	$(document).ready(function() {
		
		/** Create AJAX datasource for assignments list */
		assignmentsDS = new kendo.data.DataSource({
			transport : {
				read : {
					url : "${pageContext.request.contextPath}/api/sites/" + siteToken + 
						"/assignments?includeDevice=true&includeAsset=true",
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
	    
		/** Create AJAX datasource for locations list */
		locationsDS = new kendo.data.DataSource({
			transport : {
				read : {
					url : "${pageContext.request.contextPath}/api/sites/" + siteToken + "/locations",
					dataType : "json",
				}
			},
			schema : {
				data: "results",
				total: "numResults",
				parse: parseEventResults,
			},
            serverPaging: true,
            serverSorting: true,
            pageSize: pageSize,
		});
		
		/** Create the location list */
        $("#locations").kendoGrid({
			dataSource : locationsDS,
            rowTemplate: kendo.template($("#tpl-location-entry").html()),
            scrollable: true,
            height: gridHeight,
        });
		
	    $("#locations-pager").kendoPager({
	        dataSource: locationsDS
	    });
		
	    $("#btn-refresh-locations").click(function() {
	    	locationsDS.read();
	    });
	    
		/** Create AJAX datasource for measurements list */
		measurementsDS = new kendo.data.DataSource({
			transport : {
				read : {
					url : "${pageContext.request.contextPath}/api/sites/" + siteToken + "/measurements",
					dataType : "json",
				}
			},
			schema : {
				data: "results",
				total: "numResults",
				parse: parseEventResults,
			},
            serverPaging: true,
            serverSorting: true,
            pageSize: pageSize,
		});
		
		/** Create the measurements list */
        $("#measurements").kendoGrid({
			dataSource : measurementsDS,
            rowTemplate: kendo.template($("#tpl-measurements-entry").html()),
            scrollable: true,
            height: gridHeight,
        });
		
	    $("#measurements-pager").kendoPager({
	        dataSource: measurementsDS
	    });
		
	    $("#btn-refresh-measurements").click(function() {
	    	measurementsDS.read();
	    });
	    
		/** Create AJAX datasource for alerts list */
		alertsDS = new kendo.data.DataSource({
			transport : {
				read : {
					url : "${pageContext.request.contextPath}/api/sites/" + siteToken + "/alerts",
					dataType : "json",
				}
			},
			schema : {
				data: "results",
				total: "numResults",
				parse: parseEventResults,
			},
            serverPaging: true,
            serverSorting: true,
            pageSize: pageSize,
		});
		
		/** Create the measurements list */
        $("#alerts").kendoGrid({
			dataSource : alertsDS,
            rowTemplate: kendo.template($("#tpl-alert-entry").html()),
            scrollable: true,
            height: gridHeight,
        });
		
	    $("#alerts-pager").kendoPager({
	        dataSource: alertsDS
	    });
		
	    $("#btn-refresh-alerts").click(function() {
	    	alertsDS.read();
	    });
	    
		/** Create AJAX datasource for alerts list */
		zonesDS = new kendo.data.DataSource({
			transport : {
				read : {
					url : "${pageContext.request.contextPath}/api/sites/" + siteToken + "/zones",
					dataType : "json",
				}
			},
			schema : {
				data: "results",
				total: "numResults",
				parse: parseZoneResults,
			},
            serverPaging: true,
            serverSorting: true,
            pageSize: 20,
		});
		
		/** Create the measurements list */
        $("#zones").kendoGrid({
			dataSource : zonesDS,
            rowTemplate: kendo.template($("#tpl-zone-entry").html()),
            scrollable: true,
            height: gridHeight,
        });
		
	    $("#zones-pager").kendoPager({
	        dataSource: zonesDS
	    });
		
	    $("#btn-refresh-zones").click(function() {
	    	zonesDS.read();
	    });
		
	    $("#btn-add-zone").click(function() {
	    	if (site) {
	    		zcOpen(site, onZoneCreateSuccess);
	    	} else {
	    		swAlert("Error", "Site has not been loaded.");
	    	}
	    });
        
        /** Handle edit dialog */
		$('#btn-edit-site').click(function(event) {
			suOpen(siteToken, onSiteEditSuccess);
		});
		
		/** Create the tab strip */
		tabs = $("#tabs").kendoTabStrip({
			animation: false,
			activate: onActivate
		}).data("kendoTabStrip");
		
		loadSite();
	});
	
	/** Force grid refresh on first tab activate (KendoUI bug) */
	function onActivate(e) {
		var tabName = e.item.textContent;
		if (!e.item.swInitialized) {
			if (tabName =="Locations") {
				locationsDS.read();
				e.item.swInitialized = true;
			} else if (tabName =="Measurements") {
				measurementsDS.read();
				e.item.swInitialized = true;
			} else if (tabName =="Alerts") {
				alertsDS.read();
				e.item.swInitialized = true;
			} else if (tabName =="Zones") {
				zonesDS.read();
				e.item.swInitialized = true;
			}
		}
	};
	
	/** Parses event response records to format dates */
	function parseEventResults(response) {
	    $.each(response.results, function (index, item) {
			parseEventData(item);
	    });
	    return response;
	}
	
	/** Parses zone response records to format dates */
	function parseZoneResults(response) {
	    $.each(response.results, function (index, item) {
			parseZoneData(item);
	    });
	    return response;
	}
	
	/** Loads information for the selected site */
	function loadSite() {
		$.getJSON("${pageContext.request.contextPath}/api/sites/" + siteToken, 
			loadGetSuccess, loadGetFailed);
	}
    
    /** Called on successful site load request */
    function loadGetSuccess(data, status, jqXHR) {
    	site = data;
		var template = kendo.template($("#tpl-site-entry").html());
		parseDeviceData(data);
		data.inDetailView = true;
		$('#site-details').html(template(data));
    }
    
	/** Handle error on getting site data */
	function loadGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load site data.");
	}
	
	/** Refresh site banner after successful edit */
	function onSiteEditSuccess() {
		loadSite();
	}
	
	/** Called after a zone has been successfully created */
	function onZoneCreateSuccess() {
    	zonesDS.read();
	}
</script>

<%@ include file="../includes/bottom.inc"%>