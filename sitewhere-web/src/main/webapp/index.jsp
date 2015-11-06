<!DOCTYPE html>
<html>
<head>
<title>SiteWhere - Swagger UI</title>
<link href='http://fonts.googleapis.com/css?family=Droid+Sans:400,700' rel='stylesheet'
	type='text/css' />
<link href='css/hightlight.default.css' media='screen' rel='stylesheet' type='text/css' />
<link href='css/bootstrap.min.css' media='screen' rel='stylesheet' type='text/css' />
<link href='css/screen.css' media='screen' rel='stylesheet' type='text/css' />
<script type="text/javascript" src="lib/shred.bundle.js"></script>
<script src='lib/jquery-1.8.0.min.js' type='text/javascript'></script>
<script src='lib/jquery.slideto.min.js' type='text/javascript'></script>
<script src='lib/jquery.wiggle.min.js' type='text/javascript'></script>
<script src='lib/jquery.ba-bbq.min.js' type='text/javascript'></script>
<script src='lib/handlebars-1.0.0.js' type='text/javascript'></script>
<script src='lib/underscore-min.js' type='text/javascript'></script>
<script src='lib/backbone-min.js' type='text/javascript'></script>
<script src='lib/swagger.js' type='text/javascript'></script>
<script src='lib/swagger-ui.js' type='text/javascript'></script>
<script src='lib/highlight.7.3.pack.js' type='text/javascript'></script>
<script src='lib/base64.js' type='text/javascript'></script>
<script src='lib/jquery-1.8.0.min.js' type='text/javascript'></script>

<style type="text/css">
.info {
	padding-bottom: 25px;
	border-bottom: 1px solid #CCC;
}

.info_title {
	font-size: 18pt;
	padding-bottom: 5px;
}

.info_description {
	font-size: 12pt;
	padding-bottom: 15px;
}

#resources_container {
	margin-left: 0;
}

#api_info a {
	color: #dc0000;
}

body ul#resources li.resource div.heading h2 {
	font-size: 11pt;
}

body ul#resources li.resource div.heading h2 a {
	font-size: 14pt;
	color: #333;
}

.swagger-ui-wrap {
	max-width: 960px;
	margin-left: auto;
	margin-right: auto;
}

.icon-btn {
	cursor: pointer;
}

#message-bar {
	min-height: 30px;
	text-align: center;
	padding-top: 10px;
}

.message-success {
	color: #89BF04;
}

.message-fail {
	color: #cc0000;
}
</style>

<script type="text/javascript">
	$(function() {
		window.swaggerUi =
				new SwaggerUi({
					url : "${pageContext.request.contextPath}/api/api-docs",
					dom_id : "swagger-ui-container",
					supportHeaderParams : true,
					supportedSubmitMethods : [ 'get', 'post', 'put', 'delete' ],
					onComplete : function(swaggerApi, swaggerUi) {
						if (console) {
							console.log("Loaded SwaggerUI")
						}
						$('pre code').each(function(i, e) {
							hljs.highlightBlock(e)
						});
						window.authorizations.add("key", new ApiKeyAuthorization("Authorization",
							"Basic YWRtaW46cGFzc3dvcmQ=", "header"));
						window.authorizations.add("tenant", new ApiKeyAuthorization("X-SiteWhere-Tenant",
							"sitewhere1234567890", "header"));
					},
					onFailure : function(data) {
						if (console) {
							console.log("Unable to Load SwaggerUI");
						}
					},
					docExpansion : "none",
					sorter : "alpha"
				});

		window.swaggerUi.load();
	});

	$(document).ready(
		function() {
			$("#sw-login").change(
				function() {
					window.authorizations.add("key", new ApiKeyAuthorization("Authorization",
						makeBasicAuth(), "header"));
				});
			$("#sw-password").change(
				function() {
					window.authorizations.add("key", new ApiKeyAuthorization("Authorization",
						makeBasicAuth(), "header"));
				});
			$("#sw-tenant").change(
				function() {
					window.authorizations.add("tenant", new ApiKeyAuthorization("X-SiteWhere-Tenant", $(
						"#sw-tenant").val(), "header"));
				});

			// Compoute the basic auth token based on username and password.
			function makeBasicAuth() {
				var user = $("#sw-login").val();
				var password = $("#sw-password").val();
				var tok = user + ':' + password;
				var hash = Base64.encode(tok);
				return "Basic " + hash;
			}
		});
</script>
</head>

<body style="position: relative;">
	<div class="sw-top-bar" style="position: fixed; top: 0px; width: 100%; z-index: 500;"></div>
	<div id='header'
		style="position: fixed; top: 2px; width: 100%; z-index: 500; background-color: rgb(255, 255, 255);">
		<div class="swagger-ui-wrap">
			<a id="logo" href="http://www.sitewhere.org" target="_blank"><img
				src="img/sitewhere-small.png" /></a>

			<form id='api_selector' class="form-inline" style="padding-top: 18px;">
				<label for="sw-login">Login:</label><input type="text" class="input-medium" id="sw-login"
					style="margin-left: 5px;" value="admin" /><label for="sw-password" style="margin-left: 15px;">Password:</label>
				<input type="password" class="input-medium" id="sw-password" style="margin-left: 5px;"
					value="password" /><label for="sw-tenant" style="margin-left: 15px;">Tenant:</label><input
					type="text" class="input-medium" id="sw-tenant" style="margin-left: 5px;"
					value="sitewhere1234567890" />
			</form>
		</div>
	</div>

	<div>
		<div style="height: 50px;"></div>
		<div id="message-bar" class="swagger-ui-wrap">&nbsp;</div>
		<div id="swagger-ui-container" class="swagger-ui-wrap"></div>


	</div>

</body>

</html>