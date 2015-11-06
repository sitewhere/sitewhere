<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="Server Startup Failed" />
<!DOCTYPE html>
<html class="sw-body">
<head>
<title>SiteWhere - <c:out value="${sitewhere_title}"/></title>
<script src="${pageContext.request.contextPath}/scripts/jquery-1.10.2.min.js"></script>
<script src="${pageContext.request.contextPath}/scripts/jquery.validity.js"></script>
<script src="${pageContext.request.contextPath}/scripts/kendo.web.min.js"></script>
<script src="${pageContext.request.contextPath}/scripts/modernizr.js"></script>
<script src="${pageContext.request.contextPath}/scripts/moment.js"></script>
<script src="${pageContext.request.contextPath}/scripts/sitewhere.js"></script>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/img/favicon.ico" />
<link href="${pageContext.request.contextPath}/css/kendo.common.min.css" rel="stylesheet" />
<link href="${pageContext.request.contextPath}/css/kendo.bootstrap.min.css" rel="stylesheet" />
<link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet" media="screen">
<link href="${pageContext.request.contextPath}/css/font-awesome.min.css" rel="stylesheet" media="screen">
<link href="${pageContext.request.contextPath}/css/jquery.validity.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/sitewhere.css" rel="stylesheet" />
<c:if test="${use_map_includes == true}">
<script src="${pageContext.request.contextPath}/scripts/leaflet.js"></script>
<script src="${pageContext.request.contextPath}/scripts/leaflet.draw.js"></script>
<link href="${pageContext.request.contextPath}/css/leaflet.css" rel="stylesheet" />
<link href="${pageContext.request.contextPath}/css/leaflet.draw.css" rel="stylesheet" />
<link  href="${pageContext.request.contextPath}/css/highlight.default.css" rel="stylesheet">
<link  href="${pageContext.request.contextPath}/css/googlecode.css" rel="stylesheet">
<script src="${pageContext.request.contextPath}/scripts/highlight.pack.js"></script>
<script src="${pageContext.request.contextPath}/scripts/sitewhere-leaflet.js"></script>
</c:if>
<c:if test="${use_color_picker_includes == true}">
<script src="${pageContext.request.contextPath}/scripts/spectrum.js"></script>
<link href="${pageContext.request.contextPath}/css/spectrum.css" rel="stylesheet" />
</c:if>
<c:if test="${use_mqtt == true}">
<script src="${pageContext.request.contextPath}/scripts/mqttws31.js"></script>
</c:if>
<%@ include file="includes/i18next.inc"%>
</head>
<body class="sw-body">
	<div class="sw-container">
		<div class="sw-top-bar"></div>
		<div class="k-content container sw-content">
			<div class="sw-header">
				<div class="sw-logo">
					<img src="${pageContext.request.contextPath}/img/sitewhere-small.png"/>
				</div>
			</div>
			
<style>
</style>

<div class="alert alert-error" style="font-size: 16pt; text-align: center; margin-top: 10px;" data-i18n="noserver.loaderrorms"></div>
<div style="font-size: 18pt;">${subsystem}</div>
<div style="font-size: 14pt; margin-top: 20px;">${component}</div>
<%@ include file="includes/bottom.inc"%>
<script type="text/javascript">
	/** Set sitewhere_title */
	sitewhere_i18next.sitewhere_title = "noserver.title";
</script>