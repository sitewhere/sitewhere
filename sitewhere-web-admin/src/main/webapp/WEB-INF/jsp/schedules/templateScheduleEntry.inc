<!-- Template for schedule row -->
<script type="text/x-kendo-tmpl" id="tpl-schedule-entry">
	<tr class="sw-list-entry row-gradient">
		<td>#:name#</td>
		<td>#:triggerType#</td>
		<td>#:token#</td>
		<td>#= formattedDate(createdDate) #</td>
		<td>
			<div style="width: 100%; text-align: center">
				<i class="fa fa-edit sw-action-glyph sw-edit-glyph" title="#= i18next("schedules.list.EditSchedule") #"
					onclick="onEditSchedule('#:token#')"></i>
				<i class="fa fa-remove sw-action-glyph sw-delete-glyph" title="#= i18next("schedules.list.DeleteSchedule") #"
					onclick="onDeleteSchedule('#:token#')"></i>
			</div>
		</td>
	</tr>
</script>