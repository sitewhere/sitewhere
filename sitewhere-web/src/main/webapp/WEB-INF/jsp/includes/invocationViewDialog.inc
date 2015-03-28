<!-- Dialog for command create/update -->
<style>
.sw-iv-invocation-params-param {
	width: 250px;
}
.sw-iv-invocation-params-value {
	width: 200px;
}
.sw-iv-invocation-params-readonly {
	font-size: 12pt;
	line-height: normal;
	padding: 4px 0px;
	margin: 0;
}
.sw-iv-invocation-response-desc {
	width: 325px;
}
.sw-iv-invocation-response-date {
	width: 100px;
}
</style>
<div id="iv-dialog" class="modal hide">
	<div class="modal-header k-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 id="ivdialog-header" data-i18n="includes.invocationViewDialog.CommandInvocationDetails"></h3>
	</div>
	<div class="modal-body">
		<div id="iv-tabs">
			<ul>
				<li class="k-state-active" data-i18n="public.Invocation"></li>
				<li data-i18n="includes.invocationViewDialog.Responses"></li>
				<li data-i18n="public.Metadata"></li>
			</ul>
			<div>
				<form id="cc-general-form" class="form-horizontal" style="padding-top: 20px;">
					<div class="control-group">
						<label class="control-label" for="iv-command-name"" data-i18n="public.Command"></label>
						<div class="controls">
							<h1 id="iv-command-name" class="sw-iv-invocation-params-readonly"></h1>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="iv-command-namespace" data-i18n="includes.Namespace"></label>
						<div class="controls">
							<h1 id="iv-command-namespace" class="sw-iv-invocation-params-readonly"></h1>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="iv-command-namespace" data-i18n="includes.invocationViewDialog.InvocationDate"></label>
						<div class="controls">
							<h1 id="iv-command-invocation-date" class="sw-iv-invocation-params-readonly"></h1>
						</div>
					</div>
				</form>
				<div id="iv-parameters-section">
					<div class="sw-sublist-header">
						<div class="sw-iv-invocation-params-param" data-i18n="public.Parameter"></div>
						<div class="sw-iv-invocation-params-value" data-i18n="public.Value"></div>
					</div>
					<table id="iv-parameters" class="sw-sublist-list" style="height: inherit; margin-bottom: 10px;">
					</table>
				</div>
			</div>
            <div>
				<div class="sw-sublist-header">
					<div class="sw-iv-invocation-response-desc" data-i18n="includes.invocationViewDialog.Response"></div>
					<div class="sw-iv-invocation-response-date" data-i18n="public.Date"></div>
				</div>
				<table id="iv-responses" class="sw-sublist-list">
				</table>
            </div>
			<div>
				<div id="iv-metadata">
					<jsp:include page="../includes/metadata.jsp" flush="true">
					    <jsp:param name="uid" value="iv"/>
					    <jsp:param name="mdReadOnly" value="true"/>
					</jsp:include>
				</div>
            </div>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" data-dismiss="modal" data-i18n="public.Close"></a> 
	</div>
</div>

<!-- Script support for command invocation view dialog -->
<script>
	
	/** Pointer to tabs instance */
	var ivTabs;

	/** Datasource for invocation parameters */
	var paramsDS;
	
	/** Datasource for invocation responses */
	var responsesDS;

	$(document).ready(function() {
		
		/** Create the tab strip */
		ivTabs = $("#iv-tabs").kendoTabStrip({
			animation: false,
		}).data("kendoTabStrip");
		
		/** Create datasource for invocation parameters */
		paramsDS = new kendo.data.DataSource();
		
		/** Create datasource for invocation responses */
		responsesDS = new kendo.data.DataSource();
		
		/** Create a list view for parameter info */
		$("#iv-parameters").kendoListView({
			dataSource : paramsDS,
			template : kendo.template($("#tpl-inv-param-summary-entry").html())
		});
		
		/** Create a list view for response info */
		$("#iv-responses").kendoListView({
			dataSource : responsesDS,
			template : kendo.template($("#tpl-inv-response-summary-entry").html())
		});
    });
		
	/** Called to load the given invocation */
	function ivOpen(invocationId) {
		$.getJSON("${pageContext.request.contextPath}/api/invocations/" + invocationId + "/summary", 
				ivSuccess, ivFailed);
	}
    
    /** Called on successful invocation load */
    function ivSuccess(data, status, jqXHR) {
    	
    	var ns = (data.namespace && data.namespace.length > 0) ? data.namespace : "(Default)";

		$('#iv-command-name').html(data.name);
		$('#iv-command-namespace').html(ns);
		$('#iv-command-invocation-date').html(formattedDate(kendo.parseDate(data.invocationDate)));
		
		// Load parameters into datasource.
		paramsDS.data(data.parameters);
		
		// Load responses into datasource.
		responsesDS.data(data.responses);
		
		if (data.parameters && data.parameters.length > 0) {
			$('#iv-parameters-section').show();
		} else {
			$('#iv-parameters-section').hide();
		}
		
		ivMetadataDS.data(swLookupAsMetadata(data.metadata));
		ivTabs.select(0);
		
		$.validity.clear();
		$('#iv-dialog').modal('show');
    }
    
	/** Handle error on getting invocation */
	function ivFailed(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to load invocaion to view.");
	}
</script>