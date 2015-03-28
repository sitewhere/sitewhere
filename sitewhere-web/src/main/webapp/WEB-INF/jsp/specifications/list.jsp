<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="Manage Device Specifications" />
<c:set var="sitewhere_section" value="specifications" />
<%@ include file="../includes/top.inc"%>

<style>
.sw-specification-list {
	border: 0px;
}
</style>

<!-- Title Bar -->
<div class="sw-title-bar content k-header">
	<h1 class="ellipsis" data-i18n="specifications.list.title"></h1>
	<div class="sw-title-bar-right">
		<a id="btn-filter-results" class="btn" href="javascript:void(0)" data-i18n="public.FilterResults">
			<i class="icon-search sw-button-icon"></i></a>
		<a id="btn-add-specification" class="btn" href="javascript:void(0)" data-i18n="specifications.list.AddNewSpecification">
			<i class="icon-plus sw-button-icon"></i></a>
	</div>
</div>
<div id="specifications" class="sw-specification-list"></div>
<div id="pager" class="k-pager-wrap"></div>

<%@ include file="../includes/specificationCreateDialog.inc"%>

<form id="view-spec-detail" method="get" action="detail.html">
	<input id="detail-spec-token" name="token" type="hidden"/>
</form>

<%@ include file="../includes/templateSpecificationEntry.inc"%>
<%@ include file="../includes/assetTemplates.inc"%>
<%@ include file="../includes/commonFunctions.inc"%>

<script>
    /** Set sitewhere_title */
    sitewhere_i18next.sitewhere_title = "specifications.list.title";

	/** Reference for device list datasource */
	var specsDS;
	
	/** Called when edit button on the list entry is pressed */
	function onSpecificationEditClicked(e, token) {
		var event = e || window.event;
		event.stopPropagation();
		spuOpen(token, onSpecificationEditComplete);
	}
	
	/** Called after successful specification update */
	function onSpecificationEditComplete() {
		specsDS.read();
	}
	
	/** Called when delete button on the list entry is pressed */
	function onSpecificationDeleteClicked(e, token) {
		var event = e || window.event;
		event.stopPropagation();
		swSpecificationDelete(token, onSpecificationDeleteComplete);
	}
	
	/** Called after successful specification delete */
	function onSpecificationDeleteComplete() {
		specsDS.read();
	}
	
	/** Called when a specification has been successfully created */
	function onSpecificationCreated() {
		specsDS.read();
	}
	
	/** Called when 'open' button on the list entry is pressed */
	function onSpecificationOpenClicked(e, token) {
		var event = e || window.event;
		event.stopPropagation();
		$('#detail-spec-token').val(token);
		$('#view-spec-detail').submit();
	}
	
    $(document).ready(function() {
		/** Create AJAX datasource for specifications list */
		specsDS = new kendo.data.DataSource({
			transport : {
				read : {
					url : "${pageContext.request.contextPath}/api/specifications?includeAsset=true",
					dataType : "json",
				}
			},
			schema : {
				data: "results",
				total: "numResults",
				parse:function (response) {
				    $.each(response.results, function (index, item) {
				    	parseSpecificationData(item);
				    });
				    return response;
				}
			},
            serverPaging: true,
            autoSync: true,
            serverSorting: true,
            pageSize: 15,
		});
		
		/** Create the list of specifications */
		$("#specifications").kendoListView({
			dataSource : specsDS,
			template : kendo.template($("#tpl-specification-entry").html()),
			change: function(e){
                       alert("ok");
                    }
		});
		
		/** Pager for specification list */
        $("#pager").kendoPager({
            dataSource: specsDS
        });
		
        /** Handle create dialog */
		$('#btn-add-specification').click(function(event) {
			spcOpen(event, onSpecificationCreated)
		});
   });
</script>

<%@ include file="../includes/bottom.inc"%>