<!-- Dialog for visually editing tenant configuration -->
<style>
div.bx-wrapper {
	margin: 25px 20px;
}

#tve-config-editor div.bx-viewport {
	box-shadow: none;
	background-color: #eee;
}
</style>
<div id="tve-dialog" class="modal hide" style="width: 800px; margin: -300px 0 0 -400px;">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 id="tve-dialog-header" data-i18n="tenants.editor.header">Edit Tenant Configuration</h3>
	</div>
	<div class="modal-body">
		<div id="tve-tabs">
			<ul>
				<li class="k-state-active" data-i18n="tenants.editor.configuration">Tenant Configuration</li>
			</ul>
			<div>
				<div id="tve-config-editor" class="carousel slide" data-interval="false">
					<div id="tve-config-pages" class="carousel-inner" role="listbox"></div>
				</div>
			</div>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" id="tve-next">Next</a> <a href="javascript:void(0)"
			class="btn" data-dismiss="modal" data-i18n="public.Cancel"></a> <a id="tve-dialog-submit"
			href="javascript:void(0)" class="btn btn-primary" data-i18n="tenant.editor.stage">Stage
			Updates</a>
	</div>
</div>

<!-- Script support for tenant create dialog -->
<script>
	/** Function called when dialog is submitted */
	var tveSubmitCallback;

	/** Provides external access to tabs */
	var tveTabs;

	/** Configuration being edited */
	var config;

	/** Context stack for editor */
	var contexts = [];

	/** Reset the wizard */
	function resetWizard() {
		contexts = [];
		addRootPanel();
	}

	/** Add the root panel */
	function addRootPanel() {
		var configNode = findConfigNodeByName(config, "tenant-configuration");
		var modelNode = findModelNodeByName(configModel, "tenant-configuration");
		addPanelFor(configNode, modelNode);
	}

	/** Add new panel for a given element */
	function addPanelFor(configNode, modelNode) {
		var panel =
				"<div class='item active'><h1>" + modelNode.name + "</h1>" + modelNode.description + "</div>";
		var context = {
			"config" : configNode,
			"model" : modelNode,
			"panel" : panel
		};
		contexts.push(context);

		panel = "<div class='item'>THIS IS THE SECOND ONE!!!!</div>";
		context = {
			"config" : configNode,
			"model" : modelNode,
			"panel" : panel
		};
		contexts.push(context);

		var allPanels = "";
		for (var i = 0; i < contexts.length; i++) {
			allPanels += contexts[i].panel;
		}
		$('#tve-config-pages').html(allPanels);
	}

	/** Find closest element with the given localName */
	function findConfigNodeByName(root, name) {
		if (root.name == name) {
			return root;
		} else {
			var found;
			if (root.children) {
				for (var i = 0; i < root.children.length; i++) {
					found = findModelNodeByName(root.children[i], name);
					if (found) {
						return found;
					}
				}
			}
		}
		return null;
	}

	/** Find closest element with the given localName */
	function findModelNodeByName(root, name) {
		if (root.nodeType == 'Element') {
			if (root.localName == name) {
				return root;
			} else {
				var found;
				if (root.elements) {
					for (var i = 0; i < root.elements.length; i++) {
						found = findConfigNodeByName(root.elements[i], name);
						if (found) {
							return found;
						}
					}
				}
			}
		}
		return null;
	}

	$(document).ready(function() {

		/** Create tab strip */
		tveTabs = $("#tve-tabs").kendoTabStrip({
			animation : false
		}).data("kendoTabStrip");

		/** Handle dialog submit */
		$('#tve-dialog-submit').click(function(event) {
			event.preventDefault();
		});
		$('#tve-next').click(function(event) {
			$("#tve-config-editor").carousel("next");
		});
	});

	/** Called to edit existing tenant */
	function tveOpen(tenantId, callback) {
		// Function called on submit.
		tveSubmitCallback = callback;

		// Get latest device data for hardware id.
		$.getJSON("${pageContext.request.contextPath}/api/tenants/" + tenantId
				+ "/engine/configuration/json?tenantAuthToken=${tenant.authenticationToken}",
			jsonConfigGetSuccess, jsonConfigGetFailed);
	}

	/** Called on successful configuration load request */
	function jsonConfigGetSuccess(data, status, jqXHR) {
		config = data;

		resetWizard();

		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#tve-dialog').modal('show');
	}

	/** Handle error on getting configuration data */
	function jsonConfigGetFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load tenant configuration as JSON.");
	}
</script>