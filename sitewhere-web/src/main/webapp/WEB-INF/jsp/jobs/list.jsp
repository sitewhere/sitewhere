<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="Manage Scheduled Jobs" />
<c:set var="sitewhere_section" value="schedules" />
<%@ include file="../includes/top.inc"%>

<style>
.sw-schedules-list {
	border: 0px;
}

table#jobs tr td {
	vertical-align: top;
}
</style>

<!-- Title Bar -->
<div class="sw-title-bar content k-header">
	<h1 class="ellipsis" data-i18n="jobs.list.title"></h1>
	<div class="sw-title-bar-right">
		<a id="btn-filter-results" class="btn" href="javascript:void(0)"> <i
			class="fa fa-filter sw-button-icon"></i> <span data-i18n="public.FilterResults">Filter
				Results</span>
		</a>
	</div>
</div>
<table id="jobs">
	<colgroup>
		<col style="width: 20%;" />
		<col style="width: 30%;" />
		<col style="width: 25%;" />
		<col style="width: 20%;" />
		<col style="width: 5%;" />
	</colgroup>
	<thead>
		<tr>
			<th data-i18n="public.Type">Type</th>
			<th data-i18n="public.Token">Details</th>
			<th data-i18n="public.Schedule">Schedule</th>
			<th data-i18n="public.CreatedDate">Created Date</th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td colspan="5"></td>
		</tr>
	</tbody>
</table>
<div id="pager" class="k-pager-wrap"></div>

<%@ include file="templateScheduledJobEntry.inc"%>
<%@ include file="../includes/commonFunctions.inc"%>

<script>
	/** Set sitewhere_title */
	sitewhere_i18next.sitewhere_title = "jobs.list.title";

	/** Reference for batch operations datasource */
	var jobsDS;

	/** Called when delete button is clicked */
	function onDeleteJob(token) {
		swConfirm(i18next("jobs.list.DeleteJob"), i18next("jobs.list.AYSD") + "?", function(result) {
			if (result) {
				$.deleteJSON("${pageContext.request.contextPath}/api/jobs/" + token
						+ "?force=true&tenantAuthToken=${tenant.authenticationToken}", onDeleteSuccess,
					onDeleteFail);
			}
		});
	}

	/** Called on successful delete */
	function onDeleteSuccess() {
		jobsDS.read();
	}

	/** Handle failed delete call */
	function onDeleteFail(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, i18next("jobs.list.UTD"));
	}

	$(document).ready(
		function() {

			jobsDS =
					new kendo.data.DataSource({
						transport : {
							read : {
								url : "${pageContext.request.contextPath}/api/jobs?includeContext=true&"
										+ "tenantAuthToken=${tenant.authenticationToken}",
								dataType : "json",
							}
						},
						schema : {
							data : "results",
							total : "numResults",
							parse : function(response) {
								$.each(response.results, function(index, item) {
									parseEntityData(item);
								});
								return response;
							}
						},
						serverPaging : true,
						serverSorting : true,
						pageSize : 50,
					});

			/** Create the list */
			$("#jobs").kendoGrid({
				dataSource : jobsDS,
				rowTemplate : kendo.template($("#tpl-scheduled-job-entry").html()),
				scrollable : true,
				height : 400,
			});

			/** Pager for list */
			$("#pager").kendoPager({
				dataSource : jobsDS
			});
		});
</script>

<%@ include file="../includes/bottom.inc"%>