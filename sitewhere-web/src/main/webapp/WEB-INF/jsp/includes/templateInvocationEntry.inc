<!-- Template for alert row -->
<script type="text/x-kendo-tmpl" id="tpl-invocation-entry">
	<tr class="sw-list-entry row-gradient">
		<td title="#:commandToken#">#:commandToken#</td>
		<td title="#:sourceActor#">#:sourceActor#</td>
		<td title="#:targetActor#">#:targetActor#</td>
		<td>#= formattedDate(eventDate) #</td>
		<td>#= formattedDate(receivedDate) #</td>
	</tr>
</script>