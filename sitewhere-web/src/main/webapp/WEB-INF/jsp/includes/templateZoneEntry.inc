<!-- Template for location row -->
<script type="text/x-kendo-tmpl" id="tpl-zone-entry">
	<tr class="sw-list-entry row-gradient">
		<td style="padding: 5px;">
			<div style="width: 15px; height: 100% margin: 3px; border: 2px solid #= borderColor #">
				<div style="width: 100%; height: 15px; background-color: #= fillColor #; opacity: #= opacity #"></div>
			</div>
		</td>
		<td class="ellipsis" style="max-width: 200px;">#:name#</td>
		<td>#:token#</td>
		<td>#= formattedDate(createdDate) #</td>
		<td>#= formattedDate(updatedDate) #</td>
		<td>
			<div style="width: 100%; text-align: left">
				<i class="icon-edit sw-action-glyph sw-edit-glyph" title="#= i18next("includes.EditZone") #"
					onclick="onEditZone(event, '#:token#')"></i>
				<i class="icon-remove sw-action-glyph sw-delete-glyph" title="#= i18next("includes.DeleteZone") #"
					onclick="onDeleteZone(event, '#:token#')"></i>
			</div>
		</td>
	</tr>
</script>