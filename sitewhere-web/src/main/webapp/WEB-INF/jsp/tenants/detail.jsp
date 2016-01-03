<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="View Tenant" />
<c:set var="sitewhere_section" value="tenants" />
<c:set var="use_highlight" value="true" />
<c:set var="use_bxslider" value="true" />
<%@ include file="../includes/top.inc"%>

<style>
div.wz-header {
	border: 1px solid #666;
	background-color: #eee;
	padding: 13px;
	margin-bottom: 10px;
	-webkit-box-shadow: 4px 4px 4px 0px rgba(192, 192, 192, 0.5);
	-moz-box-shadow: 4px 4px 4px 0px rgba(192, 192, 192, 0.5);
	box-shadow: 4px 4px 4px 0px rgba(192, 192, 192, 0.5);
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

.wz-drag-icon {
	padding: 10px 10px 10px 5px;
	font-size: 20px;
	color: #ccc;
	cursor: move;
	float: left;
	border-right: 1px solid #ccc;
}

.wz-role {
	border: 1px solid #999;
	padding: 15px 10px 10px;
	position: relative;
	margin: 10px 0px 25px;
	box-shadow: 4px 4px 4px 0px rgba(192, 192, 192, 0.3);
}

.wz-role-label {
	position: absolute;
	top: -10px;
	left: 5px;
	font-size: 12px;
	background-color: #999;
	color: #fff;
	padding: 1px 5px;
}

.wz-role-required {
	border-width: 2px;
}

.wz-role-label-required {
	background-color: #666;
}

.wz-role-missing {
	border: 1px solid #cc3;
	background-color: #ffe;
}

.wz-role-missing-optional {
	border: 1px solid #eee;
	box-shadow: none;
}

.wz-role-label-missing {
	
}

.wz-child {
	border: 1px solid #ccc;
	background-color: #eee;
	padding: 5px;
	margin-bottom: 5px;
	list-style-type: none;
	list-style-position: inside;
}

.wz-child-required {
	border-width: 2px;
}

.wz-child-missing {
	border-style: dashed;
	border-color: #999;
}

.wz-child .wz-child-icon {
	float: left;
	padding: 8px 10px;
	font-size: 22px;
}

.wz-child .wz-child-name {
	display: inline;
	font-size: 20px;
	padding: 0;
	margin: 0;
}

.wz-child .wz-child-nav {
	float: right;
	padding: 9px;
}

.wz-sortable-placeholder {
	min-height: 40px;
	border: 2px dashed #aaa;
	background-color: #ccc;
	padding: 5px;
	margin-bottom: 5px;
	list-style-type: none;
	list-style-position: inside;
	border: 2px dashed #aaa;
}

.wz-sortable-item {
	
}

div.wz-divider {
	clear: both;
	padding-top: 10px;
	margin-top: 10px;
	border-top: 1px solid #ddd;
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

.sw-attribute-group {
	border: 1px solid #ccc;
	padding: 25px 10px 10px;
	margin-bottom: 20px;
	position: relative;
	margin-top: 5px;
}

.sw-attribute-group h1 {
	margin: 0;
	padding: 2px 5px;
	font-size: 12px;
	line-height: 1em;
	position: absolute;
	background-color: #666;
	color: #fff;
	top: -9px;
}

label.sw-control-label {
	font-weight: bold;
	font-size: 17px;
	width: 250px;
}

label.sw-control-label i {
	color: #ccc;
	padding-left: 5px;
	margin-top: 2px;
	vertical-align: top;
	font-size: 10px;
}

div.sw-controls {
	margin-left: 290px;
	font-size: 17px;
	line-height: 1.7em;
}

div.wz-button-bar {
	padding: 10px 0px;
	margin-top: 10px;
	border-top: 1px solid #ddd;
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
				<a id="btn-stage-updates" href="javascript:void(0)" class="btn btn-primary"
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
<%@ include file="configEditDialog.inc"%>
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

	/** Configuration element roles */
	var roles = <c:out value="${roles}" escapeXml="false"/>;

	/** Sites for tenant */
	var sites = <c:out value="${sites}" escapeXml="false"/>;

	/** Specifications for tenant */
	var specifications = <c:out value="${specifications}" escapeXml="false"/>;

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
		pushContext(configNode, modelNode);
	}

	/** Push context on the stack */
	function pushContext(configNode, modelNode) {
		var context = {
			"config" : configNode,
			"model" : modelNode,
		};
		editorContexts.push(context);
		showPanelFor(context);
		return context;
	}

	/** Pop elements off the stack until the given element name is found */
	function popToContext(elementName) {
		var context = editorContexts[editorContexts.length - 1];
		var config = context["config"];
		if ((config.name == elementName) || (editorContexts.length == 1)) {
			showPanelFor(context);
		} else {
			// Pop the top item and recurse.
			editorContexts.pop();
			popToContext(elementName);
		}
	}

	/** Pop up one level */
	function popOne() {
		if (editorContexts.length > 1) {
			var context = editorContexts[editorContexts.length - 2];
			var config = context["config"];
			popToContext(config.name);
		}
	}

	/** Show panel for a given context */
	function showPanelFor(context) {
		var configNode = context["config"];
		var modelNode = context["model"];

		var panel = "<div>";

		// Add breadcrumbs for quickly navigating to parent nodes.
		panel += addBreadcrumbs();

		panel += "<div class='wz-header'>";
		panel += "<i class='wz-header-icon fa fa-" + modelNode.icon + " fa-white'></i>";
		panel += "<h1>" + modelNode.name + "</h1>";

		// Create buttons for various node actions.
		panel += createActionButtons(modelNode, configNode);

		panel += "<h2>" + modelNode.description + "</h2>";
		panel += "</div>";
		panel += "<div class='wz-divider'>";

		// If model node has attributes, add form.
		if (modelNode.attributes) {
			panel += addAttributesForm(configNode, modelNode);
		}

		/** If children are configured, add navigation */
		panel += addChildElements(configNode, modelNode);

		panel += "</div>";
		panel += "</div>";

		$('#tve-config-page').html(panel);
	}

	/** Refresh contents of current panel */
	function refresh() {
		showPanelFor(editorContexts[editorContexts.length - 1]);
	}

	/** Add buttons that allow actions to be taken on the current node */
	function createActionButtons(modelNode, configNode) {
		var panel = "";

		// Do not allow actions at the root level.
		if (editorContexts.length > 1) {
			panel += "<div style='float: right;' class='btn-group'>";
			panel += "<a onclick='popOne()' title='Up One Level' class='btn' href='javascript:void(0)'>";
			panel += "<i class='fa fa-arrow-up sw-button-icon'></i>";
			panel += "</a>";

			// Only allow configuration if there are configurable attributes.
			if (modelNode.attributes) {
				panel +=
						"<a onclick='configureCurrent()' title='Configure " + modelNode.name
								+ "' class='btn' href='javascript:void(0)'>";
				panel += "<i class='fa fa-gear sw-button-icon'></i>";
				panel += "</a>";
			}

			// Only allow optional elements to be deleted.
			if (modelNode.role.optional) {
				panel +=
						"<a onclick='deleteCurrent()' style='color: #900;' title='Delete " + modelNode.name
								+ "' class='btn' href='javascript:void(0)'>";
				panel += "<i class='fa fa-times sw-button-icon'></i>";
				panel += "</a>";
			}
			panel += "</div>"
		}
		return panel;
	}

	/** Configure the current element */
	function configureCurrent() {
		var top = editorContexts[editorContexts.length - 1];
		var topModel = top["model"];
		var topConfig = top["config"];
		ceComponentEdit(topModel, topConfig, onCurrentEdited)
	}

	/** Called after editing is complete */
	function onCurrentEdited(updated) {
		refresh();
	}

	/** Delete the current element */
	function deleteCurrent() {
	}

	/** Add breadcrumbs to all access to parent nodes */
	function addBreadcrumbs() {
		var bc = "<ol class='breadcrumb wz-breadcrumb' style='margin-top: 8px;' role='group'>";
		for (var i = 0; i < editorContexts.length; i++) {
			var modelNode = editorContexts[i]["model"];
			var configNode = editorContexts[i]["config"];
			var active = (i == (editorContexts.length - 1));
			if (active) {
				bc += "<li class='active'>" + modelNode.name + "</li>";
			} else {
				bc +=
						"<li><a href='javacsript:void(0);' onclick='popToContext(\"" + configNode.name
								+ "\")'>" + modelNode.name + "</a><span class='divider'>/</span></li>";
			}
		}
		bc += "</ol>";
		return bc;
	}

	/** Add attributes form for panel */
	function addAttributesForm(configNode, modelNode) {
		var section = "<form class='form-horizontal'>";
		var valuesByName = [];
		if (configNode.attributes) {
			for (var i = 0; i < configNode.attributes.length; i++) {
				valuesByName[configNode.attributes[i].name] = configNode.attributes[i].value;
			}
		}
		var lastGroup;
		for (var i = 0; i < modelNode.attributes.length; i++) {
			var attr = modelNode.attributes[i];
			if (attr.group != lastGroup) {
				var groupName = modelNode.attributeGroups[attr.group];
				if (lastGroup) {
					section += "</div>";
				}
				section += "<div class='sw-attribute-group'><h1>" + groupName + "</h1>";
				lastGroup = attr.group;
			}
			section += "<div class='control-group'>";
			section +=
					"  <label class='control-label sw-control-label' style='width: 275px;' for='tc-" + attr.localName + "'>"
							+ attr.name
							+ "<i class='fa fa-info-circle fa-white' title='" + attr.description + "'></i></label>";
			section += "  <div class='controls sw-controls' style='margin-left: 300px;'>";
			if (valuesByName[attr.localName]) {
				if (attr.type == 'SiteReference') {
					var siteName = getSiteNamesByToken()[valuesByName[attr.localName]];
					section += "    " + (siteName ? siteName : valuesByName[attr.localName]);
				} else if (attr.type == 'SpecificationReference') {
					var specName = getSpecificationNamesByToken()[valuesByName[attr.localName]];
					section += "    " + (specName ? specName : valuesByName[attr.localName]);
				} else {
					section += "    " + valuesByName[attr.localName];
				}
			} else if (attr.defaultValue) {
				section += "    (defaulted to '" + attr.defaultValue + "')";
			}
			section += "  </div>";
			section += "</div>";
		}
		if (lastGroup) {
			section += "</div>";
		}
		section += "</form>";
		return section;
	}

	/** Create lookup of site names by token */
	function getSiteNamesByToken() {
		var mapped = {};
		for (var i = 0; i < sites.length; i++) {
			mapped[sites[i].token] = sites[i].name;
		}
		return mapped;
	}

	/** Create lookup of site names by token */
	function getSpecificationNamesByToken() {
		var mapped = {};
		for (var i = 0; i < specifications.length; i++) {
			mapped[specifications[i].token] = specifications[i].name;
		}
		return mapped;
	}

	/** Add child element navigation for panel */
	function addChildElements(configNode, modelNode) {
		var section = "";
		var childrenByRole = getConfigChildrenByRole(modelNode, configNode);
		var role = roles[modelNode.role];
		if (!role) {
			return section;
		}
		var childRoles = getSpecializedRoleChildren(role, modelNode);

		// Loop through role children in order.
		for (var i = 0; i < childRoles.length; i++) {
			var childRoleName = childRoles[i];
			var childRole = roles[childRoleName];
			var childrenWithRole = childrenByRole[childRoleName];

			// Add children.
			if (childRole.multiple) {
				section += addSortableRoleChildren(childRoleName, childRole, childrenWithRole);
			} else {
				section += addNonSortableRoleChildren(childRoleName, childRole, childrenWithRole);
			}
		}

		// Show children that do not have a model.
		var noModel = childrenByRole["?"];
		for (var i = 0; i < noModel.length; i++) {
			section += "<div class='wz-child'>";
			section += "<h1>Unknown model element: " + noModel[i] + "</h1>";
			section += "</div>";
		}

		// Add script that allows drag-and-drop sorting.
		section += "<scr" + "ipt>";
		section += "$('.wz-sortable').sortable({";
		section += "  placeholder: 'wz-sortable-placeholder', ";
		section += "  stop: function(event,ui) { childOrderChanged('" + childRoleName + "'); }, ";
		section += "  items : 'li.wz-sortable-item' ";
		section += "});";
		section += "</scr" + "ipt>";

		return section;
	}

	/** Called when drag-and-drop sorting is done */
	function childOrderChanged(roleName) {
		var ids = [];
		$('.wz-child').each(function() {
			ids.push($(this).attr("id"));
		});
		var context = editorContexts[editorContexts.length - 1];
		var config = context["config"];
		var reordered = [];
		for (var i = 0; i < ids.length; i++) {
			var child = findConfigNodeById(config, ids[i]);
			if (child) {
				reordered.push(child);
			}
		}
		config.children = reordered;
	}

	/** Add html for child icon and name */
	function addChildFields(childModel, childConfig, childRole) {
		var section = "<i class='wz-child-icon fa fa-" + childModel.icon + " fa-white'></i>";
		section += "<h1 class='wz-child-name'>" + childModel.name;

		// Show index value if specified.
		if (childModel.indexAttribute) {
			var modelAttr = null;
			for (var i = 0; i < childModel.attributes.length; i++) {
				if (childModel.indexAttribute == childModel.attributes[i].localName) {
					modelAttr = childModel.attributes[i];
					break;
				}
			}
			for (var j = 0; j < childConfig.attributes.length; j++) {
				var attrName = childConfig.attributes[j].name;
				if (childModel.indexAttribute == attrName) {
					if (modelAttr && (modelAttr.type == 'SiteReference')) {
						var siteName = getSiteNamesByToken()[childConfig.attributes[j].value];
						section += " (" + (siteName ? siteName : childConfig.attributes[j].value) + ")";
					} else if (modelAttr && (modelAttr.type == 'SpecificationReference')) {
						var specName = getSpecificationNamesByToken()[childConfig.attributes[j].value];
						section += " (" + (specName ? specName : childConfig.attributes[j].value) + ")";
					} else {
						section += " (" + childConfig.attributes[j].value + ")";
					}
				}
			}
		}

		section += "</h1>";

		section += "<a class='wz-child-nav btn' title='Open' ";
		section += "  style='color: #060;' href='javascript:void(0)' ";
		section +=
				"  onclick='onChildOpenClicked(\"" + childConfig.name + "\", \"" + childConfig.id + "\")'>";
		section += "  <i class='fa fa-chevron-right fa-white'></i>";
		section += "</a>";

		if (!childRole.permanent) {
			section += "<a class='wz-child-nav btn' title='Delete' ";
			section += "  style='color: #900;' href='javascript:void(0)' ";
			section +=
					"  onclick='onChildDeleteClicked(\"" + childConfig.name + "\", \"" + childConfig.id
							+ "\")'>";
			section += "  <i class='fa fa-times fa-white'></i>";
			section += "</a>";
		}
		return section;
	}

	/** Add children that are in a fixed format */
	function addNonSortableRoleChildren(childRoleName, childRole, childrenWithRole) {
		var section = "";
		var roleClasses = "wz-role";
		var roleLabelClasses = "wz-role-label";
		var childClasses = "wz-child";
		var missingRequired = false;
		var missingOptional = false;
		if (!childRole.optional) {
			if (childrenWithRole.length == 0) {
				missingRequired = true;
				roleClasses += " wz-role-missing";
				roleLabelClasses += " wz-role-label-required";
			} else {
				roleClasses += " wz-role-required";
				roleLabelClasses += " wz-role-label-required";
				childClasses += " wz-child-required";
			}
		} else {
			if (childrenWithRole.length == 0) {
				missingOptional = true;
				roleClasses += " wz-role-missing-optional";
			}
		}
		if (!childRole.permanent) {
			section +=
					"<div class='" + roleClasses + "'><div class='" + roleLabelClasses + "'>"
							+ childRole.name + "</div>";
		}

		if (missingRequired) {
			section += addMissingRequired(childRoleName, childRole);
		} else if (missingOptional) {
			section += addMissingOptional(childRoleName, childRole);
		} else {
			for (var j = 0; j < childrenWithRole.length; j++) {
				var childContext = childrenWithRole[j];
				var childModel = childContext["model"];
				var childConfig = childContext["config"];

				section += "<div class='" + childClasses + "' id='" + childConfig.id + "'>";

				// Adds icon, name, and navigation.
				section += addChildFields(childModel, childConfig, childRole);

				section += "</div>";
			}
		}
		if (!childRole.permanent) {
			section += "</div>";
		}
		return section;
	}

	/** Add placeholder for missing required field */
	function addMissingRequired(roleName, role) {
		var modelsForRole = findModelChildrenInRole(roleName);

		var section = "";
		section += "<div class='wz-child wz-child-missing'>";
		section += "<i class='wz-child-icon fa fa-warning fa-white'></i>";
		section += "<h1 class='wz-child-name'>" + role.name + " is Required</h1>";

		section +=
				"<div class='wz-child-nav btn-group dropup' style='padding: 0; margin-top: 5px; margin-right: 5px;'>";
		section += "<a class='btn dropdown-toggle' title='Add Component' data-toggle='dropdown'>";
		section += "Add Component<span class='caret'style='margin-left: 5px'></span></a>";
		section += "<ul class='dropdown-menu pull-right'>";

		// Add item in dropdown for each component in the given role.
		for (var i = 0; i < modelsForRole.length; i++) {
			var roleModel = modelsForRole[i];
			section +=
					"<li><a href='#' onclick='onAddChild(event, \"" + roleModel.localName + "\")'>"
							+ roleModel.name + "</a></li>";
		}

		section += "</ul>";
		section += "</div>";

		section += "</div>";
		return section;
	}

	/** Add placeholder for missing optional field */
	function addMissingOptional(roleName, role) {
		var modelsForRole = findModelChildrenInRole(roleName);

		var section = "";
		section += "<div class='wz-child wz-child-missing'>";
		section += "<i class='wz-child-icon fa fa-plus fa-white'></i>";
		section += "<h1 class='wz-child-name'>Add " + role.name + "</h1>";

		section +=
				"<div class='wz-child-nav btn-group dropup' style='padding: 0; margin-top: 5px; margin-right: 5px;'>";
		section += "<a class='btn dropdown-toggle' title='Add Component' data-toggle='dropdown'>";
		section += "Add Component<span class='caret'style='margin-left: 5px'></span></a>";
		section += "<ul class='dropdown-menu pull-right'>";

		// Add item in dropdown for each component in the given role.
		for (var i = 0; i < modelsForRole.length; i++) {
			var roleModel = modelsForRole[i];
			section +=
					"<li><a href='#' onclick='onAddChild(event, \"" + roleModel.localName + "\")'>"
							+ roleModel.name + "</a></li>";
		}

		section += "</ul>";
		section += "</div>";

		section += "</div>";
		return section;
	}

	/** Add child of **/
	function onAddChild(event, name) {
		event.preventDefault();

		var context = editorContexts[editorContexts.length - 1];
		var model = context["model"];

		// Create new config element based on selected model.
		var childModel = findModelNodeByName(model, name);
		var childConfig = {
			'name' : childModel.localName,
			'id' : generateUniqueId()
		};
		if (childModel.namespace) {
			childConfig.namespace = childModel.namespace;
		}

		// If the new model element has no attributes, there is nothing to configure.
		if (childModel.attributes) {
			ceComponentCreate(childModel, childConfig, onChildAdded);
		} else {
			onChildAdded(childConfig);
		}
	}

	/** Generate a unique id */
	function generateUniqueId() {
		return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g,
			function(c) {
				var r = crypto.getRandomValues(new Uint8Array(1))[0] % 16 | 0, v =
						c == 'x' ? r : (r & 0x3 | 0x8);
				return v.toString(16);
			});
	}

	/** Add newly created config element */
	function onChildAdded(newConfig) {
		var context = editorContexts[editorContexts.length - 1];
		var topModel = context["model"];
		var topConfig = context["config"];
		if (!topConfig.children) {
			topConfig.children = [];
		}
		topConfig.children.push(newConfig);
		fixChildOrder(topModel, topConfig);
		refresh();
	}

	/** Fix order of children to match model */
	function fixChildOrder(modelNode, configNode) {
		var childrenByRole = getConfigChildrenByRole(modelNode, configNode);
		var role = roles[modelNode.role];
		var childRoles = getSpecializedRoleChildren(role, modelNode);

		var updated = [];
		if (childRoles) {
			for (var i = 0; i < childRoles.length; i++) {
				var childrenInRole = childrenByRole[childRoles[i]];
				if (childrenInRole) {
					for (var j = 0; j < childrenInRole.length; j++) {
						var childConfig = childrenInRole[j].config;
						updated.push(childConfig);
					}
				}
			}
		}
		configNode.children = updated;
	}

	/** Add children that are in a sortable format */
	function addSortableRoleChildren(childRoleName, childRole, childrenWithRole) {
		var modelsForRole = findModelChildrenInRole(childRoleName);

		var section =
				"<ul class='wz-role wz-sortable'><div class='wz-role-label'>" + childRole.name + "</div>";
		for (var j = 0; j < childrenWithRole.length; j++) {
			var childContext = childrenWithRole[j];
			var childModel = childContext["model"];
			var childConfig = childContext["config"];

			section += "<li class='wz-child wz-sortable-item' draggable='true' id='" + childConfig.id + "'>";
			section += "<i class='wz-drag-icon fa fa-bars fa-white'></i>";

			// Adds icon, name, and navigation.
			section += addChildFields(childModel, childConfig, childRole);

			section += "</li>";
		}

		// Separator.
		if (childrenWithRole.length > 0) {
			section +=
					"<li style='padding-bottom: 10px; border-bottom: 1px dashed #aaa; margin-bottom: 10px; ";
			section += "margin-left: 20px; margin-right: 20px; list-style-type: none; list-style-position: inside;'></li>";
		}

		// Non-draggable item for creating new children.
		section += "<li class='wz-child'><i class='wz-child-icon fa fa-plus fa-white'></i>";
		section += "<h1 class='wz-child-name'>Add " + childRole.name + "</h1>";
		section +=
				"<div class='wz-child-nav btn-group dropup' style='padding: 0; margin-top: 5px; margin-right: 5px;'>";
		section += "<a class='btn dropdown-toggle' title='Add Component' data-toggle='dropdown'>";
		section += "Add Component<span class='caret'style='margin-left: 5px'></span></a>";
		section += "<ul class='dropdown-menu pull-right'>";

		// Add item in dropdown for each component in the given role.
		if (modelsForRole) {
			for (var i = 0; i < modelsForRole.length; i++) {
				var roleModel = modelsForRole[i];
				section +=
						"<li><a href='#' onclick='onAddChild(event, \"" + roleModel.localName + "\")'>"
								+ roleModel.name + "</a></li>";
			}
		}

		section += "</ul>";
		section += "</div>";
		section += "</li>";

		section += "</ul>";
		return section;
	}

	/** Delete the given element */
	function onChildDeleteClicked(childName, childId) {
		var top = editorContexts[editorContexts.length - 1];
		var topModel = top["model"];
		var topConfig = top["config"];
		var childModel = findModelNodeByName(topModel, childName);
		var childConfig = findConfigNodeById(topConfig, childId);
		var childRole = roles[childModel.role];

		var dialogTitle = "Delete Component?";
		var dialogMessage = "Are you sure that you want to delete '" + childModel.name + "'?";
		if (!childRole.optional) {
			dialogTitle = "Delete Required Component?";
			dialogMessage =
					"Are you sure that you want to delete '" + childModel.name + "'? "
							+ "This component is required by '" + topModel.name + "'.";
		}

		// Confirm delete, then remove the element and reload the panel.
		swConfirm(dialogTitle, dialogMessage, function(result) {
			if (result) {
				for (var i = 0; i < topConfig.children.length; i++) {
					if (topConfig.children[i].id == childConfig.id) {
						topConfig.children.splice(i, 1);
						break;
					}
				}
				refresh();
			}
		});
	}

	/** Open a child page in the wizard */
	function onChildOpenClicked(childName, childId) {
		var top = editorContexts[editorContexts.length - 1];
		var topModel = top["model"];
		var topConfig = top["config"];
		var childModel = findModelNodeByName(topModel, childName);
		var childConfig = findConfigNodeById(topConfig, childId);
		if (childModel && childConfig) {
			pushContext(childConfig, childModel);
		}
	}

	/** Find children of a model node with the given role */
	function findModelChildrenInRole(roleName) {
		var role = roles[roleName];
		var all = [];
		all.push.apply(all, configModel.elementsByRole[roleName]);

		// Also matches of subtypes.
		if (role.subtypes) {
			for (var i = 0; i < role.subtypes.length; i++) {
				var subtypeName = role.subtypes[i];
				all.push.apply(all, configModel.elementsByRole[subtypeName]);
			}
		}
		all.sort(function(a, b) {
			if (a.name < b.name) {
				return -1;
			} else if (a.name > b.name) {
				return 1;
			} else {
				return 0;
			}
		});
		return all;
	}

	/** Get configuration children grouped by role */
	function getConfigChildrenByRole(modelNode, configNode) {
		var role = roles[modelNode.role];
		var result = {};

		// Get child roles with constraints.
		var childRoles = getSpecializedRoleChildren(role, modelNode);
		var modelNotFound = [];
		for (var i = 0; i < childRoles.length; i++) {
			var childRoleName = childRoles[i];
			var childRole = roles[childRoleName];
			var roleSubtypes = [];
			roleSubtypes.push(childRoleName);
			if (childRole.subtypes) {
				roleSubtypes.push.apply(roleSubtypes, childRole.subtypes);
			}

			var matches = [];
			result[childRoleName] = matches;
			if (configNode.children) {
				for (var j = 0; j < configNode.children.length; j++) {
					var childConfig = configNode.children[j];
					if (modelNotFound.indexOf(childConfig.name) == -1) {
						var childModel = findModelNodeByName(modelNode, childConfig.name);
						if (!childModel) {
							modelNotFound.push(childConfig.name);
						} else if (roleSubtypes.indexOf(childModel.role) != -1) {
							var childContext = {};
							childContext["model"] = childModel;
							childContext["config"] = childConfig;
							matches.push(childContext);
						}
					}
				}
			}
		}
		result["?"] = modelNotFound;
		return result;
	}

	/** Get child roles, taking into account model specializations */
	function getSpecializedRoleChildren(role, modelNode) {
		var specialized = [];
		var childRoles = role.children;
		for (var i = 0; i < childRoles.length; i++) {
			var childRole = childRoles[i];
			if (modelNode.specializes) {
				var match = modelNode.specializes[childRole];
				if (match) {
					specialized.push(match);
				} else {
					specialized.push(childRole);
				}
			} else {
				specialized.push(childRole);
			}
		}
		return specialized;
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

	/** Find closest element with the given uuid */
	function findConfigNodeById(root, uuid) {
		if (root.id == uuid) {
			return root;
		} else {
			var found;
			if (root.children) {
				for (var i = 0; i < root.children.length; i++) {
					found = findConfigNodeById(root.children[i], uuid);
					if (found) {
						return found;
					}
				}
			}
		}
		return null;
	}

	/** Find a child model based on config element name */
	function findModelNodeByName(model, name) {
		if (model.localName == name) {
			return model;
		} else {
			var role = roles[model.role];
			var childRoles = getSpecializedRoleChildren(role, model);

			// Loop through all possible child roles for model.
			for (var i = 0; i < childRoles.length; i++) {
				var potential = findModelChildrenInRole(childRoles[i]);
				if (potential) {
					for (var j = 0; j < potential.length; j++) {
						if (name == potential[j].localName) {
							return potential[j];
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

		/** Handle 'stage updates' button */
		$('#btn-stage-updates').click(function(event) {
			stageUpdates();
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

	/** Stage updated configuration */
	function stageUpdates() {
		swConfirm("Stage Configuration", "Are you sure you want to stage the updated tenant configuration?",
			function(result) {
				if (result) {
					$.postJSON("${pageContext.request.contextPath}/api/tenants/" + tenant.id
							+ "/engine/configuration/json?tenantAuthToken=${tenant.authenticationToken}",
						config, stageSuccess, stageFail);
				}
			});
	}

	/** Called on successful create */
	function stageSuccess() {
	}

	/** Handle failed call to create tenant */
	function stageFail(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to stage tenant configuration.");
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