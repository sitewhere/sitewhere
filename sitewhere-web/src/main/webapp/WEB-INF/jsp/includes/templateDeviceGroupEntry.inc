<style>
.sw-groups-list-entry {
	clear: both;
	height: 80px;
	border: 1px solid #dcdcdc;
	padding: 10px;
	margin-bottom: 15px;
	font-size: 11pt;
	text-align: left;
	display: block;
}

.sw-groups-list-entry-heading {
	font-size: 14pt;
	font-weight: bold;
	line-height: 1;
	margin-bottom: 5px;
}

.sw-groups-list-entry-subhead {
	margin: 1px 0px;
}

.sw-groups-list-entry-label {
	font-size: 11pt;
	font-weight: bold;
	display: inline-block;
	min-width: 70px;
}

.sw-groups-list-entry-logowrapper {
	float: left;
	margin-top: 10px;
	margin-right: 20px;
	width: 70px;
	height: 70px;
	position: relative;
	border: 1px solid #ddd;
}

.sw-groups-list-entry-logo-tag {
	position: absolute;
	top: -15px;
	left: -2px;
}
.sw-groups-list-entry-actions {
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
<script type="text/x-kendo-tmpl" id="tpl-device-group-entry">
	<div class="sw-groups-list-entry gradient-bg">
		<div class="sw-groups-list-entry-logowrapper"
			style="background-image: url(${pageContext.request.contextPath}/img/devicegroup.png); background-size: contain; background-repeat: no-repeat; background-position: 50% 50%;">
			<span class="label label-info sw-groups-list-entry-logo-tag">Device Group</span>
		</div>
		<div class="sw-groups-list-entry-actions">
			<p class="ellipsis" style="margin-bottom: 5px;"><span class="sw-groups-list-entry-label">Created:</span> #= formattedDate(createdDate) #</p>
			<p class="ellipsis"><span class="sw-groups-list-entry-label">Updated:</span> #= formattedDate(updatedDate) #</p>
# if (data.inDetailView == null) { #
			<div class="btn-group btn-group-vertical" style="position: absolute; right: 0px; top: 3px;">
				<a class="btn btn-small btn-primary" title="Edit Device Group" 
					href="javascript:void(0)" onclick="onDeviceGroupEditClicked(event, '#:token#');">
					<i class="icon-edit icon-white"></i></a>
				<a class="btn btn-small btn-danger" title="Delete Device Group" 
					href="javascript:void(0)" onclick="onDeviceGroupDeleteClicked(event, '#:token#')">
					<i class="icon-remove icon-white"></i></a>
				<a class="btn btn-small btn-success" title="View Device Group" 
					href="javascript:void(0)" onclick="onDeviceGroupOpenClicked(event, '#:token#')">
					<i class="icon-chevron-right icon-white"></i></a>
			</div>
# } #
		</div>
		<div>
			<p class="sw-groups-list-entry-heading ellipsis"><span title="#:name#">#:name#</span></p>
			<p class="ellipsis sw-groups-list-entry-subhead">
				<span class="sw-groups-list-entry-label" style="min-width: 50px;">Token:</span><span title="#:token#">#:token#</span></p>
			<p class="ellipsis sw-groups-list-entry-subhead">
				<span class="sw-groups-list-entry-label" style="min-width: 50px;">Desc:</span><span title="#:description#">#:description#</span></p>
		</div>
	</div>
</script>