<style>
.sw-spec-list-entry {
	clear: both;
	height: 80px;
	border: 1px solid #dcdcdc;
	padding: 10px;
	margin-bottom: 15px;
	font-size: 11pt;
	text-align: left;
	display: block;
}

.sw-spec-list-entry-heading {
	font-size: 14pt;
	font-weight: bold;
	line-height: 1;
	margin-bottom: 5px;
}

.sw-spec-list-entry-subhead {
	margin: 1px 0px;
}

.sw-spec-list-entry-label {
	font-size: 11pt;
	font-weight: bold;
	display: inline-block;
	min-width: 70px;
}

.sw-spec-list-entry-logowrapper {
	float: left;
	margin-top: 13px;
	margin-right: 15px;
	width: 70px;
	height: 70px;
	position: relative;
	border: 1px solid #ddd;
}

.sw-spec-list-entry-logo-tag {
	position: absolute;
	top: -18px;
	left: 0px;
}
.sw-spec-list-entry-actions {
	float: right;
	width: 250px;
	height: 100%;
	padding-left: 10px;
	margin-left: 10px;
	border-left: solid 1px #e0e0e0;
	position: relative;
}
</style>

<!-- Device specification list item template -->
<script type="text/x-kendo-tmpl" id="tpl-specification-entry">
	<div class="sw-spec-list-entry gradient-bg sw-list-entry">
		<div class="sw-spec-list-entry-logowrapper"
			style="background-image: url(#:asset.imageUrl#); background-size: contain; background-repeat: no-repeat; background-position: 50% 50%;">
			<span class="label label-info sw-spec-list-entry-logo-tag">#= i18next('public.Specification') #</span>
		</div>
		<div class="sw-spec-list-entry-actions">
			<p class="ellipsis" style="margin-bottom: 5px;"><span class="sw-spec-list-entry-label">#= i18next("public.Created") #:</span> #= formattedDate(createdDate) #</p>
			<p class="ellipsis"><span class="sw-spec-list-entry-label">#= i18next("public.Updated") #:</span> #= formattedDate(updatedDate) #</p>
# if (data.inDetailView == null) { #
			<div class="btn-group btn-group-vertical" style="position: absolute; right: 0px; top: 3px;">
				<a class="btn btn-small sw-action-glyph sw-primary-glyph" title="#= i18next('public.EditSpecification') #" style="color: \\#009;"  
					href="javascript:void(0)" onclick="onSpecificationEditClicked(event, '#:token#');">
					<i class="icon-edit icon-white"></i></a>
				<a class="btn btn-small sw-action-glyph sw-primary-glyph" title="#= i18next('public.DeleteSpecification') #" style="color: \\#900;" 
					href="javascript:void(0)" onclick="onSpecificationDeleteClicked(event, '#:token#')">
					<i class="icon-remove icon-white"></i></a>
				<a class="btn btn-small sw-action-glyph sw-primary-glyph" title="#= i18next("public.ViewSpecification") #" style="color: \\#060;" 
					href="javascript:void(0)" onclick="onSpecificationOpenClicked(event, '#:token#')">
					<i class="icon-chevron-right icon-white"></i></a>
			</div>
# } #
		</div>
		<div>
			<p class="sw-spec-list-entry-heading ellipsis"><span title="#:name#">#:name#</span></p>
			<p class="ellipsis sw-spec-list-entry-subhead">
				<span class="sw-spec-list-entry-label" style="min-width: 50px;">#= i18next("public.Asset") #:</span><span title="#:asset.name#">#:asset.name# (SKU: #:asset.sku#)</span></p>
			<p class="ellipsis sw-spec-list-entry-subhead">
				<span class="sw-spec-list-entry-label" style="min-width: 50px;">Token:</span><span title="#:token#">#:token#</span></p>
			<p class="ellipsis sw-spec-list-entry-subhead">
				<span class="sw-spec-list-entry-label" style="min-width: 50px;">#= i18next("includes.Desc") #:</span><span title="#:asset.description#">#:asset.description#</span></p>
		</div>
	</div>
</script>