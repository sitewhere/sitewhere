<style>
.sw-assignment-update-imgwrapper {
	float: left;
	margin-left: 60px;
	margin-right: 20px;
	width: 100px;
	height: 100px;
	position: relative;
}

.sw-assignment-update-img {
	display: block;
	margin-left: auto;
	margin-right: auto;
    max-width: 100px;
    max-height: 100px;
    border: 1px solid rgb(221, 221, 221);
}

.sw-assignment-update-label {
	font-size: 10pt;
	font-weight: bold;
	min-width: 100px;
	display: inline-block;
}
</style>

<!-- Dialog for updating a device assignment -->
<div id="au-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 id="dialog-header">Update Device Assignment</h3>
	</div>
	<div class="modal-body">
		<div id="au-tabs">
			<ul>
				<li class="k-state-active">Assignment Details</li>
				<li>Metadata</li>
			</ul>
			<div>
				<div style="padding-top: 20px;">
					<div id="au-static-header"></div>
				</div>
			</div>
			<div>
				<div id="au-metadata" class="sw-metadata-grid"></div>
            </div>
		</div>
		<input type="hidden" id="au-token"/>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal">Cancel</a> 
		<a id="au-submit" href="javascript:void(0)" class="btn btn-primary">Update</a>
	</div>
</div>

<!-- Static content in top of update dialog -->
<script type="text/x-kendo-tmpl" id="au-static-block">
# if ((assignmentType == 'Hardware') && (associatedHardware)) { #
	<div style="min-height: 120px;">
		<div class="sw-assignment-update-imgwrapper">
			<img class="sw-assignment-update-img" src="#:associatedHardware.imageUrl#"/>
			<span class="label label-info sw-assignment-list-entry-logo-tag">Asset</span>
		</div>
		<div style="display: inline-block; max-width: 280px;">
			<p class="ellipsis"><span class="sw-assignment-update-label">Asset:</span> #:associatedHardware.name#</p>
			<p class="ellipsis"><span class="sw-assignment-update-label">SKU:</span> #:associatedHardware.sku#</p>
			<p class="ellipsis"><span class="sw-assignment-update-label">Info:</span> #:associatedHardware.description#</p>
# } else if ((assignmentType == 'Person') && (associatedPerson)) { #
	<div style="min-height: 120px;">
		<div class="sw-assignment-update-imgwrapper">
			<img class="sw-assignment-update-img" src="#:associatedPerson.photoUrl#"/>
			<span class="label label-info sw-assignment-list-entry-logo-tag">Asset</span>
		</div>
		<div style="display: inline-block; max-width: 280px;">
			<p class="ellipsis"><span class="sw-assignment-update-label">Asset:</span> #:associatedPerson.name#</p>
			<p class="ellipsis"><span class="sw-assignment-update-label">Email:</span> #:associatedPerson.emailAddress#</p>
			<p class="ellipsis"><span class="sw-assignment-update-label">Roles:</span> #:swArrayAsCommaDelimited(associatedPerson.roles)#</p>
# } else if (assignmentType == 'Unassociated') { #
	<div style="min-height: 120px;">
		<div class="sw-assignment-update-imgwrapper">
			<img class="sw-assignment-update-img" src="#:device.assetImageUrl#"/>
			<span class="label label-info sw-assignment-list-entry-logo-tag">Unassociated</span>
		</div>
		<div style="display: inline-block;" max-width: 280px;>
			<p class="ellipsis"><span class="sw-assignment-update-label">Type:</span> Unassociated Device</p>
# } #
			<p class="ellipsis"><span class="sw-assignment-update-label">Assigned:</span> #= formattedDate(kendo.parseDate(activeDate)) #</p>
			<p class="ellipsis"><span class="sw-assignment-update-label">Released:</span> #= formattedDate(kendo.parseDate(releasedDate)) #</p>
			<p class="ellipsis"><span class="sw-assignment-update-label">Status:</span> #:status#</p>
		</div>
	</div>
	<div style="min-height: 120px; clear: both; border-top: 1px solid \\#eeeeee; padding-top: 15px;">
		<div class="sw-assignment-update-imgwrapper">
			<img class="sw-assignment-update-img" src="#:device.assetImageUrl#"/>
			<span class="label label-info sw-assignment-list-entry-logo-tag">Device</span>
		</div>
		<p class="ellipsis"><span class="sw-assignment-update-label">Device:</span> #:device.assetName#</p>
		<p class="ellipsis"><span class="sw-assignment-update-label">Hardware Id:</span> #:device.hardwareId#</p>
	</div>
</script>

<!-- Script support for assignment create dialog -->
<script>
	/** Function called when dialog is submitted */
	var auSubmitCallback;

	/** Provides external access to tabs */
	var auTabs;
	
	/** Metadata datasource */
	var auMetadataDS;

	$(document).ready(function() {
		
		/** Datasource for metadata entries */
		auMetadataDS = swMetadataDatasource();
		
		/** Grid for metadata */
        $("#au-metadata").kendoGrid(swMetadataGridOptions(auMetadataDS));
		
		/** Create tab strip for the update dialog */
		auTabs = $("#au-tabs").kendoTabStrip({
			animation: false
		}).data("kendoTabStrip");
		
        /** Handle dialog submit */
		$('#au-submit').click(function(event) {
			event.preventDefault();
			if (!auValidate()) {
				return;
			}
	        
			var token = $('#au-token').val();
			var data = {
				"metadata": swMetadataAsLookup(auMetadataDS.data()),
			}
			$.putJSON("${pageContext.request.contextPath}/api/assignments/" + token + "/metadata", 
					data, onUpdateSuccess, onUpdateFail);
		});
        
        /** Called on successful update */
        function onUpdateSuccess() {
        	$('#au-dialog').modal('hide');
			if (auSubmitCallback != null) {
				auSubmitCallback();
			}
        }
        
		/** Handle failed call to update device */
		function onUpdateFail(jqXHR, textStatus, errorThrown) {
			handleError(jqXHR, "Unable to update assignment.");
		}
    });
	
	/** Validate the dialog */
	function auValidate() {
		$.validity.setup({ outputMode:"label" });
		$.validity.start();

        /** These should all be in hidden fields */
		$("#au-token").require();
     
		var result = $.validity.end();
		return result.valid;
	}
	
	/** Called to open the assignment update dialog */
	function auOpen(token, callback) {
		// Function called on submit.
		auSubmitCallback = callback;
		
		// Get latest device data for hardware id.
		$.getJSON("${pageContext.request.contextPath}/api/assignments/" + token, 
			auUpdateGetSuccess, auUpdateGetFailed);
	}
    
    /** Called on successful assignment load request */
    function auUpdateGetSuccess(data, status, jqXHR) {
		var template = kendo.template($("#au-static-block").html());
		$('#au-static-header').html(template(data));
		$('#au-token').val(data.token);
		auMetadataDS.data(swLookupAsMetadata(data.metadata));
		
		auTabs.select(0);
		
		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#au-dialog').modal('show');
    }
    
	/** Handle error on getting site */
	function auUpdateGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to get assignment for update.");
	}
</script>