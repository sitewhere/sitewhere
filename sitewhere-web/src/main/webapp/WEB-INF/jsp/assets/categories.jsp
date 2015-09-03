<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="Manage Asset Categories" />
<c:set var="sitewhere_section" value="assets" />
<%@ include file="../includes/top.inc"%>

<style>
.w-category-list {
	border: 0px;
}
</style>

<!-- Title Bar -->
<div class="sw-title-bar content k-header">
	<h1 class="ellipsis" data-i18n="assets.list.title">Manage Asset Categories</h1>
	<div class="sw-title-bar-right">
		<a id="btn-add-category" class="btn" href="javascript:void(0)"> <i
			class="fa fa-plus sw-button-icon"></i> <span data-i18n="assetCategories.list.AddNew">Add
				New</span></a>
	</div>
</div>
<div id="categories" class="w-category-list"></div>
<div id="pager" class="k-pager-wrap"></div>

<form id="view-category-assets" method="get"></form>

<%@ include file="assetCategoryEntry.inc"%>
<%@ include file="categoryCreateDialog.inc"%>

<script>
	/** Asset categories datasource */
	var categoriesDS;

	/** Called when edit button is clicked */
	function onCategoryEditClicked(e, categoryId) {
		var event = e || window.event;
		event.stopPropagation();
		cuOpen(categoryId, onEditSuccess);
	}

	/** Called on successful edit */
	function onEditSuccess() {
		categoriesDS.read();
	}

	/** Called when delete button is clicked */
	function onCategoryDeleteClicked(e, categoryId) {
		var event = e || window.event;
		event.stopPropagation();
		swConfirm(i18next("public.DeleteAssetCategory"), i18next("assetCategories.list.AYSD") + "?",
			function(result) {
				if (result) {
					$
							.deleteJSON("${pageContext.request.contextPath}/api/assets/categories/"
									+ categoryId + "?tenantAuthToken=${tenant.authenticationToken}",
								onDeleteSuccess, onDeleteFail);
				}
			});
	}

	/** Called on successful delete */
	function onDeleteSuccess() {
		categoriesDS.read();
	}

	/** Handle failed delete call */
	function onDeleteFail(jqXHR, textStatus, errorThrown) {
		handleError(jqXHR, i18next("assetCategories.list.UTD"));
	}

	/** Called when open button is clicked */
	function onCategoryOpenClicked(e, categoryId, assetType) {
		var event = e || window.event;
		event.stopPropagation();
		$("#view-category-assets").attr("action",
			"${pageContext.request.contextPath}/admin/assets/categories/" + categoryId + ".html");
		$('#view-category-assets').submit();
	}

	/** Called after a new asset category has been created */
	function onCategoryCreated() {
		categoriesDS.read();
	}

	$(document)
			.ready(
				function() {
					/** Create AJAX datasource for sites list */
					categoriesDS =
							new kendo.data.DataSource(
								{
									transport : {
										read : {
											url : "${pageContext.request.contextPath}/api/assets/categories?tenantAuthToken=${tenant.authenticationToken}",
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
					$("#categories").kendoListView({
						dataSource : categoriesDS,
						template : kendo.template($("#tpl-category-entry").html())
					});

					$("#pager").kendoPager({
						dataSource : categoriesDS
					});

					/** Handle add category functionality */
					$('#btn-add-category').click(function(event) {
						ccOpen(event, onCategoryCreated);
					});
				});
</script>

<%@ include file="../includes/bottom.inc"%>