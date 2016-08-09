<!-- Dialog used to search for a single tenant -->
<div id="tsr-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal"
			aria-hidden="true">&times;</button>
		<h3 id="dvs-dialog-header">Tenant Search</h3>
	</div>
	<div class="modal-body">
		<div>
			<form id="tsr-general-form" class="form-inline"
				style="padding-top: 20px; margin-left: 20px;">
				<div>
					<label style="margin-right: 10px;" for="tsr-search-criteria">Search
						(name or id)</label> <input type="text" id="tsr-search-criteria"
						class="input-xlarge"> <input id="btn-search-tenants"
						class="btn btn-default" type="button" value="Search">
				</div>
				<input id="tsr-selected-tenant-id" type="hidden" />
			</form>
			<table id="tsr-tenant-matches">
				<colgroup>
					<col style="width: 35%;" />
					<col style="width: 65%;" />
				</colgroup>
				<thead>
					<tr>
						<th data-i18n="public.TenantId">Tenant Id</th>
						<th data-i18n="public.Name">Name</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td colspan="2"></td>
					</tr>
				</tbody>
			</table>
			<div id="tsr-tenant-matches-pager" class="k-pager-wrap"></div>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal"
			data-i18n="public.Cancel"></a> <a id="tsr-dialog-submit"
			href="javascript:void(0)" class="btn btn-primary">Select</a>
	</div>
</div>

<!-- Script support for tenant element add dialog -->
<script>
	/** Function called when dialog is submitted */
	var tsrSubmitCallback;

	/** Datasource for tenant matches */
	var tsrMatchesDS;

	/** Tenant matches list view */
	var tsrMatches;

	/** Last tenant search value */
	var tsrLastSearch = "";

	$(document)
			.ready(
					function() {

						/** Create AJAX datasource for devices list */
						tsrMatchesDS = new kendo.data.DataSource(
								{
									transport : {
										read : {
											url : "${pageContext.request.contextPath}/api/tenants",
											beforeSend : function(req) {
												req.setRequestHeader(
														'Authorization',
														"Basic ${basicAuth}");
												req
														.setRequestHeader(
																'X-SiteWhere-Tenant',
																"${tenant.authenticationToken}");
											},
											dataType : "json",
										}
									},
									schema : {
										data : "results",
										total : "numResults",
									},
									serverPaging : true,
									serverSorting : true,
									pageSize : 10,
								});

						/** Create the list of matching tenants */
						tsrMatches = $("#tsr-tenant-matches").kendoGrid(
								{
									dataSource : tsrMatchesDS,
									selectable : "single",
									rowTemplate : kendo.template($(
											"#tpl-tenant-entry-small").html()),
									scrollable : true,
									height : 250,
									change : tsrTenantChosen
								}).data("kendoGrid");

						/** Handle add tenant functionality */
						$('#btn-search-tenants').click(function(event) {
							tsrUpdateTenantSearchResults();
						});

						/** Pager for device list */
						$("#tsr-tenant-matches-pager").kendoPager({
							dataSource : tsrMatchesDS
						});

						/** Handle create dialog submit */
						$('#tsr-dialog-submit').click(
								function(event) {
									event.preventDefault();
									if (!tsrValidate()) {
										return;
									}
									$('#tsr-dialog').modal('hide');
									if (tsrSubmitCallback != null) {
										tsrSubmitCallback($(
												'#tsr-selected-tenant-id')
												.val());
									}
								});
					});

	/** Perform validation */
	function tsrValidate() {
		$.validity.setup({
			outputMode : "label"
		});
		$.validity.start();

		/** Validate fields */
		$("#tsr-selected-tenant-id").require();

		var result = $.validity.end();
		return result.valid;
	}

	/** Called when tenant selection changes */
	function tsrTenantChosen() {
		var listView = tsrMatchesDS.view();
		var selected = $.map(this.select(), function(item) {
			return listView[$(item).index()];
		});

		if (selected.length > 0) {
			$('#tsr-selected-tenant-id').val(selected[0].id);
		} else {
			$('#tsr-selected-tenant-id').val("");
		}
	}

	/** Update tenant results based on criteria */
	function tsrUpdateTenantSearchResults() {
		var criteria = $('#tsr-search-criteria').val();
		if (criteria != tsrLastSearch) {
			var url = "${pageContext.request.contextPath}/api/tenants?textSearch="
					+ criteria;
			tsrMatchesDS.transport.options.read.url = url;
			tsrMatchesDS.read();
		}
		tsrLastSearch = criteria;
	}

	/** Open the dialog */
	function tsrOpen(callback) {
		// Reset selection.
		$('#tsr-selected-tenant-id').val("");

		// Clear existing selection.
		tsrMatches.clearSelection();

		// Function called on submit.
		tsrSubmitCallback = callback;

		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#tsr-dialog').modal('show');
	}
</script>