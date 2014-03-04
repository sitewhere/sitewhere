=========================
 SiteWhere Configuration
=========================
SiteWhere uses a hierarchy of `Spring <http://projects.spring.io/spring-framework/>`_ XML files as
its configuration mechanism. When the SiteWhere server starts, one of the first steps is to bootstrap
the core system components by loading the **${SITEWHERE_HOME}/conf/sitewhere/sitewhere-server.xml** file.
Depending on which features are needed, the administrator may choose to comment/uncomment sections of the file
to enable/disable various pieces of the system. To prevent clutter, many features have their own separate XML 
files that may be imported if they are needed.

.. toctree::
   :maxdepth: 3

   configuration/datasources
