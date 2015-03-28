<!-- Dialog for invoking a command -->
<div id="ci-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 data-i18n="includes.commandInvokeDialog.InvokeDeviceCommand"></h3>
	</div>
	<div class="modal-body">
		<div id="ci-tabs">
			<ul>
				<li class="k-state-active" data-i18n="includes.commandInvokeDialog.InvocationDetails"></li>
				<li data-i18n="public.Metadata"></li>
			</ul>
			<div>
				<form id="ci-form" class="form-horizontal" style="padding-top: 15px;">
					<div class="control-group" style="border-bottom: 1px solid #eeeeee; padding-bottom: 10px;">
						<label class="control-label" for="ci-command-choice" data-i18n="public.Command"></label>
						<div class="controls">
							<input id="ci-command-choice"/>
						</div>
					</div>
					<div id="ci-parameters"></div>
				</form>
			</div>
			<div>
				<div id="ci-metadata">
					<jsp:include page="../includes/metadata.jsp" flush="true">
					    <jsp:param name="uid" value="ci"/>
					</jsp:include>
				</div>
            </div>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal" data-i18n="public.Cancel"></a> 
		<a id="ci-dialog-submit" href="javascript:void(0)" class="btn btn-primary" data-i18n="public.Invoke"></a>
	</div>
</div>

<!-- Script support for device create dialog -->
<script>
	/** Token for assignment */
	var ciAssignmentToken;
	
	/** Function called when dialog is submitted */
	var ciSubmitCallback;

	/** Provides external access to tabs */
	var ciTabs;
	
	/** Datasource for specification commands */
	var ciCommandsDS;
	
	/** Commands dropdown */
	var ciCommandList;
	
	/** Data for commands list */
	var commands;
	
	/** Currently selected command */
	var selectedCommand;

	$(document).ready(function() {
		
		/** Create tab strip */
		ciTabs = $("#ci-tabs").kendoTabStrip({
			animation: false
		}).data("kendoTabStrip");
		
		/** Create AJAX datasource for specifications list */
		ciCommandsDS = new kendo.data.DataSource();
		
    	// Create dropdown for datatypes.
    	ciCommandList = $("#ci-command-choice").kendoDropDownList({
    		dataTextField: "name",
    		dataValueField: "token",
    	    dataSource: ciCommandsDS,
    	    change: onCommandChange,
    	}).data("kendoDropDownList");
		
        /** Handle create dialog submit */
		$('#ci-dialog-submit').click(function(event) {
			event.preventDefault();
			if (!ciValidate()) {
				return;
			}
			var invocationData = {
				"initiator": "REST",
				"initiatorId": "${currentUser.username}",
				"target": "Assignment",
				"commandToken": selectedCommand.token,
				"status": "Pending",
				"metadata": swMetadataAsLookup(ciMetadataDS.data()),
			}
			var params = {};
			for (var i = 0; i < selectedCommand.parameters.length; i++) {
				var param = selectedCommand.parameters[i];
				var value = $("#ci-param-" + param.name).val();
				if (param.type == 'Bool') {
					value = ($("#ci-param-" + param.name).is(':checked') ? "true" : "false");
				}
				params[param.name] = value; 
			}
			invocationData.parameterValues = params;
			$.postJSON("${pageContext.request.contextPath}/api/assignments/" + ciAssignmentToken + "/invocations", 
					invocationData, onCreateSuccess, onCreateFail);
		});
        
        /** Called on successful create */
        function onCreateSuccess() {
        	$('#ci-dialog').modal('hide');
			if (ciSubmitCallback != null) {
				ciSubmitCallback();
			}
        }
        
		/** Handle failed call to invoke device command */
		function onCreateFail(jqXHR, textStatus, errorThrown) {
			handleError(jqXHR, "Unable to invoke device command.");
		}
    });
	
	/** Validate everything */
	function ciValidate() {
		$.validity.setup({ outputMode:"label" });
		$.validity.start();
      
		var result = $.validity.end();
		return result.valid;
	}
	
	/** Open the dialog */
	function ciOpen(assignmentToken, specificationId, callback) {
		// Store assignment token.
		ciAssignmentToken = assignmentToken
		
		// Function called on submit.
		ciSubmitCallback = callback;
		
		// Get latest device data for hardware id.
		$.getJSON("${pageContext.request.contextPath}/api/specifications/" + specificationId + "/commands", 
				ciCommandsGetSuccess, ciCommandsGetFailed);
	}
    
    /** Called on successful listing of specfication commands */
    function ciCommandsGetSuccess(data, status, jqXHR) {
		// Reset form and metadata.
    	$('#ci-form')[0].reset();
		ciMetadataDS.data(new Array());
		
		// Load command list into datasource.
		ciCommandsDS.data(data.results);
		commands = data.results;
		if (commands.length > 0) {
			selectedCommand = commands[0];
			showCommandForm(selectedCommand);
		}
   	
    	// Select first tab.
		ciTabs.select(0);
		
		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#ci-dialog').modal('show');
	}
    
	/** Handle error on listint specification commands */
	function ciCommandsGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to list commands for specification.");
	}
	
	/** Called when selected command changes */
	function onCommandChange() {
		var value = ciCommandList.value();
		for (var i=0; i < commands.length; i++) {
			if (commands[i].token == value) {
				selectedCommand = commands[i];
				break;
			}
		}
		showCommandForm(selectedCommand);
	}
	
	/** Shows the command form for a given command */
	function showCommandForm(command) {
		var html = "";
		for (var i = 0; i < command.parameters.length; i++) {
			var param = command.parameters[i];
			html += "<div class='control-group'>";
			html += "<label class='control-label' for='ci-param-" + param.name + "'>" + param.name + "</label>";
			html += "<div class='controls'>";
			if (param.type == "Bool") {
				html += "<input type='checkbox' id='ci-param-" + param.name + "' name='" + param.name + "'>";
			} else {
				html += "<input type='text' id='ci-param-" + param.name + "' name='" + param.name + "' class='input-xlarge'>";
			}
			html += "</div>";
			html += "</div>";
		}
		$('#ci-parameters').html(html);
	}
</script>