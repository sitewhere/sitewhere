<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="Manage Sites" />
<c:set var="sitewhere_section" value="sites" />
<c:set var="use_map_includes" value="true" />
<%@ include file="../includes/top.inc"%>

<style>
.sw-site-list {
	border: 0px;
}

.command-buttons {
	text-align: center;
}

#metadataGrid {
	margin-top: 15px;
	margin-bottom: 15px;
}
</style>

<%@ include file="../includes/siteCreateDialog.inc"%>

<!-- Title Bar -->
<div class="sw-title-bar content k-header">
	<h1 class="ellipsis"><c:out value="${sitewhere_title}"/></h1>
	<div class="sw-title-bar-right">
		<a id="btn-filter-results" class="btn" href="javascript:void(0)">
			<i class="icon-search sw-button-icon"></i> Filter Results</a>
		<a id="btn-add-site" class="btn" href="javascript:void(0)">
			<i class="icon-plus sw-button-icon"></i> Add New Site</a>
	</div>
</div>
<div id="sites" class="sw-site-list"></div>
<div id="pager" class="k-pager-wrap"></div>

<form id="view-site-detail" method="get" action="detail.html">
	<input id="detail-site-token" name="siteToken" type="hidden" value="${site.token}"/>
</form>

<%@ include file="../includes/templateSiteEntry.inc"%>

<script>
	/** Sites datasource */
	var sitesDS;

	/** Called when edit button is clicked */
	function onSiteEditClicked(e, siteToken) {
		var event = e || window.event;
		event.stopPropagation();
		suOpen(siteToken, onEditSuccess);
	}
    
    /** Called on successful edit */
    function onEditSuccess() {
    	sitesDS.read();
    }

	/** Called when delete button is clicked */
	function onSiteDeleteClicked(e, siteToken) {
		var event = e || window.event;
		event.stopPropagation();
		swConfirm("Delete Site", "Are you sure you want to delete this site?", function(result) {
			if (result) {
				$.deleteJSON("${pageContext.request.contextPath}/api/sites/" + siteToken + "?force=true", 
						onDeleteSuccess, onDeleteFail);
			}
		}); 
	}
    
    /** Called on successful delete */
    function onDeleteSuccess() {
    	sitesDS.read();
    }
    
	/** Handle failed delete call */
	function onDeleteFail(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, "Unable to delete site.");
	}
	
	/** Called when open button is clicked */
	function onSiteOpenClicked(e, siteToken) {
		var event = e || window.event;
		event.stopPropagation();
		$('#detail-site-token').val(siteToken);
		$('#view-site-detail').submit();
	}
	
	/** Called after a new site has been created */
	function onSiteCreated() {
		sitesDS.read();
	}
	
    $(document).ready(function() {
		/** Create AJAX datasource for sites list */
		sitesDS = new kendo.data.DataSource({
			transport : {
				read : {
					url : "${pageContext.request.contextPath}/api/sites",
					dataType : "json",
				}
			},
			schema : {
				data: "results",
				total: "numResults",
				parse:function (response) {
				    $.each(response.results, function (index, item) {
				    	parseSiteData(item);
				    });
				    return response;
				}
			},
            serverPaging: true,
            serverSorting: true,
			pageSize: 10
		});
		
		/** Create the site list */
		$("#sites").kendoListView({
			dataSource : sitesDS,
			template : kendo.template($("#tpl-site-entry").html())
		});
		
        $("#pager").kendoPager({
            dataSource: sitesDS
        });
        
        /** Handle add site functionality */
		$('#btn-add-site').click(function(event) {
			scOpen(event, onSiteCreated);
		});
    });
</script>

<%@ include file="../includes/bottom.inc"%>