<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="View Assignment" />
<c:set var="sitewhere_section" value="sites" />
<%@ include file="../includes/top.inc"%>

<style>
.event-pager {
	margin-top: 10px;
}
</style>

<!-- Title Bar -->
<div class="sw-title-bar content k-header" style="margin-bottom: -1px;">
	<h1 class="ellipsis" data-i18n="assignments.detail.title"></h1>
	<div class="sw-title-bar-right">
		<a id="btn-emulator" class="btn" href="emulator.html?token=<c:out value="${assignment.token}"/>" data-i18n="assignments.detail.EmulateAssignment">
			<i class="icon-bolt sw-button-icon"></i></a>
		<a id="btn-edit-assignment" class="btn" href="javascript:void(0)" data-i18n="public.EditAssignment">
			<i class="icon-edit sw-button-icon"></i></a>
	</div>
</div>

<!-- Detail panel for selected assignment -->
<div id="assignment-details" style="line-height: normal;"></div>

<!-- Tab panel -->
<div id="tabs">
	<ul>
		<li class="k-state-active" >&nbsp;<font data-i18n="public.Locations"></font></li>
		<li >&nbsp;<font data-i18n="public.Measurements"></font></li>
		<li >&nbsp;<font data-i18n="public.Alerts"></font></li>
		<li >&nbsp;<font data-i18n="public.CommandInvocations"></font></li>
	</ul>
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
				<col style="width: 20%;"/>
				<col style="width: 20%;"/>
				<col style="width: 20%;"/>
			</colgroup>
			<thead>
				<tr>
					<th data-i18n="public.Latitude"></th>
					<th data-i18n="public.Longitude"></th>
					<th data-i18n="public.Elevation"></th>
					<th data-i18n="public.EventDate"></th>
				</tr>
			</thead>
			<tbody>
				<tr><td colspan="5"></td></tr>
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
				<col style="width: 37%;"/>
				<col style="width: 20%;"/>
			</colgroup>
			<thead>
				<tr>
					<th data-i18n="public.Measurements"></th>
					<th data-i18n="public.EventDate"></th>
				</tr>
			</thead>
			<tbody>
				<tr><td colspan="3"></td></tr>
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
				<col style="width: 10%;"/>
				<col style="width: 20%;"/>
				<col style="width: 10%;"/>
				<col style="width: 20%;"/>
			</colgroup>
			<thead>
				<tr>
					<th data-i18n="public.Type"></th>
					<th data-i18n="public.Message"></th>
					<th data-i18n="public.Source"></th>
					<th data-i18n="public.EventDate"></th>
				</tr>
			</thead>
			<tbody>
				<tr><td colspan="5"></td></tr>
			</tbody>
		</table>
		<div id="alerts-pager" class="k-pager-wrap event-pager"></div>
	</div>
	<div>
		<div class="k-header sw-button-bar">
			<div class="sw-button-bar-title" data-i18n="assignments.detail.DeviceCommandInvocations"></div>
			<div>
				<a id="btn-filter-invocations" class="btn" href="javascript:void(0)" data-i18n="public.FilterResults">
					<i class="icon-search sw-button-icon"></i></a>
				<a id="btn-refresh-invocations" class="btn" href="javascript:void(0)" data-i18n="public.Refresh">
					<i class="icon-refresh sw-button-icon"></i></a>
				<a id="btn-create-invocation" class="btn" href="javascript:void(0)" data-i18n="assignments.detail.InvokeCommand">
					<i class="icon-bolt sw-button-icon"></i></a>
			</div>
		</div>
		<table id="invocations">
			<colgroup>
				<col style="width: 32%;"/>
				<col style="width: 15%;"/>
				<col style="width: 12%;"/>
				<col style="width: 20%;"/>
				<col style="width: 8%;"/>
			</colgroup>
			<thead>
				<tr>
					<th data-i18n="public.Command"></th>
					<th data-i18n="public.Source"></th>
					<th data-i18n="assignments.detail.Target"></th>
					<th data-i18n="public.EventDate"></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<tr><td colspan="5"></td></tr>
			</tbody>
		</table>
		<div id="invocations-pager" class="k-pager-wrap event-pager"></div>
	</div>
</div>

<%@ include file="../includes/assignmentUpdateDialog.inc"%>
<%@ include file="../includes/commandInvokeDialog.inc"%>
<%@ include file="../includes/invocationViewDialog.inc"%>
<%@ include file="../includes/templateAssignmentDetailHeader.inc"%>
<%@ include file="../includes/templateAssignmentEntry.inc"%>
<%@ include file="../includes/templateInvocationEntry.inc"%>
<%@ include file="../includes/templateInvocationSummaryEntry.inc"%>
<%@ include file="../includes/templateResponseSummaryEntry.inc"%>
<%@ include file="../includes/templateLocationEntry.inc"%>
<%@ include file="../includes/templateMeasurementsEntry.inc"%>
<%@ include file="../includes/templateAlertEntry.inc"%>
<%@ include file="../includes/commonFunctions.inc"%>

<script>
    /** Set sitewhere_title */
    sitewhere_i18next.sitewhere_title = "assignments.detail.title";
    
	/** Assignment token */
	var token = '<c:out value="${assignment.token}"/>';
	
	/** Device specification token */
	var specificationToken = '<c:out value="${assignment.device.specificationToken}"/>';
	
	/** Datasource for invocations */
	var invocationsDS;
	
	/** Datasource for locations */
	var locationsDS;
	
	/** Datasource for measurements */
	var measurementsDS;
	
	/** Datasource for alerts */
	var alertsDS;
	
	/** Reference to tab panel */
	var tabs;
	
	/** Size of pages from server */
	var pageSize = 100;
	
	/** Height of event grids */
	var gridHeight = 350;
	
	/** Called when 'edit assignment' is clicked */
	function onEditAssignment(e, token) {
		var event = e || window.event;
		event.stopPropagation();
		auOpen(token, onEditAssignmentComplete);
	}
	
	/** Called after successful edit assignment */
	function onEditAssignmentComplete() {
		// Handle reload.
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
	
	/** Called to view an invocation */
	function onViewInvocation(id) {
		ivOpen(id);
	}
	
	$(document).ready(function() {
		
		/** Create AJAX datasource for locations list */
		locationsDS = new kendo.data.DataSource({
			transport : {
				read : {
					url : "${pageContext.request.contextPath}/api/assignments/" + token + "/locations",
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
	    $('#btn-filter-locations').attr('disabled', true);
	    
		/** Create AJAX datasource for measurements list */
		measurementsDS = new kendo.data.DataSource({
			transport : {
				read : {
					url : "${pageContext.request.contextPath}/api/assignments/" + token + "/measurements",
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
	    $('#btn-filter-measurements').attr('disabled', true);
	    
		/** Create AJAX datasource for alerts list */
		alertsDS = new kendo.data.DataSource({
			transport : {
				read : {
					url : "${pageContext.request.contextPath}/api/assignments/" + token + "/alerts",
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
		
		/** Create the alerts list */
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
	    $('#btn-filter-alerts').attr('disabled', true);
	    
		/** Create AJAX datasource for invocations list */
		invocationsDS = new kendo.data.DataSource({
			transport : {
				read : {
					url : "${pageContext.request.contextPath}/api/assignments/" + token + "/invocations",
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
		
		/** Create the invocations list */
        $("#invocations").kendoGrid({
			dataSource : invocationsDS,
            rowTemplate: kendo.template($("#tpl-invocation-entry").html()),
            scrollable: true,
            height: gridHeight,
        });
		
	    $("#btn-refresh-invocations").click(function() {
	    	invocationsDS.read();
	    });
	    $('#btn-filter-invocations').attr('disabled', true);
		
	    $("#btn-create-invocation").click(function() {
			ciOpen(token, specificationToken, onInvokeCommandSuccess);
	    });
		
	    $("#invocations-pager").kendoPager({
	        dataSource: invocationsDS
	    });
		
	    $("#btn-edit-assignment").click(function() {
			auOpen(token, onAssignmentEditSuccess);
	    });
		
		/** Create the tab strip */
		tabs = $("#tabs").kendoTabStrip({
			animation: false,
			activate: onActivate
		}).data("kendoTabStrip");
		
		loadAssignment();
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
			} else if (tabName =="Command Invocations") {
				invocationsDS.read();
				e.item.swInitialized = true;
			}
		}
	};
	
	/** Loads information for the selected assignment */
	function loadAssignment() {
		$.getJSON("${pageContext.request.contextPath}/api/assignments/" + token, 
			loadGetSuccess, loadGetFailed);
	}
    
    /** Called on successful assignment load request */
    function loadGetSuccess(data, status, jqXHR) {
		var template = kendo.template($("#tpl-assignment-detail-header").html());
		parseAssignmentData(data);
		data.inDetailView = true;
		$('#assignment-details').html(template(data));
    }
    
	/** Handle error on getting assignment data */
	function loadGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load assignment data.");
	}
	
	/** Parses event response records to format dates */
	function parseEventResults(response) {
	    $.each(response.results, function (index, item) {
			parseEventData(item);
	    });
	    return response;
	}
	
	/** Called after successful edit of assignment */
	function onAssignmentEditSuccess() {
		loadAssignment();
	}
	
	/** Called after successful edit of assignment */
	function onInvokeCommandSuccess() {
    	invocationsDS.read();
	}
</script>

<%@ include file="../includes/bottom.inc"%>