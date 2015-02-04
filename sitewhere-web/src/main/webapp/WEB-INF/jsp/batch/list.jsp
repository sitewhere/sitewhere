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
	<h1 class="ellipsis">
		<c:out value="${sitewhere_title}" />
	</h1>
	<div class="sw-title-bar-right">
		<a id="btn-filter-results" class="btn" href="javascript:void(0)">
			<i class="icon-search sw-button-icon"></i> Filter Results
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
			<th>Operation</th>
			<th>Processing Status</th>
			<th>Operation Created</th>
			<th>Processing Started</th>
			<th>Processing Finished</th>
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