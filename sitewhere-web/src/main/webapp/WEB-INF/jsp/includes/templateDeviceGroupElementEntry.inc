<!-- Template for device group element row -->
<script type="text/x-kendo-tmpl" id="tpl-device-group-element-entry">
	<tr class="sw-list-entry row-gradient">
		<td>#:type#</td>
		<td>#:elementId#</td>
		<td>#:cdRoles#</td>
		<td>
			<div style="width: 100%; text-align: left">
				<i class="icon-remove sw-action-glyph sw-delete-glyph" title="Delete Zone"
					onclick="onDeleteElement(event, '#:elementId#')"></i>
			</div>
		</td>
	</tr>
</script>