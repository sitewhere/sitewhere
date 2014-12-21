<!-- Dialog for filtering device list -->
<div id="dfl-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal"
			aria-hidden="true">&times;</button>
		<h3>Filter Device List</h3>
	</div>
	<div class="modal-body">
		<div id="dfl-tabs">
			<ul>
				<li class="k-state-active">Filter Type</li>
				<li>Created Date</li>
				<li>Other Options</li>
			</ul>
			<div>
				<form id="dfl-options-form" class="form-horizontal"
					style="padding-top: 15px;">
					<div class="control-group"
						style="border-bottom: 1px solid #eeeeee; padding-bottom: 10px;">
						<label class="control-label" for="dfl-filter-type">Filter
							Type</label>
						<div class="controls">
							<input id="dfl-filter-type" class="input-xlarge" />
						</div>
					</div>
				</form>
				<div id="dfl-filter-type-forms">
					<form id="dfl-all-filter" class="form-horizontal">
						<div class="sw-info-text">This filter begins with all
							devices in the system and applies the other filter options to
							constrain the query.</div>
					</form>
					<form id="dfl-spec-filter" class="form-horizontal hide">
						<div class="control-group">
							<label class="control-label" for="dfl-chosen-specification-token">Choose
								Specification</label>
							<div class="controls">
								<div id="dfl-specifications" class="sw-form-search-results"></div>
								<input type="hidden" id="dfl-chosen-specification-token"
									title="Specification" />
							</div>
						</div>
					</form>
					<form id="dfl-group-filter" class="form-horizontal hide">
						<div class="control-group">
							<label class="control-label" for="dfl-chosen-group-token">Choose
								Device Group</label>
							<div class="controls">
								<div id="dfl-devicegroups" class="sw-form-search-results"></div>
								<input type="hidden" id="dfl-chosen-group-token"
									title="Device Group" />
							</div>
						</div>
					</form>
				</div>
			</div>
			<div>
				<form id="dfl-cdate-form" class="form-horizontal"
					style="padding-top: 15px;">
					<div class="control-group"
						style="border-bottom: 1px solid #eeeeee; padding-bottom: 10px;">
						<label class="control-label" for="dfl-cdate-type">Created
							Date Range</label>
						<div class="controls">
							<input id="dfl-cdate-type" class="input-xlarge" />
						</div>
					</div>
				</form>
				<div id="dfl-cdate-type-forms">
					<form id="dfl-cdate-any" class="form-horizontal">
						<div class="sw-info-text">All devices are returned
							regardless of when they were created.</div>
					</form>
					<form id="dfl-cdate-hour" class="form-horizontal hide">
						<div class="sw-info-text">Devices that were created more
							than and hour ago are filtered.</div>
					</form>
					<form id="dfl-cdate-day" class="form-horizontal hide">
						<div class="sw-info-text">Devices that were created more
							than a day ago are filtered.</div>
					</form>
					<form id="dfl-cdate-week" class="form-horizontal hide">
						<div class="sw-info-text">Devices that were created more
							than a week ago are filtered.</div>
					</form>
					<form id="dfl-cdate-after" class="form-horizontal hide">
						<div class="sw-info-text">Devices that were created after
							the given date are included.</div>
						<div class="control-group">
							<label class="control-label" for="dfl-cdate-after-date">After
								Date</label>
							<div class="controls">
								<input id="dfl-cdate-after-date" />
							</div>
						</div>
					</form>
					<form id="dfl-cdate-before" class="form-horizontal hide">
						<div class="sw-info-text">Devices that were created before
							the given date are included.</div>
						<div class="control-group">
							<label class="control-label" for="dfl-cdate-before-date">Before
								Date</label>
							<div class="controls">
								<input id="dfl-cdate-before-date" />
							</div>
						</div>
					</form>
					<form id="dfl-cdate-between" class="form-horizontal hide">
						<div class="sw-info-text">Devices that were created within
							the date range are included.</div>
						<div class="control-group">
							<label class="control-label" for="dfl-cdate-between-date-start">Start
								Date</label>
							<div class="controls">
								<input id="dfl-cdate-between-date-start" />
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="dfl-cdate-between-date-end">End
								Date</label>
							<div class="controls">
								<input id="dfl-cdate-between-date-end" />
							</div>
						</div>
					</form>
				</div>
			</div>
			<div>
				<form id="dfl-other-options" class="form-horizontal"
					style="padding-top: 15px;">
					<div class="sw-info-text">Choose the option below to include
						devices that have a current assignment. Otherwise, only unassigned
						devices are returned.</div>
					<div class="control-group">
						<label class="control-label" for="dfl-option-incl-assigned">Include
							Assigned</label>
						<div class="controls">
							<input type="checkbox" id="dfl-option-incl-assigned" />
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal">Cancel</a>
		<a id="dfl-dialog-submit" href="javascript:void(0)"
			class="btn btn-primary">Apply Filter</a>
	</div>
</div>

<!-- Script support for device create dialog -->
<script>
	/** Function called when dialog is submitted */
	var dflSubmitCallback;

	/** Provides external access to tabs */
	var dflTabs;

	/** Available filter types shown in dropdown */
	var dflFilterTypes = [ {
		text : "Show all devices",
		value : "dfl-all-filter"
	}, {
		text : "Show devices using a given specification",
		value : "dfl-spec-filter"
	}, {
		text : "Show devices belonging to a device group",
		value : "dfl-group-filter"
	}, ];

	/** Datasource for specifications */
	var dflSpecificationsDS;

	/** Specifications list */
	var dflSpecifications;

	/** Datasource for device groups */
	var dflDeviceGroupsDS;

	/** Device groups list */
	var dflDeviceGroups;

	/** Available date ranges in dropdown */
	var dflDateRanges = [ {
		text : "Devices created at any time",
		value : "dfl-cdate-any"
	}, {
		text : "Devices created in the last hour",
		value : "dfl-cdate-hour"
	}, {
		text : "Devices created in the last day",
		value : "dfl-cdate-day"
	}, {
		text : "Devices created in the last week",
		value : "dfl-cdate-week"
	}, {
		text : "Devices created after a given date",
		value : "dfl-cdate-after"
	}, {
		text : "Devices created before a given date",
		value : "dfl-cdate-before"
	}, {
		text : "Devices created between two dates",
		value : "dfl-cdate-between"
	}, ];

	$(document).ready(function() {

		/** Create tab strip */
		dflTabs = $("#dfl-tabs").kendoTabStrip({
			animation : false
		}).data("kendoTabStrip");

		// create DropDownList from input HTML element
		$("#dfl-filter-type").kendoDropDownList({
			dataTextField : "text",
			dataValueField : "value",
			dataSource : dflFilterTypes,
			index : 0,
			change : dflFilterTypeChanged
		});

		/** Create AJAX datasource for specifications list */
		dflSpecificationsDS = new kendo.data.DataSource({
			transport : {
				read : {
					url : "${pageContext.request.contextPath}/api/specifications",
					dataType : "json",
				}
			},
			schema : {
				data : "results",
				total : "numResults",
			},
		});

		/** Create the specifications list */
		dflSpecifications = $("#dfl-specifications").kendoListView({
			dataSource : dflSpecificationsDS,
			selectable : "single",
			template : kendo.template($("#specification-entry-small").html()),
			change : dflSpecificationChosen
		}).data("kendoListView");

		/** Create AJAX datasource for device groups list */
		dflDeviceGroupsDS = new kendo.data.DataSource({
			transport : {
				read : {
					url : "${pageContext.request.contextPath}/api/devicegroups",
					dataType : "json",
				}
			},
			schema : {
				data : "results",
				total : "numResults",
			},
		});

		/** Create the specifications list */
		dflDeviceGroups = $("#dfl-devicegroups").kendoListView({
			dataSource : dflDeviceGroupsDS,
			selectable : "single",
			template : kendo.template($("#devicegroup-entry-small").html()),
			change : dflDeviceGroupChosen
		}).data("kendoListView");

		/** Create dropdown list for choosing create date range */
		$("#dfl-cdate-type").kendoDropDownList({
			dataTextField : "text",
			dataValueField : "value",
			dataSource : dflDateRanges,
			index : 0,
			change : dflCreateDateTypeChanged
		});

		$("#dfl-cdate-after-date").kendoDateTimePicker({
			value : new Date()
		});
		$("#dfl-cdate-before-date").kendoDateTimePicker({
			value : new Date()
		});
		$("#dfl-cdate-between-date-start").kendoDateTimePicker({
			value : new Date()
		});
		$("#dfl-cdate-between-date-end").kendoDateTimePicker({
			value : new Date()
		});

		/** Handle dialog submit */
		$('#dfl-dialog-submit').click(function(event) {
			event.preventDefault();
			if (!dflValidate()) {
				return;
			}
			if (dflSubmitCallback != null) {
				dflSubmitCallback(createFilterCriteria());
			}
			$('#dfl-dialog').modal('hide');
		});
	});

	/** Create an object that describes chosen filter criteria */
	function createFilterCriteria() {
		var filterType = dflGetFilterType();
		var dateRangeType = dflGetCreateDateType();
		var criteria = {};

		// Handle filter types.
		if ("dfl-spec-filter" == filterType) {
			criteria.filter = "specification";
			criteria.token = $("#dfl-chosen-specification-token").val();
		} else if ("dfl-group-filter" == filterType) {
			criteria.filter = "group";
			criteria.token = $("#dfl-chosen-group-token").val();
		}

		// Handle date range types.
		if ("dfl-cdate-hour" == dateRangeType) {
			criteria.dateRange = "hour";
		} else if ("dfl-cdate-day" == dateRangeType) {
			criteria.dateRange = "day";
		} else if ("dfl-cdate-week" == dateRangeType) {
			criteria.dateRange = "week";
		}

		return criteria;
	}

	/** Get the currently selected filter type */
	function dflGetFilterType() {
		return $("#dfl-filter-type").val();
	}

	/** Called when filter type dropdown value changes */
	function dflFilterTypeChanged() {
		var selectedFilterType = dflGetFilterType();
		$("#dfl-filter-type-forms").children().each(function(i) {
			if (selectedFilterType == $(this).attr("id")) {
				$(this).removeClass("hide");
			} else if (!$(this).hasClass("hide")) {
				$(this).addClass("hide");
			}
		});
	}

	/** Get the currently selected create date range type */
	function dflGetCreateDateType() {
		return $("#dfl-cdate-type").val();
	}

	/** Called when create date range type dropdown value changes */
	function dflCreateDateTypeChanged() {
		var selectedCreateDateType = dflGetCreateDateType();
		$("#dfl-cdate-type-forms").children().each(function(i) {
			if (selectedCreateDateType == $(this).attr("id")) {
				$(this).removeClass("hide");
			} else if (!$(this).hasClass("hide")) {
				$(this).addClass("hide");
			}
		});
	}

	/** Called when a specification is chosen */
	function dflSpecificationChosen() {
		var listView = dflSpecificationsDS.view();
		var selected = $.map(this.select(), function(item) {
			return listView[$(item).index()];
		});

		if (selected.length > 0) {
			$('#dfl-chosen-specification-token').val(selected[0].token);
		} else {
			$('#dfl-chosen-specification-token').val("");
		}
	}

	/** Called when a device group is chosen */
	function dflDeviceGroupChosen() {
		var listView = dflDeviceGroupsDS.view();
		var selected = $.map(this.select(), function(item) {
			return listView[$(item).index()];
		});

		if (selected.length > 0) {
			$('#dfl-chosen-group-token').val(selected[0].token);
		} else {
			$('#dfl-chosen-group-token').val("");
		}
	}

	/** Validate everything */
	function dflValidate() {
		$.validity.setup({
			outputMode : "label"
		});
		$.validity.start();

		var result = $.validity.end();
		return result.valid;
	}

	/** Open the dialog */
	function dflOpen(e, criteria, callback) {
		var event = e || window.event;
		event.stopPropagation();

		// Select first tab.
		dflTabs.select(0);

		// Function called on submit.
		dflSubmitCallback = callback;
		
		// Set filter type dropdown choice.
		var filterChoice = "dfl-all-filter";
		if (criteria.filter == "specification") {
			filterChoice = "dfl-spec-filter";
		} else if (criteria.filter == "group") {
			filterChoice = "dfl-group-filter";
		}
		$("#dfl-filter-type").val(filterChoice);
		dflFilterTypeChanged();
		
		// Set date range type dropdown choice.
		$("#dfl-cdate-type").val(criteria.filter);
		dflCreateDateTypeChanged();

		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#dfl-dialog').modal('show');
	}
</script>