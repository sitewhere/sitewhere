<!-- Template for scheduled job row -->
<script type="text/x-kendo-tmpl" id="tpl-scheduled-job-entry">
	<tr class="sw-list-entry row-gradient">
# if (data.jobType == 'CommandInvocation') { #
		<td>Invoke Command</td>
		<td>Invoke #= context['invocationHtml'] # for <a href="../devices/#:context['assignment'].deviceHardwareId#.html">#= context['assignment'].device.assetName #</a> assigned to <a href="../assignments/#:context['assignment'].token#.html">#= context['assignment'].assetName #</a>.</td>
# } else if (data.jobType == 'BatchCommandInvocation') { #
		<td>Invoke Batch Command</td>
		<td>Invoke #= context['invocationHtml'] # for #= context['criteriaHtml'] #.</td>
# } else { #
		<td>#:jobType#</td>
		<td>#:token#</td>
# } #
# if (data.context['schedule']) { #
		<td>#= context['schedule'].name #</td>
# } else { #
		<td>#:scheduleToken#</td>
# } #
		<td>#= formattedDate(createdDate) #</td>
		<td>
			<div style="width: 100%; text-align: center">
				<i class="fa fa-remove sw-action-glyph sw-delete-glyph" title="#= i18next("jobs.list.DeleteJob") #"
					onclick="onDeleteJob('#:token#')"></i>
			</div>
		</td>
	</tr>
</script>