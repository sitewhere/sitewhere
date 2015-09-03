<!-- Dialog for creating a person asset -->
<div id="ac-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 id="ac-dialog-header" data-i18n="assetCategories.dialog.LocationCreateTitle">Create
			Location Asset</h3>
	</div>
	<div class="modal-body">
		<div id="ac-tabs">
			<ul>
				<li class="k-state-active" data-i18n="public.AssetDetails">Asset Details</li>
				<li data-i18n="public.Properties">Properties</li>
			</ul>
			<div>
				<form id="ac-general-form" class="form-horizontal" style="padding-top: 20px;">
					<div class="control-group" id="ac-asset-id-row">
						<label class="control-label" for="ac-asset-id" data-i18n="public.AssetId">Id</label>
						<div class="controls">
							<input type="text" id="ac-asset-id" title="Asset Id" class="input-xlarge">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="ac-asset-name" data-i18n="public.AssetName">Name</label>
						<div class="controls">
							<input type="text" id="ac-asset-name" title="Name" class="input-xlarge">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="ac-asset-image" data-i18n="public.AssetImageUrl">Image
							URL</label>
						<div class="controls">
							<input type="text" id="ac-asset-image" title="Image URL" class="input-xlarge">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="ac-asset-latitude" data-i18n="public.AssetLatitude">Latitude</label>
						<div class="controls">
							<input type="text" id="ac-asset-latitude" title="Latitude" class="input-xlarge">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="ac-asset-longitude" data-i18n="public.AssetLongitude">Longitude</label>
						<div class="controls">
							<input type="text" id="ac-asset-longitude" title="Longitude" class="input-xlarge">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="ac-asset-elevation" data-i18n="public.AssetElevation">Elevation</label>
						<div class="controls">
							<input type="text" id="ac-asset-elevation" title="Elevation" class="input-xlarge">
						</div>
					</div>
					<input id="ac-init-asset-id" type="hidden" value="" />
				</form>
			</div>
			<div>
				<div class="sw-sublist-header">
					<div style="width: 205px;" data-i18n="public.Name"></div>
					<div style="width: 145px" data-i18n="public.Value"></div>
				</div>
				<table id="ac-properties" class="sw-sublist-list" style="height: 190px;"></table>
				<div class="sw-sublist-add-new">
					<div class="sw-sublist-footer">
						<div style="width: 225px; margin-left: 3px;" data-i18n="public.Name"></div>
						<div style="width: 145px" data-i18n="public.Value"></div>
					</div>
					<input type="text" id="ac-property-name"
						style="width: 205px; margin-bottom: 0px; margin-right: 10px;" title="Metadata name"> <input
						type="text" id="ac-property-value"
						style="width: 150px; margin-bottom: 0px; margin-right: 10px;" title="Metadata value">
					<a class="btn" href="javascript:void(0)" onclick="acAddProperty()" data-i18n="public.Add">
						<i class="fa fa-plus sw-button-icon"></i>
					</a>
					<div id="ac-property-error" style="color: #f00; display: none;"></div>
				</div>
			</div>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal" data-i18n="public.Cancel"></a> <a
			id="ac-dialog-submit" href="javascript:void(0)" class="btn btn-primary" data-i18n="public.Create"></a>
	</div>
</div>

<!-- Template for property list entry -->
<script type="text/x-kendo-tmpl" id="tpl-property-entry">
	<tr class="sw-list-entry">
		<td style="width: 205px">#:name#</td>
		<td style="width: 145px">#:value#</td>
		<td>
			<div style="text-align: right;">
				<i class="fa fa-remove sw-action-glyph sw-delete-glyph" title="Delete Property"
					onclick="acDeleteProperty('#:name#')"></i>
			</div>
		</td>
	</tr>
</script>

<!-- Script support for asset create dialog -->
<script>
	/** Function called when dialog is submitted */
	var acSubmitCallback;

	/** Provides external access to tabs */
	var acTabs;

	/** Properties datasource */
	var acPropertiesDS;

	/** Properties list */
	var acProperties;

	/** Add a new property */
	function acAddProperty() {
		// Reset error.
		$("#ac-property-error").hide();
		var error = "";

		// Create property entry.
		var property = {};
		property.name = $("#ac-property-name").val();
		property.value = $("#ac-property-value").val();

		// Check for empty.
		if (property.name.length == 0) {
			error = "Property name can not be empty.";
		}
		var regex = /^[\w-]+$/;
		if (!regex.test(property.name)) {
			error = "Property name includes invalid characters.";
		}

		// Check for already used.
		var data = acPropertiesDS.data();
		for (var index = 0, existing; existing = data[index]; index++) {
			if (property.name == existing.name) {
				error = "Property name already in use.";
				break;
			}
		}
		if (error.length > 0) {
			$("#ac-property-error").html(error);
			$("#ac-property-error").toggle();
		} else {
			acPropertiesDS.data().push(property);
			$("#ac-property-name").val("");
			$("#ac-property-value").val("");
		}
	}

	/** Deletes a property entry by name */
	function acDeleteProperty(name) {
		var data = acPropertiesDS.data();
		for (var index = 0, existing; existing = data[index]; index++) {
			if (existing.name == name) {
				acPropertiesDS.data().splice(index, 1);
				return;
			}
		}
	}

	$(document).ready(
		function() {

			/** Create tab strip */
			acTabs = $("#ac-tabs").kendoTabStrip({
				animation : false
			}).data("kendoTabStrip");

			/** Create properties datasource */
			acPropertiesDS = swMetadataDatasource();

			/** Create properties list */
			acProperties = $("#ac-properties").kendoListView({
				dataSource : acPropertiesDS,
				template : kendo.template($("#tpl-property-entry").html()),
				height : 400,
			}).data("kendoListView");

			$("#ac-asset-latitude").kendoNumericTextBox({
				format : "n6",
				decimals : 6
			});
			$("#ac-asset-longitude").kendoNumericTextBox({
				format : "n6",
				decimals : 6
			});
			$("#ac-asset-elevation").kendoNumericTextBox({
				format : "n6",
				decimals : 6
			});

			/** Handle create dialog submit */
			$('#ac-dialog-submit').click(
				function(event) {
					event.preventDefault();
					if (!acValidate()) {
						return;
					}
					var initAssetId = $('#ac-init-asset-id').val();
					var isCreate = (initAssetId.length == 0);
					var assetData = {
						"id" : $('#ac-asset-id').val(),
						"name" : $('#ac-asset-name').val(),
						"latitude" : $('#ac-asset-latitude').val(),
						"longitude" : $('#ac-asset-longitude').val(),
						"elevation" : $('#ac-asset-elevation').val(),
						"imageUrl" : $('#ac-asset-image').val(),
						"properties" : swMetadataAsLookup(acPropertiesDS.data()),
					}
					if (isCreate) {
						$.postJSON("${pageContext.request.contextPath}/api/assets/categories/" + categoryId
								+ "/locations?tenantAuthToken=${tenant.authenticationToken}", assetData,
							onCreateSuccess, onCreateFail);
					} else {
						$.putJSON("${pageContext.request.contextPath}/api/assets/categories/" + categoryId
								+ "/locations/" + initAssetId
								+ "?tenantAuthToken=${tenant.authenticationToken}", assetData,
							onCreateSuccess, onUpdateFail);
					}
				});

			/** Called on successful create */
			function onCreateSuccess() {
				$('#ac-dialog').modal('hide');
				if (acSubmitCallback != null) {
					acSubmitCallback();
				}
			}

			/** Handle failed call to create asset */
			function onCreateFail(jqXHR, textStatus, errorThrown) {
				handleError(jqXHR, "Unable to create asset.");
			}

			/** Handle failed call to update asset */
			function onUpdateFail(jqXHR, textStatus, errorThrown) {
				handleError(jqXHR, "Unable to update asset.");
			}
		});

	/** Validate everything */
	function acValidate() {
		$.validity.setup({
			outputMode : "label"
		});
		$.validity.start();

		$("#ac-asset-id").require();
		$("#ac-asset-name").require();
		$("#ac-asset-image").require();
		$("#ac-asset-username").require();
		$("#ac-asset-email").require();

		$('#ac-asset-id').match(/^[\w\-]+$/, "Ids are alphanumeric with dashes or underscores.");

		var result = $.validity.end();
		return result.valid;
	}

	/** Open the dialog */
	function acOpen(e, callback) {
		var event = e || window.event;
		event.stopPropagation();

		$('#ac-init-asset-id').val("");
		$('#ac-asset-id-row').show();

		$('#ac-dialog-header').html(i18next("public.Create") + " Location Asset");
		$('#ac-dialog-submit').html(i18next("public.Create"));

		$('#ac-asset-id').val("");
		$('#ac-asset-name').val("");
		$('#ac-asset-latitude').val("");
		$('#ac-asset-longitude').val("");
		$('#ac-asset-elevation').val("");
		$('#ac-asset-image').val("");

		acPropertiesDS.data(new Array());

		// Function called on submit.
		acSubmitCallback = callback;

		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#ac-dialog').modal('show');
	}

	/** Called to edit existing */
	function auOpen(assetId, callback) {
		// Function called on submit.
		acSubmitCallback = callback;

		// Get latest device data for hardware id.
		$.getJSON("${pageContext.request.contextPath}/api/assets/categories/" + categoryId + "/assets/"
				+ assetId + "?tenantAuthToken=${tenant.authenticationToken}", auUpdateGetSuccess,
			auUpdateGetFailed);
	}

	/** Called on successful device load request */
	function auUpdateGetSuccess(data, status, jqXHR) {
		// Indicates an update operation.
		$('#ac-init-asset-id').val(data.id);
		$('#ac-asset-id-row').hide();

		$('#ac-general-form')[0].reset();

		$('#ac-dialog-header').html(i18next("public.Update") + " Location Asset");
		$('#ac-dialog-submit').html(i18next("public.Update"));

		acTabs.select(0);

		$('#ac-asset-id').val(data.id);
		$('#ac-asset-name').val(data.name);
		$('#ac-asset-latitude').val(data.latitude);
		$('#ac-asset-longitude').val(data.longitude);
		$('#ac-asset-elevation').val(data.elevation);
		$('#ac-asset-image').val(data.imageUrl);

		acPropertiesDS.data(swLookupAsMetadata(data.properties));

		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#ac-dialog').modal('show');
	}

	/** Handle error on getting device information */
	function auUpdateGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to get asset for update.");
	}
</script>