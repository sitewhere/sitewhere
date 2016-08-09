<!-- Specification template -->
<script type="text/x-kendo-tmpl" id="specification-entry-small">
	<div class="asset-entry gradient-bg" id="asset-#:asset.id#">
		<div class="asset-entry-logowrapper">
			<img class="asset-entry-logo" src="#:asset.imageUrl#"/>
		</div>
		<div class="ellipsis"><b>#:name#</b></div>
		<div class="ellipsis">#:asset.name# (SKU: #:asset.sku#)</div>
	</div>
</script>

<style>
	.sw-spec-entry {
		padding: 5px;
		border-bottom: 1px solid #ddd;
		cursor: pointer;
	}
	
	.sw-spec-entry.k-state-selected {
		padding: 5px;
		border: 2px solid #dc0000;
		color: #1a1a1a;
	}

	.sw-spec-entry img {
		float: left;
	}

	.sw-spec-entry-logowrapper {
		float: left;
		margin-right: 15px;	
		width: 35px;
		height: 35px;
		background-color: #f0f0f0;
		border: 1px solid #dddddd;
		position: relative;
	}
	
	.sw-spec-entry-logo {
		display: block;
		margin-left: auto;
		margin-right: auto;
	    max-width: 35px;
	    max-height: 35px;
	}
</style>