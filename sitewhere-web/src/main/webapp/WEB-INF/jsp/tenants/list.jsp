<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="Manage Tenants" />
<c:set var="sitewhere_section" value="tenants" />
<%@ include file="../includes/top.inc"%>

<style>
.w-tenant-list {
	border: 0px;
}
</style>

<%@ include file="tenantCreateDialog.inc"%>
<%@ include file="tenantEntry.inc"%>

<!-- Title Bar -->
<div class="sw-title-bar content k-header">
	<h1 class="ellipsis" data-i18n="tenants.list.title">Manage Tenants</h1>
	<div class="sw-title-bar-right">
		<a id="btn-add-tenant" class="btn" href="javascript:void(0)"> <i
			class="fa fa-plus sw-button-icon"></i> <span data-i18n="tenants.list.AddNew">Add Tenant</span>
		</a> <a id="btn-refresh-tenants" class="btn" href="javascript:void(0)"> <i
			class="fa fa-refresh sw-button-icon"></i> <span data-i18n="public.Refresh">Refresh</span>
		</a>
	</div>
</div>
<div id="tenants" class="w-tenant-list"></div>
<div id="pager" class="k-pager-wrap"></div>

<form id="view-tenant" method="get"></form>

<script>
	/** Tenants datasource */
	var tenantsDS;

	/** Called when open button is clicked */
	function onTenantOpenClicked(e, tenantId) {
		var event = e || window.event;
		event.stopPropagation();
		$("#view-tenant").attr("action",
			"${pageContext.request.contextPath}/admin/tenants/" + tenantId + ".html");
		$('#view-tenant').submit();
	}

	/** Called after a new tenant has been created */
	function onTenantCreated() {
		tenantsDS.read();
	}

	$(document)
			.ready(
				function() {
					/** Create AJAX datasource for sites list */
					tenantsDS =
							new kendo.data.DataSource(
								{
									transport : {
										read : {
											url : "${pageContext.request.contextPath}/api/tenants?includeRuntimeInfo=true&tenantAuthToken=${tenant.authenticationToken}",
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

					/** Create the site list */
					$("#tenants").kendoListView({
						dataSource : tenantsDS,
						template : kendo.template($("#tpl-tenant-entry").html())
					});

					$("#pager").kendoPager({
						dataSource : tenantsDS
					});

					/** Handle add tenant functionality */
					$('#btn-add-tenant').click(function(event) {
						tcOpen(event, onTenantCreated);
					});

					/** Handle refresh functionality */
					$('#btn-refresh-tenants').click(function(event) {
						tenantsDS.read();
					});
				});
</script>

<%@ include file="../includes/bottom.inc"%>