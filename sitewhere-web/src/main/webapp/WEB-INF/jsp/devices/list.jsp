<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="Manage Devices" />
<c:set var="sitewhere_section" value="devices" />
<%@ include file="../includes/top.inc"%>

<style>
.sw-device-list {
	border: 0px;
}
</style>

<%@ include file="../includes/deviceCreateDialog.inc"%>

<%@ include file="../includes/deviceUpdateDialog.inc"%>

<%@ include file="../includes/assignmentCreateDialog.inc"%>

<!-- Title Bar -->
<div class="sw-title-bar content k-header">
	<h1 class="ellipsis"><c:out value="${sitewhere_title}"/></h1>
	<div class="sw-title-bar-right">
		<a id="btn-filter-results" class="btn" href="javascript:void(0)">
			<i class="icon-search sw-button-icon"></i> Filter Results</a>
		<a id="btn-add-device" class="btn" href="javascript:void(0)">
			<i class="icon-plus sw-button-icon"></i> Add New Device</a>
	</div>
</div>
<div id="devices" class="sw-device-list"></div>
<div id="pager" class="k-pager-wrap"></div>

<form id="view-device-detail" method="get" action="detail.html">
	<input id="detail-hardware-id" name="hardwareId" type="hidden"/>
</form>

<%@ include file="../includes/templateDeviceEntry.inc"%>

<%@ include file="../includes/templateSpecificationEntrySmall.inc"%>

<%@ include file="../includes/assetTemplates.inc"%>

<%@ include file="../includes/commonFunctions.inc"%>

<script>
	/** Reference for device list datasource */
	var devicesDS;
	
	/** Called when edit button on the list entry is pressed */
	function onDeviceEditClicked(e, hardwareId) {
		var event = e || window.event;
		event.stopPropagation();
		duOpen(hardwareId, onDeviceEditComplete);
	}
	
	/** Called after successful device update */
	function onDeviceEditComplete() {
    	devicesDS.read();
	}
	
	/** Called when delete button on the list entry is pressed */
	function onDeviceDeleteClicked(e, hardwareId) {
		var event = e || window.event;
		event.stopPropagation();
		swDeviceDelete(hardwareId, onDeviceDeleteComplete);
	}
	
	/** Called after successful device delete */
	function onDeviceDeleteComplete() {
    	devicesDS.read();
	}
	
	/** Called when 'open' button on the list entry is pressed */
	function onDeviceOpenClicked(e, hardwareId) {
		var event = e || window.event;
		event.stopPropagation();
		$('#detail-hardware-id').val(hardwareId);
		$('#view-device-detail').submit();
	}
	
	/** Called when 'release assignment' is clicked */
	function onReleaseAssignment(e, token) {
		var event = e || window.event;
		event.stopPropagation();
		swReleaseAssignment(token, onReleaseAssignmentComplete);
	}
	
	/** Called after successful release assignment */
	function onReleaseAssignmentComplete() {
    	devicesDS.read();
	}
	
	/** Called when 'missing assignment' is clicked */
	function onMissingAssignment(e, token) {
		var event = e || window.event;
		event.stopPropagation();
		swAssignmentMissing(token, onMissingAssignmentComplete);
	}
	
	/** Called after successful missing assignment */
	function onMissingAssignmentComplete() {
    	devicesDS.read();
	}
	
	/** Called on succesfull device assignment */
	function onAssignmentAdded() {
		devicesDS.read();
	}
	
	/** Called when a device has been successfully created */
	function onDeviceCreated() {
    	devicesDS.read();
	}
	
    $(document).ready(function() {
		/** Create AJAX datasource for devices list */
		devicesDS = new kendo.data.DataSource({
			transport : {
				read : {
					url : "${pageContext.request.contextPath}/api/devices?includeSpecification=true&includeAssignment=true",
					dataType : "json",
				}
			},
			schema : {
				data: "results",
				total: "numResults",
				parse:function (response) {
				    $.each(response.results, function (index, item) {
				    	parseDeviceData(item);
				    });
				    return response;
				}
			},
            serverPaging: true,
            serverSorting: true,
            pageSize: 15,
		});
		
		/** Create the list of devices */
		$("#devices").kendoListView({
			dataSource : devicesDS,
			template : kendo.template($("#tpl-device-entry").html()),
		});
		
		/** Pager for device list */
        $("#pager").kendoPager({
            dataSource: devicesDS
        });
		
        /** Handle create dialog */
		$('#btn-add-device').click(function(event) {
			dcOpen(event, onDeviceCreated)
		});
   });
</script>

<%@ include file="../includes/bottom.inc"%>