<!-- Dialog for visually editing tenant configuration -->
<style>
div.bx-wrapper {
	margin: 25px 20px;
}

#tve-config-editor div.bx-viewport {
	box-shadow: none;
	background-color: #eee;
}
</style>
<div id="tve-dialog" class="modal hide" style="width: 800px; margin: -300px 0 0 -400px;">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 id="tve-dialog-header" data-i18n="tenants.editor.header">Edit Tenant Configuration</h3>
	</div>
	<div class="modal-body">
		<div id="tve-tabs">
			<ul>
				<li class="k-state-active" data-i18n="tenants.editor.configuration">Tenant Configuration</li>
			</ul>
			<div>
				<div id="tve-config-editor">
					<ul id="tve-config-pages" class="bxslider">
					</ul>
				</div>
			</div>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal" data-i18n="public.Cancel"></a> <a
			id="tve-dialog-submit" href="javascript:void(0)" class="btn btn-primary"
			data-i18n="tenant.editor.stage">Stage Updates</a>
	</div>
</div>

<!-- Script support for tenant create dialog -->
<script>
	/** Function called when dialog is submitted */
	var tveSubmitCallback;

	/** Provides external access to tabs */
	var tveTabs;

	/** Configuration being edited */
	var config;

	$(document).ready(function() {

		/** Create tab strip */
		tveTabs = $("#tve-tabs").kendoTabStrip({
			animation : false
		}).data("kendoTabStrip");

		/** Handle dialog submit */
		$('#tve-dialog-submit').click(function(event) {
			event.preventDefault();
		});
	});

	/** Open the dialog */
	function tveOpen(config, callback) {

		// Indicates an update operation.
		$('#tc-tenant-id').val("");
		$('#tc-tenant-id-row').show();

		$('#tc-dialog-header').html(i18next("public.Create") + " Tenant");
		$('#tc-dialog-submit').html(i18next("public.Create"));

		$('#tc-tenant-name').val("");
		$('#tc-tenant-logo').val("");
		$('#tc-tenant-auth-token').val("");
		$('#tc-configuration-type').select(0);

		// Function called on submit.
		tcSubmitCallback = callback;

		// Clear user values.
		tcAuthUsersChooser.value([]);

		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#tc-dialog').modal('show');
	}

	/** Called to edit existing tenant */
	function tveOpen(tenantId, callback) {
		// Function called on submit.
		tveSubmitCallback = callback;

		// Get latest device data for hardware id.
		$.getJSON("${pageContext.request.contextPath}/api/tenants/" + tenantId
				+ "/engine/configuration/json?tenantAuthToken=${tenant.authenticationToken}",
			jsonConfigGetSuccess, jsonConfigGetFailed);
	}

	/** Called on successful configuration load request */
	function jsonConfigGetSuccess(data, status, jqXHR) {
		config = data;

		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#tve-dialog').modal('show');
	}

	/** Handle error on getting configuration data */
	function jsonConfigGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load tenant configuration as JSON.");
	}
</script>