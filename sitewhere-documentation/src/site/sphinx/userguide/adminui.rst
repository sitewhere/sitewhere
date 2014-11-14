======================
Administrative Console
======================
SiteWhere is distributed with an HTML5 administrative console application that provides an
easy way to create and maintain SiteWhere data without having to do everything via the REST 
services. The administrative console (often referred to as the admin console or admin ui) is
available as part of any standalone or cloud install of SiteWhere. In a default local installation
the admin console is accessible via:

	http://localhost:8080/sitewhere/admin/

.. contents:: Contents
   :local:

----------
Logging In
----------
	
Assuming SiteWhere is running at the given URL, the admin console login page will prompt
for credentials to log in.

.. image:: /_static/images/userguide/admin-login.png
   :width: 100%
   :alt: Administrative Console Login
   :align: left

In pre-1.0 versions of SiteWhere, user credentials are automatically filled in based
on the default data populated at system startup. The default username and password are

	:username: admin
	:password: password
	
The login credentials authenticate against the SiteWhere user model, which is populated
with a single default user account upon the first system startup. User accounts can be
added or updated by clicking on the **Users** tab at the top of the admin console application.

--------------
Managing Sites
--------------
Sites are top-level entities that represent physical 
areas in which devices are deployed. Depending on the system being modeled, sites may
represent very different things. For instance, a healthcare system may have a site for
each hospital in the system. A system for monitoring heavy equipment at a number of 
construction sites in a city may have a site for each. A system monitoring airport
personnel may have a separate site for each airport.

Site List
---------

The first page displayed after logging in to the SiteWhere admin console is the list of
sites configured in the system. The site list page has an entry for each site in 
the system:

.. image:: /_static/images/userguide/site-list.png
   :width: 100%
   :alt: Site List
   :align: left

Each entry has icons on the right side that allow the given site to be edited, deleted,
or opened.

Creating and Editing Sites
--------------------------
To create a new site, click on the **Add New Site** button at the top of the site list
page. To change an existing site, click the edit icon at the right side of the site entry.
The following sections cover information needed to create or edit a site.

Edit Site - Site Details Tab
****************************

The **Site Details** tab includes basic information about a site.

.. image:: /_static/images/userguide/site-edit-details.png
   :width: 100%
   :alt: Edit Site - Site Details
   :align: left

+----------------------+--------------------------------------------------------+
| Field                | Description                                            |
+======================+========================================================+
| Site Name            | Human-readable name that provides a short description  |
|                      | of the site.                                           |
+----------------------+--------------------------------------------------------+
| Site Description     | A longer description of the site which is shown in the |
|                      | sites list page and available via the REST services.   |
+----------------------+--------------------------------------------------------+
| Image URL            | URL that points to an image that is associated with    |
|                      | the site. This image is shown in the sites list page   |
|                      | and may be used by applications as a visual indicator  |
|                      | of which site is being shown.                          |
+----------------------+--------------------------------------------------------+

Edit Site - Map Information Tab
*******************************

Not all sites involve location data, so the map information is optional. 
For sites that do need to display geospatial data, the map association 
allows a given map, latitude, longitude, and zoom level to be 
associated with a site. When viewing location data for a site, the map specified for
the site is the default used for visualizations. 

Rather than manually typing the latitude, longitude, and zoom level for
a map, the information may be specified interactively by clicking
**Choose location and zoom on map**. This option presents a map which
can be manipulated via drag-and-drop to choose the default map settings.

.. image:: /_static/images/userguide/site-edit-map.png
   :width: 100%
   :alt: Edit Site - Map Information
   :align: left

+----------------------+--------------------------------------------------------+
| Field                | Description                                            |
+======================+========================================================+
| Map Type             | Allows the map visualization style to be chosen. The   |
|                      | list of available options currently includes           |
|                      | MapQuest for standard maps and GeoServer for floor     |
|                      | plans and other custom maps.                           |
+----------------------+--------------------------------------------------------+
| Center Latitude      | Latitude coordinate of the default map center.         |
+----------------------+--------------------------------------------------------+
| Center Longitude     | Longitude coordinate of the default map center.        |
+----------------------+--------------------------------------------------------+
| Zoom Level           | Zoom level setting as interpreted by the underlying    |
|                      | map technology.                                        |
+----------------------+--------------------------------------------------------+

.. image:: /_static/images/userguide/site-edit-map-set.png
   :width: 100%
   :alt: Edit Site - Interactive Map Selection
   :align: left

Edit Site - Metadata Tab
************************

As with most SiteWhere data, each site may have arbitrary metadata associated
with it. This allows sites to be tailored for application-specific functionality.
For instance, if a system requires an indicator for whether a given site
has wifi access or needs to store the phone number for contacting the site, this
information can be associated as metadata. The metadata can be used in device
event processing to take different actions based on site-specific settings.
For instance, if a site offers wifi access, devices can automatically be 
configured to use the access point.

.. image:: /_static/images/userguide/site-edit-metadata.png
   :width: 100%
   :alt: Edit Site - Metadata
   :align: left

Managing Sites - Site Details
-----------------------------
From the site list page, clicking on the green arrow to the right of an entry opens
the site detail page. This page shows the basic site information as well as a list
of all devices assigned to the site and all associated event information.

.. image:: /_static/images/userguide/site-details.png
   :width: 100%
   :alt: Site Details
   :align: left

Site Details - Assignments Tab
******************************
The **Assignments** tab contains a list of devices assigned to the site ordered with
the most recent assignments first. Clicking on any of the assignments will open
the assignment details page for the assignment.

Site Details - Locations Tab
****************************
The **Locations** tab contains a list of the most recent location measurements for 
devices assigned to the site.

Site Details - Measurements Tab
*******************************
The **Measurements** tab contains a list of the most recent measurement values for 
devices assigned to the site.

Site Details - Alerts Tab
*************************
The **Alerts** tab contains a list of the most recent alerts for 
devices assigned to the site.

Site Details - Zones Tab
************************
The **Zones** tab contains a list of the zones defined for the site. Zones define
geospatial areas that are of interest for a site. For more information on zones, 
see the next section.

Managing Sites - Zones
----------------------
Zones define geospatial areas that are of interest for a site. For instance, an
airport may have secure zones that define areas where only authorized agents are
allowed to enter. A site may have any number of zones and the zones may overlap.
A zone is defined by a list of lat/long pairs that define a polygon. Zones may
be large and complex, so there is no limit to the number of points that make up
the boundary. In addition to the boundaries, a border color, fill color, and fill
opacity are defined for each zone. This allows the appearance of zones to be 
tailored and presented on map views.

Creating a New Zone
*******************
To create a new zone, click on the **Zones** tab on the site details page and click
the **Add New Zone** button. The zone create dialog will appear as shown below:

.. image:: /_static/images/userguide/site-zone-create.png
   :width: 100%
   :alt: Create Zone
   :align: left
   
Enter a name for the zone into the text field at the bottom of the dialog and choose
the zone border color, fill color, and fill opacity. To add a polygon, click on the 
polygon tool in the upper right corner, then start clicking locations on the map to
add points. Click the first point added to complete the polygon. You can also create
a rectangle by clicking on the rectangle tool and dragging to size the area. Click
**Create** when you are satisfied and the zone will be created.

Editing an Existing Zone
************************

To change an existing zone, click the edit icon at the right side of the zone entry
and make the desired changes. Existing zone borders may be altered by clicking on 
the existing points and dragging them to new locations. New border points may be 
added by clicking between existing points to add a new point, then dragging it to
the desired location.

------------------------------
Managing Device Specifications
------------------------------
Device specifications represent unique hardware configurations which may be assigned to
devices. Two separate device specifications may use the same base platform, yet have enough
differences in hardware or software configuration to consider them separate when defining
devices. For instance, you may deploy devices with two configurations, one with
a standard LED and another with an RGB LED. In this case, it makes sense to create separate
specifications since a command **setColor()** would only make sense for the device with
an RGB LED.

Device Specifications List
--------------------------

Clicking on the **Specifications** tab in the navigation bar opens the device specifications
list page. All existing device specifications are listed in alphabetical order as shown below:

.. image:: /_static/images/userguide/spec-list.png
   :width: 100%
   :alt: Device Specification List
   :align: left

Each entry has icons on the right side that allow the given specification to be edited, deleted,
or opened.

Creating and Editing Device Specifications
------------------------------------------
To create a new specification, click on the **Add New Specification** button at the top of the list
page. To change an existing specification, click the edit icon at the right side of the entry.
The following sections cover information needed to create or edit a specification.

Edit Device Specification - Specification Details Tab
*****************************************************

The **Specification Details** tab includes basic information about a device specification.

.. image:: /_static/images/userguide/spec-edit-details.png
   :width: 100%
   :alt: Edit Device Specification - Specification Details
   :align: left

+----------------------+--------------------------------------------------------+
| Field                | Description                                            |
+======================+========================================================+
| Specficiation Name   | Human-readable name that provides a short description  |
|                      | of the device specification.                           |
+----------------------+--------------------------------------------------------+
| Specification Type   | Indicates if a specification is for a standalone       |
|                      | device or a composite device such as a gateway.        |
|                      | Composite devices contain nested devices that may      |
|                      | be addressed by sending messages to the parent         |
|                      | composite device.                                      |
+----------------------+--------------------------------------------------------+
| Asset Provider       | The asset provider that contains the asset definition  |
|                      | for the device.                                        |
+----------------------+--------------------------------------------------------+
| Device Type          | The asset definition as chosen from the list made      |
|                      | available from the chosen device provider. This        |
|                      | determines the physical hardware used by devices       |
|                      | referencing the specification.                         |
+----------------------+--------------------------------------------------------+

Edit Device Specification - Metadata Tab
****************************************
