<script>

	/** Reference for metadata datasource */
	var <%= request.getParameter("uid")%>MetadataDS = swMetadataDatasource();

	/** Metadata UI */
	$(document).ready(function() {
		$("#sw-metadata-<%= request.getParameter("uid")%>").kendoListView({
			dataSource : <%= request.getParameter("uid")%>MetadataDS,
			template : kendo.template($("#tpl-sw-metadata-<%= request.getParameter("uid")%>").html())
		});
	});

	function onAddMetadata_<%= request.getParameter("uid")%>() {
		// Reset error.
		$("#sw-metadata-error-<%= request.getParameter("uid")%>").hide();
		var error = "";
		
		// Create metadata entry.
		var metadata = {};
		metadata.name = $("#sw-metadata-name-<%= request.getParameter("uid")%>").val();
		metadata.value = $("#sw-metadata-value-<%= request.getParameter("uid")%>").val();
		
		// Check for empty.
		if (metadata.name.length == 0) {
			error = i18next("includes.metadata.Nameisrequired");
		}
		var regex = /^[\w-]+$/;
		if (!regex.test(metadata.name)) {
			error = i18next("includes.metadata.ICIN");
		}
		
		// Check for already used.
		var data = <%= request.getParameter("uid")%>MetadataDS.data();
		for (var index = 0, existing; existing = data[index]; index++) {
			if (metadata.name == existing.name) {
				error = i18next("includes.metadata.NIABU");
				break;
			}
		}
		if (error.length > 0) {
			$("#sw-metadata-error-<%= request.getParameter("uid")%>").html(error);
			$("#sw-metadata-error-<%= request.getParameter("uid")%>").toggle();
		} else {
			<%= request.getParameter("uid")%>MetadataDS.data().push(metadata);
			$("#sw-metadata-name-<%= request.getParameter("uid")%>").val("");
			$("#sw-metadata-value-<%= request.getParameter("uid")%>").val("");
		}
	}

	/** Deletes a metadata entry by name */
	function onDeleteMetadata_<%= request.getParameter("uid")%>(name) {
		var data = <%= request.getParameter("uid")%>MetadataDS.data();
		for (var index = 0, existing; existing = data[index]; index++) {
			if (existing.name == name) {
				<%= request.getParameter("uid")%>MetadataDS.data().splice(index, 1);
				return;
			}
		}
	}
</script>
<script type="text/x-kendo-tmpl" id="tpl-sw-metadata-<%= request.getParameter("uid")%>">
	<tr class="sw-list-entry">
		<td style="width: 205px">#:name#</td>
		<td style="width: 145px">#:value#</td>
		<td>
<% if (request.getParameter("mdReadOnly") == null) { %>
			<div style="text-align: right;">
				<i class="icon-remove sw-action-glyph sw-delete-glyph" title="#= i18next("includes.metadata.DeleteMetadata") #"
					onclick="onDeleteMetadata_<%= request.getParameter("uid")%>('#:name#')"></i>
			</div>
<% } %>
		</td>
	</tr>
</script>
<div class="sw-sublist-header">
	<div style="width: 205px;" data-i18n="public.Name"></div>
	<div style="width: 145px" data-i18n="public.Value"></div>
</div>
<table id="sw-metadata-<%= request.getParameter("uid")%>" class="sw-sublist-list">
</table>

<% if (request.getParameter("mdReadOnly") == null) { %>
<div class="sw-sublist-add-new">
	<div class="sw-sublist-footer">
		<div style="width: 225px; margin-left: 3px;" data-i18n="public.Name"></div>
		<div style="width: 145px" data-i18n="public.Value"></div>
	</div>
	<input type="text" id="sw-metadata-name-<%= request.getParameter("uid")%>" 
		style="width: 205px; margin-bottom: 0px; margin-right: 10px;" title="Metadata name">
	<input type="text" id="sw-metadata-value-<%= request.getParameter("uid")%>" 
		style="width: 150px; margin-bottom: 0px; margin-right: 10px;" title="Metadata value">
	<a class="btn" href="javascript:void(0)" onclick="onAddMetadata_<%= request.getParameter("uid")%>()" data-i18n="public.Add">
		<i class="icon-plus sw-button-icon"></i></a>
	<div id="sw-metadata-error-<%= request.getParameter("uid")%>" style="color: #f00; display: none;"></div>
</div>	
<% } %>
