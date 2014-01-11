<!-- Dialog for creating a device -->
<div id="spc-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 id="spc-dialog-header">Create Specification</h3>
	</div>
	<div class="modal-body">
		<div id="spc-tabs">
			<ul>
				<li class="k-state-active">Specification Details</li>
				<li>Metadata</li>
			</ul>
			<div>
				<form id="spc-general-form" class="form-horizontal form-search" style="padding-top: 20px;">
					<div class="control-group">
						<label class="control-label" for="spc-name">Name</label>
						<div class="controls">
							<input type="text" id="spc-name" title="Specification Name" class="input-xlarge">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="spc-hardware-search">Choose Hardware Type</label>
						<div class="controls">
							<div class="input-append">
	    						<input class="input-xlarge" id="spc-hardware-search" type="text">
								<span class="add-on"><i class="icon-search"></i></span>
	    					</div>
							<div id="spc-hardware-matches" class="sw-form-search-results"></div>
							<input type="hidden" id="spc-chosen-asset-id" title="Hardware type"/>
	    				</div>
	    			</div>
	    		</form>		
			</div>
			<div>
				<div id="spc-metadata" class="sw-metadata-grid"></div>
            </div>
		</div>
	</div>
	<input id="spec-token" type="hidden" value="" />
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal">Cancel</a> 
		<a id="spc-dialog-submit" href="javascript:void(0)" class="btn btn-primary">Create</a>
	</div>
</div>

<!-- Script support for device specification create dialog -->
<script>
	/** Function called when dialog is submitted */
	var spcSubmitCallback;

	/** Provides external access to tabs */
	var spcTabs;
	
	/** Metadata datasource */
	var spcMetadataDS;
	
	/** Datasource for hardware assets */
	var spcHardwareDS;
	
	/** Hardware matches list */
	var spcHardwareMatches;
	
	/** Used for delayed submit on search */
	var spcTimeout;
	var spcLastSearch;

	$(document).ready(function() {
		
		/** Create tab strip */
		spcTabs = $("#spc-tabs").kendoTabStrip({
			animation: false
		}).data("kendoTabStrip");
		
		/** Local source for metadata entries */
		spcMetadataDS = swMetadataDatasource();
		
		/** Grid for metadata */
        $("#spc-metadata").kendoGrid(swMetadataGridOptions(spcMetadataDS));
		
		/** Create AJAX datasource for hardware search */
		spcHardwareDS = new kendo.data.DataSource({
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
		spcHardwareMatches = $("#spc-hardware-matches").kendoListView({
			dataSource : spcHardwareDS,
			selectable : "single",
			template : kendo.template($("#hardware-asset-entry").html()),
			change: spcHardwareAssetChosen,
			dataBound: function(e) {
		    	var assetId = $('#spc-chosen-asset-id').val();
		    	var assetElm = $('#asset-' + assetId).get(0);
		    	if (assetElm) {
		    		spcHardwareMatches.select(assetElm);
		    	}
			}
		}).data("kendoListView");
		
		/** Update hardware search datasource based on entered criteria */
		$("#spc-hardware-search").bind("change paste keyup", function() {
		    window.clearTimeout(spcTimeout);
		    spcTimeout = window.setTimeout(spcHardwareSearchCriteriaUpdated, 300); 
		});
		
        /** Handle create dialog submit */
		$('#spc-dialog-submit').click(function(event) {
			event.preventDefault();
			if (!spcValidate()) {
				return;
			}
			var specData = {
				"name": $('#spc-name').val(), 
				"assetId": $('#spc-chosen-asset-id').val(), 
				"metadata": swMetadataAsLookup(spcMetadataDS.data()),
			}
			var specToken = $('#spec-token').val();
			if (specToken == "") {
				$.postJSON("${pageContext.request.contextPath}/api/specifications", 
						specData, onCreateSuccess, onCreateFail);
			} else {
				$.putJSON("${pageContext.request.contextPath}/api/specifications/" + specToken, 
						specData, onCreateSuccess, onUpdateFail);
			}
		});
        
        /** Called on successful create */
        function onCreateSuccess() {
        	$('#spc-dialog').modal('hide');
			if (spcSubmitCallback != null) {
				spcSubmitCallback();
			}
        }
        
		/** Handle failed call to create device specification */
		function onCreateFail(jqXHR, textStatus, errorThrown) {
			handleError(jqXHR, "Unable to create specification.");
		}
        
		/** Handle failed call to update device specification */
		function onUpdateFail(jqXHR, textStatus, errorThrown) {
			handleError(jqXHR, "Unable to update specification.");
		}
    });
	
	/** Validate everything */
	function spcValidate() {
		$.validity.setup({ outputMode:"label" });
		$.validity.start();

        /** Validate spec name */
		$("#spc-name").require();

        /** Verify that an asset was chosen */
		$("#spc-chosen-asset-id").require();
      
		var result = $.validity.end();
		return result.valid;
	}
	
	/** Called when hardware search criteria has changed */
	function spcHardwareSearchCriteriaUpdated() {
		var criteria = $('#spc-hardware-search').val();
		if (criteria != spcLastSearch) {
			var url = "${pageContext.request.contextPath}/api/assets/devices?criteria=" + criteria;
			spcHardwareDS.transport.options.read.url = url;
			spcHardwareDS.read();
		}
		spcLastSearch = criteria;
	}
	
	/** Called when a hardware asset is chosen from the list */
	function spcHardwareAssetChosen() {
		var listView = spcHardwareDS.view();
		var selected = $.map(this.select(), function(item) {
			return listView[$(item).index()];
		});

		if (selected.length > 0) {
			$('#spc-chosen-asset-id').val(selected[0].id);
		} else {
			$('#spc-chosen-asset-id').val("");
		}				
	}
	
	/** Open the dialog */
	function spcOpen(e, callback) {
		var event = e || window.event;
		event.stopPropagation();
		
		// Reset form and metadata.
    	$('#spc-general-form')[0].reset();
		spcMetadataDS.data(new Array());
		$('#spc-dialog-header').html("Create Specification");
		$('#spc-dialog-submit').html("Create");
		
		// Reset hardware search.
    	$('#spc-hardware-search').val("");
    	$('#spc-chosen-asset-id').val("");
    	spcHardwareSearchCriteriaUpdated();
    	spcHardwareMatches.clearSelection();
    	
    	// Select first tab.
		spcTabs.select(0);
		
		// Function called on submit.
		spcSubmitCallback = callback;
		
		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#spc-dialog').modal('show');
	}
	
	/** Called to load the given specification and open it for edit */
	function spuOpen(token, callback) {
		// Function called on submit.
		spcSubmitCallback = callback;
		
		// Get specification for edit.
		$.getJSON("${pageContext.request.contextPath}/api/specifications/" + token, 
				spuSpecificationGetSuccess, spuSpecificationGetFailed);
	}
	
	/** Open the dialog */
	function spuSpecificationGetSuccess(data, status, jqXHR) {
		$('#spc-dialog-header').html("Edit Specification");
		$('#spc-dialog-submit').html("Save");
		
		// Save token for update.
		$('#spec-token').val(data.token);
		
		// Reset form and metadata.
		$('#spc-name').val(data.name);
		spcMetadataDS.data(swLookupAsMetadata(data.metadata));
		
		// Reset hardware search.
    	$('#spc-hardware-search').val("");
    	$('#spc-chosen-asset-id').val(data.asset.id);
    	spcLastSearch = null;
    	spcHardwareSearchCriteriaUpdated();
    	
    	// Select first tab.
		spcTabs.select(0);
		
		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#spc-dialog').modal('show');
	}
    
	/** Handle error on getting specification */
	function spuSpecificationGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load specification for edit.");
	}
</script>