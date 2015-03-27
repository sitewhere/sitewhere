<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="Manage Batch Operations" />
<c:set var="sitewhere_section" value="batch" />
<%@ include file="../includes/top.inc"%>

<style>
.sw-device-list {
	border: 0px;
}
</style>

<!-- Title Bar -->
<div class="sw-title-bar content k-header">
	<h1 class="ellipsis" data-i18n="batch.list.title"></h1>
	<div class="sw-title-bar-right">
		<a id="btn-filter-results" class="btn" href="javascript:void(0)" data-i18n="public.FilterResults">
			<i class="icon-search sw-button-icon"></i>
		</a>
	</div>
</div>
<table id="batchoperations">
	<colgroup>
		<col style="width: 19%;" />
		<col style="width: 19%;" />
		<col style="width: 19%;" />
		<col style="width: 19%;" />
		<col style="width: 19%;" />
		<col style="width: 5%;" />
	</colgroup>
	<thead>
		<tr>
			<th data-i18n="public.Operation"></th>
			<th data-i18n="batch.ProcessingStatus"></th>
			<th data-i18n="batch.list.OperationCreated"></th>
			<th data-i18n="public.ProcessingStarted"></th>
			<th data-i18n="public.ProcessingFinished"></th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td colspan="6"></td>
		</tr>
	</tbody>
</table>
<div id="pager" class="k-pager-wrap"></div>

<form id="view-batch-operation-detail" method="get">
	<input id="batch-operation-token" name="token" type="hidden" />
</form>

<%@ include file="../includes/templateBatchOperationEntry.inc"%>
<%@ include file="../includes/commonFunctions.inc"%>

<script>
    /** Set sitewhere_title */
    sitewhere_i18next.sitewhere_title = "batch.list.title";

	/** Reference for batch operations datasource */
	var batchOpsDS;

	/** View a batch operation */
	function viewBatchOperation(type, token) {
		if (type == "InvokeCommand") {
			$("#batch-operation-token").val(token);
			$("#view-batch-operation-detail").get(0).setAttribute('action', 'command.html');
			$("#view-batch-operation-detail").submit();
		}
	}

	$(document).ready(function() {

		/** Create AJAX datasource for devices list */
		batchOpsDS = new kendo.data.DataSource({
			transport : {
				read : {
					url : "${pageContext.request.contextPath}/api/batch",
					dataType : "json",
				}
			},
			schema : {
				data : "results",
				total : "numResults",
				parse : function(response) {
					$.each(response.results, function(index, item) {
						parseBatchOperationData(item);
					});
					return response;
				}
			},
			serverPaging : true,
			serverSorting : true,
			pageSize : 50,
		});

		/** Create the list of batch operations */
		$("#batchoperations").kendoGrid({
			dataSource : batchOpsDS,
			rowTemplate : kendo.template($("#tpl-batch-operation-entry").html()),
			scrollable : true,
			height : 400,
		});

		/** Pager for batch operation list */
		$("#pager").kendoPager({
			dataSource : batchOpsDS
		});
	});
</script>

<%@ include file="../includes/bottom.inc"%>