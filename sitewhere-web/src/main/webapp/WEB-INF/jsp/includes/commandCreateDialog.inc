<!-- Dialog for command create/update -->
<style>
.sw-cc-command-params-col-name {
	width: 180px;
}
.sw-cc-command-params-col-type {
	width: 140px;
}
.sw-cc-command-params-col-req {
	width: 90px;
}
</style>
<div id="cc-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 id="cc-dialog-header">Create Device Command</h3>
	</div>
	<div class="modal-body">
		<div id="cc-tabs">
			<ul>
				<li class="k-state-active">Command</li>
				<li>Parameters</li>
				<li>Metadata</li>
			</ul>
			<div>
				<form id="cc-general-form" class="form-horizontal" style="padding-top: 20px;">
					<div class="control-group">
						<label class="control-label" for="cc-name">Name</label>
						<div class="controls">
							<input type="text" id="cc-name" class="input-xlarge" title="Name">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="cc-namespace">Namespace</label>
						<div class="controls">
							<input type="text" id="cc-namespace" class="input-xlarge" title="Namespace">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="cc-description">Description</label>
						<div class="controls">
							<textarea id="cc-description" class="input-xlarge" style="height: 7em;"></textarea>
						</div>
					</div>
				</form>
			</div>
			<div>
				<div class="sw-sublist-header">
					<div class="sw-cc-command-params-col-name">Parameter Name</div>
					<div class="sw-cc-command-params-col-type">Parameter Type</div>
					<div class="sw-cc-command-params-col-req">Required</div>
				</div>
				<table id="cc-parameters" class="sw-sublist-list">
				</table>
				<div class="sw-sublist-add-new">
					<div class="sw-sublist-footer">
						<div style="width: 205px">Parameter Name</div>
						<div style="width: 145px">Parameter Type</div>
						<div>Req</div>
					</div>
					<input type="text" id="cc-new-name" class="sw-cc-command-params-col-name" 
						style="margin-bottom: 0px; margin-right: 10px;" title="Name">
					<input type="text" id="cc-new-datatype" class="sw-cc-command-params-col-type" 
						style="margin-right: 10px;" title="Datatype">
					<input type="checkbox" id="cc-new-required" 
						style="margin-right: 35px;"title="Required">
					<a class="btn" href="javascript:void(0)" onclick="onAddParameter()">
						<i class="icon-plus sw-button-icon"></i> Add</a>
					<div id="cc-new-error" style="color: #f00; display: none;"></div>
				</div>
			</div>
			<div>
				<div id="cc-metadata">
					<jsp:include page="../includes/metadata.jsp" flush="true">
					    <jsp:param name="uid" value="cc"/>
					</jsp:include>
				</div>
            </div>
		</div>
	</div>
	<input id="cc-specification-token" type="hidden" value="" />
	<input id="cc-command-token" type="hidden" value="" />
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal">Cancel</a> 
		<a id="cc-dialog-submit" href="javascript:void(0)" class="btn btn-primary">Create</a>
	</div>
</div>

<!-- Script support for command create/update dialog -->
<script>
	/** Function called when dialog is submitted */
	var ccSubmitCallback;
	
	/** Pointer to tabs instance */
	var ccTabs;
	
	/** List of command parameters */
	var paramsDS;
	
	/** Parameters list view */
	var paramsList;
	
	/** Dropdown list of datatypes */
	var ccDatatypeList;
	
	/** Available parameter datatypes shown in dropdown */
    var ccParameterDatatypes = [
		{ text: "String", datatype: "String" },
		{ text: "Double", datatype: "Double" },
		{ text: "Float", datatype: "Float" },
		{ text: "Boolean", datatype: "Boolean" },
		{ text: "Int32", datatype: "Int32" },
		{ text: "Int64", datatype: "Int64" },
		{ text: "UInt32", datatype: "UInt32" },
		{ text: "UInt64", datatype: "UInt64" },
		{ text: "SInt32", datatype: "SInt32" },
		{ text: "SInt64", datatype: "SInt64" },
		{ text: "Fixed32", datatype: "Fixed32" },
		{ text: "Fixed64", datatype: "Fixed64" },
		{ text: "SFixed32", datatype: "SFixed32" },
		{ text: "SFixed64", datatype: "SFixed64" },
	];

	$(document).ready(function() {
		
		/** Create the tab strip */
		ccTabs = $("#cc-tabs").kendoTabStrip({
			animation: false,
		}).data("kendoTabStrip");

    	// Create dropdown for datatypes.
    	ccDatatypeList = $("#cc-new-datatype").kendoDropDownList({
    		dataTextField: "text",
    		dataValueField: "datatype",
    	    dataSource: ccParameterDatatypes,
    	}).data("kendoDropDownList");
		
    	/** Parameters datasource */
		paramsDS = new kendo.data.DataSource({
			data: []
		});

    	/** Parameters list */
		paramsList = $("#cc-parameters").kendoListView({
			dataSource : paramsDS,
			template : kendo.template($("#tpl-command-param-entry").html())
		});
		
        /** Handle dialog submit */
		$('#cc-dialog-submit').click(function(event) {
			event.preventDefault();
			var specToken = $('#cc-specification-token').val();
			if (specToken.length == 0) {
				return;
			}
			
			var name = $('#cc-name').val();
			var namespace = $('#cc-namespace').val();
			var description = $('#cc-description').val();
			var token = $('#cc-command-token').val();
			var isCreate = (token.length == 0);
			if (!ccValidate()) {
				return;
			}
			
			if (namespace.length == 0) {
				namespace = null;
			}
			if (description.length == 0) {
				description = null;
			}

			var command = {
					"name": name,
					"namespace": namespace,
					"description": description,
					"parameters": paramsDS.data(),
					"metadata": swMetadataAsLookup(ccMetadataDS.data()),
			};
			if (isCreate) {
				$.postJSON("${pageContext.request.contextPath}/api/specifications/" + specToken + "/commands", 
						command, onSuccess, onCreateFail);
			} else {
				$.putJSON("${pageContext.request.contextPath}/api/commands/" + token, 
						command, onSuccess, onUpdateFail);
			}
		});
        
        /** Called on successful create/update */
        function onSuccess() {
        	$('#cc-dialog').modal('hide');
			if (ccSubmitCallback != null) {
				ccSubmitCallback();
			}
        }
        
		/** Handle failed call to create command */
		function onCreateFail(jqXHR, textStatus, errorThrown) {
			handleError(jqXHR, "Unable to create device command.");
		}
        
		/** Handle failed call to update command */
		function onUpdateFail(jqXHR, textStatus, errorThrown) {
			handleError(jqXHR, "Unable to update device command.");
		}
    });
	
	/** Called when 'add parameter' button is pressed */
	function onAddParameter() {
		$("#cc-new-error").hide();
		var error = "";
		var parameter = {};
		parameter.name = $("#cc-new-name").val();
		parameter.type = $("#cc-new-datatype").val();
		parameter.required = $("#cc-new-required").is(':checked');
		
		// Check for empty.
		if (parameter.name.length == 0) {
			error = "Parameter name is required.";
		}
		var regex = /^\w+$/;
		if (!regex.test(parameter.name)) {
			error = "Invalid character in parameter name."
		}
		
		// Check for already used.
		var data = paramsDS.data();
		for (var index = 0, param; param = data[index]; index++) {
			if (parameter.name == param.name) {
				error = "Parameter name is already being used.";
				break;
			}
		}
		if (error.length > 0) {
			$("#cc-new-error").html(error);
			$("#cc-new-error").toggle();
		} else {
			paramsDS.data().push(parameter);
			$("#cc-new-name").val("");
		}
	}
	
	/** Deletes a parameter by name */
	function onDeleteParameter(name) {
		var data = paramsDS.data();
		for (var index = 0, param; param = data[index]; index++) {
			if (param.name == name) {
				paramsDS.data().splice(index, 1);
				return;
			}
		}
	}

	/** Validate dialog contents */
	function ccValidate() {
		$.validity.setup({ outputMode:"label" });
		$.validity.start();

        /** Validate user form */
		$("#cc-name").require();
		$("#cc-name").match(/^\w+$/, "Command name contains an invalid character.");
      
		var result = $.validity.end();
		return result.valid;
	}

	/** Open the command create dialog */
	function ccOpen(specificationToken, callback) {
		ccClearDialog();
		
		$('#cc-dialog-header').html("Create Device Command");
		$('#cc-dialog-submit').html("Create");
		
		paramsDS.data(new Array());
		ccMetadataDS.data(new Array());
		ccTabs.select(0);
		
		// Load hidden fields for tokens.
		$('#cc-specification-token').val(specificationToken);
		$('#cc-command-token').val("");
		
		$('#cc-new-name').val("");

		// Function called on submit.
		ccSubmitCallback = callback;
		
		$.validity.clear();
		$('#cc-dialog').modal('show');
	}
		
	/** Called to load the given command and open it for edit */
	function cuOpen(token, callback) {
		// Function called on submit.
		ccSubmitCallback = callback;
		
		// Get command for edit.
		$.getJSON("${pageContext.request.contextPath}/api/commands/" + token, 
				cuCommandGetSuccess, suCommandGetFailed);
	}
    
    /** Called on successful user load request */
    function cuCommandGetSuccess(data, status, jqXHR) {
		$('#cc-dialog-header').html("Edit Device Command");
		$('#cc-dialog-submit').html("Save");
		
		// Load hidden fields for tokens.
		$('#cc-specification-token').val(data.specificationToken);
		$('#cc-command-token').val(data.token);
		
		// Load form fields.
		$('#cc-name').val(data.name);
		$('#cc-namespace').val(data.namespace);
		$('#cc-description').val(data.description);
		$('#cc-new-name').val("");
		
		// Load parameters.
		paramsDS.data(data.parameters);
		
		ccMetadataDS.data(swLookupAsMetadata(data.metadata));
		ccTabs.select(0);
		
		$.validity.clear();
		$('#cc-dialog').modal('show');
    }
    
	/** Handle error on getting command */
	function suCommandGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load device command for edit.");
	}
    
    /** Clear all dialog fields */
    function ccClearDialog() {
    	$('#cc-general-form')[0].reset();
    }
</script>