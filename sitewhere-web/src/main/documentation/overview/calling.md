## Calling SiteWhere REST Services
SiteWhere REST services can be invoked by submitting an HTTP request to 
the proper URI for the specified operation. An example of a valid HTTP
request-response interaction is captured below:

<pre><code class="http">GET /sitewhere/api/sites HTTP/1.1
Authorization: Basic YWRtaW46cGFzc3dvcmQ=
X-SiteWhere-Tenant: sitewhere1234567890

HTTP/1.1 200 OK
Server: Apache-Coyote/1.1
Cache-Control: max-age=0,no-cache,no-store,post-check=0,pre-check=0
Expires: Mon, 26 Jul 1997 05:00:00 GMT
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Wed, 14 Oct 2015 13:00:23 GMT

{"numResults":1,"results":[{"createdDate":"2015-10-13T12:41:04.061-0400","createdBy":"system","updatedDate":null,"deleted":false,"token":"bb105f8d-3150-41f5-b9d1-db04965668d3","name":"Construction Site 1","description":"A construction site with many high-value assets that should not be taken offsite. The system provides location tracking for the assets and notifies administrators if any of the assets move outside of the general site area or into areas where they are not allowed.","imageUrl":"https://s3.amazonaws.com/sitewhere-demo/construction/construction.jpg","map":{"type":"mapquest","metadata":{"centerLongitude":"-84.23966646194458","centerLatitude":"34.10469794977326","zoomLevel":"15"}},"metadata":{}}]}
</code></pre>

### HTTP Verbs
SiteWhere REST services follow the standard pattern of using HTTP verbs to 
indicate the type of operation to be performed as detailed below:

- **GET** - Get information about a given resource. The result will be in 
the form of an entity or list of entities. Many list operations return
responses in the form of search results that include the number of matching
records and the list of matching results.
- **POST** - Create a new resource or cause an effect on some part of the
system. Most POST operations return an entity that is the result of the
effect of the submission.
- **PUT** - Update an existing resource with new information. The general
pattern with updates is to only submit the fields that changed. Not passing
a given field leaves the existing value as-is.
- **DELETE** - Deletes an existing entity from the system. Most entities
support either a *forced* delete which deletes the entity from the datastore 
or a *marked* delete which flags the entity as deleted. See [delete policies](delete-policies)
for more information.

### Required Headers
Certain headers are required as part of the HTTP request in order for
SiteWhere to process it properly. They include:
 
- **Authorization** - [Basic authentication](https://en.wikipedia.org/wiki/Basic_access_authentication)
is used to identify the user accessing the REST resource. The value passed must match credentials for
a user in the system that has access to the given resource. If not authenticated, the response
status code will be 403.
- **X-SiteWhere-Tenant** - The authentication token for the tenant that the operation is to
occur on. The authenticated user must have permissions to access the given tenant. Another option
for passing the tenant auth token is to send a request parameter named **tenantAuthToken** with
the value. Not passing the token will result in a response status code of 401.