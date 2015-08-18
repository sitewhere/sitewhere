<!-- Dialog for creating an asset category -->
<div id="cc-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 data-i18n="includes.CreateAssetCategory">Create Asset Category</h3>
	</div>
	<div class="modal-body">
		<div id="cc-tabs">
			<ul>
				<li class="k-state-active" data-i18n="includes.AssetCategoryDetails">Category Details</li>
			</ul>
			<div>
				<form id="cc-general-form" class="form-horizontal" style="padding-top: 20px;">
					<div class="control-group">
						<label class="control-label" for="cc-category-id" data-i18n="public.CategoryId">Category
							Id</label>
						<div class="controls">
							<input type="text" id="cc-category-id" title="Category Id" class="input-xlarge">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="cc-category-name" data-i18n="public.CategoryId">Category
							Name</label>
						<div class="controls">
							<input type="text" id="cc-category-name" title="Category Name" class="input-xlarge">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="cc-asset-type" data-i18n="public.AssetType">Asset
							Type</label>
						<div class="controls">
							<select id="cc-asset-type"></select>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal" data-i18n="public.Cancel"></a> <a
			id="cc-dialog-submit" href="javascript:void(0)" class="btn btn-primary" data-i18n="public.Create"></a>
	</div>
</div>

<!-- Script support for asset category create dialog -->
<script>
	/** Function called when dialog is submitted */
	var ccSubmitCallback;

	/** Provides external access to tabs */
	var ccTabs;

	/** Dropdown with list of asset types */
	var ccAssetTypesDropdown;

	$(document).ready(
		function() {

			/** Available asset types shown in dropdown */
			var ccAssetTypes = [ {
				text : "Device",
				value : "Device"
			}, {
				text : "Hardware",
				value : "Hardware"
			}, {
				text : "Person",
				value : "Person"
			}, {
				text : "Location",
				value : "Location"
			}, ];

			/** Create tab strip */
			ccTabs = $("#cc-tabs").kendoTabStrip({
				animation : false
			}).data("kendoTabStrip");

			ccAssetTypesDropdown = $("#cc-asset-type").kendoDropDownList({
				dataTextField : "text",
				dataValueField : "value",
				dataSource : ccAssetTypes,
			}).data("kendoDropDownList");

			/** Handle create dialog submit */
			$('#cc-dialog-submit').click(
				function(event) {
					event.preventDefault();
					if (!ccValidate()) {
						return;
					}
					var categoryData = {
						"id" : $('#cc-category-id').val(),
						"name" : $('#cc-category-name').val(),
						"assetType" : $('#cc-asset-type').val(),
					}
					$.postJSON("${pageContext.request.contextPath}/api/assets/categories", categoryData,
						onCreateSuccess, onCreateFail);
				});

			/** Called on successful create */
			function onCreateSuccess() {
				$('#cc-dialog').modal('hide');
				if (ccSubmitCallback != null) {
					ccSubmitCallback();
				}
			}

			/** Handle failed call to create device */
			function onCreateFail(jqXHR, textStatus, errorThrown) {
				handleError(jqXHR, "Unable to create asset category.");
			}
		});

	/** Validate everything */
	function ccValidate() {
		$.validity.setup({
			outputMode : "label"
		});
		$.validity.start();

		$("#cc-category-id").require();
		$("#cc-category-name").require();

		var result = $.validity.end();
		return result.valid;
	}

	/** Open the dialog */
	function ccOpen(e, callback) {
		var event = e || window.event;
		event.stopPropagation();

		$('#cc-category-id').val("");
		$('#cc-category-name').val("");
		$('#cc-asset-type').select(0);

		// Function called on submit.
		ccSubmitCallback = callback;

		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#cc-dialog').modal('show');
	}
</script>