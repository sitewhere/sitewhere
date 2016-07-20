<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="View Tenant Group" />
<c:set var="sitewhere_section" value="tenants" />
<%@ include file="../includes/top.inc"%>

<style>
.group-pager {
	margin-top: 10px;
}
</style>

<!-- Title Bar -->
<div class="sw-title-bar content k-header" style="margin-bottom: 15px;">
	<h1 class="ellipsis">View Tenant Group</h1>
	<div class="sw-title-bar-right">
		<a id="btn-edit-tenant-group" class="btn" href="javascript:void(0)">
			<i class="fa fa-pencil sw-button-icon"></i> <span
			data-i18n="public.EditTenantGroup">Edit Tenant Group</span>
		</a>
	</div>
</div>

<!-- Detail panel for selected tenant group -->
<div id="tenant-group-details" style="line-height: normal;"></div>

<!-- Tab panel -->
<div id="tabs">
	<ul>
		<li class="k-state-active">Tenants</li>
	</ul>
	<div>
		<div class="k-header sw-button-bar">
			<div class="sw-button-bar-title">Tenants</div>
			<div>
				<a id="btn-filter-elements" class="btn" href="javascript:void(0)">
					<i class="fa fa-filter sw-button-icon"></i> <span
					data-i18n="public.FilterResults">Filter Results</span>
				</a> <a id="btn-refresh-elements" class="btn" href="javascript:void(0)">
					<i class="fa fa-refresh sw-button-icon"></i> <span
					data-i18n="public.Refresh">Refresh</span>
				</a> <a id="btn-add-tenant" class="btn" href="javascript:void(0)"> <i
					class="fa fa-plus sw-button-icon"></i> <span
					data-i18n="public.AddTenant">Add Tenant</span>
				</a>
			</div>
		</div>
		<table id="elements">
			<colgroup>
				<col style="width: 25%;" />
				<col style="width: 45%;" />
				<col style="width: 20%;" />
				<col style="width: 10%;" />
			</colgroup>
			<thead>
				<tr>
					<th data-i18n="public.TenantId">Tenant Id</th>
					<th data-i18n="public.Name">Name</th>
					<th data-i18n="public.TenantState">State</th>
					<th></th>
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

<%@ include file="tgroupEntry.inc"%>
<%@ include file="tgroupElementEntry.inc"%>
<%@ include file="tgroupCreateDialog.inc"%>
<%@ include file="../tenants/tenantEntry.inc"%>
<%@ include file="../tenants/tenantSearchDialog.inc"%>

<script>
	/** Set sitewhere_title */
	sitewhere_i18next.sitewhere_title = "View Tenant Group";

	var groupToken = '<c:out value="${tgroup.token}"/>';

	/** Datasource for elements */
	var elementsDS;

	$(document)
			.ready(
					function() {

						/** Create AJAX datasource for elements list */
						elementsDS = new kendo.data.DataSource(
								{
									transport : {
										read : {
											url : "${pageContext.request.contextPath}/api/tgroups/"
													+ groupToken
													+ "/elements?includeTenantDetails=true",
											beforeSend : function(req) {
												req.setRequestHeader(
														'Authorization',
														"Basic ${basicAuth}");
												req
														.setRequestHeader(
																'X-SiteWhere-Tenant',
																"${tenant.authenticationToken}");
											},
											dataType : "json",
										}
									},
									schema : {
										data : "results",
										total : "numResults",
										parse : parseElementResults,
									},
									serverPaging : true,
									serverSorting : true,
									pageSize : 50,
								});

						/** Create the elements grid */
						$("#elements")
								.kendoGrid(
										{
											dataSource : elementsDS,
											rowTemplate : kendo
													.template($(
															"#tpl-tgroup-element-entry")
															.html()),
											scrollable : true,
											height : 400,
										});

						$("#elements-pager").kendoPager({
							dataSource : elementsDS
						});

						$("#btn-refresh-elements").click(function() {
							elementsDS.read();
						});

						$("#btn-edit-tenant-group").click(function() {
							tguOpen(groupToken, onTenantGroupEditComplete)
						});

						$("#btn-add-tenant").click(function() {
							tsrOpen(onAddTenantChoiceComplete);
						});

						/** Create the tab strip */
						tabs = $("#tabs").kendoTabStrip({
							animation : false
						}).data("kendoTabStrip");

						loadTenantGroup();
					});

	/** Called after device group is edited */
	function onTenantGroupEditComplete() {
		loadTenantGroup();
	}

	/** Called after tenant is chosen in the dialog */
	function onAddTenantChoiceComplete(tenantId) {
		var toAdd = [ {
			"tenantId" : tenantId
		} ];
		$.putAuthJSON("${pageContext.request.contextPath}/api/tgroups/"
				+ groupToken + "/elements", toAdd, "${basicAuth}",
				"${tenant.authenticationToken}", addTenantSuccess,
				addTenantFailed);
	}

	/** Called after successfully adding tenant to group */
	function addTenantSuccess(data, status, jqXHR) {
		elementsDS.read();
	}

	/** Handle error on adding tenant to tenant group */
	function addTenantFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to add tenant to group.");
	}

	/** Parses result records to format data */
	function parseElementResults(response) {
		return response;
	}

	/** Loads information for the selected device group */
	function loadTenantGroup() {
		$.getAuthJSON("${pageContext.request.contextPath}/api/tgroups/"
				+ groupToken, "${basicAuth}", "${tenant.authenticationToken}",
				loadGetSuccess, loadGetFailed);
	}

	/** Called on successful device group load request */
	function loadGetSuccess(data, status, jqXHR) {
		var template = kendo.template($("#tpl-tgroup-entry").html());
		parseEntityData(data);
		data.inDetailView = true;
		$('#tenant-group-details').html(template(data));
	}

	/** Handle error on getting device group data */
	function loadGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load tenant group data.");
	}

	/** Called when a group element is to be deleted */
	function onDeleteTenantGroupElement(event, type, tenantId) {
		var toDelete = [ {
			"tenantId" : tenantId
		} ];
		swConfirm("Remove Tenant"), i18next(
				"Are you sure you want to remove this tenant from the group?",
				function(result) {
					if (result) {
						$.deleteWithInputAuthJSON(
								"${pageContext.request.contextPath}/api/tgroups/"
										+ groupToken + "/elements", toDelete,
								"${basicAuth}",
								"${tenant.authenticationToken}",
								elementDeleteSuccess, elementDeleteFailed);
					}
				});
	}

	/** Called on successful device group element delete request */
	function elementDeleteSuccess(data, status, jqXHR) {
		elementsDS.read();
	}

	/** Called on failed tenant group element delete request */
	function elementDeleteFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to delete teneant group element.");
	}
</script>

<%@ include file="../includes/bottom.inc"%>