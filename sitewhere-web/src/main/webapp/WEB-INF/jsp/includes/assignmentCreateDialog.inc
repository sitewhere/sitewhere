<!-- Dialog for assigning a device -->
<div id="ac-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 id="dialog-header">Assign Device</h3>
	</div>
	<div class="modal-body">
		<div id="ac-tabs">
			<ul>
				<li class="k-state-active">Assignment Details</li>
				<li>Metadata</li>
			</ul>
			<div>
				<form id="ac-general-form" class="form-horizontal" style="padding-top: 20px;">
					<div class="control-group">
						<label class="control-label" for="ac-hardware-id">Hardware Id</label>
						<div class="controls">
							<input type="text" id="ac-hardware-id" title="Hardware id" class="input-xlarge">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="ac-site-token">Site</label>
						<div class="controls">
							<select id="ac-site-token"></select>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="ac-asset-type">Assignment Type</label>
						<div class="controls">
							<select id="ac-assignment-type"/></select>
						</div>
					</div>
					<div id="ac-assignment-type-forms">
						<div class="control-group" id="ac-asset-hardware-panel">
							<label class="control-label" for="ac-hardware-search">Choose Hardware</label>
							<div class="controls">
								<div class="input-append">
		    						<input class="input-xlarge" id="ac-hardware-search" type="text">
									<span class="add-on"><i class="icon-search"></i></span>
		    					</div>
								<div id="ac-hardware-matches" class="sw-form-search-results"
									style="height: 120px;"></div>
								<input type="hidden" id="ac-hardware-asset-id" title="Asset"/>
							</div>
						</div>
						<div class="control-group hide" id="ac-asset-person-panel">
							<label class="control-label" for="ac-person-search">Choose Person</label>
							<div class="controls">
								<div class="input-append">
		    						<input class="input-xlarge" id="ac-person-search" type="text">
									<span class="add-on"><i class="icon-search"></i></span>
		    					</div>
								<div id="ac-person-matches" class="sw-form-search-results"
									style="height: 120px;"></div>
								<input type="hidden" id="ac-person-asset-id" title="Asset"/>
							</div>
						</div>
						<div class="control-group hide" id="ac-asset-unassociated-panel"></div>
					</div>
				</form>
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
		<a href="javascript:void(0)" class="btn" data-dismiss="modal">Cancel</a> 
		<a id="ac-submit" href="javascript:void(0)" class="btn btn-primary">Assign</a>
	</div>
</div>

<!-- Script support for assignment create dialog -->
<script>
	/** Function called when dialog is submitted */
	var acSubmitCallback;

	/** Provides external access to tabs */
	var acTabs;
	
	/** Datasource for hardware assets */
	var acHardwareDS;
	
	/** Datasource for person assets */
	var acPersonDS;
	
	/** Hardware matches list */
	var acHardwareMatches;
	
	/** Person matches list */
	var acPersonMatches;
	
	/** Sites dropdown */
	var acSitesDropdown;
	
	/** Used for delayed submit on search */
	var acTimeout;
	var acLastSearch;

	$(document).ready(function() {

        acSitesDropdown = $("#ac-site-token").kendoDropDownList({
			dataTextField: "name",
			dataValueField: "token",
            dataSource: {
    			transport : {
    				read : {
    					url : "${pageContext.request.contextPath}/api/sites",
    					dataType : "json",
    				}
    			},
    			schema : {
    				data: "results",
    				total: "numResults",
    			},
            }
        }).data("kendoDropDownList");
		
		/** Available assignment types shown in dropdown */
	    var assignmentTypes = [
			{ text: "Hardware", value: "Hardware" },
			{ text: "Person", value: "Person" },
			{ text: "Unassociated", value: "Unassociated" },
		];

    	// create DropDownList from input HTML element
    	$("#ac-assignment-type").kendoDropDownList({
    		dataTextField: "text",
    		dataValueField: "value",
    	    dataSource: assignmentTypes,
    	    index: 0,
    	    change: acAssignmentTypeChanged
    	});
		
		/** Create AJAX datasource for hardware search */
		acHardwareDS = new kendo.data.DataSource({
			transport : {
				read : {
					url : "${pageContext.request.contextPath}/api/assets/hardware",
					dataType : "json",
				}
			},
			schema : {
				data: "results",
				total: "numResults",
			},
		});
		
		/** Create the hardware match list */
		acHardwareMatches = $("#ac-hardware-matches").kendoListView({
			dataSource : acHardwareDS,
			selectable : "single",
			template : kendo.template($("#hardware-asset-entry").html()),
			change: acHardwareAssetChosen
		}).data("kendoListView");
		
		/** Update hardware search datasource based on entered criteria */
		$("#ac-hardware-search").bind("change paste keyup", function() {
		    window.clearTimeout(acTimeout);
		    acTimeout = window.setTimeout(acHardwareSearchCriteriaUpdated, 300); 
		});
		
		/** Create AJAX datasource for person search */
		acPersonDS = new kendo.data.DataSource({
			transport : {
				read : {
					url : "${pageContext.request.contextPath}/api/assets/people",
					dataType : "json",
				}
			},
			schema : {
				data: "results",
				total: "numResults",
			},
		});
		
		/** Create the person match list */
		acPersonMatches = $("#ac-person-matches").kendoListView({
			dataSource : acPersonDS,
			selectable : "single",
			template : kendo.template($("#person-asset-entry").html()),
			change: acPersonAssetChosen
		}).data("kendoListView");
		
		/** Update person search datasource based on entered criteria */
		$("#ac-person-search").bind("change paste keyup", function() {
		    window.clearTimeout(acTimeout);
		    acTimeout = window.setTimeout(acPersonSearchCriteriaUpdated, 300); 
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
	        
	        var assignmentType = $("#ac-assignment-type").val();
	        var assetId;
	        if (assignmentType == "Hardware") {
	        	assetId = $('#ac-hardware-asset-id').val();
	        } else if (assignmentType == "Person") {
	        	assetId = $('#ac-person-asset-id').val();
	        }
	        
			var assignmentData = {
				"deviceHardwareId": $('#ac-hardware-id').val(), 
				"siteToken": $('#ac-site-token').val(), 
				"assignmentType": $('#ac-assignment-type').val(), 
				"assetId": assetId, 
				"metadata": swMetadataAsLookup(acMetadataDS.data()),
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
    });
	
	/** Validate the dialog */
	function acValidate() {
		$.validity.setup({ outputMode:"label" });
		$.validity.start();

        /** Validate main form */
		$("#ac-hardware-id").require();

        /** Validate main form */
		$("#ac-site-token").require();

        /** Verify that a site was chosen */
		$("#ac-asset-type").require();
        
        var assignmentType = $("#ac-assignment-type").val();
        if (assignmentType == "Hardware") {
        	$('#ac-hardware-asset-id').require();
        } else if (assignmentType == "Person") {
        	$('#ac-person-asset-id').require();
        }
      
		var result = $.validity.end();
		return result.valid;
	}
	
	/** Called when hardware search criteria has changed */
	function acHardwareSearchCriteriaUpdated() {
		var criteria = $('#ac-hardware-search').val();
		if (criteria != acLastSearch) {
			var url = "${pageContext.request.contextPath}/api/assets/hardware?criteria=" + criteria;
			acHardwareDS.transport.options.read.url = url;
			acHardwareDS.read();
		}
		acLastSearch = criteria;
	}
	
	/** Called when hardware search criteria has changed */
	function acPersonSearchCriteriaUpdated() {
		var criteria = $('#ac-person-search').val();
		if (criteria != acLastSearch) {
			var url = "${pageContext.request.contextPath}/api/assets/people?criteria=" + criteria;
			acPersonDS.transport.options.read.url = url;
			acPersonDS.read();
		}
		acLastSearch = criteria;
	}
	
	/** Called to swap panel when assignment type dropdown value changes */
	function acAssignmentTypeChanged() {
		var assignmentType = $("#ac-assignment-type").val();
		var panelName = "ac-asset-" + assignmentType.toLowerCase() + "-panel";
		$("#ac-assignment-type-forms").children().each(function(i) {
			if (panelName == $(this).attr("id")) {
				$(this).removeClass("hide");
			} else if (!$(this).hasClass("hide")) {
				$(this).addClass("hide");
			}
		});
	}
	
	/** Called when a hardware asset is chosen from the list */
	function acHardwareAssetChosen() {
		var listView = acHardwareDS.view();
		var selected = $.map(this.select(), function(item) {
			return listView[$(item).index()];
		});

		if (selected.length > 0) {
			$('#ac-hardware-asset-id').val(selected[0].id);
		} else {
			$('#ac-hardware-asset-id').val("");
		}	
	}
	
	/** Called when a hardware asset is chosen from the list */
	function acPersonAssetChosen() {
		var listView = acPersonDS.view();
		var selected = $.map(this.select(), function(item) {
			return listView[$(item).index()];
		});

		if (selected.length > 0) {
			$('#ac-person-asset-id').val(selected[0].id);
		} else {
			$('#ac-person-asset-id').val("");
		}		
	}
	
	/** Open the dialog */
	function acOpen(e, hardwareId, callback) {
		var event = e || window.event;
		event.stopPropagation();
		
		$('#ac-general-form')[0].reset();
		$('#ac-hardware-id').val(hardwareId);
		acMetadataDS.data(new Array());
		
		// Reset list selections.
		$('#ac-hardware-asset-id').val("");
		acHardwareMatches.clearSelection();
		$('#ac-person-asset-id').val("");
		acPersonMatches.clearSelection();
		$("#ac-assignment-type").val("Hardware");
		acAssignmentTypeChanged();
   	
		// Select first tab.
		acTabs.select(0);
		
		// Function called on submit.
		acSubmitCallback = callback;
		
		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#ac-dialog').modal('show');
	}
</script>