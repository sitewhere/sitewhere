<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="Server Information" />
<c:set var="sitewhere_section" value="server" />
<%@ include file="../includes/top.inc"%>

<!-- Title Bar -->
<div class="sw-title-bar content k-header">
	<h1 class="ellipsis" data-i18n="server.title">Server Information</h1>
	<div class="sw-title-bar-right">
		<a id="btn-filter-results" class="btn" href="javascript:void(0)" data-i18n="public.FilterResults">
			<i class="icon-search sw-button-icon"></i>
		</a>
	</div>
</div>

<%@ include file="../includes/bottom.inc"%>