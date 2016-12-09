<!-- Template for command invocation parameter summary entry -->
<style>
.sw-iv-invocation-response-col-desc {
	position: absolute;
	left: 3px;
	max-width: 300px;
}
.sw-iv-invocation-response-col-date {
	position: absolute;
	left: 333px;
}
</style>
<script type="text/x-kendo-tmpl" id="tpl-inv-response-summary-entry">
	<tr class="sw-list-entry" style="position: relative;">
		<td class="sw-iv-invocation-response-col-desc ellipsis" style="border-bottom: none; padding: 4px;" title="#:description#">#:description#</td>
		<td class="sw-iv-invocation-response-col-date" style="border-bottom: none; padding: 4px;">#= formattedDate(kendo.parseDate(data.date))#</td>
	</tr>
</script>