<style>
.sw-site-header {
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

.sw-site-header-label {
	font-size: 12pt;
	font-weight: bold;
	min-width: 200px;
	display: inline-block;
	line-height: 1.5;
}

.sw-site-header-logowrapper {
	float: left;
	margin-right: 40px;
	margin-left: 10px;
	margin-bottom: 10px;
	width: 130px;
	height: 130px;
	position: relative;
	border: 1px solid #ccc;
}

.sw-site-header-logo-tag {
	position: absolute;
	top: -2px;
	left: -4px;
}

.sw-site-bottom {
	clear: both;
	font-size: 12pt;
}
</style>

<!-- Header for device detail page -->
<script type="text/x-kendo-tmpl" id="tpl-site-detail-header">
	<div class="sw-site-header gradient-bg">
		<div class="sw-site-header-logowrapper"
			style="background-image: url(#:imageUrl#); background-size: contain; background-repeat: no-repeat; background-position: 50% 50%;">
			<span class="label label-info sw-site-header-logo-tag">#= i18next("public.Site") #</span>
		</div>
		<div>
			<div>
				<span class="sw-site-header-label" title="Unique site token">Token:</span>
				<span title="#:token#">#:token#</span>
			</div>
			<div>
				<span class="sw-site-header-label" title="Name">Name:</span>
				<span title="#:name#">#:name#</span>
			</div>
			<div>
				<span class="sw-site-header-label" title="#= i18next("includes.i18next.DDWATTS") #">#= i18next("public.CreatedDate") #:</span>
				<span title="#: formattedDate(data.createdDate) #">#: formattedDate(data.createdDate) #</span>
			</div>
			<div>
				<span class="sw-site-header-label" title="#= i18next("includes.i18next.DDWLU") #">#= i18next("public.UpdatedDate") #:</span>
				<span title="#: formattedDate(data.createdDate) #">#: formattedDate(data.updatedDate) #</span>
			</div>
		</div>
		<div class="sw-site-bottom">
			#:description#
		</div>
	</div>
</script>
