<style>

.sw-assignment-list-entry {
	clear: both;
	height: 70px;
	padding: 10px;
	margin-bottom: 15px;
	font-size: 10pt;
	text-align: left;
	display: block;
	position: relative;
}

.sw-assignment-list-entry-heading {
	font-size: 12pt;
	font-weight: bold;
	line-height: 1;
}

.sw-assignment-list-entry-label {
	font-size: 10pt;
	font-weight: bold;
	display: inline-block;
}

.sw-assignment-list-entry-logowrapper {
	float: left;
	margin-right: 15px;	
	width: 70px;
	height: 70px;
	position: relative;
}

.sw-assignment-list-entry-logo-tag {
	position: absolute;
	top: -2px;
	left: -4px;
}

.sw-assignment-list-entry-logo {
	display: block;
	margin-left: auto;
	margin-right: auto;
    max-width: 70px;
    max-height: 70px;
    border: 1px solid rgb(221, 221, 221);
}

.sw-assignment-list-entry-device {
	float: right;
	width: 33%;
	height: 100%;
	padding-left: 15px;
	margin-left: 15px;
	border-left: solid 1px #e0e0e0;
}

.sw-assignment-list-entry-no-device {
	float: right;
	width: 33%;
	height: 100%;
	padding-left: 15px;
	margin-left: 15px;
	border-left: solid 1px #e0e0e0;
	text-align: center;
}

.sw-assignment-list-entry-actions {
	float: right;
	width: 30%;
	height: 100%;
	padding-left: 15px;
	margin-left: 15px;
	border-left: solid 1px #e0e0e0;
	position: relative;
}

.sw-assignment-status-indicator {
	height: 3px;
	position: absolute;
	top: 0px;
	left: 0px;
	right: 0px
}
</style>

<!-- Template for assignment row -->
<script type="text/x-kendo-tmpl" id="tpl-assignment-entry">
# if (data.status == 'Active') { #
	<div class="sw-assignment-list-entry sw-assignment-active">
		<div class="sw-assignment-active-indicator sw-assignment-status-indicator"></div>
# } else if (data.status == 'Missing') { #
	<div class="sw-assignment-list-entry sw-assignment-missing">
		<div class="sw-assignment-missing-indicator sw-assignment-status-indicator"></div>
# } else { #
	<div class="sw-assignment-list-entry sw-assignment-released">
		<div class="sw-assignment-status-indicator"></div>
# } #
# if (data.associatedPerson) { #
		<div class="sw-assignment-list-entry-logowrapper"
			style="background-image: url(#:associatedPerson.photoUrl#); background-size: contain; background-repeat: no-repeat; background-position: 50% 50%;">
			<span class="label label-info sw-assignment-list-entry-logo-tag">Asset</span>
		</div>
# } else if (data.associatedHardware) { #
		<div class="sw-assignment-list-entry-logowrapper"
			style="background-image: url(#:associatedHardware.imageUrl#); background-size: contain; background-repeat: no-repeat; background-position: 50% 50%;">
			<span class="label label-info sw-assignment-list-entry-logo-tag">Asset</span>
		</div>
# } else if ((data.assignmentType == 'Unassociated') && (data.device)) { #
		<div class="sw-assignment-list-entry-logowrapper"
			style="background-image: url(#:device.assetImageUrl#); background-size: contain; background-repeat: no-repeat; background-position: 50% 50%;">
			<span class="label label-info sw-assignment-list-entry-logo-tag">Unassociated</span>
		</div>
# } #
		<div class="sw-assignment-list-entry-actions">
			<p class="ellipsis"><span class="sw-assignment-list-entry-label" style="min-width: 70px;">Assigned:</span> #= formattedDate(activeDate) #</p>
			<p class="ellipsis"><span class="sw-assignment-list-entry-label" style="min-width: 70px;">Released:</span> #= formattedDate(releasedDate) #</p>
# if (data.status == 'Active') { #
			<span class="sw-min-70 sw-assignment-list-entry-label">Status:</span>			
			<div class="btn-group" style="margin-top: -6px;">
				<a class="btn btn-small dropdown-toggle" data-toggle="dropdown" href="javascript:void(0)">
					Active
					<span class="caret"></span>
				</a>
				<ul class="dropdown-menu">
					<li><a tabindex="-1" href="javascript:void(0)" title="Release Assignment"
						onclick="onReleaseAssignment(event, '#:token#')">Release Assignment</a></li>
					<li><a tabindex="-1" href="javascript:void(0)" title="Report Device/Asset Missing"
						onclick="onMissingAssignment(event, '#:token#')">Report Missing</a></li>
				</ul>
			</div>
# } else if (data.status == 'Missing') { #
			<span class="sw-min-70 sw-assignment-list-entry-label">Status:</span>			
			<div class="btn-group" style="margin-top: -6px;">
				<a class="btn btn-small dropdown-toggle" data-toggle="dropdown" href="javascript:void(0)">
					Missing
					<span class="caret"></span>
				</a>
				<ul class="dropdown-menu">
					<li><a tabindex="-1" href="javascript:void(0)" title="Release Assignment"
						onclick="onReleaseAssignment(event, '#:token#')">Release Assignment</a></li>
				</ul>
			</div>
# } else { #
			<p class="ellipsis"><span class="sw-min-70 sw-assignment-list-entry-label">Status:</span> #:status#</p>
# } #
# if (data.inDetailView == null) { #
			<div class="btn-group btn-group-vertical" style="position: absolute; right: 0px; top: -2px;">
				<a class="btn btn-small btn-primary" title="Edit Assignment"
					href="javascript:void(0)" onclick="onEditAssignment(event, '#:token#')">
					<i class="icon-edit icon-white"></i></a>
				<a class="btn btn-small btn-danger" title="Delete Assignment" 
					href="javascript:void(0)" onclick="onDeleteAssignment(event, '#:token#')">
					<i class="icon-remove icon-white"></i></a>
				<a class="btn btn-small btn-success" title="View Assignment" 
					href="javascript:void(0)" onclick="onViewAssignment(event, '#:token#')">
					<i class="icon-chevron-right icon-white"></i></a>
			</div>
# } #
		</div>
# if (data.device) { #
		<div class="sw-assignment-list-entry-device">
			<div class="sw-assignment-list-entry-logowrapper"
				style="background-image: url(#:device.assetImageUrl#); background-size: contain; background-repeat: no-repeat; background-position: 50% 50%;">
				<span class="label label-info sw-assignment-list-entry-logo-tag">Device</span>
			</div>
			<p class="sw-assignment-list-entry-heading ellipsis" title="#:device.assetName#">#:device.assetName#</p>
			<p class="ellipsis"><span class="sw-assignment-list-entry-label" style="min-width: 40px;">Id:</span><span title="#:device.hardwareId#">#:device.hardwareId#</span></p>
			<p class="ellipsis"><span class="sw-assignment-list-entry-label" style="min-width: 40px;">Info:</span><span title="#:device.comments#">#:device.comments#</span></p>
		</div>
# } else { #
		<div class="sw-assignment-list-entry-no-device">
    		<div class="alert alert-error">
    			Invalid hardware id. Device not found!
			</div>
		</div>
# } #
# if (data.associatedPerson) { #
		<div>
			<p class="sw-assignment-list-entry-heading ellipsis" title="#:associatedPerson.name#">#:associatedPerson.name#</p>
			<p class="ellipsis"><span class="sw-assignment-list-entry-label" style="min-width: 45px;">Email:</span><span title="#:associatedPerson.emailAddress#">#:associatedPerson.emailAddress#</span></p>
			<p class="ellipsis"><span class="sw-assignment-list-entry-label" style="min-width: 45px;">Roles:</span><span title="#:swArrayAsCommaDelimited(associatedPerson.roles)#">#:swArrayAsCommaDelimited(associatedPerson.roles)#</span></p>
		</div>
# } else if (data.associatedHardware) { #
		<div>
			<p class="sw-assignment-list-entry-heading ellipsis" title="#:associatedHardware.name#">#:associatedHardware.name#</p>
			<p class="ellipsis"><span class="sw-assignment-list-entry-label" style="min-width: 35px;">SKU:</span><span title="#:associatedHardware.sku#">#:associatedHardware.sku#</span></p>
			<p class="ellipsis"><span class="sw-assignment-list-entry-label" style="min-width: 35px;">Info:</span><span title="#:associatedHardware.description#">#:associatedHardware.description#</span></p>
		</div>
# } else if (data.assignmentType == 'Unassociated') { #
		<div>
			<p class="sw-assignment-list-entry-heading ellipsis">Unassociated Device</p>
		</div>
# } #
	</div>
</script>
