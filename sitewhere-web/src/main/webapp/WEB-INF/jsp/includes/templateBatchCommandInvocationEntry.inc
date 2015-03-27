<style>
.sw-batch-command-invocation-entry {
	padding: 20px;
	border: 1px solid #eee;
	margin-bottom: 15px;
}
</style>

<script type="text/x-kendo-tmpl" id="tpl-batch-command-invocation-entry">
	<div class="sw-batch-command-invocation-entry gradient-bg sw-list-entry">
		<div style="float: right; min-width: 330px;">
			<div><span class="sw-field-name">#= i18next("public.CreatedDate") #:</span><span class="sw-field-content">#= formattedDate(createdDate) #</span></div>
			<div><span class="sw-field-name">#= i18next("public.ProcessingStarted") #:</span><span class="sw-field-content">#= formattedDate(processingStartedDate) #</span></div>
			<div><span class="sw-field-name">#= i18next("public.ProcessingFinished") #:</span><span class="sw-field-content">#= formattedDate(processingEndedDate) #</span></div>
		</div>
		<div style="margin-bottom: 10px; border-bottom: 1px solid \\#ccc; padding-bottom: 10px;">
			<div><span class="sw-field-name">Token:</span><span class="sw-field-content">#:token#</span></div>
			<div><span class="sw-field-name">#= i18next("public.Operation") #:</span><span class="sw-field-content">#:operationType#</span></div>
			<div><span class="sw-field-name">#= i18next("includes.templateBatchCommandInvocationEntry.ProcessingStatus") #:</span><span class="sw-field-content">#:processingStatus#</span></div>
		</div>
		<div>
			#=commandHtml#
		</div>
	</div>
</script>

<script type="text/x-kendo-tmpl"
	id="tpl-batch-command-invocation-element-entry">
	<tr class="sw-list-entry row-gradient">
		<td>#:hardwareId#</td>
		<td>#:processingStatus#</td>
		<td>#= formattedDate(processedDate) #</td>
# if (processingStatus == 'Succeeded') { #
		<td><a href="javascript:void(0)" onclick="viewCommandInvocation(event, '#:metadata['invocation']#')">#:metadata['invocation']#</a></td>
# } else { #
		<td></td>
# } #
	</tr>
</script>