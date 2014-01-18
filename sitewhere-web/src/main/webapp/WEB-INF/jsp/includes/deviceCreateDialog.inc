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
				<li>Specification</li>
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
				<form id="dc-specification-form" class="form-horizontal form-search" style="padding-top: 20px;">
					<div class="control-group">
						<label class="control-label" for="dc-specification-search">Choose Specification</label>
						<div class="controls">
							<div id="dc-specifications" class="sw-form-search-results"></div>
							<input type="hidden" id="dc-chosen-specification-token" title="Specification"/>
	    				</div>
	    			</div>
	    		</form>		
    		</div>
			<div>
				<div id="dc-metadata">
					<jsp:include page="../includes/metadata.jsp" flush="true">
					    <jsp:param name="uid" value="dc"/>
					</jsp:include>
				</div>
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
	
	/** Datasource for specifications */
	var dcSpecificationsDS;
	
	/** Specifications list */
	var dcSpecifications;

	$(document).ready(function() {
		
		/** Create tab strip */
		dcTabs = $("#dc-tabs").kendoTabStrip({
			animation: false
		}).data("kendoTabStrip");
		
		/** Create AJAX datasource for specifications list */
		dcSpecificationsDS = new kendo.data.DataSource({
			transport : {
				read : {
					url : "${pageContext.request.contextPath}/api/specifications",
					dataType : "json",
				}
			},
			schema : {
				data: "results",
				total: "numResults",
			},
		});
		
		/** Create the hardware match list */
		dcSpecifications = $("#dc-specifications").kendoListView({
			dataSource : dcSpecificationsDS,
			selectable : "single",
			template : kendo.template($("#specification-entry-small").html()),
			change: dcSpecificationChosen
		}).data("kendoListView");
		
        /** Handle create dialog submit */
		$('#dc-dialog-submit').click(function(event) {
			event.preventDefault();
			if (!dcValidate()) {
				return;
			}
			var deviceData = {
				"hardwareId": $('#dc-hardware-id').val(), 
				"comments": $('#dc-comments').val(), 
				"specificationToken": $('#dc-chosen-specification-token').val(), 
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

        /** Verify that a specification was chosen */
		$("#dc-chosen-specification-token").require();
      
		var result = $.validity.end();
		return result.valid;
	}
	
	/** Called when a specification is chosen from the list */
	function dcSpecificationChosen() {
		var listView = dcSpecificationsDS.view();
		var selected = $.map(this.select(), function(item) {
			return listView[$(item).index()];
		});

		if (selected.length > 0) {
			$('#dc-chosen-specification-token').val(selected[0].token);
		} else {
			$('#dc-chosen-specification-token').val("");
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
    	$('#dc-chosen-asset-id').val("");
    	
    	// Select first tab.
		dcTabs.select(0);
		
		// Function called on submit.
		dcSubmitCallback = callback;
		
		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#dc-dialog').modal('show');
	}
</script>