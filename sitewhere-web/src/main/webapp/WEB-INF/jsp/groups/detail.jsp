<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="View Device Group" />
<c:set var="sitewhere_section" value="devicegroups" />
<%@ include file="../includes/top.inc"%>

<style>
.group-pager {
	margin-top: 10px;
}
</style>

<!-- Title Bar -->
<div class="sw-title-bar content k-header" style="margin-bottom: -1px;">
	<h1 class="ellipsis" data-i18n="groups.detail.title"></h1>
	<div class="sw-title-bar-right">
		<a id="btn-edit-device-group" class="btn" href="javascript:void(0)" data-i18n="public.EditDeviceGroup">
			<i class="icon-pencil sw-button-icon"></i></a>
	</div>
</div>

<!-- Detail panel for selected device group -->
<div id="device-group-details" style="line-height: normal;"></div>

<!-- Tab panel -->
<div id="tabs">
	<ul>
		<li class="k-state-active" data-i18n="groups.detail.GroupElements"></li>
	</ul>
	<div>
		<div class="k-header sw-button-bar">
			<div class="sw-button-bar-title" data-i18n="groups.detail.DeviceGroupElements"></div>
			<div>
				<a id="btn-filter-elements" class="btn" href="javascript:void(0)" data-i18n="public.FilterResults">
					<i class="icon-search sw-button-icon"></i></a>
				<a id="btn-refresh-elements" class="btn" href="javascript:void(0)" data-i18n="public.Refresh">
					<i class="icon-refresh sw-button-icon"></i></a>
				<a id="btn-add-element" class="btn" href="javascript:void(0)" data-i18n="groups.detail.AddGroupElement">
					<i class="icon-plus sw-button-icon"></i></a>
			</div>
		</div>
		<table id="elements">
			<colgroup>
				<col style="width: 35%;"/>
				<col style="width: 30%;"/>
				<col style="width: 25%;"/>
				<col style="width: 10%;"/>
			</colgroup>
			<thead>
				<tr>
					<th data-i18n="public.Element"></th>
					<th data-i18n="public.Description"></th>
					<th data-i18n="public.Roles"></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<tr><td colspan="4"></td></tr>
			</tbody>
		</table>
		<div id="elements-pager" class="k-pager-wrap"></div>
	</div>
</div>

<%@ include file="../includes/templateRoleEntry.inc"%>
<%@ include file="../includes/templateElementRoleEntry.inc"%>
<%@ include file="../includes/deviceGroupElementAddDialog.inc"%>
<%@ include file="../includes/deviceGroupCreateDialog.inc"%>
<%@ include file="../includes/templateDeviceGroupEntry.inc"%>
<%@ include file="../includes/templateDeviceGroupElementEntry.inc"%>
<%@ include file="../includes/commonFunctions.inc"%>

<script>
    /** Set sitewhere_title */
    sitewhere_i18next.sitewhere_title = "groups.detail.title";

	var groupToken = '<c:out value="${group.token}"/>';

	/** Datasource for elements */
	var elementsDS;
	
	$(document).ready(function() {
		
		/** Create AJAX datasource for elements list */
		elementsDS = new kendo.data.DataSource({
			transport : {
				read : {
					url : "${pageContext.request.contextPath}/api/devicegroups/" + groupToken + "/elements?includeDetails=true",
					dataType : "json",
				}
			},
			schema : {
				data: "results",
				total: "numResults",
				parse: parseElementResults,
			},
            serverPaging: true,
            serverSorting: true,
            pageSize: 50,
		});
		
		/** Create the elements grid */
        $("#elements").kendoGrid({
			dataSource : elementsDS,
            rowTemplate: kendo.template($("#tpl-device-group-element-entry").html()),
            scrollable: true,
            height: 400,
        });
		
	    $("#elements-pager").kendoPager({
	        dataSource: elementsDS
	    });
		
	    $("#btn-refresh-elements").click(function() {
	    	elementsDS.read();
	    });
		
	    $("#btn-edit-device-group").click(function() {
			dguOpen(groupToken, onDeviceGroupEditComplete);
	    });
		
	    $("#btn-add-element").click(function() {
			geaOpen(groupToken, onAddElementComplete);
	    });
		
		/** Create the tab strip */
		tabs = $("#tabs").kendoTabStrip({
			animation: false
		}).data("kendoTabStrip");
		
		loadDeviceGroup();
	});
	
	/** Called after device group is edited */
	function onDeviceGroupEditComplete() {
		loadDeviceGroup();
	}
	
	/** Called after group element is added */
	function onAddElementComplete() {
    	elementsDS.read();
	}
	
	/** Parses result records to format data */
	function parseElementResults(response) {
	    $.each(response.results, function (index, item) {
	        if (item.roles) {
	        	item.cdRoles = swArrayAsCommaDelimited(item.roles);
	        }
	    });
	    return response;
	}
	
	/** Loads information for the selected device group */
	function loadDeviceGroup() {
		$.getJSON("${pageContext.request.contextPath}/api/devicegroups/" + groupToken, 
			loadGetSuccess, loadGetFailed);
	}
    
    /** Called on successful device group load request */
    function loadGetSuccess(data, status, jqXHR) {
		var template = kendo.template($("#tpl-device-group-entry").html());
    	parseDeviceGroupData(data);
		data.inDetailView = true;
		$('#device-group-details').html(template(data));
    }
    
	/** Handle error on getting device group data */
	function loadGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load device group data.");
	}
	
	/** Called when a group element is to be deleted */
	function onDeleteDeviceGroupElement(event, type, elementId) {
		var toDelete = [{"type": type, "elementId": elementId}];
		swConfirm(i18next("groups.detail.DeleteDeviceGroupElement"), i18next("groups.detail.AYSTYWTDD")+" id " +
			elementId + "'?", function(result) {
			if (result) {
				$.deleteWithInputJSON("${pageContext.request.contextPath}/api/devicegroups/" + groupToken + "/elements", 
					toDelete, elementDeleteSuccess, elementDeleteFailed);
			}
		});
	}
    
    /** Called on successful device group element delete request */
    function elementDeleteSuccess(data, status, jqXHR) {
    	elementsDS.read();
    }
    
    /** Called on failed device group element delete request */
	function elementDeleteFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, i18next("groups.detail.UTDDGE"));
	}
</script>

<%@ include file="../includes/bottom.inc"%>