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
	height: 100%;
	padding-left: 20px;
	margin-left: 10px;
	border-left: 1px solid #E0E0E0;
	position: relative;
	margin-top: 9px;
	margin-right: 10px;
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
			<a href="javascript:void(0);" class="btn btn-large btn-primary" onclick="onTenantSelected(event, '#:id#');">Select</a>
		</div>
		<div>
			<p class="ellipsis sw-tenant-list-label">#:name# (#:id#)</p>
		</div>
	</div>
</script>
