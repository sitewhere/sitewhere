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
		<h3 id="dialog-header" data-i18n="includes.assignmentUpdateDialog.UpdateDeviceAssignment"></h3>
	</div>
	<div class="modal-body">
		<div id="au-tabs">
			<ul>
				<li class="k-state-active" data-i18n="includes.assignmentUpdateDialog.AssignmentDetails"></li>
				<li data-i18n="public.Metadata"></li>
			</ul>
			<div>
				<div style="padding-top: 20px;">
					<div id="au-static-header"></div>
				</div>
			</div>
			<div>
				<div id="au-metadata">
					<jsp:include page="../includes/metadata.jsp" flush="true">
					    <jsp:param name="uid" value="au"/>
					</jsp:include>
				</div>
            </div>
		</div>
		<input type="hidden" id="au-token"/>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal" data-i18n="public.Cancel"></a> 
		<a id="au-submit" href="javascript:void(0)" class="btn btn-primary" data-i18n="public.Update"></a>
	</div>
</div>

<!-- Static content in top of update dialog -->
<script type="text/x-kendo-tmpl" id="au-static-block">
# if (data.associatedHardware) { #
	<div style="min-height: 120px;">
		<div class="sw-assignment-update-imgwrapper">
			<img class="sw-assignment-update-img" src="#:associatedHardware.imageUrl#"/>
			<span class="label label-info sw-assignment-list-entry-logo-tag">#= i18next("public.Asset") #</span>
		</div>
		<div style="display: inline-block; max-width: 280px;">
			<p class="ellipsis"><span class="sw-assignment-update-label">#= i18next("public.Asset") #:</span> #:associatedHardware.name#</p>
			<p class="ellipsis"><span class="sw-assignment-update-label">SKU:</span> #:associatedHardware.sku#</p>
			<p class="ellipsis"><span class="sw-assignment-update-label">Info:</span> #:associatedHardware.description#</p>
# } else if (data.associatedPerson) { #
	<div style="min-height: 120px;">
		<div class="sw-assignment-update-imgwrapper">
			<img class="sw-assignment-update-img" src="#:associatedPerson.imageUrl#"/>
			<span class="label label-info sw-assignment-list-entry-logo-tag">#= i18next("public.Asset") #</span>
		</div>
		<div style="display: inline-block; max-width: 280px;">
			<p class="ellipsis"><span class="sw-assignment-update-label">#= i18next("public.Asset") #:</span> #:associatedPerson.name#</p>
			<p class="ellipsis"><span class="sw-assignment-update-label">#= i18next("public.Email") #:</span> #:associatedPerson.emailAddress#</p>
			<p class="ellipsis"><span class="sw-assignment-update-label">#= i18next("public.Roles") #:</span> #:swArrayAsCommaDelimited(associatedPerson.roles)#</p>
# } else if (data.associatedLocation) { #
	<div style="min-height: 120px;">
		<div class="sw-assignment-update-imgwrapper">
			<img class="sw-assignment-update-img" src="#:associatedLocation.imageUrl#"/>
			<span class="label label-info sw-assignment-list-entry-logo-tag">#= i18next("public.Asset") #</span>
		</div>
		<div style="display: inline-block; max-width: 280px;">
			<p class="ellipsis"><span class="sw-assignment-update-label">Latitude:</span> #:associatedLocation.latitude#</p>
			<p class="ellipsis"><span class="sw-assignment-update-label">Longitude:</span> #:associatedLocation.longitude#</p>
			<p class="ellipsis"><span class="sw-assignment-update-label">Elevation:</span> #:associatedLocation.elevation#</p>
# } else if (assignmentType == 'Unassociated') { #
	<div style="min-height: 120px;">
		<div class="sw-assignment-update-imgwrapper">
			<img class="sw-assignment-update-img" src="#:device.assetImageUrl#"/>
			<span class="label label-info sw-assignment-list-entry-logo-tag">Unassociated</span>
		</div>
		<div style="display: inline-block;" max-width: 280px;>
			<p class="ellipsis"><span class="sw-assignment-update-label">Type:</span> Unassociated Device</p>
# } #
			<p class="ellipsis"><span class="sw-assignment-update-label">#= i18next("public.Assigned") #:</span> #= formattedDate(kendo.parseDate(activeDate)) #</p>
			<p class="ellipsis"><span class="sw-assignment-update-label">#= i18next("public.Released") #:</span> #= formattedDate(kendo.parseDate(releasedDate)) #</p>
			<p class="ellipsis"><span class="sw-assignment-update-label">#= i18next("public.Status") #:</span> #:status#</p>
		</div>
	</div>
	<div style="min-height: 120px; clear: both; border-top: 1px solid \\#eeeeee; padding-top: 15px;">
		<div class="sw-assignment-update-imgwrapper">
			<img class="sw-assignment-update-img" src="#:device.assetImageUrl#"/>
			<span class="label label-info sw-assignment-list-entry-logo-tag">#= i18next("public.Device") #</span>
		</div>
		<p class="ellipsis"><span class="sw-assignment-update-label">#= i18next("public.Device") #:</span> #:device.assetName#</p>
		<p class="ellipsis"><span class="sw-assignment-update-label">#= i18next("public.HardwareId") #:</span> #:device.hardwareId#</p>
	</div>
</script>

<!-- Script support for assignment create dialog -->
<script>
	/** Function called when dialog is submitted */
	var auSubmitCallback;

	/** Provides external access to tabs */
	var auTabs;

	$(document).ready(function() {
		
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