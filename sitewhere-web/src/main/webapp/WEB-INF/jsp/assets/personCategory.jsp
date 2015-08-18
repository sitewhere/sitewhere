<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="Manage Person Assets" />
<c:set var="sitewhere_section" value="assets" />
<%@ include file="../includes/top.inc"%>

<style>
.w-asset-list {
	border: 0px;
	margin-bottom: 10px;
}
</style>

<!-- Title Bar -->
<div class="sw-title-bar content k-header">
	<h1 class="ellipsis">
		<c:out value="${category.name}" />
		(
		<c:out value="${category.id}" />
		)
	</h1>
	<div class="sw-title-bar-right">
		<a id="btn-add-asset" class="btn" href="javascript:void(0)" data-i18n="public.AddNew"> <i
			class="icon-plus sw-button-icon"></i>Add New
		</a>
	</div>
</div>
<div id="assets" class="w-asset-list"></div>
<div id="pager" class="k-pager-wrap"></div>

<%@ include file="assetEntries.inc"%>
<%@ include file="personAssetCreateDialog.inc"%>

<script>
	/** Asset category id */
	var categoryId = '<c:out value="${category.id}"/>';

	/** Assets datasource */
	var assetsDS;

	/** Called when edit button is clicked */
	function onAssetEditClicked(e, assetId) {
		var event = e || window.event;
		event.stopPropagation();
		auOpen(assetId, onEditSuccess);
	}

	/** Called on successful edit */
	function onEditSuccess() {
		assetsDS.read();
	}

	/** Called when delete button is clicked */
	function onAssetDeleteClicked(e, assetId) {
		var event = e || window.event;
		event.stopPropagation();
		swConfirm(i18next("public.DeleteAsset"), i18next("assets.list.AYSD") + "?", function(result) {
			if (result) {
				$.deleteJSON("${pageContext.request.contextPath}/api/assets/categories/" + categoryId
						+ "/assets/" + assetId, onDeleteSuccess, onDeleteFail);
			}
		});
	}

	/** Called on successful delete */
	function onDeleteSuccess() {
		assetsDS.read();
	}

	/** Handle failed delete call */
	function onDeleteFail(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, i18next("assets.list.UTD"));
	}

	/** Called after a new asset category has been created */
	function onAssetCreated() {
		assetsDS.read();
	}

	$(document).ready(
		function() {
			/** Create AJAX datasource for assets list */
			assetsDS =
					new kendo.data.DataSource({
						transport : {
							read : {
								url : "${pageContext.request.contextPath}/api/assets/categories/"
										+ categoryId + "/assets",
								dataType : "json",
							}
						},
						schema : {
							data : "results",
							total : "numResults",
						},
						serverPaging : true,
						serverSorting : true,
						pageSize : 10
					});

			/** Create the site list */
			$("#assets").kendoListView({
				dataSource : assetsDS,
				template : kendo.template($("#tpl-person-asset-entry").html())
			});

			$("#pager").kendoPager({
				dataSource : assetsDS
			});

			/** Handle add category functionality */
			$('#btn-add-asset').click(function(event) {
				acOpen(event, onAssetCreated);
			});
		});
</script>

<%@ include file="../includes/bottom.inc"%>