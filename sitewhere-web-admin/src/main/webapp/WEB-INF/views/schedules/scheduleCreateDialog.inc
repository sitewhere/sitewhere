<!-- Dialog for schedule create/update -->
<div id="sc-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal"
			aria-hidden="true">&times;</button>
		<h3 id="sc-dialog-header" data-i18n="schedules.create.title"></h3>
	</div>
	<div class="modal-body">
		<div id="sc-tabs">
			<ul>
				<li class="k-state-active">&nbsp;<font
					data-i18n="schedules.create.detailsTab"></font></li>
				<li>&nbsp;<font data-i18n="public.Metadata"></font></li>
			</ul>
			<div>
				<form id="details-form" class="form-horizontal"
					style="padding-top: 20px;">
					<div class="control-group">
						<label class="control-label" for="sc-name" data-i18n="public.Name"></label>
						<div class="controls">
							<input type="text" id="sc-name" class="input-xlarge"
								title="Schedule name">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="sc-start-date"
							data-i18n="public.StartDate"></label>
						<div class="controls">
							<input type="text" id="sc-start-date" title="Start date">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="sc-end-date"
							data-i18n="public.EndDate"></label>
						<div class="controls">
							<input type="text" id="sc-end-date" title="End date">
						</div>
					</div>
					<div class="control-group"
						style="border-bottom: 1px solid #eeeeee; padding-bottom: 10px;">
						<label class="control-label" for="sc-schedule-type"
							data-i18n="public.Type"></label>
						<div class="controls">
							<input id="sc-schedule-type" />
						</div>
					</div>
					<div id="schedule-forms">
						<div id="sc-simple">
							<div class="control-group">
								<label class="control-label" for="sc-simple-interval"
									data-i18n="schedules.create.simpleInterval"></label>
								<div class="controls">
									<input type="text" id="sc-simple-interval" class="input-large">
								</div>
							</div>
							<div class="control-group">
								<label class="control-label" for="sc-simple-repeat"
									data-i18n="schedules.create.simpleRepeat"></label>
								<div class="controls">
									<input type="text" id="sc-simple-repeat" class="input-large">
								</div>
							</div>
						</div>
						<div id="sc-cron" class="hide">
							<div class="control-group">
								<label class="control-label" for="sc-cron-expression"
									data-i18n="schedules.create.cronExpression"></label>
								<div class="controls">
									<input type="text" id="sc-cron-expression" class="input-large">
								</div>
							</div>
						</div>
					</div>
				</form>
			</div>
			<div>
				<div id="sc-metadata">
					<!-- #set ($uid = "sc") -->
					#parse ("includes/metadata.inc")
				</div>
			</div>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal"
			data-i18n="public.Cancel"></a> <a id="sc-dialog-submit"
			href="javascript:void(0)" class="btn btn-primary"
			data-i18n="public.Create"></a>
	</div>
	<input id="schedule-token" type="hidden" value="" />
</div>

<!-- Script support for schedule create/update dialog -->
<script>
	/** Function called when dialog is submitted */
	var scSubmitCallback;

	/** Pointer to tabs instance */
	var scTabs;

	/** Available schedule types shown in dropdown */
	var scScheduleTypes = [ {
		text : "Simple Schedule",
		value : "sc-simple"
	}, {
		text : "Cron Schedule",
		value : "sc-cron"
	}, ];

	/** Schedule types dropdown */
	var schedTypesDD;

	/** Schedule start and end date widgets */
	var startDate, endDate;

	$(document).ready(
			function() {

				scTabs = $("#sc-tabs").kendoTabStrip({
					animation : false
				}).data("kendoTabStrip");

				schedTypesDD = $("#sc-schedule-type").kendoDropDownList({
					dataTextField : "text",
					dataValueField : "value",
					dataSource : scScheduleTypes,
					index : 0,
					change : scScheduleTypeChanged
				}).data("kendoDropDownList");

				startDate = $("#sc-start-date").kendoDateTimePicker({
					value : null
				}).data("kendoDateTimePicker");

				endDate = $("#sc-end-date").kendoDateTimePicker({
					value : null
				}).data("kendoDateTimePicker");

				/** Handle dialog submit */
				$('#sc-dialog-submit').click(
						function(event) {
							event.preventDefault();
							if (!scValidate()) {
								return;
							}
							var schedToken = $('#schedule-token').val();
							var schedData = scCreateScheduleData();
							if (schedToken == "") {
								$.postAuthJSON("${request.contextPath}/api/schedules", schedData, "${basicAuth}",
										"${tenant.authenticationToken}", onSuccess, onCreateFail);
							} else {
								schedData.token = schedToken;
								$.putAuthJSON("${request.contextPath}/api/schedules/" + schedToken, schedData,
										"${basicAuth}", "${tenant.authenticationToken}", onSuccess, onUpdateFail);
							}
						});

				/** Called on successful create/update */
				function onSuccess() {
					$('#sc-dialog').modal('hide');
					if (scSubmitCallback != null) {
						scSubmitCallback();
					}
				}

				/** Handle failed call to create */
				function onCreateFail(jqXHR, textStatus, errorThrown) {
					handleError(jqXHR, "Unable to create schedule.");
				}

				/** Handle failed call to update */
				function onUpdateFail(jqXHR, textStatus, errorThrown) {
					handleError(jqXHR, "Unable to update schedule.");
				}
			});

	/** Create schedule data structure based on form input */
	function scCreateScheduleData() {
		var triggerType = scGetTriggerType();
		var triggerConfig = scBuildScheduleConfiguration();
		var schedData = {
			"name" : $('#sc-name').val(),
			"triggerType" : triggerType,
			"triggerConfiguration" : triggerConfig,
			"metadata" : swMetadataAsLookup(scMetadataDS.data()),
		}
		var startDateStr = $('#sc-start-date').val();
		if (startDateStr != "") {
			var startDate = kendo.parseDate(startDateStr);
			schedData.startDate = startDate.toISOString();
		}
		var endDateStr = $('#sc-end-date').val();
		if (endDateStr != "") {
			var endDate = kendo.parseDate(endDateStr);
			schedData.endDate = endDate.toISOString();
		}
		return schedData;
	}

	/** Validate everything */
	function scValidate() {
		$.validity.setup({
			outputMode : "label"
		});
		$.validity.start();

		/** Validate main form */
		$("#sc-name").require();

		var triggerType = scGetTriggerType();

		/** Validate simple trigger form */
		if (triggerType == "SimpleTrigger") {
			$("#gs-base-url").require();
		}

		/** Validate cron trigger form */
		else if (triggerType == "CronTrigger") {
			$("#gs-base-url").require();
		}

		var result = $.validity.end();
		return result.valid;
	}

	/** Open the site create dialog */
	function scOpen(e, callback) {
		scClear();
		$('#sc-dialog-header').html(i18next("schedules.create.title"));
		$('#sc-dialog-submit').html(i18next("public.Create"));

		$('#schedule-token').val("");

		// Function called on submit.
		scSubmitCallback = callback;

		$.validity.clear();
		$('#sc-dialog').modal('show');
	}

	/** Clear fields and reset UI */
	function scClear() {
		scMetadataDS.data(new Array());
		scTabs.select(0);
		$('#sc-name').val("");
		$('#sc-start-date').val("");
		$('#sc-end-date').val("");
		$('#sc-simple-interval').val("");
		$('#sc-simple-repeat').val("");
		$('#sc-cron-expression').val("");
		schedTypesDD.select(0);
	}

	/** Called to load the given schedule and open it for edit */
	function suOpen(scheduleToken, callback) {
		// Function called on submit.
		scSubmitCallback = callback;

		// Get site for edit.
		$.getAuthJSON("${request.contextPath}/api/schedules/" + scheduleToken, "${basicAuth}",
				"${tenant.authenticationToken}", suScheduleGetSuccess, suScheduleGetFailed);
	}

	/** Called on successful schedule load request */
	function suScheduleGetSuccess(data, status, jqXHR) {
		scClear();
		$('#sc-dialog-header').html(i18next("schedules.update.title"));
		$('#sc-dialog-submit').html(i18next("public.Save"));

		parseScheduleData(data);
		$('#schedule-token').val(data.token);
		if (data.startDate) {
			startDate.value(data.startDate);
		}
		if (data.endDate) {
			endDate.value(data.endDate);
		}

		scMetadataDS.data(swLookupAsMetadata(data.metadata));
		scSelectScheduleType(data);
		scLoadScheduleFormFromMetadata(data);

		$.validity.clear();
		$('#sc-dialog').modal('show');
	}

	/** Handle error on getting site */
	function suScheduleGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load schedule for edit.");
	}

	/** Based on map type, load fields into proper form */
	function scLoadScheduleFormFromMetadata(schedule) {
		var config = schedule.triggerConfiguration;

		$("#sc-name").val(schedule.name);
		if (schedule.triggerType == "SimpleTrigger") {
			$("#sc-simple-repeat").val(config['repeatCount']);
			$("#sc-simple-interval").val(config['repeatInterval']);
		} else if (schedule.triggerType == "CronTrigger") {
			$("#sc-cron-expression").val(config['cronExpression']);
		}
	}

	/** Gets the value associated with a given field name */
	function scGetMetadataValue(lookup, field) {
		if (lookup && lookup[field]) {
			return lookup[field];
		}
		return "";
	}

	/** Pull data from map form and populate metadata */
	function scBuildScheduleConfiguration() {
		var config = {};
		var scheduleType = scGetScheduleType();

		if (scheduleType == "sc-simple") {
			if ($("#sc-simple-repeat").val() != "") {
				config['repeatCount'] = $("#sc-simple-repeat").val();
			}
			if ($("#sc-simple-interval").val() != "") {
				config['repeatInterval'] = $("#sc-simple-interval").val();
			}
		} else if (scheduleType == "sc-cron") {
			config['cronExpression'] = $("#sc-cron-expression").val();
		}
		return config;
	}

	/** Select the given schedule type and show its associated panel */
	function scSelectScheduleType(schedule) {
		var type = (schedule.triggerType == 'CronTrigger' ? 'sc-cron' : 'sc-simple');
		schedTypesDD.value(type);
		scScheduleTypeChanged();
	}

	/** Get the currently selected schedule type */
	function scGetScheduleType() {
		return $("#sc-schedule-type").val();
	}

	/** Get the currently selected schedule type */
	function scGetTriggerType() {
		var selected = $("#sc-schedule-type").val();
		return (selected == 'sc-cron' ? "CronTrigger" : "SimpleTrigger");
	}

	/** Called when schedule type dropdown value changes */
	function scScheduleTypeChanged() {
		var selectedSchedType = scGetScheduleType();
		$("#schedule-forms").children().each(function(i) {
			if (selectedSchedType == $(this).attr("id")) {
				$(this).removeClass("hide");
			} else if (!$(this).hasClass("hide")) {
				$(this).addClass("hide");
			}
		});
	}
</script>