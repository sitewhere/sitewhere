<style>
.sw-tenant-header {
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

.sw-tenant-header-label {
	font-size: 12pt;
	font-weight: bold;
	min-width: 190px;
	display: inline-block;
	line-height: 1.5;
}

.sw-tenant-header-logowrapper {
	float: left;
	margin-right: 80px;
	margin-left: 5px;
	width: 220px;
	height: 60px;
	position: relative;
	margin-bottom: 70px;
	margin-left: 5px;
}

.sw-tenant-header-logo-tag {
	position: absolute;
	top: -2px;
	left: -4px;
}

.sw-tenant-buttons {
	position: absolute;
	top: 10px;
	right: 10px;
}
</style>

<!-- Header for tenant detail page -->
<script type="text/x-kendo-tmpl" id="tpl-tenant-detail-header">
	<div class="sw-tenant-header gradient-bg">
		<div class="sw-tenant-header-logowrapper"
			style="background-image: url(#:logoUrl#); background-size: contain; background-repeat: no-repeat; background-position: 50% 50%;">
			<span class="label label-info sw-tenant-header-logo-tag">Tenant</span>
		</div>
		<div>
			<div>
				<span class="sw-tenant-header-label" title="Unique tenant id">Tenant Id:</span>
				<span title="#:id#">#:id#</span>
			</div>
			<div>
				<span class="sw-tenant-header-label" title="Tenant name">Tenant Name:</span>
				<span title="#:name#">#:name#</span>
			</div>
			<div>
				<span class="sw-tenant-header-label" title="Tenant state">Tenant State:</span>
				<span title="#:engineState.lifecycleStatus#">#:engineState.lifecycleStatus#</span>
			</div>
			<div>
				<span class="sw-tenant-header-label" title="#= i18next("includes.i18next.DDWATTS") #">#= i18next("public.CreatedDate") #:</span>
				<span title="#= formattedDate(createdDate) #">#= formattedDate(createdDate) #</span>
			</div>
			<div>
				<span class="sw-tenant-header-label" title="#= i18next("includes.i18next.DDWLU") #">#= i18next("public.UpdatedDate") #:</span>
				<span title="#= formattedDate(createdDate) #">#= formattedDate(updatedDate) #</span>
			</div>
		</div>
		<div class="sw-tenant-buttons btn-group">
			<a id="tenant-edit" class="btn btn-large" title="Edit Tenant" style="color: \\#009; font-size: 25px;" href="javascript:void(0)" onclick="onEditClicked()">
				<i class="fa fa-edit fa-white"></i>
			</a>
			<a id="tenant-delete" class="btn btn-large" title="Delete Tenant" style="color: \\#c00; font-size: 25px;" href="javascript:void(0)" onclick="onDeleteClicked()">
				<i class="fa fa-remove fa-white"></i>
			</a>
			<a id="tenant-power-off" class="btn btn-large" title="Stop Tenant Engine" style="color: \\#c00; font-size: 25px;" href="javascript:void(0)" onclick="onTenantStopClicked()">
				<i class="fa fa-power-off fa-white"></i>
			</a>
			<a id="tenant-power-on" class="btn btn-large" title="Start Tenant Engine" style="color: \\#090; font-size: 25px;" href="javascript:void(0)" onclick="onTenantStartClicked()">
				<i class="fa fa-power-off fa-white"></i>
			</a>
		</div>
	</div>
</script>
