<!-- Dialog for assigning a device -->
<div id="ac-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 id="dialog-header">Assign Device</h3>
	</div>
	<div class="modal-body">
		<div id="ac-tabs">
			<ul>
				<li class="k-state-active" data-i18n="includes.assignmentUpdateDialog.AssignmentDetails"></li>
				<li data-i18n="includes.assignmentUpdateDialog.AssetAssociation"></li>
				<li data-i18n="public.Metadata"></li>
			</ul>
			<div>
				<form id="ac-general-form" class="form-horizontal" style="padding-top: 20px;">
					<div class="control-group">
						<label class="control-label" for="ac-hardware-id">Hardware Id</label>
						<div class="controls">
							<input type="text" id="ac-hardware-id" title="Hardware id" class="input-xlarge">
						</div>
					</div>
				</form>
			</div>
			<div>
				<div id="ac-asset-association">
				<form id="ac-asset-form" class="form-horizontal" style="padding-top: 20px;">
					<div class="control-group">
						<label class="control-label" for="ac-associate-asset">Associate Asset</label>
						<div class="controls">
							<input type="checkbox" id="ac-associate-asset" class="input-large"/>
						</div>
					</div>
					<div id="ac-asset-information" style="display: none;">
						<div class="control-group">
							<label class="control-label" for="ac-asset-module">Asset Provider</label>
							<div class="controls">
								<select id="ac-asset-module" class="input-xlarge"/></select>
							</div>
						</div>
						<div class="control-group"">
							<label class="control-label" for="ac-asset-search">Choose Asset</label>
							<div class="controls">
								<div class="input-append">
		    						<input class="input-xlarge" id="ac-asset-search" type="text">
									<span class="add-on"><i class="icon-search"></i></span>
		    					</div>
								<div id="ac-asset-matches" class="sw-form-search-results"
									style="height: 180px;"></div>
								<input type="hidden" id="ac-asset-id" title="Asset"/>
							</div>
						</div>
					</div>
				</form>
				</div>
            </div>
			<div>
				<div id="ac-metadata">
					<jsp:include page="../includes/metadata.jsp" flush="true">
					    <jsp:param name="uid" value="ac"/>
					</jsp:include>
				</div>
            </div>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal" data-i18n="public.Cancel"></a> 
		<a id="ac-submit" href="javascript:void(0)" class="btn btn-primary" data-i18n="includes.assignmentCreateDialog.Assign"></a>
	</div>
</div>

<!-- Script support for assignment create dialog -->
<script>
	/** Function called when dialog is submitted */
	var acSubmitCallback;

	/** Provides external access to tabs */
	var acTabs;
	
	/** Datasource for asset module list */
	var acAssetModulesDS;
	
	/** Asset modules dropdown */
	var acAssetModules;
	
	/** Datasource for matching assets */
	var acAssetsDS;
	
	/** Asset matches list */
	var acAssetMatches;
	
	/** Used for delayed submit on search */
	var acTimeout;
	var acLastSearch;

	$(document).ready(function() {
        
        /** Handle hiding asset selection based on checkbox */
        $("#ac-associate-asset").click(onAssetCheckChange).change(onAssetCheckChange);
		
		/** Create AJAX datasource for all asset modules */
		acAssetModulesDS = new kendo.data.DataSource({
			transport : {
				read : {
					url : "${pageContext.request.contextPath}/api/assets/modules",
					dataType : "json",
				}
			},
		});

		/** Create asset modules dropdown */
    	acAssetModules = $("#ac-asset-module").kendoDropDownList({
    		dataTextField: "name",
    		dataValueField: "id",
    	    dataSource: acAssetModulesDS,
    	    index: 0,
    	    change: acAssetModuleChanged,
			dataBound: function(e) {
				acAssetModuleChanged();
			}
    	}).data("kendoDropDownList");
		
		/** Create the asset match list */
		acAssetMatches = $("#ac-asset-matches").kendoListView({
			selectable : "single",
			template : kendo.template($("#hardware-asset-entry").html()),
			change: acAssetChosen
		}).data("kendoListView");
		
		/** Update hardware search datasource based on entered criteria */
		$("#ac-asset-search").bind("change paste keyup", function() {
		    window.clearTimeout(acTimeout);
		    acTimeout = window.setTimeout(acSearchCriteriaUpdated, 300); 
		});
		
		/** Create tab strip for the update dialog */
		acTabs = $("#ac-tabs").kendoTabStrip({
			animation: false
		}).data("kendoTabStrip");
		
        /** Handle dialog submit */
		$('#ac-submit').click(function(event) {
			event.preventDefault();
			if (!acValidate()) {
				return;
			}
	        
			var assignmentData = {
				"deviceHardwareId": $('#ac-hardware-id').val(), 
				"metadata": swMetadataAsLookup(acMetadataDS.data()),
			}
			if (hasAssociatedAsset()) {
		        var assetModuleId = $("#ac-asset-module").val();
		        var assetId = $('#ac-asset-id').val();
		        assignmentData.assignmentType = "Associated";
		        assignmentData.assetModuleId = assetModuleId;
		        assignmentData.assetId = assetId;
			} else {
		        assignmentData.assignmentType = "Unassociated";
			}
			$.postJSON("${pageContext.request.contextPath}/api/assignments", 
					assignmentData, onCreateSuccess, onCreateFail);
		});
        
        /** Called on successful create */
        function onCreateSuccess() {
        	$('#ac-dialog').modal('hide');
			if (acSubmitCallback != null) {
				acSubmitCallback();
			}
        }
        
		/** Handle failed call to create device */
		function onCreateFail(jqXHR, textStatus, errorThrown) {
			handleError(jqXHR, "Unable to assign device.");
		}
		
		/** Called when checkbox for asset association changes */
		function onAssetCheckChange() {
			if (hasAssociatedAsset()) {
				$("#ac-asset-information").show();
			} else {
				$("#ac-asset-information").hide();
			}
		}
    });
	
	/** Validate the dialog */
	function acValidate() {
		$.validity.setup({ outputMode:"label" });
		$.validity.start();

        /** Validate main form */
		$("#ac-hardware-id").require();
	    
        /** If associating asset, verify fields */
	    if (hasAssociatedAsset()) {
			$("#ac-asset-module").require();
	        $('#ac-asset-id').require();
		}
      
		var result = $.validity.end();
		return result.valid;
	}
	
	/** Indicates if the asset association checkbox is checked */
	function hasAssociatedAsset() {
		return $("#ac-associate-asset").is(':checked');
	}
	
	/** Called when selected asset module changes */
	function acAssetModuleChanged() {
		var assetModuleId = acGetSelectedAssetModuleId();
		var criteria = $('#ac-asset-search').val();
		acAssetDS = new kendo.data.DataSource({ 
			transport : {
				read : {
					url : "${pageContext.request.contextPath}/api/assets/" + assetModuleId + "?criteria=" + criteria,
					dataType : "json",
				}
			},
			schema : {
				data: "results",
				total: "numResults",
			},
		});
		var module = acGetSelectedAssetModule();
		var template = getTemplateForAssetModule(module);
		if (acAssetMatches) {
			acAssetMatches.destroy();
		}
		acAssetMatches = $("#ac-asset-matches").kendoListView({
			dataSource: acAssetDS,
			selectable : "single",
			template : template,
			change: acAssetChosen
		}).data("kendoListView");
	}
	
	/** Gets the currently selected asset module id */
	function acGetSelectedAssetModuleId() {
		return $("#ac-asset-module").val();
	}
	
	/** Called when an asset is chosen from the list */
	function acGetSelectedAssetModule() {
		var listView = acAssetModulesDS.view();
		var selIndex = acAssetModules.select();
		return listView[selIndex];
	}
	
	/** Get asset template based on asset module */
	function getTemplateForAssetModule(module) {
		if (module.assetType == "Person") {
			return kendo.template($("#person-asset-entry").html());
		} else if (module.assetType == "Location") {
			return kendo.template($("#location-asset-entry").html());
		}
		return kendo.template($("#hardware-asset-entry").html());
	}
	
	/** Called when search criteria has changed */
	function acSearchCriteriaUpdated() {
		var assetModuleId = acGetSelectedAssetModuleId();
		var criteria = $('#ac-asset-search').val();
		if (criteria != acLastSearch) {
			var url = "${pageContext.request.contextPath}/api/assets/" + assetModuleId + "?criteria=" + criteria;
			acAssetDS.transport.options.read.url = url;
			acAssetDS.read();
		}
		acLastSearch = criteria;
	}
	
	/** Called when an asset is chosen from the list */
	function acAssetChosen() {
		var listView = acAssetDS.view();
		var selected = $.map(this.select(), function(item) {
			return listView[$(item).index()];
		});

		if (selected.length > 0) {
			$('#ac-asset-id').val(selected[0].id);
		} else {
			$('#ac-asset-id').val("");
		}	
	}
	
	/** Open the dialog */
	function acOpen(e, hardwareId, callback) {
		var event = e || window.event;
		if (event) {
			event.stopPropagation();
		}
		
		$('#ac-general-form')[0].reset();
		$('#ac-hardware-id').val(hardwareId);
		acMetadataDS.data(new Array());
		
		// Reset list selections.
		$('#ac-asset-id').val("");
		acAssetMatches.clearSelection();
   	
		// Select first tab.
		acTabs.select(0);
		
		// Function called on submit.
		acSubmitCallback = callback;
		
		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#ac-dialog').modal('show');
	}
</script>