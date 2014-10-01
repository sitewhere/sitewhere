==================================
 SiteWhere Administrative Console
==================================
SiteWhere is distributed with an HTML5 administrative console application that provides an
easy way to create and maintain SiteWhere data without having to do everything via the REST 
services. The administrative console (often refered to as the admin console or admin ui) is
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
page. For an existing site, click the edit icon at the right side of the site entry.
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

