<!-- Dialog for invoking a batch command -->
<div id="bci-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal"
			aria-hidden="true">&times;</button>
		<h3>Invoke Batch Command</h3>
	</div>
	<div class="modal-body">
		<div id="bci-tabs">
			<ul>
				<li class="k-state-active" data-i18n="includes.CommandDetails"></li>
				<li data-i18n="public.Metadata"></li>
			</ul>
			<div>
				<form id="bci-form" class="form-horizontal"
					style="padding-top: 15px;">
					<div class="control-group"
						style="border-bottom: 1px solid #eeeeee; padding-bottom: 10px;">
						<label class="control-label" for="ci-command-choice">Command</label>
						<div class="controls">
							<input id="bci-command-choice" />
						</div>
					</div>
					<div id="bci-parameters"></div>
				</form>
			</div>
			<div>
				<div id="bci-metadata">
					<jsp:include page="../includes/metadata.jsp" flush="true">
						<jsp:param name="uid" value="bci" />
					</jsp:include>
				</div>
			</div>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal" data-i18n="public.Cancel"></a>
		<a id="bci-dialog-submit" href="javascript:void(0)"
			class="btn btn-primary" data-i18n="public.Invoke"></a>
	</div>
</div>

<!-- Script support for device create dialog -->
<script>
	/** Criteria for choosing devices */
	var bciCriteria;

	/** Function called when dialog is submitted */
	var bciSubmitCallback;

	/** Provides external access to tabs */
	var bciTabs;

	/** Datasource for specification commands */
	var bciCommandsDS;

	/** Commands dropdown */
	var bciCommandList;

	/** Data for commands list */
	var commands;

	/** Currently selected command */
	var selectedCommand;

	$(document).ready(
			function() {

				/** Create tab strip */
				bciTabs = $("#bci-tabs").kendoTabStrip({
					animation : false
				}).data("kendoTabStrip");

				/** Create AJAX datasource for specifications list */
				bciCommandsDS = new kendo.data.DataSource();

				// Create dropdown for datatypes.
				bciCommandList = $("#bci-command-choice").kendoDropDownList({
					dataTextField : "name",
					dataValueField : "token",
					dataSource : bciCommandsDS,
					change : onCommandChange,
				}).data("kendoDropDownList");

				/** Handle create dialog submit */
				$('#bci-dialog-submit')
						.click(
								function(event) {
									event.preventDefault();
									if (!bciValidate()) {
										return;
									}
									var invocationData = {
										"commandToken" : selectedCommand.token,
										"startDate" : bciCriteria.afterDate,
										"endDate" : bciCriteria.beforeDate,
										"specificationToken" : bciCriteria.specification,
										"groupToken" : bciCriteria.group,
										"groupsWithRole" : bciCriteria.groupsWithRole,
										"excludeAssigned" : bciCriteria.excludeAssigned,
									//				"metadata": swMetadataAsLookup(bciMetadataDS.data()),
									}
									var params = {};
									for (var i = 0; i < selectedCommand.parameters.length; i++) {
										var param = selectedCommand.parameters[i];
										var value = $("#bci-param-" + param.name).val();
										if (param.type == 'Bool') {
											value = ($("#bci-param-" + param.name).is(':checked') ? "true"
													: "false");
										}
										params[param.name] = value;
									}
									invocationData.parameterValues = params;
									$.postJSON(
											"${pageContext.request.contextPath}/api/batch/command/criteria",
											invocationData, onCreateSuccess, onCreateFail);
								});

				/** Called on successful create */
				function onCreateSuccess() {
					$('#bci-dialog').modal('hide');
					if (bciSubmitCallback != null) {
						bciSubmitCallback();
					}
				}

				/** Handle failed call to invoke batch command */
				function onCreateFail(jqXHR, textStatus, errorThrown) {
					handleError(jqXHR, "Unable to invoke batch command.");
				}
			});

	/** Validate everything */
	function bciValidate() {
		$.validity.setup({
			outputMode : "label"
		});
		$.validity.start();

		var result = $.validity.end();
		return result.valid;
	}

	/** Open the dialog */
	function bciOpen(criteria, callback) {
		// Store device criteria.
		bciCriteria = criteria;

		// Function called on submit.
		bciSubmitCallback = callback;

		// Get latest device data for hardware id.
		$.getJSON("${pageContext.request.contextPath}/api/specifications/" + criteria.specification
				+ "/commands", bciCommandsGetSuccess, bciCommandsGetFailed);
	}

	/** Called on successful listing of specfication commands */
	function bciCommandsGetSuccess(data, status, jqXHR) {
		// Reset form and metadata.
		$('#bci-form')[0].reset();
		bciMetadataDS.data(new Array());

		// Load command list into datasource.
		bciCommandsDS.data(data.results);
		commands = data.results;
		if (commands.length > 0) {
			selectedCommand = commands[0];
			showCommandForm(selectedCommand);
		}

		// Select first tab.
		bciTabs.select(0);

		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#bci-dialog').modal('show');
	}

	/** Handle error on listing specification commands */
	function bciCommandsGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to list commands for specification.");
	}

	/** Called when selected command changes */
	function onCommandChange() {
		var value = bciCommandList.value();
		for (var i = 0; i < commands.length; i++) {
			if (commands[i].token == value) {
				selectedCommand = commands[i];
				break;
			}
		}
		showCommandForm(selectedCommand);
	}

	/** Shows the command form for a given command */
	function showCommandForm(command) {
		var html = "";
		for (var i = 0; i < command.parameters.length; i++) {
			var param = command.parameters[i];
			html += "<div class='control-group'>";
			html += "<label class='control-label' for='bci-param-" + param.name + "'>" + param.name
					+ "</label>";
			html += "<div class='controls'>";
			if (param.type == "Bool") {
				html += "<input type='checkbox' id='bci-param-" + param.name + "' name='" + param.name + "'>";
			} else {
				html += "<input type='text' id='bci-param-" + param.name + "' name='" + param.name + "' class='input-xlarge'>";
			}
			html += "</div>";
			html += "</div>";
		}
		$('#bci-parameters').html(html);
	}
</script>