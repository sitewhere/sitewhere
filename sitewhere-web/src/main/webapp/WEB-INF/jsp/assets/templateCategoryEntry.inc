<style>
.sw-category-list-entry {
	clear: both;
	height: 40px;
	border: 1px solid #dcdcdc;
	padding: 10px;
	margin-bottom: 15px;
	font-size: 10pt;
	text-align: left;
	display: block;
}

.sw-category-list-entry-heading {
	font-size: 14pt;
	font-weight: bold;
	line-height: 1;
}

.sw-category-list-entry-logowrapper {
	float: left;
	margin-right: 15px;
	width: 40px;
	height: 40px;
	background-color: #F0F0F0;
	border: 1px solid #DDD;
	font-size: 35px;
	text-align: center;
	vertical-align: middle;
}

.sw-category-list-entry-actions {
	float: right;
	width: 110px;
	height: 100%;
	padding-left: 10px;
	margin-left: 10px;
	border-left: solid 1px #e0e0e0;
	position: relative;
}

.sw-category-list-label {
	font-size: 16pt; 
	padding-top: 7px;
}
</style>

<!-- Template for site row -->
<script type="text/x-kendo-tmpl" id="tpl-category-entry">
	<div class="sw-category-list-entry sw-list-entry gradient-bg">
		<div class="sw-category-list-entry-logowrapper"> 
# if (data.assetType == 'Device') { #
			<i class="icon-tablet icon-white"></i>
# } else if (data.assetType == 'Hardware') { #
			<i class="icon-laptop icon-white"></i>
# } else if (data.assetType == 'Person') { #
			<i class="icon-user icon-white"></i>
# } else if (data.assetType == 'Location') { #
			<i class="icon-map-marker icon-white"></i>
# } #
		</div>
		<div class="sw-category-list-entry-actions">
			<div class="btn-group" style="position: absolute; right: 0px; top: 7px;">
				<a class="btn btn-small sw-action-glyph sw-primary-glyph" title="Edit Asset Category" style="color: \\#009;" 
					href="javascript:void(0)" onclick="onCategoryEditClicked(event, '#:id#');">
					<i class="icon-edit icon-white"></i></a>
				<a class="btn btn-small sw-action-glyph sw-primary-glyph" title="Delete Asset Category" style="color: \\#900;" 
					href="javascript:void(0)" onclick="onCategoryDeleteClicked(event, '#:id#')">
					<i class="icon-remove icon-white"></i></a>
				<a class="btn btn-small sw-action-glyph sw-primary-glyph" title="View Asset Category" style="color: \\#060;" 
					href="javascript:void(0)" onclick="onCategoryOpenClicked(event, '#:id#')">
					<i class="icon-chevron-right icon-white"></i></a>
			</div>
		</div>
		<div>
			<p class="ellipsis sw-category-list-label">#:name# (#:id#)</p>
		</div>
	</div>
</script>
