<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="View Batch Command Invocation" />
<c:set var="sitewhere_section" value="batch" />
<%@ include file="../includes/top.inc"%>

<style>
.group-pager {
	margin-top: 10px;
}
</style>

<!-- Title Bar -->
<div class="sw-title-bar content k-header" style="margin-bottom: -1px;">
	<h1 class="ellipsis" data-i18n="batch.command.title"></h1>
	<div class="sw-title-bar-right">
		<a id="btn-refresh-operation" class="btn" href="javascript:void(0)" data-i18n="public.Refresh">
			<i class="icon-refresh sw-button-icon"></i> 
		</a>
	</div>
</div>

<!-- Detail panel for selected batch command invocation -->
<div id="batch-command-invocation-details" style="line-height: normal;"></div>

<!-- Tab panel -->
<div id="tabs">
	<ul>
		<li class="k-state-active" data-i18n="public." data-i18n="public.Elements"></li>
	</ul>
	<div>
		<div class="k-header sw-button-bar">
			<div class="sw-button-bar-title" data-i18n="batch.command.BCIE"></div>
			<div>
				<a id="btn-filter-elements" class="btn" href="javascript:void(0)" data-i18n="public.FilterResults">
					<i class="icon-search sw-button-icon"></i>
				</a> <a id="btn-refresh-elements" class="btn" href="javascript:void(0)" data-i18n="public.Refresh">
					<i class="icon-refresh sw-button-icon"></i> 
				</a>
			</div>
		</div>
		<table id="elements">
			<colgroup>
				<col style="width: 35%;" />
				<col style="width: 15%;" />
				<col style="width: 20%;" />
				<col style="width: 30%;" />
			</colgroup>
			<thead>
				<tr>
					<th data-i18n="public.HardwareId"></th>
					<th data-i18n="batch.ProcessingStatus"></th>
					<th data-i18n="batch.command.ProcessedDate"></th>
					<th data-i18n="batch.command.InvocationEventId"></th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td colspan="4"></td>
				</tr>
			</tbody>
		</table>
		<div id="elements-pager" class="k-pager-wrap"></div>
	</div>
</div>

<%@ include file="../includes/templateInvocationSummaryEntry.inc"%>
<%@ include file="../includes/templateResponseSummaryEntry.inc"%>
<%@ include file="../includes/invocationViewDialog.inc"%>

<%@ include file="../includes/templateBatchCommandInvocationEntry.inc"%>
<%@ include file="../includes/commonFunctions.inc"%>

<script>
    /** Set sitewhere_title */
    sitewhere_i18next.sitewhere_title = "batch.command.title";

	/** Unique batch operation token */
	var batchToken = '<c:out value="${operation.token}"/>';
	
	/** JSON command information */
	var command = ${command};

	/** Datasource for elements */
	var elementsDS;

	$(document).ready(function() {

		/** Create AJAX datasource for elements list */
		elementsDS = new kendo.data.DataSource({
			transport : {
				read : {
					url : "${pageContext.request.contextPath}/api/batch/" + batchToken + "/elements",
					dataType : "json",
				}
			},
			schema : {
				data : "results",
				total : "numResults",
				parse : function(response) {
					$.each(response.results, function(index, item) {
						parseBatchElementData(item);
					});
					return response;
				}
			},
			serverPaging : true,
			serverSorting : true,
			pageSize : 50,
		});

		/** Create the elements grid */
		$("#elements").kendoGrid({
			dataSource : elementsDS,
			rowTemplate : kendo.template($("#tpl-batch-command-invocation-element-entry").html()),
			scrollable : true,
			height : 400,
		});

		$("#elements-pager").kendoPager({
			dataSource : elementsDS
		});

		$("#btn-refresh-operation").click(function() {
			loadBatchOperation();
		});

		$("#btn-refresh-elements").click(function() {
			elementsDS.read();
		});

		/** Create the tab strip */
		tabs = $("#tabs").kendoTabStrip({
			animation : false
		}).data("kendoTabStrip");

		loadBatchOperation();
	});

	/** Loads information for the selected batch command invocation */
	function loadBatchOperation() {
		$
				.getJSON("${pageContext.request.contextPath}/api/batch/" + batchToken, loadGetSuccess,
					loadGetFailed);
	}

	/** Called on successful batch operation load request */
	function loadGetSuccess(data, status, jqXHR) {
		var template = kendo.template($("#tpl-batch-command-invocation-entry").html());
		parseBatchOperationData(data);
		data.command = command;
		data.commandHtml = swHtmlifyCommandWithValues(command, data.metadata);
		$('#batch-command-invocation-details').html(template(data));
	}

	/** Handle error on getting batch operation data */
	function loadGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load batch command invocation data.");
	}
	
	/** View a command invocation that has been clicked on */
	function viewCommandInvocation(event, id) {
		ivOpen(id);
	}
</script>

<%@ include file="../includes/bottom.inc"%>