<style>
.sw-tgroup-list-entry {
	clear: both;
	border: 1px solid #ccc;
	padding: 10px;
	margin-bottom: 15px;
	font-size: 12pt;
	text-align: left;
	display: block;
	min-height: 65px;
	position: relative;
	background-color: #f0f0f0;
}

.sw-tgroup-list-entry-heading {
	font-size: 14pt;
	font-weight: bold;
	line-height: 1;
	position: absolute;
	left: 400px;
}

.sw-tgroup-list-state-icon {
	font-size: 50px;
	position: absolute;
	padding: 2px 7px;
	width: 50px;
	text-align: center;
}

.sw-tgroup-list-state-label {
	position: absolute;
	font-size: 12px;
	top: 0px;
	left: 0px;
	width: 70px;
}

.sw-tgroup-list-entry-logowrapper {
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

.sw-tgroup-logo-tag {
	position: absolute;
	top: -6px;
	left: -4px;
}

.sw-tgroup-list-entry-label {
	font-size: 12pt;
	font-weight: bold;
	min-width: 110px;
	display: inline-block;
	line-height: 1.5;
}

.sw-tgroup-buttons {
	position: absolute;
	top: 17px;
	right: 10px;
}
</style>

<!-- Template for tenant group row -->
<script type="text/x-kendo-tmpl" id="tpl-tgroup-entry">
	<div class="sw-tgroup-list-entry sw-list-entry">
		<div class="sw-tgroup-list-entry-logowrapper"
			style="background-image: url(#:imageUrl#); background-size: contain; background-repeat: no-repeat; background-position: 50% 50%;">
			<span class="label label-info sw-tgroup-logo-tag">Tenant Group</span>
		</div>
		<div style="min-height: 70px;">
			<div>
				<span class="sw-tgroup-list-entry-label" title="Unique tenant group token">Token:</span>
				<span title="#:token#">#:token#</span>
			</div>
			<div>
				<span class="sw-tgroup-list-entry-label" title="Group name">Name:</span>
				<span title="#:name#">#:name#</span>
			</div>
			<div>
				<span class="sw-tgroup-list-entry-label" title="Group name">Description:</span>
				<span class="ellipsis" style="max-width: 400px; line-height: 1; vertical-align: middle; display: inline-block;" 
					title="#:description#">#:description#</span>
			</div>
		</div>
		<div class="btn-group btn-group-vertical" style="position: absolute; right: 10px; top: 7px;">
			<a class="btn btn-small sw-action-glyph sw-primary-glyph" title="Edit Tenant Group" style="color: \\#009;" 
				href="javascript:void(0)" onclick="onTenantGroupEditClicked(event, '#:token#');">
				<i class="fa fa-edit fa-white"></i></a>
			<a class="btn btn-small sw-action-glyph sw-primary-glyph" title="Delete Tenant Group" style="color: \\#900;" 
				href="javascript:void(0)" onclick="onTenantGroupDeleteClicked(event, '#:token#')">
				<i class="fa fa-remove fa-white"></i></a>
			<a class="btn btn-small sw-action-glyph sw-primary-glyph" title="Open Tenant Group" style="color: \\#060;" 
				href="javascript:void(0)" onclick="onTenantGroupOpenClicked(event, '#:token#')">
				<i class="fa fa-chevron-right fa-white"></i></a>
		</div>
	</div>
</script>
