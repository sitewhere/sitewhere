<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="Server Startup Failed" />
<c:set var="sitewhere_section" value="sites" />
<%@ include file="includes/top.inc"%>

<style>
</style>

<!-- Title Bar -->
<div class="sw-title-bar content k-header">
	<h1 data-i18n="noserver.title">
	</h1>
</div>
<div class="alert alert-error" data-i18n="noserver.loaderrorms">
</div>
<%@ include file="includes/bottom.inc"%>
<script type="text/javascript">
    /** Set sitewhere_title */
    sitewhere_i18next.sitewhere_title = "noserver.title";
</script>