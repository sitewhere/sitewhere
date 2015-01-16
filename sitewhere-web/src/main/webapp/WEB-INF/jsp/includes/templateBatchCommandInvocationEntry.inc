<style>
.sw-batch-command-invocation-entry {
	
}
</style>

<script type="text/x-kendo-tmpl" id="tpl-batch-command-invocation-entry">
	<div class="sw-batch-command-invocation-entry gradient-bg sw-list-entry">
		<div>
			Batch Command Invocation Information
		</div>
	</div>
</script>

<script type="text/x-kendo-tmpl" id="tpl-batch-command-invocation-element-entry">
	<tr class="sw-list-entry row-gradient">
		<td>#:hardwareId#</td>
		<td>#:index#</td>
		<td>#:processingStatus#</td>
		<td>
			<i class="icon-circle-arrow-right sw-action-glyph sw-view-glyph" title="View Command Invocation"
				style="vertical-align: middle;" onclick="viewCommandInvocation(event, '#:hardwareId#')"></i>
		</td>
	</tr>
</script>