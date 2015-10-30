<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="Manage Devices" />
<c:set var="sitewhere_section" value="devices" />
<%@ include file="../includes/top.inc"%>

<style>
.sw-device-list {
	border: 0px;
}
</style>

<%@ include file="../includes/deviceCreateDialog.inc"%>
<%@ include file="../includes/deviceUpdateDialog.inc"%>
<%@ include file="../includes/deviceFilterDialog.inc"%>
<%@ include file="../includes/assignmentCreateDialog.inc"%>
<%@ include file="../includes/batchCommandInvokeDialog.inc"%>

<!-- Title Bar -->
<div class="sw-title-bar content k-header">
	<h1 class="ellipsis" data-i18n="devices.list.title"></h1>
	<div class="sw-title-bar-right">
		<a id="btn-filter-results" class="btn" href="javascript:void(0)"> <i
			class="fa fa-search sw-button-icon"></i> <span data-i18n="public.FilterResults">Filter
				Results</span>
		</a> <a id="btn-batch-command" class="btn" href="javascript:void(0)"> <i
			class="fa fa-bolt sw-button-icon"></i> <span data-i18n="devices.list.BatchCommand">Batch
				Command</span>
		</a> <a id="btn-add-device" class="btn" href="javascript:void(0)"> <i
			class="fa fa-plus sw-button-icon"></i> <span data-i18n="devices.list.AddNewDevice">Add New
				Device</span>
		</a>
	</div>
</div>
<div id="filter-criteria" class="sw-title-bar sw-filter-criteria hide"></div>
<div id="devices" class="sw-device-list"></div>
<div id="pager" class="k-pager-wrap"></div>

<form id="view-device-detail" method="get"></form>

<%@ include file="../includes/templateDeviceEntry.inc"%>
<%@ include file="../includes/templateSpecificationEntrySmall.inc"%>
<%@ include file="../includes/templateDeviceGroupEntrySmall.inc"%>
<%@ include file="../includes/assetTemplates.inc"%>
<%@ include file="../includes/commonFunctions.inc"%>

<script>
	/** Set sitewhere_title */
	sitewhere_i18next.sitewhere_title = "devices.list.title";

	/** Reference for device list datasource */
	var devicesDS;

	/** Specification token specified in request parameters */
	var rqSpecificationToken = '<c:out value="${specification.token}"/>';

	/** Group token specified in request parameters */
	var rqGroupToken = '<c:out value="${group.token}"/>';

	/** Role specified in 'groups with role' request parameters */
	var rqGroupsWithRole = '<c:out value="${groupsWithRole}"/>';

	/** Site token specified in request parameters */
	var rqSiteToken = '<c:out value="${site.token}"/>';

	/** Date range type specified in request parameters */
	var rqDateRange = '<c:out value="${dateRange}"/>';

	/** Date specified for 'after' period */
	var rqAfterDate = '<c:out value="${afterDate}"/>';

	/** Date specified for 'before' period */
	var rqBeforeDate = '<c:out value="${beforeDate}"/>';

	/** Date specified for 'before' period */
	var rqExcludeAssigned = '<c:out value="${excludeAssigned}"/>';

	/** Called when edit button on the list entry is pressed */
	function onDeviceEditClicked(e, hardwareId) {
		var event = e || window.event;
		event.stopPropagation();
		duOpen(hardwareId, onDeviceEditComplete);
	}

	/** Called after successful device update */
	function onDeviceEditComplete() {
		devicesDS.read();
	}

	/** Called when delete button on the list entry is pressed */
	function onDeviceDeleteClicked(e, hardwareId) {
		var event = e || window.event;
		event.stopPropagation();
		swDeviceDelete(hardwareId, '${tenant.authenticationToken}', onDeviceDeleteComplete);
	}

	/** Called after successful device delete */
	function onDeviceDeleteComplete() {
		devicesDS.read();
	}

	/** Called when 'open' button on the list entry is pressed */
	function onDeviceOpenClicked(e, hardwareId) {
		var event = e || window.event;
		event.stopPropagation();
		$("#view-device-detail").attr("action",
			"${pageContext.request.contextPath}/admin/devices/" + hardwareId + ".html");
		$('#view-device-detail').submit();
	}

	/** Called when 'release assignment' is clicked */
	function onReleaseAssignment(e, token) {
		var event = e || window.event;
		event.stopPropagation();
		swReleaseAssignment(token, '${tenant.authenticationToken}', onReleaseAssignmentComplete);
	}

	/** Called after successful release assignment */
	function onReleaseAssignmentComplete() {
		devicesDS.read();
	}

	/** Called when 'missing assignment' is clicked */
	function onMissingAssignment(e, token) {
		var event = e || window.event;
		event.stopPropagation();
		swAssignmentMissing(token, '${tenant.authenticationToken}', onMissingAssignmentComplete);
	}

	/** Called after successful missing assignment */
	function onMissingAssignmentComplete() {
		devicesDS.read();
	}

	/** Called on succesfull device assignment */
	function onAssignmentAdded() {
		devicesDS.read();
	}

	/** Called when a device has been successfully created */
	function onDeviceCreated() {
		devicesDS.read();
	}

	/** Called when filter options have changed */
	function onFilterChanged(criteria) {
		var redirect = location.protocol + '//' + location.host + location.pathname;
		redirect += "?";
		for ( var property in criteria) {
			if (criteria.hasOwnProperty(property)) {
				redirect += "&" + property + "=" + criteria[property];
			}
		}
		location.assign(redirect);
	}

	/** Build criteria object to pass state to filter dialog */
	function buildFilterCriteria() {
		var criteria = {
			"specification" : rqSpecificationToken,
			"group" : rqGroupToken,
			"groupsWithRole" : rqGroupsWithRole,
			"site" : rqSiteToken,
			"dateRange" : rqDateRange,
			"afterDate" : rqAfterDate,
			"beforeDate" : rqBeforeDate,
			"excludeAssigned" : ("true" == rqExcludeAssigned),
		};
		return criteria;
	}

	/** Clear all criteria */
	function clearCriteria() {
		var redirect = location.protocol + '//' + location.host + location.pathname;
		location.assign(redirect);
	}

	/** Display filter criteria being used */
	function showFilterCriteria() {
		var showCriteria = false;
		var criteriaDesc =
				"<a class='btn btn-mini' style='float: right;' href='javascript:void(0)' onclick='clearCriteria()'>Clear Filter</a>";
		criteriaDesc +=
				"<span style='width: 90%; display: block;'><i class='fa fa-filter sw-button-icon'></i> Displaying";

		if ("true" == rqExcludeAssigned) {
			criteriaDesc += " <strong>unassigned</strong> devices";
			showCriteria = true;
		} else {
			criteriaDesc += " devices";
		}

		if (rqSpecificationToken) {
			criteriaDesc += " of specification <strong>${specification.name}</strong>";
			showCriteria = true;
		}

		if (rqSiteToken) {
			criteriaDesc += " belonging to site <strong>${site.name}</strong>";
			showCriteria = true;
		}

		if (rqGroupToken) {
			criteriaDesc += " belonging to group <strong>${group.name}</strong>";
			showCriteria = true;
		} else if (rqGroupsWithRole) {
			criteriaDesc += " in groups with role <strong>${groupsWithRole}</strong>";
			showCriteria = true;
		}

		if (rqDateRange == "hour") {
			criteriaDesc += " created in the last <strong>hour</strong>";
			showCriteria = true;
		} else if (rqDateRange == "day") {
			criteriaDesc += " created in the last <strong>day</strong>";
			showCriteria = true;
		} else if (rqDateRange == "week") {
			criteriaDesc += " created in the last <strong>week</strong>";
			showCriteria = true;
		} else if (rqDateRange == "before") {
			criteriaDesc +=
					" created before <strong>"
							+ moment(rqBeforeDate, moment.ISO_8601).format('MM/DD/YYYY HH:mm:ss')
							+ "</strong>";
			showCriteria = true;
		} else if (rqDateRange == "after") {
			criteriaDesc +=
					" created after <strong>"
							+ moment(rqAfterDate, moment.ISO_8601).format('MM/DD/YYYY HH:mm:ss')
							+ "</strong>";
			showCriteria = true;
		} else if (rqDateRange == "between") {
			criteriaDesc +=
					" created between <strong>"
							+ moment(rqAfterDate, moment.ISO_8601).format('MM/DD/YYYY HH:mm:ss')
							+ "</strong> and <strong>"
							+ moment(rqBeforeDate, moment.ISO_8601).format('MM/DD/YYYY HH:mm:ss')
							+ "</strong>";
			showCriteria = true;
		}

		if (showCriteria) {
			criteriaDesc += ".</span>";
			$('#filter-criteria').html(criteriaDesc);
			$('#filter-criteria').show();
		}
	}

	/** Called after a batch command is invoked */
	function onBatchCommandInvoked() {
	}

	$(document).ready(function() {
		var dsUrl = "${pageContext.request.contextPath}/api/";

		// Handle group filter.
		if (rqGroupToken) {
			dsUrl += "devices/group/${group.token}";
		}
		// Handle groups with role filter.
		else if (rqGroupsWithRole) {
			dsUrl += "devices/grouprole/" + rqGroupsWithRole;
		}
		// Handle no filter.
		else {
			dsUrl += "devices";
		}
		dsUrl += "?includeSpecification=true&includeAssignment=true";
		if (rqSpecificationToken) {
			dsUrl += "&specification=${specification.token}";
		}
		if (rqSiteToken) {
			dsUrl += "&site=${site.token}";
		}

		// Handle date ranges.
		var windowStart = new Date();
		if ((rqDateRange == "before") || (rqDateRange == "after") || (rqDateRange == "between")) {
			if (rqAfterDate.length > 0) {
				dsUrl += "&startDate=" + rqAfterDate;
			}
			if (rqBeforeDate.length > 0) {
				dsUrl += "&endDate=" + rqBeforeDate;
			}
		} else if (rqDateRange == "hour") {
			dsUrl += "&startDate=" + moment().subtract(1, 'hours').toISOString();
		} else if (rqDateRange == "day") {
			dsUrl += "&startDate=" + moment().subtract(1, 'days').toISOString();
		} else if (rqDateRange == "week") {
			dsUrl += "&startDate=" + moment().subtract(7, 'days').toISOString();
		}

		// Handle 'exclude assigned' flag.
		if ("true" == rqExcludeAssigned) {
			dsUrl += "&excludeAssigned=true";
		}

		/** Create AJAX datasource for devices list */
		devicesDS = new kendo.data.DataSource({
			transport : {
				read : {
					url : dsUrl + "&tenantAuthToken=${tenant.authenticationToken}",
					dataType : "json",
				}
			},
			schema : {
				data : "results",
				total : "numResults",
				parse : function(response) {
					$.each(response.results, function(index, item) {
						parseDeviceData(item);
					});
					return response;
				}
			},
			serverPaging : true,
			serverSorting : true,
			pageSize : 15,
		});

		// Only show batch command button if specification is chosen.
		if ((!rqSpecificationToken) || ("true" == rqExcludeAssigned)) {
			$('#btn-batch-command').hide();
		}

		/** Create the list of devices */
		$("#devices").kendoListView({
			dataSource : devicesDS,
			template : kendo.template($("#tpl-device-entry").html()),
		});

		/** Pager for device list */
		$("#pager").kendoPager({
			dataSource : devicesDS
		});

		/** Handle create dialog */
		$('#btn-add-device').click(function(event) {
			dcOpen(event, onDeviceCreated);
		});

		/** Handle filter options dialog */
		$('#btn-filter-results').click(function(event) {
			dflOpen(event, buildFilterCriteria(), onFilterChanged);
		});

		/** Handle batch command dialog */
		$('#btn-batch-command').click(function(event) {
			bciOpen(buildFilterCriteria(), onBatchCommandInvoked);
		});

		showFilterCriteria();
	});
</script>

<%@ include file="../includes/bottom.inc"%>