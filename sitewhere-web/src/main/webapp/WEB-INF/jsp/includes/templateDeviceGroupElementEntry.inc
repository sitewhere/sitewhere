<!-- Template for device group element row -->
<style>
.grp-elm-icon {
	margin-right: 7px;
	vertical-align: middle;
}
</style>
<script type="text/x-kendo-tmpl" id="tpl-device-group-element-entry">
	<tr class="sw-list-entry row-gradient">
# if (data.device) { #
		<td class="ellipsis"><i class="icon-desktop icon-white grp-elm-icon"></i>#:type# [#:elementId#]</td>
		<td class="ellipsis">#:device.specification.assetName#</td>
# } else if (data.group) { #
		<td class="ellipsis"><i class="icon-sitemap icon-white grp-elm-icon"></i>#:type# [#:elementId#]</td>
		<td class="ellipsis">#:group.name#</td>
# } #
		<td class="ellipsis">#:cdRoles#</td>
		<td>
			<div style="width: 100%; text-align: left">
				<i class="icon-remove sw-action-glyph sw-delete-glyph" title="Delete Group Element"
					onclick="onDeleteDeviceGroupElement(event, '#:type#', '#:elementId#')"></i>
			</div>
		</td>
	</tr>
</script>