<!-- Dialog for creating a device -->
<div id="dc-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3>Create Device</h3>
	</div>
	<div class="modal-body">
		<div id="dc-tabs">
			<ul>
				<li class="k-state-active">Device Details</li>
				<li>Hardware</li>
				<li>Metadata</li>
			</ul>
			<div>
				<form id="dc-general-form" class="form-horizontal" style="padding-top: 20px;">
					<div class="control-group">
						<label class="control-label" for="dc-hardware-id">Hardware Id</label>
						<div class="controls">
							<input type="text" id="dc-hardware-id" title="Hardware id" class="input-xlarge">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="dc-comments">Comments</label>
						<div class="controls">
							<textarea id="dc-comments" class="input-xlarge" style="height: 10em;"></textarea>
						</div>
					</div>
				</form>
			</div>
			<div>
				<form id="dc-hardware-form" class="form-horizontal form-search" style="padding-top: 20px;">
					<div class="control-group">
						<label class="control-label" for="dc-hardware-search">Choose Hardware Type</label>
						<div class="controls">
							<div class="input-append">
	    						<input class="input-xlarge" id="dc-hardware-search" type="text">
								<span class="add-on"><i class="icon-search"></i></span>
	    					</div>
							<div id="dc-hardware-matches" class="sw-form-search-results"></div>
							<input type="hidden" id="dc-chosen-asset-id" title="Hardware type"/>
	    				</div>
	    			</div>
	    		</form>		
    		</div>
			<div>
				<div id="dc-metadata" class="sw-metadata-grid"></div>
            </div>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal">Cancel</a> 
		<a id="dc-dialog-submit" href="javascript:void(0)" class="btn btn-primary">Create</a>
	</div>
</div>

<!-- Script support for device create dialog -->
<script>
	/** Function called when dialog is submitted */
	var dcSubmitCallback;

	/** Provides external access to tabs */
	var dcTabs;
	
	/** Metadata datasource */
	var dcMetadataDS;
	
	/** Datasource for hardware assets */
	var dcHardwareDS;
	
	/** Hardware matches list */
	var dcHardwareMatches;
	
	/** Used for delayed submit on search */
	var dcTimeout;
	var dcLastSearch;

	$(document).ready(function() {
		
		/** Create tab strip */
		dcTabs = $("#dc-tabs").kendoTabStrip({
			animation: false
		}).data("kendoTabStrip");
		
		/** Local source for metadata entries */
		dcMetadataDS = swMetadataDatasource();
		
		/** Grid for metadata */
        $("#dc-metadata").kendoGrid(swMetadataGridOptions(dcMetadataDS));
		
		/** Create AJAX datasource for hardware search */
		dcHardwareDS = new kendo.data.DataSource({
			transport : {
				read : {
					url : "${pageContext.request.contextPath}/api/assets/devices",
					dataType : "json",
				}
			},
			schema : {
				data: "results",
				total: "numResults",
			},
		});
		
		/** Create the hardware match list */
		dcHardwareMatches = $("#dc-hardware-matches").kendoListView({
			dataSource : dcHardwareDS,
			selectable : "single",
			template : kendo.template($("#hardware-asset-entry").html()),
			change: dcHardwareAssetChosen
		}).data("kendoListView");
		
		/** Update hardware search datasource based on entered criteria */
		$("#dc-hardware-search").bind("change paste keyup", function() {
		    window.clearTimeout(dcTimeout);
		    dcTimeout = window.setTimeout(dcHardwareSearchCriteriaUpdated, 300); 
		});
		
        /** Handle create dialog submit */
		$('#dc-dialog-submit').click(function(event) {
			event.preventDefault();
			if (!dcValidate()) {
				return;
			}
			var deviceData = {
				"hardwareId": $('#dc-hardware-id').val(), 
				"comments": $('#dc-comments').val(), 
				"assetId": $('#dc-chosen-asset-id').val(), 
				"metadata": swMetadataAsLookup(dcMetadataDS.data()),
			}
			$.postJSON("${pageContext.request.contextPath}/api/devices", 
					deviceData, onCreateSuccess, onCreateFail);
		});
        
        /** Called on successful create */
        function onCreateSuccess() {
        	$('#dc-dialog').modal('hide');
			if (dcSubmitCallback != null) {
				dcSubmitCallback();
			}
        }
        
		/** Handle failed call to create device */
		function onCreateFail(jqXHR, textStatus, errorThrown) {
			handleError(jqXHR, "Unable to create device.");
		}
    });
	
	/** Validate everything */
	function dcValidate() {
		$.validity.setup({ outputMode:"label" });
		$.validity.start();

        /** Validate main form */
		$("#dc-hardware-id").require();

        /** Verify that an asset was chosen */
		$("#dc-chosen-asset-id").require();
      
		var result = $.validity.end();
		return result.valid;
	}
	
	/** Called when hardware search criteria has changed */
	function dcHardwareSearchCriteriaUpdated() {
		var criteria = $('#dc-hardware-search').val();
		if (criteria != dcLastSearch) {
			var url = "${pageContext.request.contextPath}/api/assets/devices?criteria=" + criteria;
			dcHardwareDS.transport.options.read.url = url;
			dcHardwareDS.read();
		}
		dcLastSearch = criteria;
	}
	
	/** Called when a hardware asset is chosen from the list */
	function dcHardwareAssetChosen() {
		var listView = dcHardwareDS.view();
		var selected = $.map(this.select(), function(item) {
			return listView[$(item).index()];
		});

		if (selected.length > 0) {
			$('#dc-chosen-asset-id').val(selected[0].id);
		} else {
			$('#dc-chosen-asset-id').val("");
		}				
	}
	
	/** Open the dialog */
	function dcOpen(e, callback) {
		var event = e || window.event;
		event.stopPropagation();
		
		// Reset form and metadata.
    	$('#dc-general-form')[0].reset();
		dcMetadataDS.data(new Array());
		
		// Reset hardware search.
    	$('#dc-hardware-search').val("");
    	$('#dc-chosen-asset-id').val("");
    	dcHardwareSearchCriteriaUpdated();
    	dcHardwareMatches.clearSelection();
    	
    	// Select first tab.
		dcTabs.select(0);
		
		// Function called on submit.
		dcSubmitCallback = callback;
		
		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#dc-dialog').modal('show');
	}
</script>