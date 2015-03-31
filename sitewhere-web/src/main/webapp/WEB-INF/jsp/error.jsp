<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="Error Loading Page" />
<c:set var="sitewhere_section" value="sites" />
<%@ include file="includes/top.inc"%>

<style>
</style>

<!-- Title Bar -->
<div class="sw-title-bar content k-header">
	<h1 data-i18n="error.title"></h1>
</div>

<h2></h2><c:out value="${message}"/></h2>

<%@ include file="includes/bottom.inc"%>
<script type="text/javascript">
    /** Set sitewhere_title */
    sitewhere_i18next.sitewhere_title = "error.title";
</script>