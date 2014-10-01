===============================
 SiteWhere System Architecture
===============================

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
System Components
-----------------
As shown in the architecture diagram, SiteWhere is composed of many different components that are wired
together to provide the core platform. In the sections below, we will cover the individual components in
more detail.

Web Application Container
-------------------------
SiteWhere is deployed as a Web Appliction Archive (WAR) file and is designed to run in a web container.
The WAR file itself is not self-sufficient since most of the library dependencies are not packaged in the
WAR. The process of building and deploying updated versions of the WAR is much faster without including
the 50+ MB of library dependencies. As a result, you will need to use the packaged version of SiteWhere
server, which is just a standard Tomcat 7 install with the extra libraries and some extra configuration 
files added. If you want to run SiteWhere in another application container, you will need to make sure the
classpath contains all of the files in the **sitewhere** folder.

Datastores
----------
When storing and retrieving data, SiteWhere never deals directly with a database. Instead, the system defines
Service Provider Interfaces (SPIs) for the data operations it needs to operate and expects datastore 
implementations to comply with the required interfaces. The two core interfaces that a datastore needs
to implement are:

:`IDeviceManagement <apidocs/com/sitewhere/spi/device/IDeviceManagement.html>`_: Contains all of the core 
	device management calls including CRUD methods for sites, specifications, devices, events, etc.
:`IUserManagement <apidocs/com/sitewhere/spi/user/IUserManagement.html>`_: Contains all of the core
	user management calls including CRUD methods for users, authorities, etc.

When configuring a new SiteWhere server instance, you change settings in the core 
Spring configuration file to indicate which type of datastore to use for the underlying 
data implementation. The types of datastores currently supported include MongoDB and Apache 
HBase. MongoDB is a great choice for running on a personal workstation or a  cloud instance with limited 
resources. HBase is better suited for projects that require massive scalability,  but at the expense of 
more overhead both in system configuration and system resources. For more information
on configuring a datastore for SiteWhere see `Datastore Configuration <configuration/datasources.html>`_ 
in the configuration guide.

Asset Modules
-------------
In SiteWhere terminology, an asset represents extra information that provides context about a device. Every
device references a device specification that indicates the type of hardware the device is using. The 
specification is in turn associated with an asset type which gives detailed information about the device.
The asset type includes things like a human-readable name, description, SKU, URL for a product image, and any number of
other properties that enhance the understanding of the hardware. Assets are also used to provide information
about entities that are associated with devices. For instance a person asset may be associated with a badge.
A hardware asset may indicate the type of vehicle a location device is attached to.

Rather than hard-coding a schema for assets in the system, SiteWhere defines SPIs for general asset types and
allows asset modules to be plugged in to provide asset definitions. This allows existing identity managment
systems to be used in providing a list of available person assets. It also allows product catalog systems to 
be used in defining available hardware assets. SiteWhere uses asset modules in a read-only manner and only 
ever references entities based on a unique id understood by the underlying asset module. Maintaining the list
of available assets is left to the systems behind the asset modules (which ususally already have a user interface
specific to the features they provide).

REST Services
-------------
Most of the core functionality related to the SiteWhere APIs is accessible externally via REST services. Using the
services, an external entity can create, view, update, or delete entities in the system. The services can also 
interact with subsystems such as asset management. All REST calls are subject to authentication and use Spring Security
to verify that the user is authorized for the operation. Currently the system uses basic authentication over an
unencrypted pipe, so the data is not secure. Data can be secured by changing communication to use basic auth over SSL,
which is considered a reasonable approach for sending REST data securely. This will become the default setup as 
SiteWhere nears a 1.0 release.

SiteWhere includes a working version of `Swagger <http://swagger.wordnik.com/>`_ which adds a user interface around
the REST services. Using the Swagger interface, you can interactively execute REST calls against a running SiteWhere
server and view the JSON responses. The default Swagger URL for a server is:

	http://sitewhere.hostname/sitewhere/
	
where **sitewhere.hostname** is the hostname of the server running SiteWhere.

Administrative Application
--------------------------
SiteWhere includes an HTML5 administrative application that can be used to set up system data to allow it to process
information from devices. The application offers the following features:

:Manage site information:
	Sites are a coarse-grained constructs used for grouping related devices. They are very useful for location-based
	processing because each site can have map information associated with it. The admin UI allows sites to be created,
	updated, viewed, and deleted. It also allows the type of map, initial location and zoom level to be associated
	so that visualizations have a map on which to display markers. A SiteWhere `Leaflet <http://leafletjs.com/>`_ 
	plugin is available which can automatically load the correct map and display device location information
	based on data stored with the site.
	
:Manage device specifications:
	Device specifications can be created, updated, viewed or deleted via the administrative interface. For a 
	given specification, you can drill down to manage the list of commands available to devices that use it. 
	The UI includes utilities for generating  
	`Google Protocol Buffer <https://developers.google.com/protocol-buffers/docs/overview>`_ descriptors for 
	automated building of an encoded command protocol between SiteWhere and connected devices. Using the 
	protocol buffer definition, code stubs can be generated in a number of languages so that messages can be
	passed to the device efficiently.
	
:Manage available devices:
	The administrative UI allows information for all existing devices to be viewed including information about
	the asset (if any) that the device is assigned to. New devices can be registered in the system manually 
	using the interface. A history of all assets the device has ever been assigned to is available as part of
	the interface. From there, a user can navigate to any individual assignment to review events that occurred
	while the device was assigned.
	
:Manage device assignments:
	Devices may be assigned to assets using the administrative UI. The interface dynamically queries the 
	asset management modules to allow the user to browse the underlying assets and associate them with devices.
	Existing assignments can have their assignment state changed from the interface to mark them as missing or 
	to end the assignment so that the device can be reassigned to another asset. For each device assignment,
	the user can view detailed records of all events that occurred during the assignment.
	
:Emulate events from an assignment:
	When viewing a device assignment, you have the option of using a built-in device emulator that sends events
	into the system under the identity of the given device. This feature is useful for testing of backend logic
	because event data can be added to the system witout the need for a physical device. The emulator interface
	includes a map for adding location data. It also includes interfaces for adding custom measurements and
	alerts on behalf of the device. The emulator uses MQTT over web sockets to deliver the event data to 
	SiteWhere, so an external MQTT broker is required for delivery of events.
	
:Manage system users:
	The user management system controls which agents are allowed to access which resources in SiteWhere. The 
	adminstrative UI allows new users to be created and provides an easy way to manage permissions for access 
	various parts of the system.

Hazelcast Services
------------------
SiteWhere uses Hazelcast to interactively broadcast event data to other interested systems. Hazelcast is an 
in-memory datagrid designed with high performance in mind. For example, the SiteWhere plugin for Mule Studio
uses Hazelcast to connect to a SiteWhere server instance and pull events into the bus as they occur. The events
can then be processed using Mule flow logic in order to integrate event data with other cloud systems or
perform other asynchronous processing tasks in real time. Access to Hazelcast clients can be limited by 
adjusting the SiteWhere server Hazelcast configuration so that, for instance, only machines in certain IP
ranges can receive the event data.

Solr Integration
----------------
The backend datastores used by SiteWhere are intended to store a lot of information which is accessed in a 
very specific pattern -- namely, events are stored based on device assignment and time they occur. This 
approach scales well and stores all the information so that it can easily be retrieved, but it does not
do a good job of supporting adhoc queries. Apache Solr Cloud is a highly scalable, distributed search
engine that indexes data in a document-centric view. Rather than try to reinvent the wheel and provide
advanced searching directly in SiteWhere, a module has been created that translates SiteWhere events into
Solr documents and indexes them in the engine. This allows for advanced queries that would not be possible
using the underlying data stores alone. Solr allows for advanced searches using a custom SiteWhere schema
that indicates how event data should be indexed. Solr can then be queried for SiteWhere events based on
features like geospatial searches, faceted result sets, and other complicated searches that make it possible
to derive more meaning from the event data.

External Search Providers
-------------------------
SiteWhere supports an abstracted view of external search engines that operate on 
SiteWhere data. External search providers allow the SiteWhere REST services to take advantage of features 
particular to the underlying search engine while still returning results in a predictable format. 
For instance, the Solr external search provider allows a user to pass a Solr query string as part of the
REST call, taking advantage of powerful Solr features while returning a result set in the same format
SiteWhere uses for searches on its core datastores. This approach allows SiteWhere to enrich the result 
data if necessary, and presents a single view of the data whether stored in SiteWhere or indexed in an
engine optimized for adhoc queries.

-------------------
Provisioning Engine
-------------------
A central concept in SiteWhere is the idea of device provisioning. Device provisioning can be a loaded term since,
depending on who you ask, it can mean anything from setting up network access, to loading firmware, to activating 
or otherwise enabling a device. Provisioning in SiteWhere involves a few key elements.

:Registration of new or existing devices:
	SiteWhere has services and API calls for creating new devices, but it is often preferable to have devices
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
	Events are delivered to SiteWhere via an inbound event pipeline, which can be configured to watch on
	various transports and use various methods of decoding events.
	
:Delivery of commands to connected devices:
	Each device registered with SiteWhere has an associated device specification which is tied to the type
	of hardware running on the device. Each device specification has a list of commands that can be executed
	against devices with that specification. SiteWhere allows any number of commands to be added for a specification
	and each command can carry any number of arguments. The commands and arguments can be added via the administrative
	user interface or via REST calls. When commands are executed, they travel through a pipeline that encodes them
	in an expected format and delivers them across an expected protocol.
	
The flow of data in the SiteWhere provisioning engine is shown below:

.. image:: /_static/images/provisioning/provisioning.png
   :width: 100%
   :alt: SiteWhere Provisioning Data Flow
   :align: left

	
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
	