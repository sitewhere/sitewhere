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
}

.sw-tenant-list-entry-heading {
	font-size: 14pt;
	font-weight: bold;
	line-height: 1;
}

.sw-tenant-list-entry-logowrapper {
	float: left;
	margin-right: 15px;
	width: 220px;
	height: 60px;
	background-color: #F0F0F0;
	border: 1px solid #DDD;
	font-size: 35px;
	text-align: center;
	vertical-align: middle;
	position: relative;
}

.sw-tenant-logo-tag {
	position: absolute;
	top: -2px;
	left: -4px;
}

.sw-tenant-list-entry-actions {
	float: right;
	width: 110px;
	height: 100%;
	padding-left: 10px;
	margin-left: 10px;
	border-left: solid 1px #e0e0e0;
	position: relative;
}

.sw-tenant-list-label {
	font-size: 16pt;
	padding-top: 17px;
}
</style>

<!-- Template for tenant row -->
<script type="text/x-kendo-tmpl" id="tpl-tenant-entry">
	<div class="sw-tenant-list-entry sw-list-entry gradient-bg">
		<div class="sw-tenant-list-entry-logowrapper" style="background-image: url(#:logoUrl#); background-size: contain; background-repeat: no-repeat; background-position: 50% 50%;">
			<span class="label label-info sw-tenant-logo-tag">Tenant</span>
		</div>
		<div class="sw-tenant-list-entry-actions">
			<div class="btn-group" style="position: absolute; right: 0px; top: 17px;">
				<a class="btn btn-small sw-action-glyph sw-primary-glyph" title="Edit Tenant" style="color: \\#009;" 
					href="javascript:void(0)" onclick="onTenantEditClicked(event, '#:id#');">
					<i class="icon-edit icon-white"></i></a>
				<a class="btn btn-small sw-action-glyph sw-primary-glyph" title="Delete Tenant" style="color: \\#900;" 
					href="javascript:void(0)" onclick="onTenantDeleteClicked(event, '#:id#')">
					<i class="icon-remove icon-white"></i></a>
				<a class="btn btn-small sw-action-glyph sw-primary-glyph" title="View Tenant" style="color: \\#060;" 
					href="javascript:void(0)" onclick="onTenantOpenClicked(event, '#:id#')">
					<i class="icon-chevron-right icon-white"></i></a>
			</div>
		</div>
		<div>
			<p class="ellipsis sw-tenant-list-label">#:name# (#:id#)</p>
		</div>
	</div>
</script>
