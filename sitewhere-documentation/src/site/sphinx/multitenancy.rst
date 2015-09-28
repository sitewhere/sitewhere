======================
Multitienancy Overview
======================
As of version 1.2.0, SiteWhere implements an advanced multiple tenant architecture that
allows many concurrent IoT applications to be deployed on a single SiteWhere instance
and managed independently. The architecture is designed to ensure maximum separation
of tenants while allowing server resources to be fully utilized.

----------------------
Backward Compatibility
----------------------
For users running pre-1.2.0 SiteWhere versions, a new installation will be required
to upgrade to 1.2.0. The SiteWhere team generally attempts to preserve backward
compatibility between versions, but the architectural changes required for 
multitenancy were significant.

-------
Tenants
-------
Before SiteWhere 1.2.0, the system was, in effect, limited to a single tenant. Even with
support for dividing devices among multiple sites, there was only a single pool of shared data
with no separation of information or processing duties. This forced the model of mapping
a single SiteWhere instance per customer, which was a limiting factor in many deployments.

The multitenant processing model allows for any number of tenants to be added to an 
instance and associated with system users. The administrative console allows permitted 
users to create new tenants and manage existing tenants. Each tenant represents an independent
processing subsystem that does not affect other tenants running concurrently in the
same SiteWhere instance.

--------------------
Tenant Configuration
--------------------
Each SiteWhere tenant has a separate configuration file that acts as an extension of the
global configuration file found in **conf/sitewhere/sitewhere-server.xml**. Spring application 
context inheritance is used to accomplish this, so beans in the global configuration are
available to the tenant configurations as well.

A default tenant is created as part of the initial bootstrap process for a SiteWhere server.
It is created with tenant id **default** and is configured by editing the
**conf/sitewhere/default-tenant.xml** file. The default configuration contents are copied
from the **conf/sitewhere/sitewhere-tenant.xml** file. In general, the first time any new
tenant is started, SiteWhere looks for an **conf/sitewhere/xxx-tenant.xml** file and, if
not found, creates it based on the **sitewhere-tenant.xml**.

-----------
Tenant Data
-----------
Separation of data is a key concern when dealing with a multitenant architecture. In many cases,
implementors will simply add a new tenant id field to their existing tables and update queries
to narrow results by tenant. This approach intermingles data and introduces security concerns.

SiteWhere takes the approach of completely separating tenant data. In the MongoDB SPI implementation,
each tenant has its own database, complete with all of the tenant-specific tables. In the HBase
implementation, a separate group of tables are used for each tenant. There is never a case where
data from two tenants resides in the same space.

--------------
Tenant Engines
--------------
SiteWhere 1.2.0 introduces the concept of **tenant engines** which are dedicated
processing engines for each system tenant. When the core server starts, it examines the list
of tenants, resolves their configuration files, then attempts to start each one in
its own dedicated engine. If one engine fails to start, the others will still be
started independently.

From within the SiteWhere administrative interface, the list of existing tenants can be
browsed and managed at runtime. A tenant can be started or stopped directly from the user
interface. Configuration of individual tenants is still handled on the file system, but a 
tenant can be stopped, reconfigured, and restarted without taking down the server and other
tenants.
