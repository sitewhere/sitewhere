Overview
========
Most of the core functionality provided by the SiteWhere APIs is accessible externally 
via REST services. Using the services, an external entity can create, view, update, or 
delete entities in the system. The services can also interact with subsystems such as 
asset management. All REST calls are subject to authentication and use the permissions 
model to verify that the authenticated user is authorized for the operation.

SiteWhere Server includes a working version of Swagger which adds a user interface around 
the REST services. Using the Swagger interface, users can interactively execute REST calls 
against a running SiteWhere instance and view the JSON responses.

The documentation that follows covers the functionality provided by the services including
the URL path used to execute the method, parameters needed to supply information to the
invocation, and examples of the request and/or response payloads.