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
SiteWhere is deployed as a *Web Appliction Archive (WAR)* file and is designed to run in a web container.
The WAR file itself is not self-sufficient since most of the library dependencies are not packaged in the
WAR. The process of building and deploying updated versions of the WAR is much faster without including
the 50+ MB of library dependencies. As a result, you will need to use the packaged version of SiteWhere
server, which is just a standard Tomcat 7 install with the extra libraries and some extra configuration 
files added. If you want to run SiteWhere in another application container, you will need to make sure the
classpath contains all of the files in the **${sitewhere_home}/sitewhere** folder.

Datastores
----------
When storing and retrieving data, SiteWhere never deals directly with a database. Instead, the system defines
*Service Provider Interfaces (SPIs)* for the data operations it needs to operate and expects datastore 
implementations to comply with the required interfaces. When configuring a new SiteWhere server instance, 
you change settings in the core Spring configuration file to indicate which type of datastore to use for 
the underlying data implementation. The types of datastores currently supported include MongoDB and Apache 
HBase. MongoDB is a great choice for running on a personal workstation or a  cloud instance with limited 
resources. HBase is better suited for projects that require massive scalability,  but at the expense of 
more overhead both in system configuration and system resources. For more information
on configuring a datastore for SiteWhere see **XXXXX** in the configuration guide.

Asset Modules
-------------
In SiteWhere terminology, an *asset* represents extra information that provides context about a device. Every
device references a *device specification* that indicates the type of hardware the device is using. The 
specification is in turn associated with an asset type which gives detailed information about the device.
The asset type includes things like a human-readable name, description, SKU, URL for a product image, and any number of
other properties that enhance the understanding of the hardware. Assets are also used to provide information
about entities that are associated with devices. For instance a *person asset* may be associated with a badge.
A *hardware asset* may indicate the type of vehicle a location device is attached to.

Rather than hard-coding a schema for assets in the system, SiteWhere defines *SPIs* for general asset types and
allows *asset modules* to be plugged in to provide asset definitions. This allows existing identity managment
systems to be used in providing a list of available person assets. It also allows product catalog systems to 
be used in defining available hardware assets. SiteWhere uses asset modules in a read-only manner and only 
ever references entities based on a unique id understood by the underlying asset module. Maintaining the list
of available assets is left to the systems behind the asset modules (which ususally already have a user interface
specific to the features they provide). For more information on configuring asset modules see **XXXXX** in the 
configuration guide.

Provisioning Engine
-------------------
A central concept in SiteWhere is the idea of device provisioning. Device provisioning is a loaded term since,
depending on who you ask, it can mean anything from setting up network access, to loading firmware, to activating 
or otherwise enabling a device. Provisioning in SiteWhere involves a few key elements.

:Registration of new or existing devices:
	SiteWhere has services and API calls for creating new devices, but it is often preferable to have devices
	self-register. In that case, the device provides a unique *hardware id* and *specification token* to the
	system which in turns creates a new device record that can start accepting events. SiteWhere assumes that
	each device will have a unique id in the system so it can be independently addressed. The specification 
	token passed at startup indicates the type of hardware the device is using and references a *device specification*
	that already exists in the system. Devices send a registration event when they boot or connect to the network
	and SiteWhere either creates a new device record or finds an existing one. SiteWhere returns a response message
	to the device indicating the registration status.
	
:Receipt of events from connected devices:
	Once registered with the system, devices can report any number or type of events to SiteWhere, which in turn stores
	the events. Event types include location updates, sensor measurements and other acquired data, or alerts in response
	to exceptional events. Devices also have the ability to acknowledge receipt of commands issued by SiteWhere.
	Events are delivered to SiteWhere via an inbound event pipeline, which can be configured to watch on
	various transports and use various methods of decoding events. For more information on customizing how events
	are processed, see **XXXXX** in the configuration guide.
	
:Delivery of commands to connected devices:
	Each device registered with SiteWhere has an associated *device specification* which is tied to the type
	of hardware running on the device. Each device specification has a list of *commands* that can be executed
	against devices with that specification. SiteWhere allows any number of commands to be added for a specification
	and each command can carry any number of arguments. The commands and arguments can be added via the administrative
	user interface or via REST calls. When commands are executed, they travel through a pipeline that encodes them
	in an expected format and delivers them across an expected protocol. For more information about commands and
	how they are delivered, see **XXXXX** in the configuration guide.
	

REST Services
-------------
Most of the core functionality related to the SiteWhere APIs is accessible externally via REST services. Using the
services, an external entity can create, view, update, or delete entities in the system. The services can also 
interact with subsystems such as asset management. All REST calls are subject to authentication and use *Spring Security*
to verify that the user is authorized for the operation. Currently the system uses basic authentication over an
unencrypted pipe, so the data is not secure. Data can be secured by changing communication to use basic auth over SSL,
which is considered a reasonable approach for sending REST data securely. This will become the default setup as 
SiteWhere nears a 1.0 release.

SiteWhere includes a working version of `Swagger <http://swagger.wordnik.com/>`_ which adds a user interface around
the REST services. Using the Swagger interface, you can interactively execute REST calls against a running SiteWhere
server and view the JSON responses. The default Swagger URL for a server is:

	*http://sitewhere.hostname/sitewhere/*
	
where **sitewhere.hostname** is the hostname of the server running SiteWhere.

Administrative Application
--------------------------
SiteWhere includes an HTML5 administrative application that can be used to set up system data to allow it to process
information from devices. The application offers the following features:

:Manage site information:
	*Sites* are a coarse-grained constructs used for grouping related devices. They are very useful for location-based
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
	to end the assignment so that the device can be reassigned to another asset. For each *device assignment*,
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

