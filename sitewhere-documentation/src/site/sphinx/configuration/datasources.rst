==========================
 Datastore Configuration
==========================
SiteWhere can use either `MongoDB <http://www.mongodb.org/>`_ or `Apache HBase <https://hbase.apache.org/>`_ for 
underlying data storage. For small installations where extreme scalability is not needed, it is much quicker and 
easier to use MongoDB. For true "big data" applications, the HBase backend is the better choice. 

-------------------------------
Configuring a MongoDB Datastore
-------------------------------
To use MongoDB as the backing datastore, edit the **${SITEWHERE_HOME}/conf/sitewhere/sitewhere-server.xml** file 
and uncomment the MongoDB import statement while leaving the HBase import statement commented as shown below:

.. code-block:: xml
   :emphasize-lines: 6, 9

	<!-- #################################### -->
	<!-- # SERVICE PROVIDER IMPLEMENTATIONS # -->
	<!-- #################################### -->

	<!-- Leave uncommented to use MongoDB datastore -->
	<import resource="sitewhere-mongodb.xml"/>

	<!-- Uncomment to use HBase datastore -->
	<!-- <import resource="sitewhere-hbase.xml"/> -->

The imported file **sitewhere-mongodb.xml** contains all of the configuration settings for the MongoDB instance.
Of particular importance are the Mongo client settings which are used for accessing the Mongo instance. The 
default settings are shown below:

.. code-block:: xml
   :emphasize-lines: 7-9

	<!-- ############################ -->
	<!-- # MONGO DATASTORE SETTINGS # -->
	<!-- ############################ -->

	<!-- Mongo client shared by SiteWhere Mongo components -->
	<bean id="mongo" class="com.sitewhere.mongodb.SiteWhereMongoClient">
		<property name="hostname" value="localhost"/>
		<property name="port" value="27017"/>
		<property name="databaseName" value="sitewhere"/>
	</bean>

Note that the default settings assume a local Mongo instance running on the default port and using a database
named **sitewhere**.

------------------------------
Configuring an HBase Datastore
------------------------------
To use Apache HBase as the backing datastore, edit the **${SITEWHERE_HOME}/conf/sitewhere/sitewhere-server.xml** file 
and uncomment the HBase import statement while leaving the MongoDB import statement commented as shown below:

.. code-block:: xml
   :emphasize-lines: 6, 9

	<!-- #################################### -->
	<!-- # SERVICE PROVIDER IMPLEMENTATIONS # -->
	<!-- #################################### -->

	<!-- Leave uncommented to use MongoDB datastore -->
	<!-- <import resource="sitewhere-mongodb.xml"/> -->

	<!-- Uncomment to use HBase datastore -->
	<import resource="sitewhere-hbase.xml"/>

The imported file **sitewhere-hbase.xml** contains all of the configuration settings for the HBase instance. It
imports **sitewhere-hbase-clients.xml** via the stanza below:

.. code-block:: xml
   :emphasize-lines: 6

	<!-- ############################ -->
	<!-- # HBASE DATASTORE SETTINGS # -->
	<!-- ############################ -->

	<!-- HBase connectivity configuration -->
	<import resource="sitewhere-hbase-clients.xml"/>
	
This Hbase clients file contains stanzas suited to particular HBase flavors. The default configuration will work
with most recent versions of HBase and is configured as shown below:

.. code-block:: xml
   :emphasize-lines: 7

	<!-- ############################### -->
	<!-- # HBASE CLIENT CONFIGURATIONS # -->
	<!-- ############################### -->

	<!-- HBase client for standard HBase distributions -->
	<bean id="hbase" class="com.sitewhere.hbase.DefaultHBaseClient">
		<property name="quorum" value="192.168.32.129"/>
	</bean>

Note that you will need to update the quorum address so that SiteWhere can locate your HBase cluster.

----------------------
Populating Sample Data
----------------------
In both MongoDB and HBase installations, SiteWhere will automatically create the underlying database if it does 
not already exist. After that, each time that SiteWhere server starts up, it will check whether there is data 
in the database and if not, will offer to populate the database with sample data (for non-console startup, 
there are properties on the model initializers in the main configuration file that allow you to specify whether 
to populate the sample data). There is sample data for both the user model and device model. It is usually a 
good choice to allow the user model to be populated since a valid user and permissions are required to log 
into the management application. Populating the sample device data gives a nice starting point for understanding 
SiteWhere in the context of a real application.