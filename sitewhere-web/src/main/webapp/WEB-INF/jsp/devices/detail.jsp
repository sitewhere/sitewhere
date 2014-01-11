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
	<h1 class="ellipsis"><c:out value="${sitewhere_title}"/></h1>
	<div class="sw-title-bar-right">
		<a id="btn-edit-device" class="btn" href="javascript:void(0)">
			<i class="icon-pencil sw-button-icon"></i> Edit Device</a>
	</div>
</div>

<!-- Detail panel for selected device -->
<div id="device-details" style="line-height: normal;"></div>

<!-- Tab panel -->
<div id="tabs">
	<ul>
		<li class="k-state-active">Assignment History</li>
	</ul>
	<div>
		<div class="k-header sw-button-bar">
			<div class="sw-button-bar-title">Device Assignment History</div>
			<div>
				<a id="btn-filter-assignments" class="btn" href="javascript:void(0)">
					<i class="icon-search sw-button-icon"></i> Filter Results</a>
				<a id="btn-refresh-assignments" class="btn" href="javascript:void(0)">
					<i class="icon-refresh sw-button-icon"></i> Refresh</a>
			</div>
		</div>
		<div id="assignments" class="sw-assignment-list"></div>
		<div id="assignments-pager" class="k-pager-wrap"></div>
	</div>
</div>

<form id="view-assignment-detail" method="get" action="../assignments/detail.html">
	<input id="detail-assignment-token" name="token" type="hidden"/>
</form>

<%@ include file="../includes/deviceUpdateDialog.inc"%>	

<%@ include file="../includes/assetTemplates.inc"%>	

<%@ include file="../includes/assignmentCreateDialog.inc"%>	

<%@ include file="../includes/assignmentUpdateDialog.inc"%>

<%@ include file="../includes/templateDeviceEntry.inc"%>

<%@ include file="../includes/templateAssignmentEntry.inc"%>

<%@ include file="../includes/commonFunctions.inc"%>

<script>
	var hardwareId = '<c:out value="${device.hardwareId}"/>';

	/** Datasource for assignments */
	var assignmentsDS;
	
	
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
		
		/** Create the tab strip */
		tabs = $("#tabs").kendoTabStrip({
			animation: false
		}).data("kendoTabStrip");
		
		loadDevice();
	});
	
	/** Loads information for the selected device */
	function loadDevice() {
		$.getJSON("${pageContext.request.contextPath}/api/devices/" + hardwareId, 
			loadGetSuccess, loadGetFailed);
	}
    
    /** Called on successful device load request */
    function loadGetSuccess(data, status, jqXHR) {
		var template = kendo.template($("#tpl-device-entry").html());
		parseDeviceData(data);
		data.inDetailView = true;
		$('#device-details').html(template(data));
    }
    
	/** Handle error on getting device data */
	function loadGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load device data.");
	}
	
	/** Called after device edit to update banner */
	function onDeviceEditSuccess() {
		loadDevice();
	}
</script>

<%@ include file="../includes/bottom.inc"%>