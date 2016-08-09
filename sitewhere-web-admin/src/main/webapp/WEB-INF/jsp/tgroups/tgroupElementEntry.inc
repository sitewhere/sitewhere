<!-- Template for tenant group element row -->
<script type="text/x-kendo-tmpl" id="tpl-tgroup-element-entry">
# if (data.tenant) { #
	<tr class="sw-list-entry row-gradient">
		<td>#:tenant.id#</td>
		<td>#:tenant.name#</td>
# if (data.tenant.engineState) { #
		<td>#:tenant.engineState.lifecycleStatus#</td>
# } else { #
		<td>Unknown</td>
# } #
		<td>
			<div style="width: 100%; text-align: left">
				<i class="fa fa-remove sw-action-glyph sw-delete-glyph" title="Remove Tenant from Group"
					onclick="onDeleteTenantGroupElement(event, '#:tenant.id#')"></i>
			</div>
		</td>
	</tr>
# } #
</script>