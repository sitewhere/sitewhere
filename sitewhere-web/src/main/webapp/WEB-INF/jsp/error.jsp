<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="sitewhere_title" value="Error" />
<!DOCTYPE html>
<html class="sw-body">
<head>
<title>OfficerGuardian - <c:out value="${sitewhere_title}" /></title>
<script src="${pageContext.request.contextPath}/scripts/jquery-1.10.2.min.js"></script>
<script src="${pageContext.request.contextPath}/scripts/jquery.validity.js"></script>
<script src="${pageContext.request.contextPath}/scripts/kendo.web.min.js"></script>
<script src="${pageContext.request.contextPath}/scripts/modernizr.js"></script>
<script src="${pageContext.request.contextPath}/scripts/moment.js"></script>
<script src="${pageContext.request.contextPath}/scripts/sitewhere.js"></script>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/img/favicon.ico" />
<link href="${pageContext.request.contextPath}/css/kendo.common.min.css" rel="stylesheet" />
<link href="${pageContext.request.contextPath}/css/kendo.bootstrap.min.css" rel="stylesheet" />
<link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet"
	media="screen">
<link href="${pageContext.request.contextPath}/css/font-awesome.min.css" rel="stylesheet"
	media="screen">
<link href="${pageContext.request.contextPath}/css/jquery.validity.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/sitewhere.css" rel="stylesheet" />
<%@ include file="includes/i18next.inc"%>
</head>

<body class="sw-body">
	<div class="sw-container">
		<div class="sw-top-bar"></div>
		<div class="k-content container sw-content">
			<div class="sw-header">
				<div class="sw-logo">
					<img src="${pageContext.request.contextPath}/img/sitewhere-small.png" />
				</div>
			</div>

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