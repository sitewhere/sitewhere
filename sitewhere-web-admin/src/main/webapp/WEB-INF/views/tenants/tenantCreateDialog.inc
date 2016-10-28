<!-- Dialog for creating or updating a tenant -->
<div id="tc-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal"
			aria-hidden="true">&times;</button>
		<h3 id="tc-dialog-header" data-i18n="includes.CreateTenant">Create
			Tenant</h3>
	</div>
	<div class="modal-body">
		<div id="tc-tabs">
			<ul>
				<li class="k-state-active" data-i18n="includes.TenantDetails">Tenant
					Details</li>
			</ul>
			<div>
				<form id="tc-general-form" class="form-horizontal"
					style="padding-top: 20px;">
					<div class="control-group" id="tc-tenant-id-row">
						<label class="control-label" for="tc-tenant-id"
							data-i18n="public.TenantId">Tenant Id</label>
						<div class="controls">
							<input type="text" id="tc-tenant-id" title="Tenant Id"
								class="input-xlarge">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="tc-tenant-name"
							data-i18n="public.TenantName">Tenant Name</label>
						<div class="controls">
							<input type="text" id="tc-tenant-name" title="Tenant Name"
								class="input-xlarge">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="tc-tenant-logo"
							data-i18n="public.TenantLogo">Logo URL</label>
						<div class="controls">
							<input type="text" id="tc-tenant-logo" title="Logo URL"
								class="input-xlarge">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="tc-tenant-auth-token"
							data-i18n="public.TenantAuthToken">Authentication Token</label>
						<div class="controls">
							<input type="text" id="tc-tenant-auth-token"
								title="Authentication Token" class="input-xlarge">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="tc-tenant-auth-users"
							data-i18n="public.TenantAuthUsers">Authorized Users</label>
						<div class="controls">
							<select id="tc-auth-users"></select>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="tc-configuration-type"
							data-i18n="public.TenantConfigType">Configuration Type</label>
						<div class="controls">
							<select id="tc-configuration-type"></select>
						</div>
					</div>
					<input id="tc-init-tenant-id" type="hidden" value="" />
				</form>
			</div>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal"
			data-i18n="public.Cancel"></a> <a id="tc-dialog-submit"
			href="javascript:void(0)" class="btn btn-primary"
			data-i18n="public.Create"></a>
	</div>
</div>

<!-- Script support for tenant create dialog -->
<script>
	/** Function called when dialog is submitted */
	var tcSubmitCallback;

	/** Provides external access to tabs */
	var tcTabs;

	/** Dropdown with list of asset types */
	var tcConfigTypesDropdown;

	/** Chooser for picking authorized users */
	var tcAuthUsersChooser;

	$(document).ready(
			function() {

				/** Available asset types shown in dropdown */
				var ccConfigTypes = [ {
					text : "Default",
					value : "default"
				} ];

				/** Create tab strip */
				tcTabs = $("#tc-tabs").kendoTabStrip({
					animation : false
				}).data("kendoTabStrip");

				tcConfigTypesDropdown = $("#tc-configuration-type").kendoDropDownList({
					dataTextField : "text",
					dataValueField : "value",
					dataSource : ccConfigTypes,
				}).data("kendoDropDownList");

				/** Chooser for authorized users */
				tcAuthUsersChooser = $("#tc-auth-users").kendoMultiSelect({
					placeholder : "Select users...",
					dataTextField : "username",
					dataValueField : "username",
					dataSource : {
						transport : {
							read : {
								url : "${request.contextPath}/api/users",
								beforeSend : function(req) {
									req.setRequestHeader('Authorization', "Basic ${basicAuth}");
									req.setRequestHeader('X-SiteWhere-Tenant', "${tenant.authenticationToken}");
								},
								dataType : "json",
							}
						},
						schema : {
							data : "results",
							total : "numResults",
							parse : function(response) {
								$.each(response.results, function(index, item) {
									parseUserData(item);
								});
								return response;
							}
						},
						pageSize : 10
					},
				}).data("kendoMultiSelect");

				/** Handle dialog submit */
				$('#tc-dialog-submit')
						.click(
								function(event) {
									event.preventDefault();
									if (!tcValidate()) {
										return;
									}
									var initTenantId = $('#tc-init-tenant-id').val();
									var isCreate = (initTenantId.length == 0);
									var configType = $('#tc-configuration-type').val();
									var tenantData = {
										"id" : $('#tc-tenant-id').val(),
										"name" : $('#tc-tenant-name').val(),
										"logoUrl" : $('#tc-tenant-logo').val(),
										"authenticationToken" : $('#tc-tenant-auth-token').val(),
										"authorizedUserIds" : tcAuthUsersChooser.value(),
									}
									if (isCreate) {
										$.postAuthJSON("${request.contextPath}/api/tenants", tenantData,
												"${basicAuth}", "${tenant.authenticationToken}", onCreateSuccess,
												onCreateFail);
									} else {
										$.putAuthJSON("${request.contextPath}/api/tenants/" + initTenantId, tenantData,
												"${basicAuth}", "${tenant.authenticationToken}", onCreateSuccess,
												onUpdateFail);
									}
								});

				/** Called on successful create */
				function onCreateSuccess() {
					$('#tc-dialog').modal('hide');
					if (tcSubmitCallback != null) {
						tcSubmitCallback();
					}
				}

				/** Handle failed call to create tenant */
				function onCreateFail(jqXHR, textStatus, errorThrown) {
					handleError(jqXHR, "Unable to create tenant.");
				}

				/** Handle failed call to update tenant */
				function onUpdateFail(jqXHR, textStatus, errorThrown) {
					handleError(jqXHR, "Unable to update tenant.");
				}
			});

	/** Validate everything */
	function tcValidate() {
		$.validity.setup({
			outputMode : "label"
		});
		$.validity.start();

		$("#tc-tenant-id").require();
		$("#tc-tenant-name").require();
		$("#tc-tenant-logo").require();
		$("#tc-tenant-auth-token").require();

		$('#tc-tenant-id').match(/^[\w\-]+$/, "Ids are alphanumeric with dashes or underscores.");

		var result = $.validity.end();
		return result.valid;
	}

	/** Open the dialog */
	function tcOpen(e, callback) {
		var event = e || window.event;
		event.stopPropagation();

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
	function tuOpen(tenantId, callback) {
		// Function called on submit.
		tcSubmitCallback = callback;

		// Get latest device data for hardware id.
		$.getAuthJSON("${request.contextPath}/api/tenants/" + tenantId, "${basicAuth}",
				"${tenant.authenticationToken}", tuUpdateGetSuccess, tuUpdateGetFailed);
	}

	/** Called on successful tenant load request */
	function tuUpdateGetSuccess(data, status, jqXHR) {
		// Indicates an update operation.
		$('#tc-init-tenant-id').val(data.id);
		$('#tc-tenant-id-row').hide();

		$('#tc-dialog-header').html(i18next("public.Update") + " Tenant");
		$('#tc-dialog-submit').html(i18next("public.Update"));

		tcTabs.select(0);

		$('#tc-tenant-id').val(data.id);
		$('#tc-tenant-name').val(data.name);
		$('#tc-tenant-logo').val(data.logoUrl);
		$('#tc-tenant-auth-token').val(data.authenticationToken);

		// Set auth user values.
		tcAuthUsersChooser.value(data.authorizedUserIds);

		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#tc-dialog').modal('show');
	}

	/** Handle error on getting tenant information */
	function tuUpdateGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to get tenant for update.");
	}
</script>