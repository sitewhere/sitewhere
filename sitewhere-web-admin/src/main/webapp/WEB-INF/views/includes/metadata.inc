<script>

	/** Reference for metadata datasource */
	var ${uid}MetadataDS = swMetadataDatasource();

	/** Metadata UI */
	$(document).ready(function() {
		$("#sw-metadata-${uid}").kendoListView({
			dataSource : ${uid}MetadataDS,
			template : kendo.template($("#tpl-sw-metadata-${uid}").html())
		});
	});

	function onAddMetadata_${uid}() {
		// Reset error.
		$("#sw-metadata-error-${uid}").hide();
		var error = "";
		
		// Create metadata entry.
		var metadata = {};
		metadata.name = $("#sw-metadata-name-${uid}").val();
		metadata.value = $("#sw-metadata-value-${uid}").val();
		
		// Check for empty.
		if (metadata.name.length == 0) {
			error = i18next("includes.metadata.Nameisrequired");
		}
		var regex = /^[\w-]+$/;
		if (!regex.test(metadata.name)) {
			error = i18next("includes.metadata.ICIN");
		}
		
		// Check for already used.
		var data = ${uid}MetadataDS.data();
		for (var index = 0, existing; existing = data[index]; index++) {
			if (metadata.name == existing.name) {
				error = i18next("includes.metadata.NIABU");
				break;
			}
		}
		if (error.length > 0) {
			$("#sw-metadata-error-${uid}").html(error);
			$("#sw-metadata-error-${uid}").toggle();
		} else {
			${uid}MetadataDS.data().push(metadata);
			$("#sw-metadata-name-${uid}").val("");
			$("#sw-metadata-value-${uid}").val("");
		}
	}

	/** Deletes a metadata entry by name */
	function onDeleteMetadata_${uid}(name) {
		var data = ${uid}MetadataDS.data();
		for (var index = 0, existing; existing = data[index]; index++) {
			if (existing.name == name) {
				${uid}MetadataDS.data().splice(index, 1);
				return;
			}
		}
	}
</script>
<script type="text/x-kendo-tmpl" id="tpl-sw-metadata-${uid}">
	<tr class="sw-list-entry">
		<td style="width: 205px">#:name#</td>
		<td style="width: 145px">#:value#</td>
		<td>
#if (!$mdReadOnly)
			<div style="text-align: right;">
				<i class="fa fa-remove sw-action-glyph sw-delete-glyph" title="#= i18next("includes.metadata.DeleteMetadata") #"
					onclick="onDeleteMetadata_${uid}('#:name#')"></i>
			</div>
#end
		</td>
	</tr>
</script>
<div style="max-height: 300px; overflow-y: auto;">
	<div class="sw-sublist-header">
		<div style="width: 205px;" data-i18n="public.Name"></div>
		<div style="width: 145px" data-i18n="public.Value"></div>
	</div>
	<table id="sw-metadata-${uid}" class="sw-sublist-list">
	</table>
</div>

<!-- #if (!$mdReadOnly) -->
<div class="sw-sublist-add-new">
	<div class="sw-sublist-footer">
		<div style="width: 225px; margin-left: 3px;" data-i18n="public.Name"></div>
		<div style="width: 145px" data-i18n="public.Value"></div>
	</div>
	<input type="text" id="sw-metadata-name-${uid}"
		style="width: 205px; margin-bottom: 0px; margin-right: 10px;"
		title="Metadata name"> <input type="text"
		id="sw-metadata-value-${uid}"
		style="width: 150px; margin-bottom: 0px; margin-right: 10px;"
		title="Metadata value"> <a class="btn"
		href="javascript:void(0)" onclick="onAddMetadata_${uid}()"> <i
		class="fa fa-plus sw-button-icon"></i> <span data-i18n="public.Add">Add</span>
	</a>
	<div id="sw-metadata-error-<${uid}" style="color: #f00; display: none;"></div>
</div>
<!-- #end -->

