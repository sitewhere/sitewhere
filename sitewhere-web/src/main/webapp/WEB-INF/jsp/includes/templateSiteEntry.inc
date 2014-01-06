<style>
.sw-site-list-entry {
	clear: both;
	height: 80px;
	border: 1px solid #dcdcdc;
	padding: 10px;
	margin-bottom: 15px;
	font-size: 10pt;
	text-align: left;
	display: block;
}

.sw-site-list-entry-heading {
	font-size: 14pt;
	font-weight: bold;
	line-height: 1;
}

.sw-site-list-entry-label {
	font-size: 10pt;
	font-weight: bold;
	min-width: 70px;
	display: inline-block;
}

.sw-site-list-entry-logowrapper {
	float: left;
	margin-right: 15px;	
	width: 80px;
	height: 80px;
	background-color: #f0f0f0;
	border: 1px solid #dddddd;
}

.sw-site-list-entry-logo {
	display: block;
	margin-left: auto;
	margin-right: auto;
    max-width: 80px;
    max-height: 80px;
}

.sw-site-list-entry-actions {
	float: right;
	width: 250px;
	height: 100%;
	padding-left: 10px;
	margin-left: 10px;
	border-left: solid 1px #e0e0e0;
	position: relative;
}
</style>

<!-- Template for site row -->
<script type="text/x-kendo-tmpl" id="tpl-site-entry">
	<div class="sw-site-list-entry gradient-bg">
		<div class="sw-site-list-entry-logowrapper" 
			style="background-image: url(#:imageUrl#); background-size: contain; background-repeat: no-repeat; background-position: 50% 50%;">
		</div>
		<div class="sw-site-list-entry-actions">
			<p class="ellipsis"><span class="sw-site-list-entry-label">Created:</span> #= formattedDate(createdDate) #</p>
			<p class="ellipsis"><span class="sw-site-list-entry-label">Updated:</span> #= formattedDate(updatedDate) #</p>
# if (data.inDetailView == null) { #
			<div class="btn-group btn-group-vertical" style="position: absolute; right: 0px; top: 3px;">
				<a class="btn btn-small btn-primary" title="Edit Site" 
					href="javascript:void(0)" onclick="onSiteEditClicked(event, '#:token#');">
					<i class="icon-edit icon-white"></i></a>
				<a class="btn btn-small btn-danger" title="Delete Site" 
					href="javascript:void(0)" onclick="onSiteDeleteClicked(event, '#:token#')">
					<i class="icon-remove icon-white"></i></a>
				<a class="btn btn-small btn-success" title="View Site" 
					href="javascript:void(0)" onclick="onSiteOpenClicked(event, '#:token#')">
					<i class="icon-chevron-right icon-white"></i></a>
			</div>
# } #
		</div>
		<div>
			<p class="sw-site-list-entry-heading ellipsis">#:name#</p>
			<p>#:description#</p>
		</div>
	</div>
</script>
