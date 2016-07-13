<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="Manage Tenant Groups" />
<c:set var="sitewhere_section" value="tenants" />
<%@ include file="../includes/top.inc"%>

<style>
.w-tgroup-list {
	border: 0px;
}
</style>

<%@ include file="tgroupCreateDialog.inc"%>
<%@ include file="tgroupEntry.inc"%>

<!-- Title Bar -->
<div class="sw-title-bar content k-header">
	<h1 class="ellipsis" data-i18n="tgroups.list.title">Manage Tenant
		Groups</h1>
	<div class="sw-title-bar-right">
		<a id="btn-add-tenant" class="btn" href="javascript:void(0)"> <i
			class="fa fa-plus sw-button-icon"></i> <span
			data-i18n="tgroups.list.AddNew">Add Tenant Group</span>
		</a> <a id="btn-refresh-tgroups" class="btn" href="javascript:void(0)">
			<i class="fa fa-refresh sw-button-icon"></i> <span
			data-i18n="public.Refresh">Refresh</span>
		</a>
	</div>
</div>
<div id="tgroups" class="w-tgroup-list"></div>
<div id="pager" class="k-pager-wrap"></div>

<form id="view-tgroup" method="get"></form>

<script>
	/** Tenant groups datasource */
	var tgroupsDS;

	/** Called when open button is clicked */
	function onTenantGroupOpenClicked(e, tenantId) {
		var event = e || window.event;
		event.stopPropagation();
		$("#view-tgroup").attr(
				"action",
				"${pageContext.request.contextPath}/admin/tgroups/" + tenantId
						+ ".html");
		$('#view-tgroup').submit();
	}

	/** Called after a new tenant group has been created */
	function onTenantGroupCreated() {
		tgroupsDS.read();
	}

	$(document)
			.ready(
					function() {
						/** Create AJAX datasource for list */
						tgroupsDS = new kendo.data.DataSource(
								{
									transport : {
										read : {
											url : "${pageContext.request.contextPath}/api/tgroups",
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
									},
									serverPaging : true,
									serverSorting : true,
									pageSize : 10
								});

						/** Create the list */
						$("#tgroups").kendoListView(
								{
									dataSource : tgroupsDS,
									template : kendo.template($(
											"#tpl-tgroup-entry").html())
								});

						$("#pager").kendoPager({
							dataSource : tgroupsDS
						});

						/** Handle add functionality */
						$('#btn-add-tgroup').click(function(event) {
							tgcOpen(event, onTenantGroupCreated);
						});

						/** Handle refresh functionality */
						$('#btn-refresh-tgroups').click(function(event) {
							tgroupsDS.read();
						});
					});
</script>

<%@ include file="../includes/bottom.inc"%>