Asset Management
================
SiteWhere assets represent objects in the physical world &ndash; **people**, **places**, and **things**. Device specification assets are used to describe the hardware information/configuration for a type of device. Device assignment assets are used to describe an entity associated with a device &ndash; a person associated with a badge, a bulldozer associated with a location tracker, or a hospital ward associated with a piece of hospital equipment.

Asset Modules
-------------
Rather than hard-coding a schema for assets in the system, SiteWhere defines SPIs for general asset types and allows **asset modules** to be plugged in to provide asset definitions. This allows existing identity management systems to be used in providing a list of available person assets. It also allows product catalog systems to be used in defining available hardware assets. Assets loaded from XML files or external systems are treated as read-only and can not be edited from within the administrative console.

Asset Categories
----------------
SiteWhere also provides the concept of **asset categories** which reside in the SiteWhere datastore. Asset categories are containers for assets of a given type and may be added/edited from within the administrative console. Asset categories are loaded as asset modules at runtime, allowing assets to be pulled from the datastore in addition to modules loaded from other sources such as XML files or third-party systems.

