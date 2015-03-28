<style>
.sw-device-list-entry-small {
	clear: both;
	height: 40px;
	border: 1px solid #dcdcdc;
	padding: 5px;
	font-size: 10pt;
	text-align: left;
	display: block;
}

.sw-device-list-entry-small.k-state-selected {
	padding: 5px;
	border: 2px solid #dc0000;
	color: #1a1a1a;
}

.sw-device-list-entry-small-logowrapper {
	float: left;
	margin-right: 15px;	
	width: 40px;
	height: 40px;
	position: relative;
	border: 1px solid #ddd;
}

.sw-device-list-entry-small-logo {
	display: block;
	margin-left: auto;
	margin-right: auto;
    max-width: 70px;
    max-height: 70px;
    border: 1px solid rgb(221, 221, 221);
}
</style>

<!-- Device list item template -->
<script type="text/x-kendo-tmpl" id="tpl-device-entry-small">
	<div class="sw-device-list-entry-small gradient-bg sw-list-entry">
		<div class="sw-device-list-entry-small-logowrapper"
			style="background-image: url(#:specification.asset.imageUrl#); background-size: contain; background-repeat: no-repeat; background-position: 50% 50%;">
		</div>
		<div>
			<div class="sw-device-list-entry-heading ellipsis"><span title="#:specification.asset.name#">#:specification.asset.name#</span></div>
			<div class="ellipsis"><span class="sw-device-list-entry-label" style="min-width: 85px; margin-top: 7px;"> #= i18next("public.HardwareId") #:</span><span title="#:hardwareId#">#:hardwareId#</span></div>
		</div>
	</div>
</script>
