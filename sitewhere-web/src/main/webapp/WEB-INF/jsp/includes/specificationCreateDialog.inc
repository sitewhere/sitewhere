<!-- Dialog for creating a device -->
<div id="spc-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 id="spc-dialog-header" data-i18n="includes.specificaationCreateDialog.CreateSpecification"></h3>
	</div>
	<div class="modal-body">
		<div id="spc-tabs">
			<ul>
				<li class="k-state-active" data-i18n="includes.specificaationCreateDialog.SpecificationDetails"></li>
				<li data-i18n="public.Metadata"></li>
			</ul>
			<div>
				<form id="spc-general-form" class="form-horizontal form-search" style="padding-top: 20px;">
					<div class="control-group">
						<label class="control-label" for="spc-name" data-i18n="public.Name"></label>
						<div class="controls">
							<input type="text" id="spc-name" title="Specification Name" class="input-xlarge">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="spc-container-policy" data-i18n="public.Type"></label>
						<div class="controls">
							<select id="spc-container-policy" class="input-xlarge"/></select>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="spc-asset-module" data-i18n="includes.specificationCreateDialog.AssetProvider"></label>
						<div class="controls">
							<select id="spc-asset-module" class="input-xlarge"/></select>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="spc-hardware-search" data-i18n="includes.specificaationCreateDialog.ChooseDeviceType"></label>
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
				<div id="spc-metadata">
					<jsp:include page="../includes/metadata.jsp" flush="true">
					    <jsp:param name="uid" value="spc"/>
					</jsp:include>
				</div>
            </div>
		</div>
	</div>
	<input id="spec-token" type="hidden" value="" />
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal" data-i18n="public.Cancel"></a> 
		<a id="spc-dialog-submit" href="javascript:void(0)" class="btn btn-primary" data-i18n="public.Create"></a>
	</div>
</div>

<!-- Script support for device specification create dialog -->
<script>
	/** Function called when dialog is submitted */
	var spcSubmitCallback;

	/** Provides external access to tabs */
	var spcTabs;
	
	/** Datasource for device asset modules */
	var spcAssetModulesDS;
	
	/** Asset modules dropdown */
	var spcAssetModules;
	
	/** Datasource for hardware assets */
	var spcHardwareDS;
	
	/** Hardware matches list */
	var spcHardwareMatches;
	
	/** Dropdown for container policies */
	var spcContainerPolicies;
	
	/** Available container policies shown in dropdown */
    var spcContainerPolicyChoices = [
		{ text: "Standalone Device", value: "Standalone" },
		{ text: "Composite Device", value: "Composite" },
	];
	
	/** Used for delayed submit on search */
	var spcTimeout;
	var spcLastSearch;

	$(document).ready(function() {
		
		/** Create tab strip */
		spcTabs = $("#spc-tabs").kendoTabStrip({
			animation: false
		}).data("kendoTabStrip");
		
		/** Create AJAX datasource for device asset modules */
		spcAssetModulesDS = new kendo.data.DataSource({
			transport : {
				read : {
					url : "${pageContext.request.contextPath}/api/assets/modules/devices",
					dataType : "json",
				}
			},
		});

		/** Create asset modules dropdown */
    	spcAssetModules = $("#spc-asset-module").kendoDropDownList({
    		dataTextField: "name",
    		dataValueField: "id",
    	    dataSource: spcAssetModulesDS,
    	    index: 0,
    	    change: spcAssetModuleChanged,
			dataBound: function(e) {
				spcAssetModuleChanged();
			}
    	}).data("kendoDropDownList");
		
		/** Create the hardware match list */
		spcHardwareMatches = $("#spc-hardware-matches").kendoListView({
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

		/** Create container policies dropdown */
    	spcContainerPolicies = $("#spc-container-policy").kendoDropDownList({
    		dataTextField: "text",
    		dataValueField: "value",
    	    dataSource: spcContainerPolicyChoices,
    	    index: 0,
    	}).data("kendoDropDownList");
		
        /** Handle create dialog submit */
		$('#spc-dialog-submit').click(function(event) {
			event.preventDefault();
			if (!spcValidate()) {
				return;
			}
			var specData = {
				"name": $('#spc-name').val(), 
				"containerPolicy": $('#spc-container-policy').val(), 
				"assetModuleId": $('#spc-asset-module').val(), 
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
	
	/** Called when selected asset module changes */
	function spcAssetModuleChanged() {
		var assetModuleId = spcGetSelectedAssetModuleId();
		var criteria = $('#spc-hardware-search').val();
		spcHardwareDS = new kendo.data.DataSource({ 
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
		spcHardwareMatches.setDataSource(spcHardwareDS);
	}
	
	/** Gets the currently selected asset module id */
	function spcGetSelectedAssetModuleId() {
		return $("#spc-asset-module").val();
	}
	
	/** Called when hardware search criteria has changed */
	function spcHardwareSearchCriteriaUpdated() {
		if (spcHardwareDS) {
			var assetModuleId = spcGetSelectedAssetModuleId();
			var criteria = $('#spc-hardware-search').val();
			if (criteria != spcLastSearch) {
				var url = "${pageContext.request.contextPath}/api/assets/" + assetModuleId + "?criteria=" + criteria;
				spcHardwareDS.transport.options.read.url = url;
				spcHardwareDS.read();
			}
			spcLastSearch = criteria;
		}
	}
	
	/** Called when a hardware asset is chosen from the list */
	function spcHardwareAssetChosen() {
		if (spcHardwareDS) {
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
	}
	
	/** Open the dialog */
	function spcOpen(e, callback) {
		var event = e || window.event;
		event.stopPropagation();
		
		// Reset form and metadata.
    	$('#spc-general-form')[0].reset();
		spcMetadataDS.data(new Array());
		$('#spc-dialog-header').html(i18next("includes.specificaationCreateDialog.CreateSpecification"));
		$('#spc-dialog-submit').html(i18next("public.Create"));
		
		// Reset hardware search.
    	$('#spc-hardware-search').val("");
    	$('#spc-chosen-asset-id').val("");
    	spcHardwareMatches.clearSelection();
		
		// Remove token to prevent update.
		$('#spec-token').val("");
    	
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
		$('#spc-dialog-header').html(i18next("public.EditSpecification"));
		$('#spc-dialog-submit').html(i18next("public.Save"));
		
		// Save token for update.
		$('#spec-token').val(data.token);
		
		// Reset form and metadata.
		$('#spc-name').val(data.name);
		spcMetadataDS.data(swLookupAsMetadata(data.metadata));
		
		// Select correct container policy.
		spcContainerPolicies.value(data.containerPolicy);
		
		// Select correct asset module.
		spcAssetModules.value(data.assetModuleId);
		
		// Reset search.
    	$('#spc-hardware-search').val("");
    	$('#spc-chosen-asset-id').val(data.assetId);
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