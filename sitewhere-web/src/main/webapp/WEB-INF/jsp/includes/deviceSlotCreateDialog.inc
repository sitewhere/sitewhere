<!-- Dialog for creating a device slot -->
<div id="dsc-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3>Create Device Slot</h3>
	</div>
	<div class="modal-body">
		<div id="dsc-tabs">
			<ul>
				<li class="k-state-active">Slot Details</li>
			</ul>
			<div>
				<form id="dsc-general-form" class="form-horizontal" style="padding-top: 20px;">
					<div class="control-group">
						<label class="control-label" for="dsc-slot-name" data-i18n="public.Name"></label>
						<div class="controls">
							<input type="text" id="dsc-slot-name" title="Slot name" class="input-xlarge">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="dsc-path" data-i18n="includes.RelativePath"></label>
						<div class="controls">
							<input type="text" id="dsc-path" title="Relative path" class="input-xlarge">
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal" data-i18n="public.Cancel"></a> 
		<a id="dsc-dialog-submit" href="javascript:void(0)" class="btn btn-primary" data-i18n="public.Create"></a>
	</div>
</div>

<!-- Script support for device create dialog -->
<script>
	/** Function called when dialog is submitted */
	var dscSubmitCallback;
	
	/** Specification token */
	var dscSpecToken;

	/** Provides external access to tabs */
	var dscTabs;

	/** Parent context */
	var dscContext;
	
	/** Schema before addition */
	var dscElementSchema;

	$(document).ready(function() {
		
		/** Create tab strip */
		dscTabs = $("#dsc-tabs").kendoTabStrip({
			animation: false
		}).data("kendoTabStrip");
		
        /** Handle create dialog submit */
		$('#dsc-dialog-submit').click(function(event) {
			event.preventDefault();
			if (!dscValidate()) {
				return;
			}
			var name = $("#dsc-slot-name").val();
			var path = $("#dsc-path").val();
			var unit = swGetDeviceUnitForContext(dscContext, dscElementSchema);
			var slot = {"name": name, "path": path};
			unit.deviceSlots.push(slot);
			var specData = {
				"deviceElementSchema": dscElementSchema, 
			}
			$.putJSON("${pageContext.request.contextPath}/api/specifications/" + dscSpecToken, 
					specData, onCreateSuccess, onUpdateFail);
		});
        
        /** Called on successful create */
        function onCreateSuccess() {
        	$('#dsc-dialog').modal('hide');
			if (dscSubmitCallback != null) {
				dscSubmitCallback();
			}
        }
        
		/** Handle failed call to create device slot */
		function onUpdateFail(jqXHR, textStatus, errorThrown) {
			handleError(jqXHR, "Unable to add device slot.");
		}
    });
	
	/** Perform validation */
	function dscValidate() {
		$.validity.setup({ outputMode:"label" });
		$.validity.start();

        /** Validate fields */
		$("#dsc-slot-name").require();
		$("#dsc-path").require();
		
		/** Make sure path is not already used */
		$("#dsc-path").assert(dscSlotNameValidate, "Path is already being used.");
      
		var result = $.validity.end();
		return result.valid;
	}
	
	/** Check for path already in use */
	function dscSlotNameValidate() {
		var value = $("#dsc-path").val();
		var unit = swGetDeviceUnitForContext(dscContext, dscElementSchema);
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
	function dscOpen(specToken, context, schema, callback) {
		dscSpecToken = specToken;

		// Store context information.
		dscContext = context;
		dscElementSchema = schema;
		
		// Reset form and metadata.
    	$('#dsc-general-form')[0].reset();

    	// Select first tab.
		dscTabs.select(0);
		
		// Function called on submit.
		dscSubmitCallback = callback;
		
		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#dsc-dialog').modal('show');
	}
</script>