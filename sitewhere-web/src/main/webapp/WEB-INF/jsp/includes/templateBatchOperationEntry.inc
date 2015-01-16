<!-- Template for batch operation row -->
<script type="text/x-kendo-tmpl" id="tpl-batch-operation-entry">
	<tr class="sw-list-entry row-gradient">
		<td>#:operationType#</td>
		<td>#:processingStatus#</td>
		<td>#= formattedDate(processingStartedDate) #</td>
		<td>#= formattedDate(processingEndedDate) #</td>
	</tr>
</script>