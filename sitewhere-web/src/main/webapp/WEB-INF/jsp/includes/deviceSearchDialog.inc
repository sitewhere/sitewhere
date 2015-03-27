<!-- Dialog used to search for a single device -->
<div id="dvs-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 id="dvs-dialog-header">Device Search</h3>
	</div>
	<div class="modal-body">
		<div>
			<form id="dvs-general-form" class="form-horizontal" style="padding-top: 20px;">
				<div class="control-group">
					<label class="control-label" for="dvs-hardware-id">Hardware Id</label>
					<div class="controls">
						<input type="text" id="dvs-hardware-id" title="Hardware id" class="input-xlarge">
					</div>
				</div>
				<div id="dvs-device-matches" style="max-height: 200px; overflow: scroll;"></div>
				<div id="dvs-device-matches-pager" class="k-pager-wrap"></div>
				<input id="dvs-selected-hardware-id" type="hidden"/>
			</form>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal" data-i18n="public.Cancel"></a> 
		<a id="dvs-dialog-submit" href="javascript:void(0)" class="btn btn-primary" data-i18n="includes.deviceSearchDialog.Select"></a>
	</div>
</div>

<!-- Script support for device element add dialog -->
<script>
	/** Data passed through to callback */
	var dvsPassthrough;

	/** Function called when dialog is submitted */
	var dvsSubmitCallback;
	
	/** Datasource for device matches */
	var dvsMatchesDS;
	
	/** Device matches list view */
	var dvsMatches;

	$(document).ready(function() {
		
		/** Create AJAX datasource for devices list */
		dvsMatchesDS = new kendo.data.DataSource({
			transport : {
				read : {
					url : "${pageContext.request.contextPath}/api/devices?includeSpecification=true",
					dataType : "json",
				}
			},
			schema : {
				data: "results",
				total: "numResults",
				parse:function (response) {
				    $.each(response.results, function (index, item) {
				    	parseDeviceData(item);
				    });
				    return response;
				}
			},
            serverPaging: true,
            serverSorting: true,
            pageSize: 10,
		});
		
		/** Create the list of devices */
		dvsMatches = $("#dvs-device-matches").kendoListView({
			dataSource : dvsMatchesDS,
			selectable : "single",
			template : kendo.template($("#tpl-device-entry").html()),
			change: dvsDeviceChosen
		}).data("kendoListView");
		
		/** Pager for device list */
        $("#dvs-device-matches-pager").kendoPager({
            dataSource: dvsMatchesDS
        });
		
        /** Handle create dialog submit */
		$('#dvs-dialog-submit').click(function(event) {
			event.preventDefault();
			if (!dvsValidate()) {
				return;
			}
        	$('#dvs-dialog').modal('hide');
			if (dvsSubmitCallback != null) {
				dvsSubmitCallback(dvsPassthrough, $('#dvs-selected-hardware-id').val());
			}
		});
    });
	
	/** Perform validation */
	function dvsValidate() {
		$.validity.setup({ outputMode:"label" });
		$.validity.start();

        /** Validate fields */
		$("#dvs-selected-hardware-id").require();
     
		var result = $.validity.end();
		return result.valid;
	}
	
	/** Called when device selection changes */
	function dvsDeviceChosen() {
		var listView = dvsMatchesDS.view();
		var selected = $.map(this.select(), function(item) {
			return listView[$(item).index()];
		});

		if (selected.length > 0) {
			$('#dvs-selected-hardware-id').val(selected[0].hardwareId);
		} else {
			$('#dvs-selected-hardware-id').val("");
		}
	}
	
	/** Open the dialog */
	function dvsOpen(passthrough, callback) {
		// Reset selection.
    	$('#dvs-selected-hardware-id').val("");
		
		// Clear existing selection.
    	dvsMatches.clearSelection();
		
		// Function called on submit.
		dvsSubmitCallback = callback;
		
		// Data passed back to callback.
		dvsPassthrough = passthrough;
		
		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#dvs-dialog').modal('show');
	}
</script>