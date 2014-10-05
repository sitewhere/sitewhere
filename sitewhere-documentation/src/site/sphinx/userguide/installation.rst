==============
 Installation
==============
SiteWhere provides many options for installation depending on user requirements. There are options
for running SiteWhere in the cloud, as a local server, installed within an existing Tomcat instance,
or running as a virtual machine.

--------------------
Running in the Cloud
--------------------
Probably the easiest method of getting started with SiteWhere is to spin up a pre-configured cloud 
instance on Amazon EC2. The instance includes SiteWhere server with a MongoDB database which comes
populated with sample data. It also includes HiveMQ MQTT broker and an installation of Apache Solr
configured to handle SiteWhere analytics. Detailed instructions are available
`here <../cloud.html>`_

------------------
Installing Locally
------------------
Sometimes it makes more sense to run SiteWhere on a local machine rather than running in the cloud.
Running locally allows more flexibility in configuring exactly the system needed to accomplish 
your goals. With increased flexibility comes a little added complexity since the dependencies
such as database, MQTT broker, and analytics engine must be configured manually.

Installing the Server
---------------------
SiteWhere server is available as a standalone download in the `downloads <http://www.sitewhere.org/downloads>`_
section of the community website. The server includes an Apache Tomcat instance with SiteWhere
configured as a web archive (WAR). Since not all libraries that SiteWhere depends on are packaged into
the WAR, the Tomcat installation includes extra folders for the library dependencies and extra classpath
entries to include the libraries on the classpath.

Installing the Database
-----------------------
SiteWhere currently offers two options for data storage: MongoDB and Apache HBase. You will need to install
one of them in order for SiteWhere to be able to store and search for device data. In general, MongoDB is
the right choice for a local install that does not require true "big data" scalability. The default 
SiteWhere configuration is set up for a local MongoDB instance using the default settings. 

Production installations with larger data requirements will need to use an Apache HBase cluster to support scalability.
Rather than setting up an HBase cluster from scratch, it is ofter easier to use one from a provider such
as `Cloudera <http://www.cloudera.com>`_ or `Hortonworks <http://hortonworks.com/>`_ that simplifies the process.

---------------------------------
Using an Existing Tomcat Instance
---------------------------------
Rather than installing the complete SiteWhere server bundle that includes its own Tomcat instance, SiteWhere may
also be installed into an existing Tomcat instance. First, download the server bundle for the version of SiteWhere
required and unzip/untar the archive to access its contents. SiteWhere requires additional libraries be added to
the standard classpath, so follow the next steps to add them:

1) Copy the **sitewhere** and **mule** folder hierarchies into the root folder of your Tomcat instance.
2) Overwrite (or append) the following line into the **catalina.properties** for the shared loader configuration:

.. code-block:: properties

  shared.loader=${catalina.home}/sitewhere/*.jar,${catalina.home}/sitewhere/hbase-default/*.jar,${catalina.home}/mule/mule/*.jar,${catalina.home}/mule/opt/*.jar,${catalina.home}/mule/shared/default/*.jar,${catalina.home}/mule/user/*.jar

3) Copy the SiteWhere configuration directory from **conf/sitewhere** in the server distro to the same location in your Tomcat installation.
4) Copy **webapps/sitewhere.war** in the server distro to the same location in your Tomcat installation.

After completing the above steps, start your Tomcat instance. There will be detailed output in the log file indicating
that SiteWhere has been loaded and properly configured. At this point SiteWhere should behave as if installed as a 
standalone server.

----------------------------
Installing a Virtual Machine
----------------------------
Beginning with the 0.9.7 release, SiteWhere is available as a VMware virtual machine. Download the virual machine from
the `downloads <http://www.sitewhere.org/downloads>`_ page on the community site and import it into your VMware installation.
