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
<%@ include file="i18next.inc"%>
</head>
<body class="sw-body">
	<div class="sw-container">
		<div class="sw-top-bar"></div>
		<div class="k-content container sw-content">
			<div class="sw-header">
				<div class="sw-logo">
					<img src="${pageContext.request.contextPath}/img/sitewhere-small.png"/>
				</div>
				<div class="sw-main-nav">
					<ul class="nav nav-pills">
<c:choose>
	<c:when test="${sitewhere_section == 'sites'}">
		<li class="active"><a href="${pageContext.request.contextPath}/admin/sites/list.html" data-i18n="public.Sites"></a></li>
	</c:when>
	<c:otherwise>
		<li><a href="${pageContext.request.contextPath}/admin/sites/list.html" data-i18n="public.Sites"></a></li>
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${sitewhere_section == 'specifications'}">
		<li class="active"><a href="${pageContext.request.contextPath}/admin/specifications/list.html" data-i18n="public.Specifications"></a></li>
	</c:when>
	<c:otherwise>
		<li><a href="${pageContext.request.contextPath}/admin/specifications/list.html" data-i18n="public.Specifications"></a></li>
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${sitewhere_section == 'devices'}">
		<li class="active"><a href="${pageContext.request.contextPath}/admin/devices/list.html" data-i18n="public.Devices"></a></li>
	</c:when>
	<c:otherwise>
		<li><a href="${pageContext.request.contextPath}/admin/devices/list.html" data-i18n="public.Devices"></a></li>
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${sitewhere_section == 'devicegroups'}">
		<li class="active"><a href="${pageContext.request.contextPath}/admin/groups/list.html" data-i18n="public.DeviceGroups"></a></li>
	</c:when>
	<c:otherwise>
		<li><a href="${pageContext.request.contextPath}/admin/groups/list.html" data-i18n="public.DeviceGroups"></a></li>
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${sitewhere_section == 'batch'}">
		<li class="active"><a href="${pageContext.request.contextPath}/admin/batch/list.html" data-i18n="public.Batch"></a></li>
	</c:when>
	<c:otherwise>
		<li><a href="${pageContext.request.contextPath}/admin/batch/list.html" data-i18n="public.Batch"></a></li>
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${sitewhere_section == 'users'}">
		<li class="active"><a href="${pageContext.request.contextPath}/admin/users/list.html" data-i18n="public.Users"></a></li>
	</c:when>
	<c:otherwise>
		<li><a href="${pageContext.request.contextPath}/admin/users/list.html" data-i18n="public.Users"></a></li>
	</c:otherwise>
</c:choose>
					</ul>
				</div>
				<div class="sw-user-info">
					<div class="btn-group">
						<a class="btn dropdown-toggle" data-toggle="dropdown" href="javascript:void(0)">
							<i class="icon-off sw-button-icon"></i>
							<div class="ellipsis" style="max-width: 120px; display: inline-block; vertical-align: top;">
								<span>
									<c:out value="${currentUser.firstName}"/>&nbsp;<c:out value="${currentUser.lastName}"/>
								</span>
							</div>
							<span class="caret" style="margin-left: 4px"></span>
						</a>
						<ul class="dropdown-menu pull-right" style="text-align: left;">
							<li><a tabindex="-1" href="<c:url value="/j_spring_security_logout"/>" data-i18n="includes.top.LogOut"></a></li>
						</ul>
					</div>			
				</div>
			</div>
			
			<!-- SiteWhere alert dialog -->
			<div id="alert-dialog" class="modal hide" style="z-index: 5000;">
				<div class="modal-header k-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h3 id="alert-dialog-title">Alert Title</h3>
				</div>
				<div id="alert-dialog-body" class="modal-body">
				</div>
				<div class="modal-footer">
					<a href="javascript:void(0)" data-dismiss="modal" class="btn btn-primary" data-i18n="public.Close"></a>
				</div>
			</div>
			
			<!-- SiteWhere confirm dialog -->
			<div id="confirm-dialog" class="modal hide" style="z-index: 5000;">
				<div class="modal-header k-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h3 id="confirm-dialog-title">Confirm Title</h3>
				</div>
				<div id="confirm-dialog-body" class="modal-body">
				</div>
				<div class="modal-footer">
					<a href="javascript:void(0)" onclick="swConfirmSubmit(false)" class="btn" data-i18n="public.Cancel"></a>
					<a href="javascript:void(0)" onclick="swConfirmSubmit(true)" id="confirm-dialog-submit" 
						class="btn btn-primary" data-i18n="public.OK"></a>
				</div>
			</div>
			