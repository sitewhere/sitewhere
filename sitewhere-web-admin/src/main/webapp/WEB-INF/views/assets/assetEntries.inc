<!-- Hardware asset template -->
<script type="text/x-kendo-tmpl" id="tpl-hardware-asset-entry">
	<div class="asset-entry gradient-bg sw-list-entry" id="asset-#:id#">
		<div class="asset-entry-logowrapper">
			<img class="asset-entry-logo" src="#:imageUrl#"/>
		</div>
		<div class="ellipsis asset-entry-label"><strong>#:name#</strong> (#:sku#)</div>
		<div class="asset-entry-actions">
			<div class="btn-group" style="position: absolute; right: 7px; top: 17px;">
				<a class="btn btn-small sw-action-glyph sw-primary-glyph" title="Edit Asset" style="color: \\#009;" 
					href="javascript:void(0)" onclick="onAssetEditClicked(event, '#:id#');">
					<i class="fa fa-edit fa-white"></i></a>
				<a class="btn btn-small sw-action-glyph sw-primary-glyph" title="Delete Asset" style="color: \\#900;" 
					href="javascript:void(0)" onclick="onAssetDeleteClicked(event, '#:id#')">
					<i class="fa fa-remove fa-white"></i></a>
			</div>
		</div>
	</div>
</script>

<!-- Person asset template -->
<script type="text/x-kendo-tmpl" id="tpl-person-asset-entry">
	<div class="asset-entry gradient-bg sw-list-entry" id="asset-#:id#">
		<div class="asset-entry-logowrapper">
			<img class="asset-entry-logo" src="#:imageUrl#"/>
		</div>
		<div class="ellipsis asset-entry-label"><strong>#:name#</strong> (#:emailAddress#)</div>
		<div class="asset-entry-actions">
			<div class="btn-group" style="position: absolute; right: 7px; top: 17px;">
				<a class="btn btn-small sw-action-glyph sw-primary-glyph" title="Edit Asset" style="color: \\#009;" 
					href="javascript:void(0)" onclick="onAssetEditClicked(event, '#:id#');">
					<i class="fa fa-edit fa-white"></i></a>
				<a class="btn btn-small sw-action-glyph sw-primary-glyph" title="Delete Asset" style="color: \\#900;" 
					href="javascript:void(0)" onclick="onAssetDeleteClicked(event, '#:id#')">
					<i class="fa fa-remove fa-white"></i></a>
			</div>
		</div>
	</div>
</script>

<!-- Location asset template -->
<script type="text/x-kendo-tmpl" id="tpl-location-asset-entry">
	<div class="asset-entry gradient-bg sw-list-entry" id="asset-#:id#">
		<div class="asset-entry-logowrapper">
			<img class="asset-entry-logo" src="#:imageUrl#"/>
		</div>
		<div class="ellipsis asset-entry-label"><strong>#:name#</strong> (Lat:#:latitude#, Lon:#:longitude#)</div>
		<div class="asset-entry-actions">
			<div class="btn-group" style="position: absolute; right: 7px; top: 17px;">
				<a class="btn btn-small sw-action-glyph sw-primary-glyph" title="Edit Asset" style="color: \\#009;" 
					href="javascript:void(0)" onclick="onAssetEditClicked(event, '#:id#');">
					<i class="fa fa-edit fa-white"></i></a>
				<a class="btn btn-small sw-action-glyph sw-primary-glyph" title="Delete Asset" style="color: \\#900;" 
					href="javascript:void(0)" onclick="onAssetDeleteClicked(event, '#:id#')">
					<i class="fa fa-remove fa-white"></i></a>
			</div>
		</div>
	</div>
</script>

<style>
.asset-entry {
	padding: 7px;
	border: 1px solid #CCC;
	margin-bottom: 5px;
	position: relative;
	min-height: 45px;
}

.asset-entry-label {
	font-size: 16pt;
	padding-top: 8px;
}

.asset-entry-actions {
	float: right;
	width: 110px;
	height: 100%;
	padding-left: 10px;
	margin-left: 10px;
	border-left: solid 1px #e0e0e0;
}

.asset-entry img {
	float: left;
}

.asset-entry-logowrapper {
	float: left;
	margin-right: 15px;
	width: 45px;
	height: 45px;
	background-color: #F0F0F0;
	border: 1px solid #DDD;
	position: relative;
}

.asset-entry-logo {
	display: block;
	margin-left: auto;
	margin-right: auto;
	max-width: 45px;
	max-height: 45px;
}
</style>