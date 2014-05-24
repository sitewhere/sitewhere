<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="View Specification" />
<c:set var="sitewhere_section" value="specifications" />
<c:set var="use_map_includes" value="true" />
<%@ include file="../includes/top.inc"%>

<style>
.sw-spec-namespace {
	border: 1px solid #ccc;
	margin-bottom: 15px;
	font-family: courier;
}
.sw-spec-namespace-name {
	color: #333;
}
.sw-spec-ns-header {
	background-color: #eee;
	padding: 5px;
	font-size: 10pt;
}
.sw-spec-command {
	font-family: courier;
	padding: 10px 15px;
	position: relative;
	border-bottom: 1px solid #eee;
}
.sw-spec-command-desc {
	color: #060;
	font-style: italic;
	max-width: 90%;
}
.sw-spec-command-name {
	color: #060;
	font-weight: bold;
}
.sw-list-entry:hover .sw-spec-command-name {
	cursor: pointer;
	border-bottom: 1px solid #060;
}
.sw-spec-command-param-name {
	color: #333;
}
.sw-spec-command-param-type {
	color: #666;
	padding-left: 3px;
}
.sw-device-slot-container {
	border: 1px solid #ccc;
	margin-bottom: 15px;
}
.sw-device-slot-header {
	padding: 5px 10px;
	font-size: 12pt;
	border-bottom: 1px solid #ccc;
	background-color: #eee;
	color: #333;
	margin-bottom: 10px;
}
.sw-device-slot {
	padding: 5px;
	border: 1px solid #ccc;
	background-color: #ffe;
	margin: 10px;
	position: relative;
}
.sw-device-slot-path {
	font-weight: bold;
	color: #050;
}
.sw-device-slot-buttons {
	position: absolute;
	right: 5px;
	top: 5px;
}
.sw-device-unit-container {
	border: 1px solid #999;
	margin-bottom: 15px;
}
.sw-device-unit-header {
	padding: 5px 10px;
	font-size: 12pt;
	border-bottom: 1px solid #ccc;
	background-color: #ccc;
	color: #333;
	margin-bottom: 10px;
	position: relative;
}
.sw-device-unit {
	padding: 5px;
	border: 1px solid #ccc;
	background-color: #ffe;
	margin-bottom: 10px;
}
.sw-device-unit-path {
	font-weight: bold;
	font-size: 10pt;
	color: #050;
}
.sw-device-unit-buttons {
	position: absolute;
	right: 5px;
	top: 5px;
}
.sw-device-unit-container .sw-device-slot-container {
	margin: 20px;
}
.sw-device-unit-container .sw-device-unit-container {
	margin: 20px;
}
.sw-nodata-container {
	padding: 10px;
	font-size: 12pt;
	text-align: center;
	margin-top: -5px;
}
</style>

<!-- Title Bar -->
<div class="sw-title-bar content k-header" style="margin-bottom: -1px;">
	<h1 class="ellipsis"><c:out value="${sitewhere_title}"/></h1>
	<div class="sw-title-bar-right">
		<a id="btn-edit-specification" class="btn" href="javascript:void(0)">
			<i class="icon-pencil sw-button-icon"></i> Edit Specification</a>
	</div>
</div>

<!-- Detail panel for selected specification -->
<div id="specification-details" style="line-height: normal;"></div>

<!-- Tab panel -->
<div id="tabs">
	<ul>
		<li class="k-state-active">Commands</li>
		<li>Code Generation</li>
<c:choose>
	<c:when test="${specification.containerPolicy == 'Composite'}">
		<li>Composition</li>
	</c:when>
</c:choose>
	</ul>
	<div>
		<div class="k-header sw-button-bar">
			<div class="sw-button-bar-title">Device Commands</div>
			<div>
				<a id="btn-refresh-commands" class="btn" href="javascript:void(0)">
					<i class="icon-refresh sw-button-icon"></i> Refresh</a>
				<a id="btn-add-command" class="btn" href="javascript:void(0)">
					<i class="icon-plus sw-button-icon"></i> Add New Command</a>
			</div>
		</div>
		<div id="commands" class="sw-assignment-list"></div>
	</div>
	<div>
		<div class="k-header sw-button-bar">
			<div class="sw-button-bar-title">Google Protocol Buffer Definition</div>
			<div>
				<a id="btn-refresh-protobuf" class="btn" href="javascript:void(0)">
					<i class="icon-refresh sw-button-icon"></i> Refresh</a>
				<a id="btn-download-protobuf" class="btn" href="javascript:void(0)">
					<i class="icon-download-alt sw-button-icon"></i> Download</a>
			</div>
		</div>
		<div id="sw-proto-section" class="protobuf"></div>
	</div>
<c:choose>
	<c:when test="${specification.containerPolicy == 'Composite'}">
	<div>
		<div class="k-header sw-button-bar">
			<div class="sw-button-bar-title">Device Element Schema</div>
			<div>
				<a id="btn-add-unit" class="btn" href="javascript:void(0)">
					<i class="icon-folder-close sw-button-icon"></i> Add Device Unit</a>
				<a id="btn-add-unit" class="btn" href="javascript:void(0)">
					<i class="icon-link sw-button-icon"></i> Add Device Slot</a>
			</div>
		</div>
		<div id="sw-composition-section"></div>
	</div>
	</c:when>
</c:choose>
</div>

<%@ include file="../includes/commandCreateDialog.inc"%>
<%@ include file="../includes/deviceSlotCreateDialog.inc"%>
<%@ include file="../includes/deviceUnitCreateDialog.inc"%>
<%@ include file="../includes/specificationCreateDialog.inc"%>
<%@ include file="../includes/templateSpecificationEntry.inc"%>
<%@ include file="../includes/templateCommandEntry.inc"%>
<%@ include file="../includes/templateCommandParamEntry.inc"%>
<%@ include file="../includes/assetTemplates.inc"%>
<%@ include file="../includes/commonFunctions.inc"%>

<script>
	// Token for specification being viewed.
	var specToken = '<c:out value="${specification.token}"/>';

	// Context used for creating new elements.
	var elementContext;
	
	$(document).ready(function() {
				
		/** Create the tab strip */
		tabs = $("#tabs").kendoTabStrip({
			animation: false
		}).data("kendoTabStrip");
		
	    $("#btn-add-command").click(function() {
	    	ccOpen(specToken, onCommandCreateSuccess);
	    });
		
	    $("#btn-edit-specification").click(function() {
	    	onSpecificationEditClicked();
	    });
		
	    $("#btn-refresh-protobuf").click(function() {
	    	loadProtobuf();
	    });
		
	    $("#btn-download-protobuf").click(function(e) {
	        e.preventDefault();
	        window.location.href = "${pageContext.request.contextPath}/api/specifications/" + specToken + "/spec.proto";
	    });
		
		loadSpecification();
		loadCommands();
	});
	
	/** Called when edit button on the list entry is pressed */
	function onSpecificationEditClicked() {
		spuOpen(specToken, onSpecificationEditComplete);
	}
	
	/** Called after successful specification update */
	function onSpecificationEditComplete() {
		loadSpecification();
	}
	
	/** Called when 'edit command' is clicked */
	function onEditCommand(e, token) {
		var event = e || window.event;
		event.stopPropagation();
		cuOpen(token, onEditCommandComplete);
	}
	
	/** Called after command has been edited */
	function onEditCommandComplete() {
		loadCommands();
	}
	
	/** Called after command has been created */
	function onCommandCreateSuccess() {
		loadCommands();
	}
	
	/** Loads information for the selected specification */
	function loadSpecification() {
		$.getJSON("${pageContext.request.contextPath}/api/specifications/" + specToken, 
			loadGetSuccess, loadGetFailed);
	}
    
    /** Called on successful specification load request */
    function loadGetSuccess(data, status, jqXHR) {
		var template = kendo.template($("#tpl-specification-entry").html());
		parseSpecificationData(data);
		data.inDetailView = true;
		$('#specification-details').html(template(data));
		loadProtobuf();
		refreshDeviceElementSchema(data, status, jqXHR);
    }
    
	/** Handle error on getting specification data */
	function loadGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load specification data.");
	}
	
	/** Called when 'delete command' is clicked */
	function onDeleteCommand(e, token) {
		var event = e || window.event;
		event.stopPropagation();
		swConfirm("Delete Command", "Are you sure that you want to delete device command with " +
			"token '" + token + "'?", function(result) {
			if (result) {
				$.deleteJSON("${pageContext.request.contextPath}/api/commands/" + token, 
						commandDeleteSuccess, commandDeleteFailed);
			}
		});
	}
    
    /** Called on successful command delete request */
    function commandDeleteSuccess(data, status, jqXHR) {
		loadCommands();
    }
    
	/** Handle error on deleting command */
	function commandDeleteFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to delete command.");
	}
	
	/** Loads commands for the selected specification */
	function loadCommands() {
		$.getJSON("${pageContext.request.contextPath}/api/specifications/" + specToken + "/namespaces", 
			loadCommandsSuccess, loadCommandsFailed);
	}
    
    /** Called on successful specification commands load request */
    function loadCommandsSuccess(data, status, jqXHR) {
		var template = kendo.template($("#tpl-command-entry").html());
		var commandData;
		
    	var index;
    	var nsHtml, allHtml = "";
    	for (var i = 0, ns; ns = data.results[i]; i++) {
        	nsHtml = "<div class='sw-spec-namespace'><div class='sw-spec-ns-header'><strong>Namespace:</strong> "
        	nsHtml += "<span class='sw-spec-namespace-name'>" + ((ns.value) ? ns.value : "(Default)") + "</span>";
        	nsHtml += "</div>";
        	for (var j=0, command; command=ns.commands[j]; j++) {
        		commandData = {"commandHtml": htmlify(command), "command": command };
           		nsHtml += template(commandData);
       		}
         	nsHtml += "</div>";
        	allHtml += nsHtml;
   		}
		$('#commands').html(allHtml);
    }
    
    /** Creates an HTML highlighted version of a command */
    function htmlify(command) {
    	var chtml = "";
    	if (command.description) {
	    	chtml += "<div class='sw-spec-command-desc'>";
	    	chtml += "/** " + command.description + " **/</div>"
		}
	    chtml += "<span class=\"sw-spec-command-name\" onclick=\"onEditCommand(event, '" + command.token + "')\">" + 
	    	command.name + "</span>(";
	    for (var i = 0, param; param = command.parameters[i]; i++) {
    		if (param.required) {
    			chtml += "<strong>"
    		}
			if (i > 0) {
				chtml += ", ";
			}
			chtml += " <span class='sw-spec-command-param-name'>" + param.name + "</span>";
			chtml += ":<span class='sw-spec-command-param-type'>" + param.type + "</span> ";
    		if (param.required) {
    			chtml += "</strong>"
    		}
    	}
		chtml += ")"
   		return chtml;
    }
    
	/** Handle error on getting specification command data */
	function loadCommandsFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load specification command data.");
	}
	
	/** Load google protocol buffer definition */
	function loadProtobuf() {
		$.get("${pageContext.request.contextPath}/api/specifications/" + specToken + "/proto", function(data) {
			$("#sw-proto-section").html("<pre><code>" + data + "</code></pre>");
			hljs.highlightBlock(document.getElementById('sw-proto-section').childNodes[0]);
		});	
	}
	
	/** Open the 'create device slot' dialog */
	function createSlot(context) {
		elementContext = context;
		loadSchemaForUpdate(showCreateSlotDialog);
	}
	
	/** Open the 'create device unit' dialog */
	function createUnit(context) {
		elementContext = context;
		loadSchemaForUpdate(showCreateUnitDialog);
	}
	
	/** Delete the given slot */
	function deleteSlot(context) {
		elementContext = context;
		loadSchemaForUpdate(handleDeleteSlot);
	}
	
	/** Delete the given unit */
	function deleteUnit(context) {
		elementContext = context;
		loadSchemaForUpdate(handleDeleteUnit);
	}
	
	/** Reloads specification and routes to a callback that will execute updates. */
	function loadSchemaForUpdate(callback) {
		$.getJSON("${pageContext.request.contextPath}/api/specifications/" + specToken, 
				callback, loadGetFailed);
	}
    
    /** Called on successful specification load request */
    function showCreateSlotDialog(data, status, jqXHR) {
		var schema = data.deviceElementSchema;
		if (!schema) {
			return;
		}
		dscOpen(specToken, elementContext, schema, onDeviceSlotCreated);
    }
    
    /** Called on successful specification load request */
    function showCreateUnitDialog(data, status, jqXHR) {
		var schema = data.deviceElementSchema;
		if (!schema) {
			return;
		}
		ducOpen(specToken, elementContext, schema, onDeviceUnitCreated);
    }
	
    /** Delete the given unit */
	function handleDeleteSlot(data, status, jqXHR) {
		var schema = data.deviceElementSchema;
		if (!schema) {
			return;
		}
		swConfirm("Delete Device Slot", "Are you sure that you want to delete the selected device slot?", function(result) {
			if (result) {
				var updated = swRemoveDeviceSlotForContext(elementContext, schema);
				if (updated) {
					var specData = {
						"deviceElementSchema": updated, 
					}
					$.putJSON("${pageContext.request.contextPath}/api/specifications/" + specToken, 
						specData, refreshDeviceElementSchema, onDeleteSlotFail);
				}
			}
		});
	}
	
    /** Delete the given unit */
	function handleDeleteUnit(data, status, jqXHR) {
		var schema = data.deviceElementSchema;
		if (!schema) {
			return;
		}
		swConfirm("Delete Device Unit", "Are you sure that you want to delete the selected device unit?", function(result) {
			if (result) {
				var updated = swRemoveDeviceUnitForContext(elementContext, schema);
				if (updated) {
					var specData = {
						"deviceElementSchema": updated, 
					}
					$.putJSON("${pageContext.request.contextPath}/api/specifications/" + specToken, 
						specData, refreshDeviceElementSchema, onDeleteUnitFail);
				}
			}
		});
	}
    
	/** Handle failed call to delete device slot */
	function onDeleteSlotFail(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to delete device slot.");
	}
   
	/** Handle failed call to delete device unit */
	function onDeleteUnitFail(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to delete device unit.");
	}
    
    /** Called after a device slot is successfully created */
    function onDeviceSlotCreated() {
    	loadSchemaForUpdate(refreshDeviceElementSchema);
    }
    
    /** Called after a device unit is successfully created */
    function onDeviceUnitCreated() {
    	loadSchemaForUpdate(refreshDeviceElementSchema);
    }
	
	/** Load HTML for device element schema */
	function refreshDeviceElementSchema(data, status, jqXHR) {
		var schema = data.deviceElementSchema;
		if (!schema) {
			return;
		}
    	var shtml = getUnitHtml(schema, "");
    	$('#sw-composition-section').html(shtml);
	}
	
	/** Create HTML for a device unit */
	function getUnitHtml(unit, context) {
    	var uhtml = "";
		var slength = unit.deviceSlots.length;
   		uhtml += "<div class='sw-device-slot-container'>";
   		uhtml += "<div class='sw-device-slot-header'><i class='icon-link sw-button-icon'></i> Device Slots</div>";
   		if (slength == 0) {
       		uhtml += "<div class='sw-nodata-container'<span class='sw-nodata-message'>No Slots Currently Configured</span></div>";
   		} else {
    		for (var i = 0; i < slength; i++) {
    			uhtml += getSlotHtml(unit.deviceSlots[i], context);
    		}
   		}
   		uhtml += "</div>";
		var ulength = unit.deviceUnits.length;
   		for (var i = 0; i < ulength; i++) {
       		var relContext = context + "/" + unit.deviceUnits[i].path;
       		uhtml += "<div class='sw-device-unit-container sw-list-entry'>";
       		uhtml += getUnitHeaderHtml(unit.deviceUnits[i], relContext);
   			uhtml += getUnitHtml(unit.deviceUnits[i], relContext);
       		uhtml += "</div>";
   		}
    	return uhtml;
	}
	
	/** Create HTML for device unit header bar */
	function getUnitHeaderHtml(unit, relContext) {
		var uhtml = "<div class='sw-device-unit-header'><i class='icon-folder-close sw-button-icon'></i>" + 
			unit.name + " (<span class='sw-device-unit-path'>" + relContext + "</span>)";
		uhtml += "<div class='sw-device-unit-buttons'>" +
			"<i class='icon-folder-close sw-button-icon sw-action-glyph sw-normal-glyph' " + 
			"style='padding-right: 5px;' title='Add Nested Device Unit' onclick=\"createUnit('" + 
			relContext + "');\"></i>" + "<i class='icon-link sw-button-icon sw-action-glyph sw-normal-glyph' " + 
			"onclick=\"createSlot('" + relContext + "');\" title='Add Device Slot'></i>" + 
			"<i class='icon-remove sw-button-icon sw-action-glyph sw-delete-glyph' " + 
			"onclick=\"deleteUnit('" + relContext + "');\" title='Delete Device Unit'></i>" +
			"</div></div>";
		return uhtml;
	}
	
	/** Create HTML for a device slot */
	function getSlotHtml(slot, context) {
		var relContext = context + "/" + slot.path;
    	var shtml = "<div class='sw-device-slot'><i class='icon-link sw-button-icon' style='padding-right: 5px'></i>" + 
    		slot.name + " (<span class='sw-device-slot-path'>" + relContext + "</span>)";
    	shtml += "<div class='sw-device-slot-buttons'>" +
			"<i class='icon-remove sw-button-icon sw-action-glyph sw-delete-glyph' " + 
			"onclick=\"deleteSlot('" + relContext + "');\" title='Delete Device Slot'></i></div></div>";
		return shtml;
	}
</script>

<%@ include file="../includes/bottom.inc"%>