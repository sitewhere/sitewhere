<!-- Dialog for creating or updating a tenant group -->
<div id="tgc-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal"
			aria-hidden="true">&times;</button>
		<h3 id="tgc-dialog-header" data-i18n="includes.CreateTenantGroup">Create
			Tenant Group</h3>
	</div>
	<div class="modal-body">
		<div id="tgc-tabs">
			<ul>
				<li class="k-state-active" data-i18n="includes.TenantDetails">Group
					Details</li>
			</ul>
			<div>
				<form id="tgc-general-form" class="form-horizontal"
					style="padding-top: 20px;">
					<div class="control-group" id="tgc-tenant-group-token-row">
						<label class="control-label" for="tgc-tgroup-token"
							data-i18n="public.TenantGroupId">Token</label>
						<div class="controls">
							<span id="tgc-tgroup-token" style="line-height: 2.0em;"
								title="Tenant Group Token"></span>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="tgc-tgroup-name"
							data-i18n="public.Name">Name</label>
						<div class="controls">
							<input type="text" id="tgc-tgroup-name" title="Tenant Group Name"
								class="input-xlarge">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="tgc-tgroup-description"
							data-i18n="public.Description">Description</label>
						<div class="controls">
							<textarea id="tgc-tgroup-description" class="input-xlarge"
								style="height: 10em;"></textarea>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="tgc-tgroup-image-url"
							data-i18n="public.TenantGroupImageUrl">Image URL</label>
						<div class="controls">
							<input type="text" id="tgc-tgroup-image-url" title="Image URL"
								class="input-xlarge">
						</div>
					</div>
					<input id="tgc-init-tgroup-token" type="hidden" value="" />
				</form>
			</div>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal"
			data-i18n="public.Cancel"></a> <a id="tgc-dialog-submit"
			href="javascript:void(0)" class="btn btn-primary"
			data-i18n="public.Create"></a>
	</div>
</div>

<!-- Script support for tenant group create dialog -->
<script>
	/** Function called when dialog is submitted */
	var tgcSubmitCallback;

	/** Provides external access to tabs */
	var tgcTabs;

	$(document).ready(
			function() {

				/** Create tab strip */
				tgcTabs = $("#tgc-tabs").kendoTabStrip({
					animation : false
				}).data("kendoTabStrip");

				/** Handle dialog submit */
				$('#tgc-dialog-submit')
						.click(
								function(event) {
									event.preventDefault();
									if (!tgcValidate()) {
										return;
									}
									var initTenantGroupToken = $('#tgc-init-tgroup-token').val();
									var isCreate = (initTenantGroupToken.length == 0);
									var tgroupData = {
										"name" : $('#tgc-tgroup-name').val(),
										"description" : $('#tgc-tgroup-description').val(),
										"imageUrl" : $('#tgc-tgroup-image-url').val(),
									}
									if (isCreate) {
										$.postAuthJSON("${request.contextPath}/api/tgroups", tgroupData,
												"${basicAuth}", "${tenant.authenticationToken}", onCreateSuccess,
												onCreateFail);
									} else {
										$.putAuthJSON("${request.contextPath}/api/tgroups/" + initTenantGroupToken,
												tgroupData, "${basicAuth}", "${tenant.authenticationToken}",
												onCreateSuccess, onUpdateFail);
									}
								});

				/** Called on successful create */
				function onCreateSuccess() {
					$('#tgc-dialog').modal('hide');
					if (tgcSubmitCallback != null) {
						tgcSubmitCallback();
					}
				}

				/** Handle failed create */
				function onCreateFail(jqXHR, textStatus, errorThrown) {
					handleError(jqXHR, "Unable to create tenant group.");
				}

				/** Handle failed update */
				function onUpdateFail(jqXHR, textStatus, errorThrown) {
					handleError(jqXHR, "Unable to update tenant group.");
				}
			});

	/** Validate everything */
	function tgcValidate() {
		$.validity.setup({
			outputMode : "label"
		});
		$.validity.start();

		$("#tgc-tgroup-name").require();
		$("#tgc-image-url").require();

		var result = $.validity.end();
		return result.valid;
	}

	/** Open the dialog */
	function tgcOpen(e, callback) {
		var event = e || window.event;
		event.stopPropagation();

		// Indicates a create operation.
		$('#tgc-tgroup-token').html("");
		$('#tgc-init-tgroup-token').val("");
		$('#tgc-tenant-group-token-row').hide();

		$('#tgc-dialog-header').html(i18next("public.Create") + " Tenant Group");
		$('#tgc-dialog-submit').html(i18next("public.Create"));

		$('#tgc-tgroup-name').val("");
		$('#tgc-tgroup-description').val("");
		$('#tgc-tgroup-image-url').val("");

		// Function called on submit.
		tgcSubmitCallback = callback;

		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#tgc-dialog').modal('show');
	}

	/** Called to edit existing */
	function tguOpen(groupId, callback) {
		// Function called on submit.
		tgcSubmitCallback = callback;

		// Get latest device data for hardware id.
		$.getAuthJSON("${request.contextPath}/api/tgroups/" + groupId, "${basicAuth}", "${tenant.authenticationToken}",
				tguUpdateGetSuccess, tguUpdateGetFailed);
	}

	/** Called on successful tenant group load request */
	function tguUpdateGetSuccess(data, status, jqXHR) {
		// Indicates an update operation.
		$('#tgc-tgroup-token').html(data.token);
		$('#tgc-init-tgroup-token').val(data.token);
		$('#tgc-tenant-group-token-row').show();

		$('#tgc-dialog-header').html(i18next("public.Update") + " Tenant Group");
		$('#tgc-dialog-submit').html(i18next("public.Update"));

		tgcTabs.select(0);

		$('#tgc-tgroup-name').val(data.name);
		$('#tgc-tgroup-description').val(data.description);
		$('#tgc-tgroup-image-url').val(data.imageUrl);

		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#tgc-dialog').modal('show');
	}

	/** Handle error on getting tenant group information */
	function tguUpdateGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to get tenant group for update.");
	}
</script>