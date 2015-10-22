<!-- Template for scheduled job row -->
<script type="text/x-kendo-tmpl" id="tpl-scheduled-job-entry">
	<tr class="sw-list-entry row-gradient">
		<td>#:jobType#</td>
		<td>#:token#</td>
		<td>#:scheduleToken#</td>
		<td>#= formattedDate(createdDate) #</td>
		<td>
			<div style="width: 100%; text-align: center">
				<i class="fa fa-remove sw-action-glyph sw-delete-glyph" title="#= i18next("jobs.list.DeleteJob") #"
					onclick="onDeleteJob('#:token#')"></i>
			</div>
		</td>
	</tr>
</script>