<!-- Dialog for editing tenant configuration elements -->
<div id="ce-dialog" class="modal hide" style="width: 760px; margin: -300px 0 0 -380px;">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 id="ce-dialog-header" data-i18n="tenant.configure.CreateConfigurationComponent"></h3>
	</div>
	<div class="modal-body">
		<div id="ce-tabs">
			<ul>
				<li class="k-state-active" data-i18n="tenant.configure.Configuration">Configuration</li>
			</ul>
			<div>
				<form id="ce-form" class="form-horizontal" style="padding-top: 20px;"></form>
			</div>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal" data-i18n="public.Cancel"></a> <a
			id="ce-dialog-submit" href="javascript:void(0)" class="btn btn-primary" data-i18n="public.Create"></a>
	</div>
</div>

<!-- Script support for configuration edit dialog -->
<script>
	/** Function called when dialog is submitted */
	var ceSubmitCallback;

	/** Provides external access to tabs */
	var ceTabs;

	/** Indicates if the operation is create */
	var ceCreate;

	/** Model for edited component */
	var ceModel;

	/** Configuration for edited component */
	var ceConfig;

	/** Add fields to the form */
	function addFields() {
		var valuesByName = [];
		if (ceConfig.attributes) {
			for (var i = 0; i < ceConfig.attributes.length; i++) {
				valuesByName[ceConfig.attributes[i].name] = ceConfig.attributes[i].value;
			}
		}
		var lastGroup;
		var fields = "";
		for (var i = 0; i < ceModel.attributes.length; i++) {
			var attr = ceModel.attributes[i];
			if (attr.group != lastGroup) {
				if (lastGroup) {
					fields += "</div>";
				}
				fields += "<div class='sw-attribute-group'><h1>" + attr.group + "</h1>";
				lastGroup = attr.group;
			}
			fields += "<div class='control-group'>";
			fields +=
					"  <label class='control-label sw-control-label' style='width: 275px;' for='tc-" + attr.localName + "'>"
							+ attr.name
							+ "<i class='fa fa-info-circle fa-white' title='" + attr.description + "'></i></label>";
			fields += "  <div class='controls' style='margin-left: 300px;'>";

			if (attr.choices) {
				fields += addChoicesField(attr, valuesByName[attr.localName]);
			} else if (attr.type == "Boolean") {
				fields += addBooleanField(attr, valuesByName[attr.localName]);
			} else if (attr.type == "Integer") {
				fields += addIntegerField(attr, valuesByName[attr.localName]);
			} else {
				fields += addTextField(attr, valuesByName[attr.localName]);
			}

			fields += "  </div>";
			fields += "</div>";
		}
		if (lastGroup) {
			fields += "</div>";
		}
		$('#ce-form').html(fields);
	}

	/** Add a text input for the given field */
	function addChoicesField(attr, value) {
		var field = "";

		// If creating, use the default value.
		if (ceCreate && attr.defaultValue) {
			value = attr.defaultValue;
		}

		// Use empty string for no value.
		if (!value) {
			value = "";
		}

		field += "<select id='tc-" + attr.localName + "'>";
		for (var i = 0; i < attr.choices.length; i++) {
			var choice = attr.choices[i];
			field += "<option>" + choice + "</option>"
		}
		field += "</select>";
		field += "<scr" + "ipt>$('#tc-" + attr.localName + "').kendoDropDownList();</scr" + "ipt>";
		return field;
	}

	/** Add a text input for the given field */
	function addTextField(attr, value) {
		var field = "";

		// If creating, use the default value.
		if (ceCreate && attr.defaultValue) {
			value = attr.defaultValue;
		}

		// Use empty string for no value.
		if (!value) {
			value = "";
		}

		field +=
				"<input class='input-xlarge' type='text' id='tc-" + attr.localName + "' value='" + value + "'>";
		return field;
	}

	/** Add a numeric input for the given field */
	function addIntegerField(attr, value) {
		var field = "";

		// If creating, use the default value.
		if (ceCreate && attr.defaultValue) {
			value = attr.defaultValue;
		}

		// Use empty string for no value.
		if (!value) {
			value = 0;
		}

		field += "<input class='input-xlarge' id='tc-" + attr.localName + "' value='" + value + "' />";
		field +=
				"<scr" + "ipt>$('#tc-" + attr.localName
						+ "').kendoNumericTextBox({decimals: 0, format: '0.'});</scr" + "ipt>";
		return field;
	}

	/** Add a decimal input for the given field */
	function addDecimalField(attr, value) {
		var field = "";

		// If creating, use the default value.
		if (ceCreate && attr.defaultValue) {
			value = attr.defaultValue;
		}

		// Use empty string for no value.
		if (!value) {
			value = 0;
		}

		field +=
				"<input class='input-xlarge' id='tc-" + attr.localName + "' type='number' value='" + value + "' />";
		field += "<scr" + "ipt>$('#tc-" + attr.localName + "').kendoNumericTextBox();</scr" + "ipt>";
		return field;
	}

	/** Add a checkbox for boolean values */
	function addBooleanField(attr, value) {
		var field = "";

		// If creating, use the default value.
		if (ceCreate && attr.defaultValue) {
			if ("true" == attr.defaultValue) {
				value = true;
			}
		}

		field +=
				"<input type='checkbox' id='tc-" + attr.localName + "' "
						+ ((value == "true") ? "checked" : "") + ">";
		return field;
	}

	$(document).ready(function() {

		/** Create tab strip */
		ceTabs = $("#ce-tabs").kendoTabStrip({
			animation : false
		}).data("kendoTabStrip");

		/** Handle dialog submit */
		$('#ce-dialog-submit').click(function(event) {
			event.preventDefault();

			// Copy updated attributes into configuration element.
			var cattrs = [];
			for (var i = 0; i < ceModel.attributes.length; i++) {
				var attr = ceModel.attributes[i];
				var entered = $('#tc-' + attr.localName).val();
				if (attr.type == 'Boolean') {
					entered = '' + $('#tc-' + attr.localName).is(':checked')
				}
				if (entered && (entered.length > 0)) {
					var cattr = {
						'name' : attr.localName,
						'value' : entered
					};
					if (attr.namespace) {
						cattr.namespace = attr.namespace;
					}
					cattrs.push(cattr);
				}
			}
			ceConfig.attributes = cattrs;

			$('#ce-dialog').modal('hide');
			if (ceSubmitCallback != null) {
				ceSubmitCallback(ceConfig);
			}
		});
	});

	/** Validate everything */
	function ceValidate() {
		$.validity.setup({
			outputMode : "label"
		});
		$.validity.start();

		// Add field validation.

		var result = $.validity.end();
		return result.valid;
	}

	/** Create new */
	function ceComponentCreate(model, config, callback) {

		// Store context.
		ceModel = model;
		ceConfig = config;
		ceSubmitCallback = callback;
		ceCreate = true;

		$('#ce-dialog-header').html("Create " + model.name);
		$('#ce-dialog-submit').html(i18next("public.Create"));

		// Add editable fields.
		addFields();

		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#ce-dialog').modal('show');
	}

	/** Edit existing */
	function ceComponentEdit(model, config, callback) {

		// Store context.
		ceModel = model;
		ceConfig = config;
		ceSubmitCallback = callback;
		ceCreate = false;

		$('#ce-dialog-header').html("Edit " + model.name);
		$('#ce-dialog-submit').html(i18next("public.Update"));

		// Add editable fields.
		addFields();

		// Clear old validation errors and show dialog.
		$.validity.clear();
		$('#ce-dialog').modal('show');
	}
</script>