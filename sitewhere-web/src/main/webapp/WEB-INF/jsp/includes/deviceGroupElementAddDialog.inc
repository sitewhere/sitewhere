<!-- Dialog for adding a device group element -->
<div id="gea-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 id="gea-dialog-header" data-i18n="includes.deviceGroupElementAddDialog.AddDeviceGroupElement"></h3>
	</div>
	<div class="modal-body">
		<div id="gea-tabs">
			<ul>
				<li class="k-state-active" data-i18n="includes.ElementDetails"></li>
				<li data-i18n="public.Roles"></li>
			</ul>
			<div>
				<form id="gea-general-form" class="form-horizontal" style="padding-top: 20px;">
					<div class="control-group">
						<label class="control-label" for="gea-element-type" data-i18n="includes.deviceGroupElementAddDialog.ElementType"></label>
						<div class="controls">
							<input id="gea-element-type"/>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="gea-element-id" data-i18n="includes.deviceGroupElementAddDialog.ElementId"></label>
						<div class="controls">
							<input type="text" id="gea-element-id" title="Element id" class="input-xlarge">
						</div>
					</div>
				</form>
			</div>
			<div>
				<div class="sw-sublist-header" data-i18n="public.Role"></div>
				<table id="gea-roles" class="sw-sublist-list" style="height: 190px;"></table>
				<div style="position: relative;" class="sw-sublist-add-new">
					<input type="text" id="gea-add-role-value" 
						style="width: 82%; margin-bottom: 0px;" title="New Role Value">
					<a style="float: right;" class="btn" href="javascript:void(0)" onclick="geaAddRole()">
						<i class="icon-plus sw-button-icon"></i>&nbsp<font data-i18n="public.Add"></font></a>
					<div id="gea-role-create-error" style="color: #f00; display: none;"></div>
				</div>
			</div>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal" data-i18n="public.Cancel"></a> 
		<a id="gea-dialog-submit" href="javascript:void(0)" class="btn btn-primary" data-i18n="public.Add"></a>
	</div>
</div>

<!-- Script support for device element add dialog -->
<script>
	/** Group token */
	var geaGroupToken;

	/** Function called when dialog is submitted */
	var geaSubmitCallback;

	/** Provides external access to tabs */
	var geaTabs;
	
	/** Datasource for roles */
	var geaRolesDS;
	
	/** List containing roles */
	var geaRoles;
	
	/** Available group element types shown in dropdown */
    var geaElementTypes = [
		{ text: "Device", value: "Device" },
		{ text: "Subgroup", value: "Group" },
	];

	$(document).ready(function() {
		
		/** Create tab strip */
		geaTabs = $("#gea-tabs").kendoTabStrip({
			animation: false
		}).data("kendoTabStrip");

    	// Create element type dropdown.
    	$("#gea-element-type").kendoDropDownList({
    		dataTextField: "text",
    		dataValueField: "value",
    	    dataSource: geaElementTypes,
    	    index: 0,
    	});
		
		/** Datasource for roles */
		geaRolesDS = new kendo.data.DataSource({
			data: new Array(),
		});
		
		/** Create the hardware match list */
		geaRoles = $("#gea-roles").kendoListView({
			dataSource : geaRolesDS,
			template : kendo.template($("#tpl-element-role-entry").html()),
			height: 400,
		}).data("kendoListView");
		
        /** Handle create dialog submit */
		$('#gea-dialog-submit').click(function(event) {
			event.preventDefault();
			if (!geaValidate()) {
				return;
			}
			
			var elementData = {
				"type": $('#gea-element-type').val(), 
				"elementId": $('#gea-element-id').val(), 
				"roles": geaRolesDS.data(),
			}
			
			$.putJSON("${pageContext.request.contextPath}/api/devicegroups/" + geaGroupToken + "/elements", 
					[elementData], onAddSuccess, onAddFail);
		});
        
        /** Called on successful add */
        function onAddSuccess() {
        	$('#gea-dialog').modal('hide');
			if (geaSubmitCallback != null) {
				geaSubmitCallback();
			}
        }
        
		/** Handle failed call to add element to device group */
		function onAddFail(jqXHR, textStatus, errorThrown) {
			handleError(jqXHR, "Unable to add element to device group.");
		}
    });
	
	/** Validate everything */
	function geaValidate() {
		$.validity.setup({ outputMode:"label" });
		$.validity.start();

        /** Validate fields */
		$("#gea-element-id").require();
     
		var result = $.validity.end();
		return result.valid;
	}
	
	/** Open the dialog */
	function geaOpen(groupToken, callback) {
		// Save group token.
		geaGroupToken = groupToken;
		
		// Reset roles.
		geaRolesDS.data(new Array());
		
		// Reset element id field.
    	$('#gea-element-id').val("");
    	
    	// Select first tab.
		geaTabs.select(0);
		
		// Function called on submit.
		geaSubmitCallback = callback;
		
		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#gea-dialog').modal('show');
	}
	
	/** Called when 'add role' button is clicked */
	function geaAddRole() {
		$("#gea-role-create-error").hide();
		var error = "";
		var role = $('#gea-add-role-value').val();
		
		// Check for empty or bad values.
		if (role.length == 0) {
			error = i18next("includes.deviceGroupElementAddDialog.YMEAV");
		}
		var regex = /^[\w-]+$/;
		if (!regex.test(role)) {
			error = i18next("includes.deviceGroupElementAddDialog.ICIR");
		}
		
		if (error.length > 0) {
			$("#gea-role-create-error").html(error);
			$("#gea-role-create-error").toggle();
		} else {
			geaRolesDS.data().push(role);
			$("#gea-add-role-value").val("");
		}
	}
	
	/** Called when 'delete role' button is clicked */
	function geaDeleteRole(role) {
		var data = geaRolesDS.data();
		for (var index = 0, existing; existing = data[index]; index++) {
			if (existing == role) {
				geaRolesDS.data().splice(index, 1);
				return;
			}
		}
	}
</script>