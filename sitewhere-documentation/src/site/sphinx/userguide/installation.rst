============
Installation
============
SiteWhere provides many options for installation depending on user requirements. There are options
for running SiteWhere in the cloud, as a local server, installed within an existing Tomcat instance,
or running as a virtual machine.

--------------------
Running in the Cloud
--------------------

Amazon EC2 and Microsoft Azure
------------------------------
Probably the easiest method of getting started with SiteWhere is to spin up a pre-configured cloud 
instance on your favorite cloud provider. SiteWhere images are currently available for 
`Amazon EC2 <http://aws.amazon.com/ec2/>`_ and `Microsoft Azure <http://azure.microsoft.com/en-us/>`_.
The images include SiteWhere server with a MongoDB database which is populated with sample data. 
They also includes the free edition of HiveMQ MQTT broker and an installation of Apache Solr
configured to handle SiteWhere analytics. Detailed instructions are available
`here <../cloud.html>`_

Docker Image
------------
SiteWhere is also available as a Docker image which may be downloaded from
`Docker Hub <https://registry.hub.docker.com/u/sitewhere/sitewhere/>`_. The SiteWhere image
may be executed on any cloud platform that implements a Docker engine.

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
configured as a web archive (WAR). The WAR includes all of the required libraries so it may be
installed on an existing Tomcat instance by copying it to the **webapps** folder. Note
that the WAR does not include the SiteWhere configuration files, so they must be copied into the
Tomcat configuration directory. The default configuration files are available on
`GitHub <https://github.com/sitewhere/sitewhere/tree/master/sitewhere-core/config>`_.

Installing the Database
-----------------------
SiteWhere currently offers two options for data storage: MongoDB and Apache HBase. You will need to install
one of them in order for SiteWhere to be able to store and search for device data. In general, MongoDB is
the right choice for a local install that does not require true "big data" scalability. The default 
SiteWhere configuration is set up for a local MongoDB instance using the default settings. 

Production installations with larger data requirements will need to use an Apache HBase cluster to support scalability.
Rather than setting up an HBase cluster from scratch, it is often easier to use one from a provider such
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
6) Install an MQTT broker if you are using MQTT event sources.

After completing the above steps, start your Tomcat instance. There will be detailed output in the log file indicating
that SiteWhere has been loaded and properly configured. At this point SiteWhere should behave as if installed as a 
standalone server.
