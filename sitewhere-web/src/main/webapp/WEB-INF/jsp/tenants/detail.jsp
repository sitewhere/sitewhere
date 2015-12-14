<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="View Tenant" />
<c:set var="sitewhere_section" value="tenants" />
<c:set var="use_highlight" value="true" />
<c:set var="use_bxslider" value="true" />
<%@ include file="../includes/top.inc"%>

<style>
div.wz-header {
	border: 1px solid #aaa;
	background-color: #eee;
	padding: 13px;
	margin-bottom: 10px;
	-webkit-box-shadow: 4px 4px 4px 0px rgba(192, 192, 192, 0.3);
	-moz-box-shadow: 4px 4px 4px 0px rgba(192, 192, 192, 0.3);
	box-shadow: 4px 4px 4px 0px rgba(192, 192, 192, 0.3);
}

div.wz-header h1 {
	font-size: 26px;
	line-height: 1em;
	vertical-align: top;
	margin: 1px;
	display: inline;
}

div.wz-header h2 {
	font-size: 16px;
	margin: 0;
	margin-top: 15px;
	line-height: 1.1em;
	font-weight: normal;
	clear: both;
	line-height: 1.1em;
}

.wz-header-icon {
	float: left;
	padding-right: 10px;
	font-size: 26px;
}

div.wz-child {
	border: 1px solid #ccc;
	background-color: #eee;
	padding: 10px;
	margin-bottom: 10px;
}

div.wz-child .wz-child-icon {
	float: left;
	padding: 8px 10px;
	font-size: 22px;
}

div.wz-child .wz-child-name {
	display: inline;
	font-size: 20px;
	padding: 0;
	margin: 0;
}

div.wz-child .wz-child-nav {
	float: right;
	padding: 10px;
}

div.wz-divider {
	clear: both;
	padding-top: 10px;
	margin-top: 10px;
	border-top: 1px solid #eee;
}

ol.wz-breadcrumb {
	margin-top: 8px;
	margin-bottom: -2px;
	margin-right: 6px;
	margin-left: 6px;
	padding: 2px 8px;
	border: 1px solid #eee;
	border-radius: 0px;
	font-size: 12px;
	background-color: #f9f9f9;
}

div.wz-button-bar {
	padding: 7px 0px;
}
</style>

<!-- Title Bar -->
<div class="sw-title-bar content k-header" style="margin-bottom: 15px;">
	<h1 class="ellipsis" data-i18n="tenant.detail.title">View Tenant</h1>
	<div class="sw-title-bar-right">
		<a id="btn-refresh-tenant" class="btn" href="javascript:void(0)"> <i
			class="fa fa-refresh sw-button-icon"></i> <span data-i18n="public.Refresh">Refresh</span>
		</a>
	</div>
</div>

<div id="tenant-details" style="line-height: normal;"></div>

<!-- Tab panel -->
<div id="tabs">
	<ul>
		<li class="k-state-active"><font data-i18n="tenants.detail.EngineConfiguration"></font></li>
		<li><font data-i18n="tenants.detail.EngineState"></font></li>
	</ul>
	<div>
		<div>
			<div>
				<div id="tve-config-editor">
					<div id="tve-config-page"></div>
				</div>
			</div>
		</div>
		<div class="wz-button-bar">
			<div style="float: right;">
				<a id="tve-dialog-submit" href="javascript:void(0)" class="btn btn-primary"
					data-i18n="tenant.editor.stage">Stage Updates</a>
			</div>
			<div style="clear: both;"></div>
		</div>
	</div>
	<div>
		<div id="detail-content"></div>
	</div>
</div>

<form id="view-tenant-list" method="get"></form>

<%@ include file="tenantCreateDialog.inc"%>
<%@ include file="tenantEntry.inc"%>

<!-- Details panel shown for a started engine -->
<script type="text/x-kendo-tmpl" id="tpl-engine-started">
	<div>
		<div id="tenant-engine-hierarchy" style="margin-top: 10px; margin-bottom: 10px;"></div>
	</div>
</script>

<!-- Details panel shown for a stopped engine -->
<script type="text/x-kendo-tmpl" id="tpl-engine-stopped">
	<div style="text-align: center; font-size: 26px; padding: 50px;">
		<i class="fa fa-power-off sw-button-icon" style="color: \\#ccc;"></i> Tenant Engine is Stopped
	</div>
</script>

<!-- Details panel shown for a engine in other non-running states -->
<script type="text/x-kendo-tmpl" id="tpl-engine-not-running">
	<div style="text-align: center; font-size: 26px; padding: 50px;">
		<i class="fa fa-power-off sw-button-icon" style="color: \\#ccc;"></i> Tenant Engine is Not Running
	</div>
</script>

<script>
	/** Selected tenant id */
	var tenantId = '<c:out value="${selected.id}"/>';

	/** Tenant configuration model */
	var configModel = <c:out value="${configModel}" escapeXml="false"/>;

	/** Configuration being edited */
	var config;

	/** Tenant information */
	var tenant;

	/** Tabs */
	var tabs;

	/** Context stack for editor */
	var editorContexts = [];

	/** Called when delete button is clicked */
	function onDeleteClicked() {
		swConfirm("Delete Tenant", "Are you sure you want to delete tenant '" + tenantId + "'?", function(
				result) {
			if (result) {
				$.deleteJSON("${pageContext.request.contextPath}/api/tenants/" + tenantId
						+ "?force=true&tenantAuthToken=${tenant.authenticationToken}", onDeleteSuccess,
					onDeleteFail);
			}
		});
	}

	/** Called on successful delete */
	function onDeleteSuccess() {
		$("#view-tenant-list").attr("action", "${pageContext.request.contextPath}/admin/tenants/list.html");
		$('#view-tenant-list').submit();
	}

	/** Handle failed delete call */
	function onDeleteFail(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to delete tenant.");
	}

	/** Called when config button is clicked */
	function onConfigClicked() {
		tveOpen(tenantId, onConfigSuccess);
	}

	/** Called on successful config */
	function onConfigSuccess() {
	}

	/** Called when edit button is clicked */
	function onEditClicked() {
		tuOpen(tenantId, onEditSuccess);
	}

	/** Called on successful edit */
	function onEditSuccess() {
		loadTenant();
	}

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

	/** Reset the wizard */
	function resetWizard() {
		editorContexts = [];
		addRootPanel();
	}

	/** Add the root panel */
	function addRootPanel() {
		var configNode = findConfigNodeByName(config, "tenant-configuration");
		var modelNode = findModelNodeByName(configModel, "tenant-configuration");
		addPanelFor(configNode, modelNode);
	}

	/** Add new panel for a given element */
	function addPanelFor(configNode, modelNode) {
		var context = {
			"config" : configNode,
			"model" : modelNode,
		};
		editorContexts.push(context);

		var panel = "<div>";
		panel += addBreadcrumbs();
		panel += "<div class='wz-header'>";
		panel += "<i class='wz-header-icon fa fa-" + modelNode.icon + " fa-white'></i>";
		panel += "<h1>" + modelNode.name + "</h1>";
		panel += "<div style='float: right;'><a id='btn-add-element' class='btn' href='javascript:void(0)'>";
		panel +=
				" <i class='fa fa-plus sw-button-icon'></i> <span data-i18n='public.Add'>Add Component</span>";
		panel += "</a></div>"
		panel += "<h2>" + modelNode.description + "</h2>";
		panel += "</div>";

		panel += "<div class='wz-divider'>";

		// If model node has attributes, add form.
		if (modelNode.attributes) {
			panel += addAttributesForm(configNode, modelNode);
		}

		/** If model node has children, add navigation */
		if (modelNode.elements) {
			panel += addChildElements(configNode, modelNode);
		}

		panel += "</div>";
		panel += "</div>";

		$('#tve-config-page').html(panel);
	}

	/** Add breadcrumbs to all access to parent nodes */
	function addBreadcrumbs() {
		var bc = "<ol class='breadcrumb wz-breadcrumb' style='margin-top: 8px;' role='group'>";
		for (var i = 0; i < editorContexts.length; i++) {
			var modelNode = editorContexts[i]["model"];
			var active = (i == (editorContexts.length - 1));
			if (active) {
				bc += "<li class='active'>" + modelNode.name + "</li>";
			} else {
				bc +=
						"<li><a href='javacsript:void(0);'>" + modelNode.name
								+ "</a><span class='divider'>/</span></li>";
			}
		}
		bc += "</ol>";
		return bc;
	}

	/** Add attributes form for panel */
	function addAttributesForm(configNode, modelNode) {
		var section = "";
		for (var i = 0; i < modelNode.attributes.length; i++) {
			var attr = modelNode.attributes[i];
			section += "<div><h3>" + attr.name + " : " + attr.type + "</h3></div>";
		}
		return section;
	}

	/** Add child element navigation for panel */
	function addChildElements(configNode, modelNode) {
		var section = "";
		for (var i = 0; i < configNode.children.length; i++) {
			var child = configNode.children[i];
			var childModel = findModelNodeByName(modelNode, child.name);
			if (childModel) {
				section += "<div class='wz-child'>";
				section += "<i class='wz-child-icon fa fa-" + childModel.icon + " fa-white'></i>";
				section += "<h1 class='wz-child-name'>" + childModel.name + "</h1>";
				section += "<a class='wz-child-nav btn' title='Open' ";
				section += "  style='color: #060;' href='javascript:void(0)' ";
				section +=
						"  onclick='onChildOpenClicked(event, \"" + modelNode.localName + "\", \""
								+ childModel.localName + "\")'>";
				section += "  <i class='fa fa-chevron-right fa-white'></i>";
				section += "</a>";
				section += "</div>";
			} else {
				section += "<div class='wz-child'>";
				section += "<h1>Unknown model element: " + child.name + "</h1>";
				section += "</div>";
			}
		}
		return section;
	}

	/** Open a child page in the wizard */
	function onChildOpenClicked(event, parentName, childName) {
		var top = editorContexts[editorContexts.length - 1];
		var topModel = top["model"];
		var topConfig = top["config"];
		var childModel = findModelNodeByName(topModel, childName);
		var childConfig = findConfigNodeByName(topConfig, childName);
		if (childModel && childConfig) {
			addPanelFor(childConfig, childModel);
		}
	}

	/** Find closest element with the given localName */
	function findConfigNodeByName(root, name) {
		if (root.name == name) {
			return root;
		} else {
			var found;
			if (root.children) {
				for (var i = 0; i < root.children.length; i++) {
					found = findConfigNodeByName(root.children[i], name);
					if (found) {
						return found;
					}
				}
			}
		}
		return null;
	}

	/** Find closest element with the given localName */
	function findModelNodeByName(root, name) {
		if (root.nodeType == 'Element') {
			if (root.localName == name) {
				return root;
			} else {
				var found;
				if (root.elements) {
					for (var i = 0; i < root.elements.length; i++) {
						found = findModelNodeByName(root.elements[i], name);
						if (found) {
							return found;
						}
					}
				}
			}
		}
		return null;
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

	/** Called on successful tenant load request */
	function loadGetSuccess(data, status, jqXHR) {
		tenant = data;
		var template = kendo.template($("#tpl-tenant-entry").html());
		data.inDetailView = true;
		$('#tenant-details').html(template(data));

		if (tenant.engineState) {
			if (data.engineState.lifecycleStatus == 'Started') {
				$('#tenant-power-off').show();
				$('#tenant-power-on').hide();
				$('#tenant-edit').hide();
				$('#tenant-delete').hide();
				template = kendo.template($("#tpl-engine-started").html());
				$('#detail-content').html(template(data));
				loadEngineHierarchy(data);
			} else if (data.engineState.lifecycleStatus == 'Stopped') {
				$('#tenant-power-off').hide();
				$('#tenant-power-on').show();
				$('#tenant-edit').show();
				$('#tenant-delete').show();
				template = kendo.template($("#tpl-engine-stopped").html());
				$('#detail-content').html(template(data));
			} else {
				$('#tenant-power-off').hide();
				$('#tenant-power-on').hide();
				$('#tenant-edit').hide();
				$('#tenant-delete').hide();
				template = kendo.template($("#tpl-engine-not-running").html());
				$('#detail-content').html(template(data));
			}
			loadEngineConfiguration();
		} else {
			$('#tenant-power-off').hide();
			$('#tenant-power-on').show();
			$('#tenant-edit').show();
			$('#tenant-delete').show();
			template = kendo.template($("#tpl-engine-not-running").html());
			$('#detail-content').html(template(data));
		}
	}

	/** Handle error on getting tenant data */
	function loadGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load tenant data.");
	}

	/** Load engine hierarchy into tree */
	function loadEngineHierarchy(engine) {
		var dataSource = new kendo.data.TreeListDataSource({
			data : engine.engineState.componentHierarchyState,
			schema : {
				model : {
					id : "id",
					expanded : true
				}
			}
		});

		$("#tenant-engine-hierarchy").kendoTreeList({
			dataSource : dataSource,
			height : 500,
			columns : [ {
				field : "name",
				title : "Component Name",
				width : 400
			}, {
				field : "type",
				title : "Type",
				width : 150
			}, {
				field : "status",
				title : "Status",
				width : 150
			} ]
		});
	}

	/** Load the running engine configuration as JSON */
	function loadEngineConfiguration() {
		$.getJSON("${pageContext.request.contextPath}/api/tenants/" + tenantId
				+ "/engine/configuration/json?tenantAuthToken=${tenant.authenticationToken}",
			jsonConfigGetSuccess, jsonConfigGetFailed);
	}

	/** Called on successful configuration load request */
	function jsonConfigGetSuccess(data, status, jqXHR) {
		config = data;
		resetWizard();
	}

	/** Handle error on getting configuration data */
	function jsonConfigGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load tenant configuration as JSON.");
	}
</script>

<%@ include file="../includes/bottom.inc"%>