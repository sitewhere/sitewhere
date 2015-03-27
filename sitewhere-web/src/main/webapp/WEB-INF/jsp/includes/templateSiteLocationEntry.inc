<!-- Template for location row -->
<script type="text/x-kendo-tmpl" id="tpl-location-entry">
	<tr class="sw-list-entry">
		<td class="ellipsis" style="max-width: 90px;" title="#:assetName#">
			<div>
				<span class="ellipsis" style="max-width: 150px; display: inline-block; vertical-align: middle; line-height: normal">
					#:assetName#</span>
				<i class="icon-circle-arrow-right sw-action-glyph sw-view-glyph" title="#= i18next("public.ViewAssignment") #"
					style="vertical-align: middle;" onclick="onViewAssignment(event, '#:deviceAssignmentToken#')"></i>
			</div>
		</td>
		<td>
			<div>#= kendo.toString(latitude, "\\#\\#\\#.000000") + ", " + kendo.toString(longitude, "\\#\\#\\#.000000")+ ", " + kendo.toString(elevation, "\\#\\#\\#.000000")#</div>
		</td>
		<td>#= formattedDate(eventDate) #</td>
		<td>#= formattedDate(receivedDate) #</td>
	</tr>
</script>