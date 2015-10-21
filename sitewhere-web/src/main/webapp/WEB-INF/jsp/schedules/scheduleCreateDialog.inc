<!-- Dialog for schedule create/update -->
<div id="sc-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 id="sc-dialog-header" data-i18n="schedules.create.title"></h3>
	</div>
	<div class="modal-body">
		<div id="sc-tabs">
			<ul>
				<li class="k-state-active">&nbsp;<font data-i18n="schedules.create.detailsTab"></font></li>
				<li>&nbsp;<font data-i18n="public.Metadata"></font></li>
			</ul>
			<div>
				<form id="details-form" class="form-horizontal" style="padding-top: 20px;">
					<div class="control-group">
						<label class="control-label" for="sc-name" data-i18n="public.Name"></label>
						<div class="controls">
							<input type="text" id="sc-name" class="input-xlarge" title="Schedule name">
						</div>
					</div>
					<div class="control-group" style="border-bottom: 1px solid #eeeeee; padding-bottom: 10px;">
						<label class="control-label" for="sc-schedule-type" data-i18n="public.Type"></label>
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
					<jsp:include page="../includes/metadata.jsp" flush="true">
						<jsp:param name="uid" value="sc" />
					</jsp:include>
				</div>
			</div>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal" data-i18n="public.Cancel"></a> <a
			id="sc-dialog-submit" href="javascript:void(0)" class="btn btn-primary" data-i18n="public.Create"></a>
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

	$(document)
			.ready(
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

					/** Handle dialog submit */
					$('#sc-dialog-submit')
							.click(
								function(event) {
									event.preventDefault();
									if (!scValidate()) {
										return;
									}
									var schedToken = $('#schedule-token').val();
									var schedData = scCreateScheduleData();
									if (schedToken == "") {
										$
												.postJSON(
													"${pageContext.request.contextPath}/api/schedules?tenantAuthToken=${tenant.authenticationToken}",
													schedData, onSuccess, onCreateFail);
									} else {
										$.putJSON("${pageContext.request.contextPath}/api/schedules/"
												+ schedToken
												+ "?tenantAuthToken=${tenant.authenticationToken}",
											schedData, onSuccess, onUpdateFail);
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
		var configData = scBuildConfig();
		var triggerType = scGetTriggerType();
		var schedData = {
			"name" : $('#sc-name').val(),
			"triggerType" : triggerType,
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
		$('#sc-dialog-header').html(i18next("schedules.create.title"));
		$('#sc-dialog-submit').html(i18next("public.Create"));

		$('#sched-token').val("");
		scMetadataDS.data(new Array());
		scTabs.select(0);

		// Function called on submit.
		scSubmitCallback = callback;

		$.validity.clear();
		$('#sc-dialog').modal('show');
	}

	/** Called to load the given schedule and open it for edit */
	function suOpen(scheduleToken, callback) {
		// Function called on submit.
		scSubmitCallback = callback;

		// Get site for edit.
		$
				.getJSON("${pageContext.request.contextPath}/api/schedules/" + scheduleToken
						+ "?tenantAuthToken=${tenant.authenticationToken}", suScheduleGetSuccess,
					suScheduleGetFailed);
	}

	/** Called on successful schedule load request */
	function suScheduleGetSuccess(data, status, jqXHR) {
		$('#sc-dialog-header').html(i18next("schedules.update.title"));
		$('#sc-dialog-submit').html(i18next("public.Save"));

		$('#sched-token').val(data.token);

		scMetadataDS.data(swLookupAsMetadata(data.metadata));
		scSelectScheduleType(data);
		scLoadScheduleFormFromMetadata(data);
		scTabs.select(0);

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

		if (schedule.triggerType == "SimpleTrigger") {
			$('#mq-center-latitude').val(scGetMetadataValue(lookup, 'centerLatitude'));
		} else if (schedule.triggerType == "CronTrigger") {
			$('#gs-base-url').val(scGetMetadataValue(lookup, 'geoserverBaseUrl'));
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
		var metadata = {};
		var scheduleType = scGetScheduleType();

		if (scheduleType == "sc-simple") {
			scAddMapMetadataEntry(metadata, 'centerLatitude', $('#mq-center-latitude').val());
		} else if (scheduleType == "sc-cron") {
			scAddMapMetadataEntry(metadata, 'geoserverBaseUrl', $('#gs-base-url').val());
		}
		return metadata;
	}

	/** Select the given schedule type and show its associated panel */
	function scSelectedScheduleType(schedule) {
		var type = (schedule.triggerType == 'CronTrigger' ? 'sc-cron' : 'sc-simple');
		schedTypesDD.value(type);
		scScheduleTypeChanged();
	}

	/** Get the currently selected schedule type */
	function scGetScheduleType() {
		return $("#sc-schedule-type").val();
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