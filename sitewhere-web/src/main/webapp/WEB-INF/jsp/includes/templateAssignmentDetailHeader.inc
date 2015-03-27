<style>
.sw-assignment-header {
	clear: both;
	border: 1px solid #dcdcdc;
	padding: 10px;
	margin-bottom: 15px;
	font-size: 12pt;
	text-align: left;
	display: block;
	min-height: 95px;
	position: relative;
}

.sw-assignment-header-label {
	font-size: 12pt;
	font-weight: bold;
	min-width: 200px;
	display: inline-block;
	line-height: 1.5;
}

.sw-assignment-header-logowrapper {
	float: left;
	margin-right: 40px;
	margin-top: 25px;
	width: 160px;
	height: 160px;
	position: relative;
	margin-left: 10px;
}

.sw-assignment-header-logo-tag {
	position: absolute;
	top: -2px;
	left: -4px;
}

.sw-assignment-status-indicator {
	height: 3px;
	position: absolute;
	top: 0px;
	left: 0px;
	right: 0px
}
</style>

<!-- Header for device detail page -->
<script type="text/x-kendo-tmpl" id="tpl-assignment-detail-header">
# if (data.status == 'Active') { #
	<div class="sw-assignment-header sw-assignment-active">
		<div class="sw-assignment-active-indicator sw-assignment-status-indicator"></div>
# } else if (data.status == 'Missing') { #
	<div class="sw-assignment-header sw-assignment-missing">
		<div class="sw-assignment-missing-indicator sw-assignment-status-indicator"></div>
# } else { #
	<div class="sw-assignment-header sw-assignment-released">
		<div class="sw-assignment-status-indicator"></div>
# } #
		<div class="sw-assignment-header-logowrapper"
# if (data.associatedPerson) { #
			style="background-image: url(#:associatedPerson.imageUrl#); background-size: contain; background-repeat: no-repeat; background-position: 50% 50%;">
			<span class="label label-info sw-assignment-header-logo-tag">#= i18next("public.Asset") #</span>
# } else if (data.associatedHardware) { #
			style="background-image: url(#:associatedHardware.imageUrl#); background-size: contain; background-repeat: no-repeat; background-position: 50% 50%;">
			<span class="label label-info sw-assignment-header-logo-tag">#= i18next("public.Asset") #</span>
# } else if (data.associatedLocation) { #
			style="background-image: url(#:associatedLocation.imageUrl#); background-size: contain; background-repeat: no-repeat; background-position: 50% 50%;">
			<span class="label label-info sw-assignment-header-logo-tag">#= i18next("public.Asset") #</span>
# } else if ((data.assignmentType == 'Unassociated') && (data.device)) { #
			style="background-image: url(#:device.assetImageUrl#); background-size: contain; background-repeat: no-repeat; background-position: 50% 50%;">
			<span class="label label-info sw-assignment-header-logo-tag">Unassociated</span>
# } #
		</div>
		<div>
			<div>
				<span class="sw-assignment-header-label" title="#= i18next("includes.i18next.UAT") #">Token:</span>
				<span title="#:token#">#:token#</span>
			</div>
# if (data.associatedPerson) { #
			<div>
				<span class="sw-assignment-header-label" title="Assigned person">Assigned Person:</span>
				<span title="#:associatedPerson.name#">#:associatedPerson.name#</span>
			</div>
# } else if (data.associatedHardware) { #
			<div>
				<span class="sw-assignment-header-label" title="#= i18next("includes.i18next.Assignedhardware") #">#= i18next("includes.i18next.AssignedHardware") #:</span>
				<span title="#:associatedHardware.name#">#:associatedHardware.name#</span>
			</div>
# } else if (data.associatedLocation) { #
			<div>
				<span class="sw-assignment-header-label" title="Assigned location">Assigned Location:</span>
				<span title="#:associatedLocation.name#">#:associatedLocation.name#</span>
			</div>
# }	#
			<div>
				<span class="sw-assignment-header-label" title="#= i18next("includes.i18next.DUBA") #">#= i18next("includes.i18next.AssignedDevice") #:</span>
				<span title="#:device.assetName#"><a href='/sitewhere/admin/devices/detail.html?hardwareId=#:device.hardwareId#'>#:device.assetName#</a></span>
			</div>
			<div>
				<span class="sw-assignment-header-label" title="#= i18next("includes.i18next.DAWC") #">#= i18next("public.CreatedDate") #:</span>
				<span title="#= formattedDate(createdDate) #">#= formattedDate(createdDate) #</span>
			</div>
			<div>
				<span class="sw-assignment-header-label" title="#= i18next("includes.i18next.DAWLU") #">#= i18next("public.UpdatedDate") #:</span>
				<span title="#= formattedDate(createdDate) #">#= formattedDate(updatedDate) #</span>
			</div>
			<div>
				<span class="sw-assignment-header-label" title="#= i18next("includes.i18next.DABA") #">#= i18next("includes.i18next.ActiveDate") #:</span>
				<span title="#= formattedDate(activeDate) #">#= formattedDate(activeDate) #</span>
			</div>
			<div>
				<span class="sw-assignment-header-label" title="#= i18next("includes.i18next.DAWR") #">#= i18next("includes.i18next.ReleasedDate") #:</span>
				<span title="#= formattedDate(releasedDate) #">#= formattedDate(releasedDate) #</releasedDate>
			</div>
			<div>
				<span class="sw-assignment-header-label" title="#= i18next("includes.i18next.Assignmentstatus") #">#= i18next("includes.i18next.AssignmentStatus") #:</span>
# if (data.status == 'Active') { #
			<div class="btn-group" style="margin-top: -6px;">
				<a class="btn btn-small dropdown-toggle" data-toggle="dropdown" href="javascript:void(0)">#= i18next("includes.Active") #<span class="caret"></span>
				</a>
				<ul class="dropdown-menu">
					<li><a tabindex="-1" href="javascript:void(0)" title="#= i18next("includes.ReleaseAssignment") #"
						onclick="onReleaseAssignment(event, '#:token#')">#= i18next("includes.ReleaseAssignment") #</a></li>
					<li><a tabindex="-1" href="javascript:void(0)" title="#= i18next("includes.i18next.RDAM") #"
						onclick="onMissingAssignment(event, '#:token#')">#= i18next("includes.ReportMissing") #</a></li>
				</ul>
			</div>
# } else if (data.status == 'Missing') { #
			<div class="btn-group" style="margin-top: -6px;">
				<a class="btn btn-small dropdown-toggle" data-toggle="dropdown" href="javascript:void(0)">#= i18next("includes.Missing") #<span class="caret"></span>
				</a>
				<ul class="dropdown-menu">
					<li><a tabindex="-1" href="javascript:void(0)" title="#= i18next("includes.ReleaseAssignment") #"
						onclick="onReleaseAssignment(event, '#:token#')">#= i18next("includes.ReleaseAssignment") #</a></li>
				</ul>
			</div>
# } else { #
				<span title="#:status#">#:status#</span>
# } #
			</div>
		</div>
	</div>
</script>
