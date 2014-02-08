<!-- Template for location row -->
<script type="text/x-kendo-tmpl" id="tpl-location-entry">
	<tr class="sw-list-entry row-gradient">
		<td>
			<div>#= kendo.toString(latitude, "\\#\\#\\#.000000")#</div>
		</td>
		<td>
			<div>#= kendo.toString(longitude, "\\#\\#\\#.000000")#</div>
		</td>
		<td>
			<div>#= kendo.toString(elevation, "\\#\\#\\#.000000")#</div>
		</td>
		<td>#= formattedDate(eventDate) #</td>
	</tr>
</script>