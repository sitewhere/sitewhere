<style>
.sw-tgroup-list-entry {
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
	min-width: 190px;
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
		<div>
			<div>
				<span class="sw-tgroup-list-entry-label" title="Unique tenant group id">Group Id:</span>
				<span title="#:id#">#:id#</span>
			</div>
			<div>
				<span class="sw-tgroup-list-entry-label" title="Group name">Group Name:</span>
				<span title="#:name#">#:name#</span>
			</div>
		</div>
		<div class="sw-tgroup-buttons btn-group">
			<a class="btn btn-large sw-action-glyph sw-primary-glyph" title="View Tenant Group" style="color: \\#090; font-size: 25px;" href="javascript:void(0)" onclick="onTenantGroupOpenClicked(event, '#:id#')">
				<i class="fa fa-chevron-right fa-white"></i>
			</a>
		</div>
	</div>
</script>
