<style>
.sw-device-header {
	clear: both;
	border: 1px solid #dcdcdc;
	padding: 10px;
	margin-bottom: 15px;
	font-size: 12pt;
	text-align: left;
	display: block;
	min-height: 95px;
}

.sw-device-header-label {
	font-size: 12pt;
	font-weight: bold;
	min-width: 200px;
	display: inline-block;
	line-height: 1.5;
}

.sw-device-header-logowrapper {
	float: left;
	margin-right: 40px;
	margin-left: 10px;
	width: 130px;
	height: 130px;
	position: relative;
}

.sw-device-header-logo-tag {
	position: absolute;
	top: -2px;
	left: -4px;
}
</style>

<!-- Header for device detail page -->
<script type="text/x-kendo-tmpl" id="tpl-device-detail-header">
	<div class="sw-device-header gradient-bg">
		<div class="sw-device-header-logowrapper"
			style="background-image: url(#:specification.asset.imageUrl#); background-size: contain; background-repeat: no-repeat; background-position: 50% 50%;">
			<span class="label label-info sw-device-header-logo-tag">#= i18next("public.Device") #</span>
		</div>
		<div>
			<div>
				<span class="sw-device-header-label" title="Unique device hardware id">Hardware Id:</span>
				<span title="#:hardwareId#">#:hardwareId#</span>
			</div>
			<div>
				<span class="sw-device-header-label" title="Device specification used">Device Specification:</span>
				<span title="#:specification.name#"><a href='/sitewhere/admin/specifications/detail.html?token=#:specification.token#'>#:specification.name#</a></span>
			</div>
			<div>
				<span class="sw-device-header-label" title="Site where device is deployed">Deployed Site:</span>
				<span title="#:site.name#"><a href='/sitewhere/admin/sites/detail.html?siteToken=#:site.token#'>#:site.name#</a></span>
			</div>
			<div>
				<span class="sw-device-header-label" title="Currently assigned asset">Current Assignment:</span>
# if (data.assignment) { #
# if (assignment.assignmentType != 'Unassociated') { #
				<span title="#:assignment.assetName#"><a href='/sitewhere/admin/assignments/detail.html?token=#:assignment.token#'>#:assignment.assetName#</a></span>
# } else { #
				<span title="Not associated with an asset">Device is not associated with an asset</span>
# } #
# } else { #
				<span title="Device is not assigned">Device is not assigned</span>
# } #
			</div>
			<div>
				<span class="sw-device-header-label" title="#= i18next("includes.i18next.DDWATTS") #">#= i18next("public.CreatedDate") #:</span>
				<span title="#= formattedDate(createdDate) #">#= formattedDate(createdDate) #</span>
			</div>
			<div>
				<span class="sw-device-header-label" title="#= i18next("includes.i18next.DDWLU") #">#= i18next("public.UpdatedDate") #:</span>
				<span title="#= formattedDate(createdDate) #">#= formattedDate(updatedDate) #</span>
			</div>
		</div>
	</div>
</script>
