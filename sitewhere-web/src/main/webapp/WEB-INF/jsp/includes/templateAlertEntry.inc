<!-- Template for alert row -->
<script type="text/x-kendo-tmpl" id="tpl-alert-entry">
	<tr class="sw-list-entry row-gradient">
		<td class="ellipsis" style="max-width: 90px;" title="#:type#">#:type#</td>
		<td class="ellipsis" style="max-width: 250px;" title="#:message#">#:message#</td>
		<td class="ellipsis">#:source#</td>
		<td>#= formattedDate(eventDate) #</td>
	</tr>
</script>