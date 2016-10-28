<!-- Template for command response row -->
<script type="text/x-kendo-tmpl" id="tpl-response-entry">
	<tr class="sw-list-entry row-gradient">
		<td class="ellipsis" style="max-width: 250px;" title="#:originatingEventId#">#:originatingEventId#</td>
		<td class="ellipsis" style="max-width: 700px;" title="#:response#">#:response#</td>
		<td>#= formattedDate(eventDate) #</td>
	</tr>
</script>