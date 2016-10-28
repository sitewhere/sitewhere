<style>
.sw-tenant-list-entry {
	clear: both;
	border: 1px solid #ccc;
	padding: 5px;
	margin-bottom: 15px;
	font-size: 12pt;
	text-align: left;
	display: block;
	min-height: 65px;
	position: relative;
	background-color: #f0f0f0;
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
	float: left;
	margin-right: 80px;
	margin-left: 5px;
	width: 220px;
	height: 60px;
	position: relative;
	margin-top: 7px;
	margin-bottom: 20px;
	margin-left: 5px;
	border: 1px solid #ccc;
}

.sw-tenant-logo-tag {
	position: absolute;
	top: -6px;
	left: -4px;
}

.sw-tenant-list-entry-label {
	font-size: 12pt;
	font-weight: bold;
	min-width: 190px;
	display: inline-block;
	line-height: 1.5;
}

.sw-tenant-buttons {
	position: absolute;
	top: 17px;
	right: 10px;
}
</style>

<!-- Template for tenant row -->
<script type="text/x-kendo-tmpl" id="tpl-tenant-entry">
	<div class="sw-tenant-list-entry sw-list-entry">
		<div class="sw-tenant-list-entry-logowrapper"
			style="background-image: url(#:logoUrl#); background-size: contain; background-repeat: no-repeat; background-position: 50% 50%;">
			<span class="label label-info sw-tenant-logo-tag">Tenant</span>
		</div>
		<div>
			<div>
				<span class="sw-tenant-list-entry-label" title="Unique tenant id">Tenant Id:</span>
				<span title="#:id#">#:id#</span>
			</div>
			<div>
				<span class="sw-tenant-list-entry-label" title="Tenant name">Tenant Name:</span>
				<span title="#:name#">#:name#</span>
			</div>
			<div>
				<span class="sw-tenant-list-entry-label" title="Tenant state">Tenant State:</span>
				<span title="#:engineState.lifecycleStatus#">#:engineState.lifecycleStatus#</span>
			</div>
		</div>
		<div class="sw-tenant-buttons btn-group">
			<a class="btn btn-large sw-action-glyph sw-primary-glyph" title="View Tenant" style="color: \\#090; font-size: 25px;" href="javascript:void(0)" onclick="onTenantOpenClicked(event, '#:id#')">
				<i class="fa fa-chevron-right fa-white"></i>
			</a>
		</div>
	</div>
</script>

<!-- Template for tenant row (small representation) -->
<script type="text/x-kendo-tmpl" id="tpl-tenant-entry-small">
	<tr>
		<td>#:id#</td>
		<td>#:name#</td>
	</tr>
</script>
