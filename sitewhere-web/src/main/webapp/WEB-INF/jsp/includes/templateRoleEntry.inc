<!-- Template for a role entry -->
<style>
</style>
<script type="text/x-kendo-tmpl" id="tpl-role-entry">
	<tr class="sw-list-entry">
		<td>#= data #</td>
		<td>
			<div style="text-align: right;">
				<i class="icon-remove sw-action-glyph sw-delete-glyph" title="Delete Role"
					onclick="dgcDeleteRole('#= data #')"></i>
			</div>
		</td>
	</tr>
</script>