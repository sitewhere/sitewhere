<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="View Site" />
<c:set var="sitewhere_section" value="tenants" />
<%@ include file="../includes/top.inc"%>

<!-- Title Bar -->
<div class="sw-title-bar content k-header" style="margin-bottom: 15px;">
	<h1 class="ellipsis" data-i18n="tenant.detail.title">View Tenant</h1>
	<div class="sw-title-bar-right">
		</a> <a id="btn-refresh-tenant" class="btn" href="javascript:void(0)" data-i18n="public.Refresh">
			<i class="icon-refresh sw-button-icon"></i>
		</a>
	</div>
</div>

<div id="tenant-details" style="line-height: normal;"></div>

<!-- Tab panel -->
<div id="tabs">
	<ul>
		<li class="k-state-active">Details<font data-i18n="tenant.detail.Details"></font></li>
		<li>Logs<font data-i18n="tenant.detail.Logs"></font></li>
	</ul>
	<div></div>
	<div></div>
</div>

<%@ include file="tenantEntry.inc"%>

<script>
	/** Set sitewhere_title */
	sitewhere_i18next.sitewhere_title = "sites.detail.title";

	var tenantId = '<c:out value="${selected.id}"/>';

	/** Tenant information */
	var tenant;

	/** Tabs */
	var tabs;

	/** Called when stop button is clicked */
	function onTenantStopClicked() {
		$.postJSON("${pageContext.request.contextPath}/api/tenants/" + tenantId
				+ "/engine/stop?tenantAuthToken=${tenant.authenticationToken}", null, commandSuccess,
			stopFailed);
	}

	function commandSuccess(data, status, jqXHR) {
		loadTenant();
	}

	function stopFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to process engine stop command.");
	}

	/** Called when start button is clicked */
	function onTenantStartClicked() {
		$.postJSON("${pageContext.request.contextPath}/api/tenants/" + tenantId
				+ "/engine/start?tenantAuthToken=${tenant.authenticationToken}", null, commandSuccess,
			startFailed);
	}

	function startFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to process engine start command.");
	}

	$(document).ready(function() {
		/** Handle refresh button */
		$('#btn-refresh-tenant').click(function(event) {
			loadTenant();
		});

		/** Create the tab strip */
		tabs = $("#tabs").kendoTabStrip({
			animation : false,
		}).data("kendoTabStrip");

		loadTenant();
	});

	/** Loads information for the selected tenant */
	function loadTenant() {
		$.getJSON("${pageContext.request.contextPath}/api/tenants/" + tenantId
				+ "?includeRuntimeInfo=true&tenantAuthToken=${tenant.authenticationToken}", loadGetSuccess,
			loadGetFailed);
	}

	/** Called on successful site load request */
	function loadGetSuccess(data, status, jqXHR) {
		tenant = data;
		var template = kendo.template($("#tpl-tenant-entry").html());
		data.inDetailView = true;
		$('#tenant-details').html(template(data));

		if (tenant.engineState) {
			if (data.engineState.lifecycleStatus == 'Started') {
				$('#tenant-power-off').show();
				$('#tenant-power-on').hide();
			} else if (data.engineState.lifecycleStatus == 'Stopped') {
				$('#tenant-power-off').hide();
				$('#tenant-power-on').show();
			}
		}
	}

	/** Handle error on getting tenant data */
	function loadGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load tenant data.");
	}
</script>

<%@ include file="../includes/bottom.inc"%>