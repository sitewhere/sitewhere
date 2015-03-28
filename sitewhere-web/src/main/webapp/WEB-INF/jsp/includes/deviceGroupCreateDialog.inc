<!-- Dialog for creating a device group -->
<div id="dgc-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 id="dgc-dialog-header" data-i18n="includes.deviceGroupCreateDialog.CreateDeviceGroup"></h3>
	</div>
	<div class="modal-body">
		<div id="dgc-tabs">
			<ul>
				<li class="k-state-active" data-i18n="includes.deviceGroupCreateDialog.GroupDetails"></li>
				<li data-i18n="public.Roles"></li>
				<li data-i18n="public.Metadata"></li>
			</ul>
			<div>
				<form id="dgc-general-form" class="form-horizontal" style="padding-top: 20px;">
					<div class="control-group">
						<label class="control-label" for="dgc-name" data-i18n="includes.deviceGroupCreateDialog.GroupName"></label>
						<div class="controls">
							<input type="text" id="dgc-name" title="Name" class="input-xlarge">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="dgc-description" data-i18n="public.Description"></label>
						<div class="controls">
							<textarea id="dgc-description" class="input-xlarge" style="height: 10em;"></textarea>
						</div>
					</div>
					<input type="hidden" id="dgc-group-token"/>
				</form>
			</div>
			<div>
				<div class="sw-sublist-header" data-i18n="public.Role"></div>
				<table id="dgc-roles" class="sw-sublist-list" style="height: 190px;"></table>
				<div style="position: relative;" class="sw-sublist-add-new">
					<input type="text" id="dgc-add-role-value" 
						style="width: 82%; margin-bottom: 0px;" title="New Role Value">
					<a style="float: right;" class="btn" href="javascript:void(0)" onclick="dgcAddRole()">
						<i class="icon-plus sw-button-icon"></i>&nbsp<font data-i18n="public.Add"></font></a>
					<div id="dgc-role-create-error" style="color: #f00; display: none;"></div>
				</div>
    		</div>
			<div>
				<div id="dgc-metadata">
					<jsp:include page="../includes/metadata.jsp" flush="true">
					    <jsp:param name="uid" value="dgc"/>
					</jsp:include>
				</div>
            </div>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal" data-i18n="public.Cancel"></a> 
		<a id="dgc-dialog-submit" href="javascript:void(0)" class="btn btn-primary" data-i18n="public.Create"></a>
	</div>
</div>

<!-- Script support for device group create dialog -->
<script>
	/** Function called when dialog is submitted */
	var dgcSubmitCallback;

	/** Provides external access to tabs */
	var dgcTabs;
	
	/** Datasource for group roles */
	var dgcRolesDS;
	
	/** List containing roles */
	var dgcRoles;

	$(document).ready(function() {
		
		/** Create tab strip */
		dgcTabs = $("#dgc-tabs").kendoTabStrip({
			animation: false
		}).data("kendoTabStrip");
		
		/** Datasource for roles */
		dgcRolesDS = new kendo.data.DataSource({
			data: dgcRoles,
		});
		
		/** Create the hardware match list */
		dgcRoles = $("#dgc-roles").kendoListView({
			dataSource : dgcRolesDS,
			template : kendo.template($("#tpl-role-entry").html()),
			height: 400,
		}).data("kendoListView");
		
        /** Handle create dialog submit */
		$('#dgc-dialog-submit').click(function(event) {
			event.preventDefault();
			if (!dgcValidate()) {
				return;
			}
			
			var groupData = {
				"name": $('#dgc-name').val(), 
				"description": $('#dgc-description').val(), 
				"roles": dgcRolesDS.data(),
				"metadata": swMetadataAsLookup(dgcMetadataDS.data()),
			}
			
			var token = $('#dgc-group-token').val();
			if (token.length == 0) {
				$.postJSON("${pageContext.request.contextPath}/api/devicegroups", 
						groupData, onCreateSuccess, onCreateFail);
			} else {
				groupData.token = token;
				$.putJSON("${pageContext.request.contextPath}/api/devicegroups/" + token, 
						groupData, onCreateSuccess, onUpdateFail);
			}
		});
        
        /** Called on successful create */
        function onCreateSuccess() {
        	$('#dgc-dialog').modal('hide');
			if (dgcSubmitCallback != null) {
				dgcSubmitCallback();
			}
        }
        
		/** Handle failed call to create device group */
		function onCreateFail(jqXHR, textStatus, errorThrown) {
			handleError(jqXHR, "Unable to create device group.");
		}
        
		/** Handle failed call to updated device group */
		function onUpdateFail(jqXHR, textStatus, errorThrown) {
			handleError(jqXHR, "Unable to updated device group.");
		}
    });
	
	/** Validate everything */
	function dgcValidate() {
		$.validity.setup({ outputMode:"label" });
		$.validity.start();

        /** Validate fields */
		$("#dgc-name").require();
		$("#dgc-description").require();
     
		var result = $.validity.end();
		return result.valid;
	}
	
	/** Open the dialog */
	function dgcOpen(e, callback) {
		var event = e || window.event;
		event.stopPropagation();
		
		$('#dgc-dialog-header').html(i18next("includes.deviceGroupCreateDialog.CreateDeviceGroup"));
		$('#dgc-dialog-submit').html(i18next("public.Create"));
		
		// Clear hidden fields.
		$('#dgc-group-token').val("");
		
		// Reset roles.
		dgcRolesDS.data(new Array());
		
		// Reset form and metadata.
    	$('#dgc-general-form')[0].reset();
		dgcMetadataDS.data(new Array());
    	
    	// Select first tab.
		dgcTabs.select(0);
		
		// Function called on submit.
		dgcSubmitCallback = callback;
		
		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#dgc-dialog').modal('show');
	}
	
	/** Called to load the given device group and open it for edit */
	function dguOpen(token, callback) {
		// Function called on submit.
		dgcSubmitCallback = callback;
		
		// Get device group for edit.
		$.getJSON("${pageContext.request.contextPath}/api/devicegroups/" + token, 
				dguGetSuccess, dguGetFailed);
	}
    
    /** Called on successful user load request */
    function dguGetSuccess(data, status, jqXHR) {
		$('#dgc-dialog-header').html(i18next("public.EditDeviceGroup"));
		$('#dgc-dialog-submit').html(i18next("public.Save"));
		
		// Load hidden fields.
		$('#dgc-group-token').val(data.token);
		
		// Load form fields.
		$('#dgc-name').val(data.name);
		$('#dgc-description').val(data.description);
		
		// Load roles.
		dgcRolesDS.data(data.roles);
		
		dgcMetadataDS.data(swLookupAsMetadata(data.metadata));
		dgcTabs.select(0);
		
		$.validity.clear();
		$('#dgc-dialog').modal('show');
    }
    
	/** Handle error on getting command */
	function dguGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load device command for edit.");
	}
	
	/** Called when 'add role' button is clicked */
	function dgcAddRole() {
		$("#dgc-role-create-error").hide();
		var error = "";
		var role = $('#dgc-add-role-value').val();
		
		// Check for empty or bad values.
		if (role.length == 0) {
			error = "You must enter a value.";
		}
		var regex = /^[\w-]+$/;
		if (!regex.test(role)) {
			error = "Invalid character in role."
		}
		
		if (error.length > 0) {
			$("#dgc-role-create-error").html(error);
			$("#dgc-role-create-error").toggle();
		} else {
			dgcRolesDS.data().push(role);
			$("#dgc-add-role-value").val("");
		}
	}
	
	/** Called when 'delete role' button is clicked */
	function dgcDeleteRole(role) {
		var data = dgcRolesDS.data();
		for (var index = 0, existing; existing = data[index]; index++) {
			if (existing == role) {
				dgcRolesDS.data().splice(index, 1);
				return;
			}
		}
	}
</script>