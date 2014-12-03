============
Installation
============
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
Starting with version 1.0.0, SiteWhere may be installed as a web archive in an existing Tomcat
instance rather than requiring the full server installation. Versions prior to 1.0.0 required
a Tomcat installation with a custom classpath since most dependencies were not stored in the
web archive.

To build and install SiteWhere:

1) Clone SiteWhere Community Edition from *https://github.com/sitewhere/sitewhere.git*.
2) Install Maven and execute *mvn clean install* at the root of the project.
3) Copy *deploy/sitewhere.war* (generated from the Maven build) into your Tomcat *webapps* directory.
4) Copy the SiteWhere configuration files from *sitewhere-core/config*
   (*https://github.com/sitewhere/sitewhere/tree/master/sitewhere-core/config*) 
   into the *conf* folder of your Tomcat installation.
5) Install the database as outlined above.

After completing the above steps, start your Tomcat instance. There will be detailed output in the log file indicating
that SiteWhere has been loaded and properly configured. At this point SiteWhere should behave as if installed as a 
standalone server.

----------------------------
Installing a Virtual Machine
----------------------------
Beginning with the 0.9.7 release, SiteWhere is available as a VMware virtual machine. Download the VM from
the `downloads <http://www.sitewhere.org/downloads>`_ page on the community site and unzip the archive. From the VMware
workstation interface, choose **Open a Virtual Machine** and navigate to the location where the archive was unzipped.

.. image:: /_static/images/userguide/vmware-open.png
   :width: 100%
   :alt: Open a VMware Virtual Machine
   :align: left

The virtual machine will be added to your library and is ready to use. Power on the virtual machine and, after 
the initial boot sequence, a login screen should appear as shown below. Choose the **SiteWhere** user and enter 
**admin** for the password.

.. image:: /_static/images/userguide/vm-login.png
   :width: 60%
   :alt: Log in to the Virtual Machine
   :align: center

Using the Virtual Machine
-------------------------
The VM is pre-configured with SiteWhere Community Edition, a MongoDB database instance, a HiveMQ MQTT broker,
and Apache Solr for handling analytics.