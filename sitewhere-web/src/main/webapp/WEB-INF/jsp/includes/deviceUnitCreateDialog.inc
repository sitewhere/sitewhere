<!-- Dialog for creating a device unit -->
<div id="duc-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3>Create Device Unit</h3>
	</div>
	<div class="modal-body">
		<div id="duc-tabs">
			<ul>
				<li class="k-state-active">Unit Details</li>
			</ul>
			<div>
				<form id="duc-general-form" class="form-horizontal" style="padding-top: 20px;">
					<div class="control-group">
						<label class="control-label" for="duc-unit-name" data-i18n="public.Name"></label>
						<div class="controls">
							<input type="text" id="duc-unit-name" title="Slot name" class="input-xlarge">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="duc-path" data-i18n="includes.RelativePath"></label>
						<div class="controls">
							<input type="text" id="duc-path" title="Relative path" class="input-xlarge">
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal" data-i18n="public.Cancel"></a> 
		<a id="duc-dialog-submit" href="javascript:void(0)" class="btn btn-primary" data-i18n="public.Create"></a>
	</div>
</div>

<!-- Script support for device create dialog -->
<script>
	/** Function called when dialog is submitted */
	var ducSubmitCallback;
	
	/** Specification token */
	var ducSpecToken;

	/** Provides external access to tabs */
	var ducTabs;

	/** Parent context */
	var ducContext;
	
	/** Schema before addition */
	var ducElementSchema;

	$(document).ready(function() {
		
		/** Create tab strip */
		ducTabs = $("#duc-tabs").kendoTabStrip({
			animation: false
		}).data("kendoTabStrip");
		
        /** Handle create dialog submit */
		$('#duc-dialog-submit').click(function(event) {
			event.preventDefault();
			if (!ducValidate()) {
				return;
			}
			var name = $("#duc-unit-name").val();
			var path = $("#duc-path").val();
			var unit = swGetDeviceUnitForContext(ducContext, ducElementSchema);
			var subunit = {"name": name, "path": path, "deviceSlots": [], "deviceUnits": []};
			unit.deviceUnits.push(subunit);
			var specData = {
				"deviceElementSchema": ducElementSchema, 
			}
			$.putJSON("${pageContext.request.contextPath}/api/specifications/" + ducSpecToken, 
					specData, onCreateSuccess, onUpdateFail);
		});
        
        /** Called on successful create */
        function onCreateSuccess() {
        	$('#duc-dialog').modal('hide');
			if (ducSubmitCallback != null) {
				ducSubmitCallback();
			}
        }
        
		/** Handle failed call to create device unit */
		function onUpdateFail(jqXHR, textStatus, errorThrown) {
			handleError(jqXHR, "Unable to add device unit.");
		}
    });
	
	/** Perform validation */
	function ducValidate() {
		$.validity.setup({ outputMode:"label" });
		$.validity.start();

        /** Validate fields */
		$("#duc-unit-name").require();
		$("#duc-path").require();
		
		/** Make sure path is not already used */
		$("#duc-path").assert(ducUnitNameValidate, "Path is already being used.");
      
		var result = $.validity.end();
		return result.valid;
	}
	
	/** Check for path already in use */
	function ducUnitNameValidate() {
		var value = $("#duc-path").val();
		var unit = swGetDeviceUnitForContext(ducContext, ducElementSchema);
		if (unit) {
			var slength = unit.deviceSlots.length;
			for (var i = 0; i < slength; i++) {
				if (unit.deviceSlots[i].path == value) {
					return false;
				}
			}
			var ulength = unit.deviceUnits.length;
			for (var j = 0; j < ulength; j++) {
				if (unit.deviceUnits[j].path == value) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	/** Open the dialog */
	function ducOpen(specToken, context, schema, callback) {
		ducSpecToken = specToken;

		// Store context information.
		ducContext = context;
		ducElementSchema = schema;
		
		// Reset form and metadata.
    	$('#duc-general-form')[0].reset();

    	// Select first tab.
		ducTabs.select(0);
		
		// Function called on submit.
		ducSubmitCallback = callback;
		
		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#duc-dialog').modal('show');
	}
</script>