<!-- Template for command parameter -->
<style>
.sw-command-param-actions {
	text-align: right;
}
</style>
<script type="text/x-kendo-tmpl" id="tpl-command-param-entry">
	<tr class="sw-list-entry">
		<td class="sw-cc-command-params-col-name">#:name#</td>
		<td class="sw-cc-command-params-col-type">#:type#</td>
# if (data.required) { #
		<td class="sw-cc-command-params-col-req"><i class="fa fa-check sw-button-icon"></i></td>
# } else { #
		<td class="sw-cc-command-params-col-req"></td>
# } #
		<td class="sw-cc-command-params-col-add">
			<div class="sw-command-param-actions">
				<i class="fa fa-remove sw-action-glyph sw-delete-glyph" title="Delete Parameter"
					onclick="onDeleteParameter('#:data.name#')"></i>
			</div>
		</td>
	</tr>
</script>