<!DOCTYPE html>
<html>
<head>
    <title>SiteWhere - Swagger UI</title>
    <link href='http://fonts.googleapis.com/css?family=Droid+Sans:400,700' rel='stylesheet' type='text/css'/>
    <link href='css/hightlight.default.css' media='screen' rel='stylesheet' type='text/css'/>
    <link href='css/bootstrap.min.css' media='screen' rel='stylesheet' type='text/css'/>
    <link href='css/screen.css' media='screen' rel='stylesheet' type='text/css'/>
    <script src='lib/jquery-1.8.0.min.js' type='text/javascript'></script>
    <script src='lib/jquery.slideto.min.js' type='text/javascript'></script>
    <script src='lib/jquery.wiggle.min.js' type='text/javascript'></script>
    <script src='lib/jquery.ba-bbq.min.js' type='text/javascript'></script>
    <script src='lib/handlebars-1.0.rc.1.js' type='text/javascript'></script>
    <script src='lib/underscore-min.js' type='text/javascript'></script>
    <script src='lib/backbone-min.js' type='text/javascript'></script>
    <script src='lib/swagger.js' type='text/javascript'></script>
    <script src='lib/swagger-ui.js' type='text/javascript'></script>
    <script src='lib/highlight.7.3.pack.js' type='text/javascript'></script>
    <script src='lib/jquery.base64.min.js' type='text/javascript'></script>
    
    <script type="text/javascript">
    	var _sw_override_basePath = "${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/api";
    </script>

    <style type="text/css">
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
        $(function () {
            window.swaggerUi = new SwaggerUi({
                discoveryUrl:"${pageContext.request.contextPath}/api/api-docs",
                apiKey:"",
                dom_id:"swagger-ui-container",
				apiKeyName: "Authorization",
				headers: {"Authorization": "Basic YXBpdXNlcjphcGl1c2VycGFzc3dvcmQ=" },
				supportHeaderParams: true,
                supportedSubmitMethods: ['get', 'post', 'put', 'delete'],
                onComplete: function(swaggerApi, swaggerUi){
                	if(console) {
                        console.log("Loaded SwaggerUI")
                        console.log(swaggerApi);
                        console.log(swaggerUi);
                    }
                  $('pre code').each(function(i, e) {hljs.highlightBlock(e)});
                },
                onFailure: function(data) {
                	if(console) {
                        console.log("Unable to Load SwaggerUI");
                        console.log(data);
                    }
                },
                docExpansion: "none"
            });

            window.swaggerUi.load();
        });
        
    	
    	$(document).ready(function() {
    		
    		$(document).on('submit', '.sandbox', function() {
				alert("Nope!");
				return false;
	        });    
    	});
	</script>
</head>

<body style="position: relative;">
<div class="sw-top-bar" style="position: fixed; top: 0px; width: 100%; z-index: 500;"></div>
<div id='header' style="position: fixed; top: 2px; width: 100%; z-index: 500; background-color: rgb(255, 255, 255);">
    <div class="swagger-ui-wrap">
        <a id="logo" href="http://www.sitewhere.org" target="_blank"><img src="img/sitewhere-small.png"/></a>

    	<form id='api_selector' class="form-inline" style="padding-top: 18px;">
            <label for="sw-login">Login:</label>
            <input type="text" id="sw-login" style="margin-left: 5px;"/>
            <label for="sw-password" style="margin-left: 15px;">Password:</label>
            <input type="password" id="sw-password" style="margin-left: 5px;"/>
			<input placeholder="http://example.com/api" id="input_baseUrl" name="baseUrl" type="hidden"/>
			<input placeholder="api_key" id="input_apiKey" name="apiKey" type="hidden"/>
    	</form>    
    </div>
</div>

<div>
	<div style="height: 50px;"></div>
	<div id="message-bar" class="swagger-ui-wrap">
		&nbsp;
	</div>
	<div id="swagger-ui-container" class="swagger-ui-wrap">
</div>


</div>

</body>

</html>
