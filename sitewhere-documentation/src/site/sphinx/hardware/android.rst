=========================================
 SiteWhere Android Integration Libraries
=========================================
SiteWhere supports rich integration with Google's `Android <http://www.android.com/>`_ platform 
including full round-trip device interactions over MQTT. The SiteWhere Android libraries 
include a sample wizard for setting up connectivity by establishing a remote SiteWhere server 
and MQTT broker to use.

--------------------------------------------------
 Getting Started with a SiteWhere Android Project
--------------------------------------------------
The SiteWhere Android libraries are available on GitHub and have been released under the
following path:

	https://github.com/reveal-technologies/sitewhere-android
	
Android in SiteWhere is divided into three separate projects:

	:SiteWhereFrameworkLibrary:
		Library with base framework code for creating a SiteWhere Android application. It contains
		the base interfaces and implementations that provide an abstracted view of SiteWhere
		interactions.
	:SiteWhereMQTTLibrary:
		Library with code specific to communicating with SiteWhere over MQTT. It uses the 
		`Fuse MQTT Client <http://mqtt-client.fusesource.org/>`_ as a means to interact with
		an MQTT broker, sending and receiving SiteWhere messages. It also contains the base
		connectivity wizard which provides an interface for configuring the remote SiteWhere
		host and MQTT broker information.
	:SiteWhereExample:
		An Android application project that uses the other two library projects to create a 
		sample application that sends sensor data to SiteWhere. It provides an example of 
		using the other libraries to create an application with a connectivity setup wizard,
		dynamic device registration, and event reporting based on real-time sensor readings.