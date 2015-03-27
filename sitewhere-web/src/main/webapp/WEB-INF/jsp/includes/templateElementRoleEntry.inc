<!-- Template for a role entry -->
<style>
</style>
<script type="text/x-kendo-tmpl" id="tpl-element-role-entry">
	<tr class="sw-list-entry">
		<td>#= data #</td>
		<td>
			<div style="text-align: right;">
				<i class="icon-remove sw-action-glyph sw-delete-glyph" title="#= i18next("includes.DeleteRole") #"
					onclick="geaDeleteRole('#= data #')"></i>
			</div>
		</td>
	</tr>
</script>