<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="Manage Schedules" />
<c:set var="sitewhere_section" value="schedules" />
<%@ include file="../includes/top.inc"%>

<style>
.sw-schedules-list {
	border: 0px;
}
</style>

<!-- Title Bar -->
<div class="sw-title-bar content k-header">
	<h1 class="ellipsis" data-i18n="schedules.list.title"></h1>
	<div class="sw-title-bar-right">
		<a id="btn-filter-results" class="btn" href="javascript:void(0)"> <i
			class="fa fa-filter sw-button-icon"></i> <span data-i18n="public.FilterResults">Filter
				Results</span>
		</a> <a id="btn-add-schedule" class="btn" href="javascript:void(0)"> <i
			class="fa fa-plus sw-button-icon"></i> <span data-i18n="schedules.list.AddNewSchedule"></span>
		</a>
	</div>
</div>
<table id="schedules">
	<colgroup>
		<col style="width: 28%;" />
		<col style="width: 15%;" />
		<col style="width: 30%;" />
		<col style="width: 20%;" />
		<col style="width: 7%;" />
	</colgroup>
	<thead>
		<tr>
			<th data-i18n="public.Name">Name</th>
			<th data-i18n="public.Type">Type</th>
			<th data-i18n="public.Token">Token</th>
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

<%@ include file="templateScheduleEntry.inc"%>
<%@ include file="../includes/commonFunctions.inc"%>

<script>
	/** Set sitewhere_title */
	sitewhere_i18next.sitewhere_title = "schedules.list.title";

	/** Reference for batch operations datasource */
	var schedDS;

	$(document)
			.ready(
				function() {

					schedDS =
							new kendo.data.DataSource(
								{
									transport : {
										read : {
											url : "${pageContext.request.contextPath}/api/schedules?tenantAuthToken=${tenant.authenticationToken}",
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
					$("#schedules").kendoGrid({
						dataSource : schedDS,
						rowTemplate : kendo.template($("#tpl-schedule-entry").html()),
						scrollable : true,
						height : 400,
					});

					/** Pager for list */
					$("#pager").kendoPager({
						dataSource : schedDS
					});
				});
</script>

<%@ include file="../includes/bottom.inc"%>