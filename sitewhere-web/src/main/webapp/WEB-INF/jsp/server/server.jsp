<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="Server Information" />
<c:set var="sitewhere_section" value="server" />
<%@ include file="../includes/top.inc"%>

<style>
.panel-item {
	font-size: 14pt;
	padding: 10px 15px;
}

.panel-item-odd {
	background-color: #f5f5f5;
}

.panel-item-label {
	font-weight: bold;
	display: inline-block;
	min-width: 300px;
}
</style>

<!-- Title Bar -->
<div class="sw-title-bar content k-header">
	<h1 class="ellipsis" data-i18n="server.title">Server Information</h1>
	<div class="sw-title-bar-right">
		<a id="btn-refresh-data" class="btn" href="javascript:void(0)"> <i
			class="fa fa-refresh sw-button-icon"></i> <span data-i18n="public.Refresh">Refresh</span>
		</a>
	</div>
</div>

<div>
	<ul id="panelbar">
		<li id="category-general">Server Runtime Information
			<div>
				<div class="panel-item panel-item-even">
					<span class="panel-item-label">Product</span><span id="gen-product"></span>
				</div>
				<div class="panel-item panel-item-odd">
					<span class="panel-item-label">Edition</span><span id="gen-edition"></span>
				</div>
				<div class="panel-item panel-item-even">
					<span class="panel-item-label">Operating System</span><span id="gen-os-name"></span>
				</div>
				<div class="panel-item panel-item-odd">
					<span class="panel-item-label">Operating System Version</span><span id="gen-os-version"></span>
				</div>
				<div class="panel-item panel-item-even">
					<span class="panel-item-label">Uptime</span><span id="gen-uptime"></span>
				</div>
			</div>
		</li>
		<li id="category-general">Java Virtual Machine Information
			<div>
				<div class="panel-item panel-item-even">
					<span class="panel-item-label">JVM Vendor</span><span id="jvm-vendor"></span>
				</div>
				<div class="panel-item panel-item-odd">
					<span class="panel-item-label">JVM Version</span><span id="jvm-version"></span>
				</div>
				<div class="panel-item panel-item-even">
					<span class="panel-item-label">JVM Max Memory</span><span id="jvm-max-memory"></span>
				</div>
				<div class="panel-item panel-item-odd">
					<span class="panel-item-label">JVM Total Memory</span><span id="jvm-total-memory"></span>
				</div>
				<div class="panel-item panel-item-even">
					<span class="panel-item-label">JVM Free Memory</span><span id="jvm-free-memory"></span>
				</div>
			</div>
		</li>
	</ul>
</div>
<script>
	/** Panel bar handle */
	var panelBar;

	$(document).ready(function() {
		panelBar = $("#panelbar").kendoPanelBar({
			expandMode : "multiple"
		}).data("kendoPanelBar");

		/** Handle add site functionality */
		$('#btn-refresh-data').click(function(event) {
			reloadData();
		});

		reloadData();
		panelBar.expand($('[id^="category"]'));

		setInterval(reloadData, 3000);
	});

	/** Reload server runtime data */
	function reloadData() {
		$.getJSON("${pageContext.request.contextPath}/api/system//state", loadGetSuccess, loadGetFailed);
	}

	/** Called on successful assignment load request */
	function loadGetSuccess(data, status, jqXHR) {
		$('#gen-product').text(
			"SiteWhere " + data.general.versionIdentifier + " " + data.general.editionIdentifier);
		$('#gen-edition').text(data.general.edition);
		$('#gen-os-name').text(data.general.operatingSystemName);
		$('#gen-os-version').text(data.general.operatingSystemVersion);
		$('#gen-uptime').text("Started " + $.timeago((new Date()).getTime() - data.general.uptime) + ".");

		$('#jvm-vendor').text(data.java.jvmVendor);
		$('#jvm-version').text(data.java.jvmVersion);
		$('#jvm-max-memory').text(Math.floor(data.java.jvmMaxMemory / (1024 * 1024)) + " MB");
		$('#jvm-total-memory').text(Math.floor(data.java.jvmTotalMemory / (1024 * 1024)) + " MB");
		$('#jvm-free-memory').text(Math.floor(data.java.jvmFreeMemory / (1024 * 1024)) + " MB");
	}

	/** Handle error on getting assignment data */
	function loadGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load server state data.");
	}
</script>

<%@ include file="../includes/bottom.inc"%>