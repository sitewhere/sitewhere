<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="View Specification" />
<c:set var="sitewhere_section" value="specifications" />
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
	color: #33c;
}
.sw-spec-command-delete {
	color: #33c;
}
</style>

<!-- Title Bar -->
<div class="sw-title-bar content k-header" style="margin-bottom: -1px;">
	<h1 class="ellipsis"><c:out value="${sitewhere_title}"/></h1>
	<div class="sw-title-bar-right">
		<a id="btn-edit-device" class="btn" href="javascript:void(0)">
			<i class="icon-pencil sw-button-icon"></i> Edit Specification</a>
	</div>
</div>

<!-- Detail panel for selected specification -->
<div id="specification-details" style="line-height: normal;"></div>

<!-- Tab panel -->
<div id="tabs">
	<ul>
		<li class="k-state-active">Commands</li>
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
</div>

<%@ include file="../includes/commandCreateDialog.inc"%>

<%@ include file="../includes/templateSpecificationEntry.inc"%>

<%@ include file="../includes/templateCommandEntry.inc"%>

<%@ include file="../includes/templateCommandParamEntry.inc"%>

<%@ include file="../includes/commonFunctions.inc"%>

<script>
	var specToken = '<c:out value="${specification.token}"/>';
	
	$(document).ready(function() {
				
		/** Create the tab strip */
		tabs = $("#tabs").kendoTabStrip({
			animation: false
		}).data("kendoTabStrip");
		
	    $("#btn-add-command").click(function() {
	    	ccOpen(specToken, onCommandCreateSuccess);
	    });
		
		loadSpecification();
		loadCommands();
	});
	
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
	    for (var i = 0, param; param = command.parameters[i]; i++) {1
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
</script>

<%@ include file="../includes/bottom.inc"%>