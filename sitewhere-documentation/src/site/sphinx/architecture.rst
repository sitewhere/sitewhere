=============================
SiteWhere System Architecture
=============================

This document describes the components that make up SiteWhere and how they relate to each other.

--------------------
Architecture Diagram
--------------------
The following diagram shows the main SiteWhere server components and how they interact with external
systems.

.. image:: /_static/images/sitewhere-architecture.png
   :width: 100%
   :alt: SiteWhere Architecture Diagram
   :align: left
   
-----------------
Global Components
-----------------
As shown in the architecture diagram, SiteWhere is composed of many different components that are wired
together to provide the core platform. In the sections below, we will cover the components that are
global to the system. All tenants share these global settings.

Web Application Container
-------------------------
SiteWhere is deployed as a Web Application Archive (WAR) file and is designed to run in a web container
such as Apache Tomcat. SiteWhere will run on a vanilla version of Apache Tomcat assuming the 
`configuration files <https://github.com/sitewhere/sitewhere/tree/master/sitewhere-core/config>`_
are copied into the Tomcat **conf** folder. The configuration files may be altered to change
the way SiteWhere processes device events and integrates with external services.

SiteWhere Server
----------------
SiteWhere Server is the central application that controls all of the other SiteWhere components. It is
started automatically from the deployed WAR file and bootstraps using a Spring configuration file located
in **conf/sitewhere/sitewhere-server.xml**. The server manages the common system components such as user 
management and the REST services. It also bootstraps one or more **tenant engines** which handle most of 
the other processing logic.

Administrative Application
--------------------------
SiteWhere includes an HTML5 administrative application that can be used to manage how the system functions.
The information that appears in the application is dependent on the login credentials provided and
the permissions associated with that user. By default, a new SiteWhere instance has a single **admin**
user that has all permissions and is associated with the default tenant.

Some aspects of the system such as users and tenants are global in scope. Changes made to these entities
will be reflected across all tenants. Administrative access at this level should be limited since these
users can create new users and tenants as well as shutting down running tenants, deleting existing 
users and affecting other system data across the board.

Most other aspects of the system are tenant-specific. Each user account may be associated with one or
more tenants, allowing the user to manage information for multiple tenants. When logging in to the
system, if a user is associated with more than one tenant, they are asked to choose which tenant to
manage before proceeding. All other system data such as sites, device specifications, devices, assignments,
etc are managed at a per-tenant level, so most of the data in the administrative interface depends on 
the user logged in and which tenant was selected.

REST Services
-------------
Most of the core functionality related to the SiteWhere APIs is accessible externally via REST services. Using the
services, an external entity can create, view, update, or delete entities in the system. The services can also 
interact with subsystems such as asset management. All REST calls are subject to authentication and use Spring Security
to verify that the user is authorized for the operation.

SiteWhere includes a working version of `Swagger <http://swagger.wordnik.com/>`_ which adds a user interface around
the REST services. Using the Swagger interface, you can interactively execute REST calls against a running SiteWhere
server and view the JSON responses. The default Swagger URL for a server is:

   http://sitewhere.hostname/sitewhere/
   
where **sitewhere.hostname** is the hostname of the server running SiteWhere.

Global Datastore
----------------
When storing and retrieving data, SiteWhere never deals directly with a database. Instead, the system defines
Service Provider Interfaces (SPIs) for the data operations it needs to operate and expects datastore 
implementations to comply with the required interfaces. The user management datastore is configured at
the global level and is based on the following APIs:

:`IUserManagement <apidocs/com/sitewhere/spi/user/IUserManagement.html>`_: Contains all of the core
   user management calls including CRUD methods for users, authorities, etc.

When configuring a new SiteWhere server instance, you change settings in the core 
Spring configuration file to indicate which type of datastore to use for the underlying 
data implementation. The types of datastores currently supported include MongoDB and Apache 
HBase.

Hazelcast Services
------------------
Hazelcast is an in-memory datagrid designed with high performance in mind. By default, each SiteWhere Server 
instance also acts as a Hazelcast instance. In the default global configuration, the Hazelcast configuration 
is loaded from **conf/sitewhere/hazelcast.xml**. Hazelcast support can be removed by removing the corresponding 
entry from the configuration file.

SiteWhere can use Hazelcast to interactively broadcast event data to other interested systems. 
For example, the SiteWhere plugin for Mule Studio uses Hazelcast to connect to a SiteWhere server 
instance and pull events into the bus as they occur. The events can then be processed using 
Mule flow logic in order to integrate event data with other cloud systems or perform other 
asynchronous processing tasks in real time. Access to Hazelcast clients can be limited by 
adjusting the SiteWhere server Hazelcast configuration so that, for instance, only machines in certain IP
ranges can receive the event data.

-----------------
Tenant Components
-----------------
Most components of the system are configured at the per-tenant level. This allows for clean separation 
of data and processing logic from one tenant to the next.

Tenant Engines
--------------
SiteWhere (starting with version 1.2.0) is designed as a multitenant system. That means that multiple IoT
applications can be served from a single SiteWhere instance. Each system tenant has a separate data store
so data is not intermingled between tenants. Each tenant also has a separate processing pipeline that can
be customized without affecting the processing of other tenants. When SiteWhere Server starts for the first
time, a default tenant is created based on the default tenant configuration file found at
**conf/sitewhere/sitewhere-tenant.xml**. The default configuration is copied to a tenant-specific
configuration file located at **conf/sitewhere/xxx-tenant.xml** where **xxx** is the unique id for the tenant.
Making changes to the configuration file for a tenant will alter the processing logic for just that tenant.
New tenants can be added from the SiteWhere administrative application. Once created, tenants can be started
and stopped dynamically without shutting down the entire server. For instance, to make configuration changes
to one tenant, it may be shut down, reconfigured, and brought back up without affecting other running tenants.

Tenant Datastores
-----------------
As with global datastores, tenant datastores configure SPI implementations that provide persistence of
tenant-level information such as device and asset management. Service provider interfaces implemented
include:

:`IDeviceManagement <apidocs/com/sitewhere/spi/device/IDeviceManagement.html>`_: Contains all of the core 
	device management calls including CRUD methods for sites, specifications, devices, events, etc.
:`IAssetManagement <apidocs/com/sitewhere/spi/asset/IAssetManagement.html>`_: Contains all of the core 
   asset management calls including CRUD methods for asset categories and assets.

Tenant datastores are configured in the **conf/sitewhere/xxx-tenant.xml** configuration file (where **xxx** 
is the tenant id).

Communication Engine
--------------------
The SiteWhere communication engine handles all functionality related to interacting with devices. 
Its responsibilities include:

:Registration of new or existing devices:
   SiteWhere devices can be created manually with API calls, but it is often preferable to have devices
   self-register. In that case, the device provides a unique hardware id and a specification token to the
   system which in turns creates a new device record that can start accepting events. SiteWhere assumes that
   each device will have a unique id in the system so it can be independently addressed. The specification 
   token passed at startup indicates the type of hardware the device is using and references a device specification
   that already exists in the system. Devices send a registration event when they boot or connect to the network
   and SiteWhere either creates a new device record or finds an existing one. SiteWhere returns a response message
   to the device indicating the registration status.
   
:Receipt of events from connected devices:
   Once registered with the system, devices can report any number or type of events to SiteWhere, which in turn stores
   the events. Event types include location updates, sensor measurements and other acquired data, or alerts in response
   to exceptional events. Devices also have the ability to acknowledge receipt of commands issued by SiteWhere.
   Events are delivered to SiteWhere via an inbound event pipeline which provides a modular way
   of introducing new functionality for processing incoming data.
   
:Delivery of commands to connected devices:
   Each device registered with SiteWhere has an associated device specification which is tied to the type
   of hardware running on the device. Each device specification has a list of commands that can be executed
   against devices with that specification. SiteWhere allows any number of commands to be added for a specification
   and each command can carry any number of arguments. The commands and arguments can be added via the administrative
   user interface or via REST calls. When commands are executed, they travel through a pipeline that encodes them
   in an expected format and delivers them across an expected protocol.
   
The flow of data in the SiteWhere communication engine is shown below:

.. image:: /_static/images/communication-engine.png
   :width: 100%
   :alt: SiteWhere Communication Engine
   :align: left

Asset Modules
-------------
SiteWhere assets represent objects in the physical world -- people, places, and things. Device specification
assets are used to describe the hardware information/configuration for a type of device. Device assignment
assets are used to describe an entity associated with a device -- a person associated with a badge or
a bulldozer associated with a location tracker or a hospital ward associated with a piece of hospital
equipment.

Rather than hard-coding a schema for assets in the system, SiteWhere defines SPIs for general asset types and
allows asset modules to be plugged in to provide asset definitions. This allows existing identity management
systems to be used in providing a list of available person assets. It also allows product catalog systems to 
be used in defining available hardware assets. SiteWhere uses asset modules in a read-only manner and only 
ever references entities based on a unique id understood by the underlying asset module. Maintaining the list
of available assets is left to the systems behind the asset modules (which usually already have a user interface
specific to the features they provide).

SiteWhere also provides the concept of **asset categories** which reside in the SiteWhere datastore. Asset 
categories are containers for assets of a given type and may be added/edited from within the administrative console. 
Asset categories are loaded as asset modules at runtime, allowing assets to be pulled from the datastore in addition 
to modules loaded from other sources such as XML files or third-party systems.
	
------------
Object Model
------------
SiteWhere provides a comprehensive object model that captures the relationships between all of the various 
concepts in tracking device data. The diagram below shows some of the core objects in the model and their 
relationships:

.. image:: /_static/images/sitewhere-object-model.png
   :width: 100%
   :alt: SiteWhere Object Model
   :align: left
   
Sites
-----
Sites are used to organize devices that are related so that their events can be looked at from a grouped perspective. 
The primary use case for sites is in location-aware devices. A site provides a containing entity to which a map can 
be assigned so that location data can be viewed in the context of that map. When creating a site in the administrative 
application, you can assign a map type and initial location/zoom so that location events for that site are rendered 
on the given map. The map rendering code uses the Leaftlet JavaScript map library and is able to create a dynamic 
overlay layer based directly on SiteWhere REST calls. Currently supported map types include:

:Mapquest World Map:
	Used to render location data on a world map using MapQuest tiles.
:GeoServer Custom Map (Custom Tileset):
	Used to render data on a custom GeoServer tile layer. This allows unique maps and floor plans to be 
	used as the background for SiteWhere location rendering. The maps can include custom vector or raster data
	specific to the location data being visualized.

More map types will be added in the near future, but most use cases are covered by the existing map types.

.. note:: SiteWhere was originally written as a system to track location-aware devices. Sites provided a way to group 
	devices in the same physical vicinity (for instance in the same office building). There are some use cases 
	that do not necessarily require the location-based aspects of sites, but they can still benefit from being 
	able to view events across a group of related devices.

Zones
-----
Another important feature for location-aware applications is the concept of zones that carry special meanings. For instance, 
in an airport, there are secure areas where only certain personnel should be allowed. In an application that monitors 
airport security, it makes sense to be able to fire an alert if an unauthorized person enters a secure zone.

The SiteWhere administrative application allows zones to be defined based on the map associated with a site. In the 
zone editor, you can click points on the map to set the boundaries of the zone. You can also specify the border and 
background colors as well as the opacity of the overlay when shown on the map. The Leaflet overlays automatically load 
the list of zones for a site when displaying its map. On the integration side of things, SiteWhere provides a node in 
Mule Studio that will compare locations coming into the system against defined zones, allowing the developer to react 
to devices entering or exiting zones.

Device Specifications
---------------------
Specifications are used to capture characteristics of a given hardware configuration. This is not necessarily a
one-to-one mapping to a part number or SKU since some peripheral devices may have been added or certain characteristics
upgraded. A device specification contains a reference to a hardware asset which provides the basic information about
the hardware including name, description, image URL, etc. 

:Device Specification Commands:
	A device specification contains a list of commands that may be invoked by SiteWhere on the device. Commands can be 
	added, updated, viewed, and deleted in the admin UI or via the REST services. It is perfectly acceptable for two 
	device specifications to point to the same asset type, but have a different set of commands, reflecting different 
	configurations of the given device.

:Device Command Invocations:
	SiteWhere provides APIs for invoking commands on a device based on the list available in its device specification.
	Each command invocation is captured as an event associated with the current device assignment. The admin UI and 
	REST services allow commands to be invoked and previous invocations for an assignment to be searched.
	
:Device Command Responses:
	After a device processes a command invocation, it may return a response to SiteWhere. Command invocation messages
	carry an originator event id that can be sent back with any responses to tie responses back to the event that
	they are responding to. SiteWhere provides a simple ack event that acknowledges receipt of an event. Devices can
	also return locations, measurements, or alerts in responses to commands and use the originator id to associate
	those with a command as well. From an API perspective, a user can list the responses for a given command (any
	number of responses can be associated) and act on the responses to initiate other actions.
	
Devices
-------
Devices are a SiteWhere representation of connected physical hardware that conforms to an assigned device specification.
Each device is addressable by a unique hardware id that identifies it uniquely in the system. A new device can register
itself in the system by providing a hardware id and device specification token. SiteWhere in turn creates a new 
device record via the APIs and (optionally) creates a placeholder unassociated device assignment (see below) to allow 
events to be collected for the device. Devices can be manually added via the REST services or via the admin UI.
	
Device Groups
-------------
Device groups allow multiple related devices or subgroups to be organized into logical units. The groups can
then be used for performing operations collectively rather than performing them on a per-device basis. 
Each group can have zero or more roles assigned to it, allowing arbitrary groupings based on application needs.
Devices may belong to multiple groups and may be assigned zero or more roles within the group. This structure allows
queries such as "find all devices from the 'heavy-equipment' group that have the role 'leased-equipment' and issue
a 'getCurrentLocation' command".

Device Assignments
------------------
Events are not logged directly against devices, since a given device may serve in a number of contexts. For instance,
a visitor badge may be assigned to a new person every day. Rather than intermingle event data from all the people a 
badge has been assigned to, the concept of a device assignment allows events to be associated with the asset they
relate to. A device assignment is an association between a device, a site, and (optionally) a related asset. Some
assignments do not specify an asset and are referred to as 'unassociated'. A real-world example of this is a
vending machine that has wireless connectivity to report inventory. The device **is** the asset, so there is no need
to associate an external asset.

:Current Device State:
	Device assignments also act as a storage bin for recent device state. As events are processed for an assignment it
	can keep a record of the most recent event values. By default, the assignment stores the most recent location measurement,
	the most recent value for each measurement identifier, and the most recent alert for each alert type. Using this
	stored state, SiteWhere can infer the current state of a device without having to send a command to request new data.
	Included in the state information is the date on which the data was stored, so logic can intelligently choose when
	to request an update of the data.

:Assignment Status Indicator:
	Each device assignment also holds a status of the assignment itself. By default, an assignment is marked 'active' 
	immediately after it is created. Using the REST services or admin UI, the status can be changed to 'missing' if the
	device or associated asset have been reported missing. Processing logic can be altered for missing assignments. The
	assignment status is updated to 'released' when an assignment is terminiated. This indicates the device is no longer 
	assigned and may be reassigned.
	
Device Events
-------------
Device events are the data generated by connected devices interacting with SiteWhere. They are the core data that SiteWhere
revolves around. SiteWhere captures many types of events including:

:Meaurements:
	Measurement events send measured values from a device to SiteWhere. Measurements are name/value pairs that capture 
	information gathered by the device. For instance, a weather sensing device might send measurements for temperature, 
	humidity, and barometric pressure. A single measurements event can send any number of measurements to SiteWhere 
	(to prevent the overhead of having to repeatedly send multiple events to capture state).

:Locations:
	Location events are used to reflect geographic location (latitude, longitude, and elevation) for devices that have 
	the ability to measure it. Location events are stored with a geospatial index when added to Solr so that they 
	may be queried as such.
	
:Alerts:
	Alert events are sent from a device to indicate exceptional conditions that SiteWhere may need to act on. 
	For instance, a fire alarm might send a "smoke detected" alert to indicate it has been triggered. Alerts contain 
	two primary pieces of information: a type and a message. The alert type is used to identify which class of alert
	is being fired. The alert message is a human-readable message suitable for displaying in a monitoring application.
	
:Command Invocations:
	Each time a command is invoked via SiteWhere, the information about the invocation is stored as an event for the
	current device assignment. The invocation captures which command was executed, when it was executed, values for
	command parameters, etc.
	
:Command Responses:
	If a device generates a response to a command invocation, the response is stored as an event. Responses may be in
	the form of measurements, locations, alerts, or acks. If the device passes an originating event id with the response,
	the response is tied back to the original command invocation. The REST APIs allow all responses for a given
	command invocation to be enumerated so that they can be processed.
	
All event types share some common information that applies more generally to events. Each event records an event date 
and a received date. The event date is the timestamp for when the data was gathered by the device. It is important to 
understand that some devices cache events to prevent the battery drain of network access. Because of this, the event 
date may differ significantly from the received date, which is the date that the event was processed by SiteWhere. 
All events also contain an area for arbitrary metadata. This allows application-specific information to be piggy-backed 
on events so it can be used in later processing.
	