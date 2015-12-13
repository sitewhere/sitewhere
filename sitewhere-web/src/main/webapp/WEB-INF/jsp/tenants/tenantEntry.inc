<style>
.sw-tenant-list-entry {
	clear: both;
	height: 40px;
	border: 1px solid #dcdcdc;
	padding: 10px;
	margin-bottom: 15px;
	font-size: 10pt;
	text-align: left;
	display: block;
	min-height: 60px;
	position: relative;
}

.sw-tenant-list-entry-heading {
	font-size: 14pt;
	font-weight: bold;
	line-height: 1;
	position: absolute;
	left: 400px;
}

.sw-tenant-list-state-icon {
	font-size: 50px;
	position: absolute;
	padding: 2px 7px;
	width: 50px;
	text-align: center;
}

.sw-tenant-list-state-label {
	position: absolute;
	font-size: 12px;
	top: 0px;
	left: 0px;
	width: 70px;
}

.sw-tenant-list-entry-logowrapper {
	width: 220px;
	height: 60px;
	background-color: #F0F0F0;
	font-size: 35px;
	text-align: center;
	vertical-align: middle;
	display: inline-block;
	left: 92px;
	position: absolute;
	border: 1px solid #DDD;
}

.sw-tenant-logo-tag {
	position: absolute;
	top: -2px;
	left: -4px;
}

.sw-tenant-list-entry-actions {
	float: right;
	margin-top: 5px;
}

.sw-tenant-list-label {
	padding-top: 17px;
	position: absolute;
	left: 335px;
	font-size: 26px;
}
</style>

<!-- Template for tenant row -->
<script type="text/x-kendo-tmpl" id="tpl-tenant-entry">
# if (!data.engineState) { #
	<div class="sw-tenant-list-entry sw-list-entry" style="background-color: \\#fee; border: 1px solid \\#933;">
		<div class="sw-tenant-list-state-icon">
			<i class="fa fa-remove" title="Uninitialized" style="color: \\#c00;"></i>
# } else if (data.engineState.lifecycleStatus == 'Started') { #
	<div class="sw-tenant-list-entry sw-list-entry" style="background-color: \\#efe; border: 1px solid \\#393;">
		<div class="sw-tenant-list-state-icon">
			<i class="fa fa-check-square" title="Started" style="color: \\#090;"></i>
# } else if (data.engineState.lifecycleStatus == 'Stopped') { #
	<div class="sw-tenant-list-entry sw-list-entry" style="background-color: \\#eee; border: 1px solid \\#999;">
		<div class="sw-tenant-list-state-icon">
			<i class="fa fa-power-off" title="Stopped" style="color: \\#ccc;"></i>
# } else if (data.engineState.lifecycleStatus == 'Error') { #
	<div class="sw-tenant-list-entry sw-list-entry" style="background-color: \\#fee; border: 1px solid \\#933;">
		<div class="sw-tenant-list-state-icon">
			<i class="fa fa-warning-sign" title="Error" style="color: \\#c00;"></i>
# } else { #
	<div class="sw-tenant-list-entry sw-list-entry" style="background-color: \\#fee; border: 1px solid \\#933;">
		<div class="sw-tenant-list-state-icon">
			<i class="fa fa-time" title="In progress" style="color: \\#c00;"></i>
# } #
		</div>
		<div class="sw-tenant-list-entry-logowrapper" style="background-image: url(#:logoUrl#); background-size: contain; background-repeat: no-repeat; background-position: 50% 50%;">
		</div>
		<div class="sw-tenant-list-entry-actions">
# if (!data.inDetailView) { #
			<a class="btn btn-large sw-action-glyph sw-primary-glyph" title="View Tenant" style="color: \\#090; font-size: 25px;" href="javascript:void(0)" onclick="onTenantOpenClicked(event, '#:id#')">
				<i class="fa fa-chevron-right fa-white"></i>
			</a>
# } else { #
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
# } #
		</div>
		<div>
			<p class="ellipsis sw-tenant-list-label">#:name# (#:id#)</p>
		</div>
	</div>
</script>
