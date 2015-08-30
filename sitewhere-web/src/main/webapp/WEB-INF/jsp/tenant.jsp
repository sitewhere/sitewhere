<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="Choose Tenant" />
<!DOCTYPE html>
<html class="sw-body">
<head>
<title>SiteWhere - <c:out value="${sitewhere_title}" /></title>
<script src="${pageContext.request.contextPath}/scripts/jquery-1.10.2.min.js"></script>
<script src="${pageContext.request.contextPath}/scripts/jquery.validity.js"></script>
<script src="${pageContext.request.contextPath}/scripts/kendo.web.min.js"></script>
<script src="${pageContext.request.contextPath}/scripts/modernizr.js"></script>
<script src="${pageContext.request.contextPath}/scripts/moment.js"></script>
<script src="${pageContext.request.contextPath}/scripts/sitewhere.js"></script>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/img/favicon.ico" />
<link href="${pageContext.request.contextPath}/css/kendo.common.min.css" rel="stylesheet" />
<link href="${pageContext.request.contextPath}/css/kendo.bootstrap.min.css" rel="stylesheet" />
<link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"
	media="screen">
<link href="${pageContext.request.contextPath}/css/font-awesome.min.css" rel="stylesheet"
	media="screen">
<link href="${pageContext.request.contextPath}/css/jquery.validity.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/sitewhere.css" rel="stylesheet" />
<%@ include file="includes/i18next.inc"%>
</head>
<style>
.sw-tenant-list {
	border: 0px;
}
</style>
<body class="sw-body">
	<div class="sw-container">
		<div class="sw-top-bar"></div>
		<div class="k-content container sw-content">
			<div class="sw-header">
				<div class="sw-logo">
					<img src="${pageContext.request.contextPath}/img/sitewhere-small.png" />
				</div>
			</div>

			<!-- Title Bar -->
			<div class="sw-title-bar content k-header">
				<h1 class="ellipsis" data-i18n="tenants.select.tenant">Select Tenant</h1>
			</div>
			<div id="tenants" class="sw-tenant-list"></div>

			<form id="view-tenant" method="get" />

			<%@ include file="tenants/tenantChooserEntry.inc"%>

			<script>
				/** Set sitewhere_title */
				sitewhere_i18next.sitewhere_title = "Choose Tenant";

				/** Tenants datasource */
				var tenantsDS;

				/** Called when tenant 'Select' button is clicked */
				function onTenantSelected(event, tenantId) {
					var redirect = '${redirect}';
					if (redirect.length == 0) {
						$("#view-tenant").attr("action",
							"${pageContext.request.contextPath}/admin/tenant/" + tenantId + ".html");
					} else {
						$("#view-tenant").attr(
							"action",
							"${pageContext.request.contextPath}/admin/tenant/" + tenantId + ".html?redirect="
									+ redirect);
					}
					$('#view-tenant').submit();
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
														url : "${pageContext.request.contextPath}/api/users/${currentUser.username}/tenants",
														dataType : "json",
													}
												},
											});

								/** Create the site list */
								$("#tenants").kendoListView({
									dataSource : tenantsDS,
									template : kendo.template($("#tpl-tenant-entry").html())
								});
							});
			</script>

			<%@ include file="includes/bottom.inc"%>