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
	font-size: 35px;
	position: absolute;
	left: 20px;
	top: 20px;
}

.sw-tenant-list-entry-logowrapper {
	width: 220px;
	height: 60px;
	background-color: #F0F0F0;
	font-size: 35px;
	text-align: center;
	vertical-align: middle;
	display: inline-block;
	left: 80px;
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
	left: 325px;
	font-size: 26px;
}
</style>

<!-- Template for tenant row -->
<script type="text/x-kendo-tmpl" id="tpl-tenant-entry">
	<div class="sw-tenant-list-entry sw-list-entry gradient-bg">
		<div class="sw-tenant-list-state-icon">
# if (!data.engineState) { #
			<i class="icon-remove" title="Uninitialized" style="color: \\#c00;"></i>
# } else if (data.engineState.lifecycleStatus == 'Started') { #
			<i class="icon-check" title="Started" style="color: \\#090;"></i>
# } else if (data.engineState.lifecycleStatus == 'Stopped') { #
			<i class="icon-power-off" title="Stopped" style="color: \\#c00;"></i>
# } else if (data.engineState.lifecycleStatus == 'Error') { #
			<i class="icon-warning-sign" title="Error" style="color: \\#c00;"></i>
# } else { #
			<i class="icon-time" title="In progress" style="color: \\#c00;"></i>
# } #
		</div>
		<div class="sw-tenant-list-entry-logowrapper" style="background-image: url(#:logoUrl#); background-size: contain; background-repeat: no-repeat; background-position: 50% 50%;">
			<span class="label label-info sw-tenant-logo-tag">Tenant</span>
		</div>
		<div class="sw-tenant-list-entry-actions">
# if (!data.inDetailView) { #
			<a class="btn btn-large sw-action-glyph sw-primary-glyph" title="View Tenant" style="color: \\#090; font-size: 25px;" href="javascript:void(0)" onclick="onTenantOpenClicked(event, '#:id#')">
				<i class="icon-chevron-right icon-white"></i>
			</a>
# } else { #
			<a id="tenant-power-off" class="btn btn-large" title="Stop Tenant Engine" style="color: \\#c00; font-size: 25px;" href="javascript:void(0)" onclick="onTenantStopClicked()">
				<i class="icon-power-off icon-white"></i>
			</a>
			<a id="tenant-power-on" class="btn btn-large" title="Start Tenant Engine" style="color: \\#090; font-size: 25px;" href="javascript:void(0)" onclick="onTenantStartClicked()">
				<i class="icon-power-off icon-white"></i>
			</a>
# } #
		</div>
		<div>
			<p class="ellipsis sw-tenant-list-label">#:name# (#:id#)</p>
		</div>
	</div>
</script>
