<style>
.sw-device-list-entry {
	clear: both;
	height: 70px;
	border: 1px solid #dcdcdc;
	padding: 10px;
	margin-bottom: 15px;
	font-size: 10pt;
	text-align: left;
	display: block;
}

.sw-device-list-entry-heading {
	font-size: 12pt;
	font-weight: bold;
	line-height: 1;
}

.sw-device-list-entry-label {
	font-size: 10pt;
	font-weight: bold;
	display: inline-block;
}

.sw-device-list-entry-logowrapper {
	float: left;
	margin-right: 15px;	
	width: 70px;
	height: 70px;
	position: relative;
}

.sw-device-list-entry-logo-tag {
	position: absolute;
	top: -2px;
	left: -4px;
}

.sw-device-list-entry-logo {
	display: block;
	margin-left: auto;
	margin-right: auto;
    max-width: 70px;
    max-height: 70px;
    border: 1px solid rgb(221, 221, 221);
}

.sw-device-list-entry-assignment {
	float: right;
	width: 320px;
	height: 100%;
	padding-left: 10px;
	margin-left: 10px;
	border-left: solid 1px #e0e0e0;
}

.sw-device-list-entry-no-assignment {
	float: right;
	width: 320px;
	height: 100%;
	padding-left: 10px;
	margin-left: 10px;
	border-left: solid 1px #e0e0e0;
	text-align: center;
}

.sw-device-list-entry-actions {
	float: right;
	width: 260px;
	height: 100%;
	padding-left: 10px;
	margin-left: 10px;
	border-left: solid 1px #e0e0e0;
	position: relative;
}
</style>

<!-- Device list item template -->
<script type="text/x-kendo-tmpl" id="tpl-device-entry">
	<div class="sw-device-list-entry gradient-bg sw-list-entry">
		<div class="sw-device-list-entry-logowrapper"
			style="background-image: url(#:specification.asset.imageUrl#); background-size: contain; background-repeat: no-repeat; background-position: 50% 50%;">
			<span class="label label-info sw-device-list-entry-logo-tag">#= i18next("public.Device") #</span>
		</div>
		<div class="sw-device-list-entry-actions">
			<p class="ellipsis"><span class="sw-device-list-entry-label sw-min-70">#= i18next("public.Created") #:</span> #= formattedDate(createdDate) #</p>
			<p class="ellipsis"><span class="sw-device-list-entry-label sw-min-70">#= i18next("public.Updated") #:</span> #= formattedDate(updatedDate) #</p>
# if (data.inDetailView == null) { #
			<div class="btn-group btn-group-vertical" style="position: absolute; right: 0px; top: -2px;">
				<a class="btn btn-small sw-action-glyph sw-primary-glyph" title="#= i18next("public.EditDevice") #" style="color: \\#009;" 
					href="javascript:void(0)" onclick="onDeviceEditClicked(event, '#:hardwareId#');">
					<i class="icon-edit icon-white"></i></a>
				<a class="btn btn-small sw-action-glyph sw-primary-glyph" title="#= i18next("public.DeleteDevice") #" style="color: \\#900;" 
					href="javascript:void(0)" onclick="onDeviceDeleteClicked(event, '#:hardwareId#')">
					<i class="icon-remove icon-white"></i></a>
				<a class="btn btn-small sw-action-glyph sw-primary-glyph" title="#= i18next("public.ViewDevice") #" style="color: \\#060;" 
					href="javascript:void(0)" onclick="onDeviceOpenClicked(event, '#:hardwareId#')">
					<i class="icon-chevron-right icon-white"></i></a>
			</div>
# } #
		</div>
# if (data.assignment) { #
		<div class="sw-device-list-entry-assignment">
# if (assignment.assignmentType != 'Unassociated') { #
			<div class="sw-device-list-entry-logowrapper">
				<img class="sw-device-list-entry-logo" src="#:assignment.assetImageUrl#"/>
				<span class="label label-info sw-device-list-entry-logo-tag">#= i18next("public.Asset") #</span>
			</div>
			<p class="sw-device-list-entry-heading ellipsis">
				<a class="sw-action-glyph sw-primary-glyph" style="color: \\#009;" 
					href="/sitewhere/admin/assignments/detail.html?token=#:assignment.token#">
					<i class="icon-external-link icon-white" title="#= i18next("includes.i18next.OpenAssignment") #"></i></a>
				<span title="#:assignment.assetName#">#:assignment.assetName#</span>
			</p>
# } else { #
			<div class="sw-device-list-entry-logowrapper">
				<img class="sw-device-list-entry-logo" src="#:specification.asset.imageUrl#"/>
				<span class="label label-info sw-device-list-entry-logo-tag">Unassociated</span>
			</div>
			<p class="sw-device-list-entry-heading ellipsis">
				<a class="sw-action-glyph sw-primary-glyph" style="color: \\#009;" 
					href="/sitewhere/admin/assignments/detail.html?token=#:assignment.token#">
					<i class="icon-external-link icon-white" title="#= i18next("includes.i18next.OpenAssignment") #"></i></a>
				Unassociated Device
			</p>
# } #
			<p class="ellipsis"><span class="sw-device-list-entry-label sw-min-70">#= i18next("public.Assigned") #:</span> #= formattedDate(assignment.activeDate) #</p>
# if (assignment.status == 'Active') { #
			<span class="sw-device-list-entry-label sw-min-70">#= i18next("public.Status") #:</span> 
			<div class="btn-group" style="margin-top: -6px;">
				<a class="btn btn-small dropdown-toggle" data-toggle="dropdown" href="javascript:void(0)">#= i18next("includes.Active") #<span class="caret"></span>
				</a>
				<ul class="dropdown-menu">
					<li><a tabindex="-1" href="javascript:void(0)" title="#= i18next("includes.ReleaseAssignment") #"
						onclick="onReleaseAssignment(event, '#:assignment.token#')">#= i18next("includes.ReleaseAssignment") #</a></li>
					<li><a tabindex="-1" href="javascript:void(0)" title="#= i18next("includes.i18next.RDAM") #"
						onclick="onMissingAssignment(event, '#:assignment.token#')">#= i18next("includes.ReportMissing") #</a></li>
				</ul>
			</div>
# } else if (assignment.status == 'Missing') { #
			<span class="sw-device-list-entry-label sw-min-70">#= i18next("public.Status") #:</span> 
			<div class="btn-group" style="margin-top: -6px;">
				<a class="btn btn-small dropdown-toggle" data-toggle="dropdown" href="javascript:void(0)">#= i18next("includes.Missing") #<span class="caret"></span>
				</a>
				<ul class="dropdown-menu">
					<li><a tabindex="-1" href="javascript:void(0)" title="#= i18next("includes.ReleaseAssignment") #"
						onclick="onReleaseAssignment(event, '#:assignment.token#')">#= i18next("includes.ReleaseAssignment") #</a></li>
				</ul>
			</div>
# } else { #
			<p class="ellipsis"><span class="sw-device-list-entry-label sw-min-70">#= i18next("public.Status") #:</span> #:assignment.status#</p>
# } #
		</div>
# } else { #
		<div class="sw-device-list-entry-no-assignment">
    		<div class="alert alert-info">
    			<p>Device is not currently assigned.</p>
				<a class="btn" title="Assign Device" href="javascript:void(0)" 
					onclick="acOpen(event, '#:hardwareId#', onAssignmentAdded)" style="margin-top: -4px;">Assign Device</a>
			</div>
		</div>
# } #
		<div>
			<p class="sw-device-list-entry-heading ellipsis">
# if (data.inDetailView == null) { #
				<a class="sw-action-glyph sw-primary-glyph" style="color: \\#009;" 
					href="/sitewhere/admin/devices/detail.html?hardwareId=#:hardwareId#">
					<i class="icon-external-link icon-white" title="#= i18next("includes.i18next.OpenDevice") #"></i></a>
# } #
				<span title="#:specification.asset.name#">#:specification.asset.name#</span>
			</p>
			<p class="ellipsis"><span class="sw-device-list-entry-label" style="min-width: 35px;">Id:</span>
				<span title="#:hardwareId#">#:hardwareId#</span>
			</p>
			<p class="ellipsis"><span class="sw-device-list-entry-label" style="min-width: 35px;">#= i18next("includes.i18next.Spec") #:</span><span title="#:specification.name#">#:specification.name#</span></p>
		</div>
	</div>
</script>
