=========================================
 SiteWhere Android Integration Libraries
=========================================
SiteWhere supports rich integration with Google's `Android <http://www.android.com/>`_ platform 
including full round-trip device interactions over MQTT. The SiteWhere Android libraries 
include a sample wizard for setting up connectivity by establishing a remote SiteWhere server 
and MQTT broker to use.

------------------------------------------
 Locating the SiteWhere Android Libraries
------------------------------------------
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

.. warning:: Make sure that you are using a version of the libraries that is compatible with your 
			SiteWhere instance. The *master* branch always contains the latest stable code, but
			there is a branch in the repository for each corresponding SiteWhere release.
            		
------------------------------------------------------
 Getting Started with a New SiteWhere Android Project
------------------------------------------------------
If you have not already done so, download and install the 
`Android ADT Bundle <http://developer.android.com/sdk/installing/bundle.html>`_ which includes
the Android Eclipse development environment and SDK. The Android documentation includes detailed
instructions on getting your development environment installed and configured for developing
applications.

Create Project Using New Project Wizard
---------------------------------------
Once the development environment is installed, a new project should be created for your
SiteWhere Android application. Click **File > New > Android Application Project** to open 
the *New Android Application* wizard as shown below:

.. image:: /_static/images/hardware/android/new-application.png
   :width: 100%
   :alt: New Android Application Wizard
   :align: left

Enter a project name and package name for your application, then choose the other settings as appropriate
for your application and click **Next**.

.. warning:: The minimum required SDK for SiteWhere Android libraries is **API 11: Android 3.0 (Honeycomb)**.
			The intent is to release future versions to support compatibility with older runtimes when time
			allows.
			
On the next page of the wizard, uncheck the *Create custom launcher icon* and *Create activity* checkboxes.

.. image:: /_static/images/hardware/android/new-application-2.png
   :width: 100%
   :alt: New Android Application Wizard
   :align: left

Click **Finish** to create the new application. The Eclipse workspace should show the *Java* perspective 
with the newly created project displayed in the *Package Explorer* pane. You may have to close the 
welcome screen or manually change over to the *Java* perspective. The screen should look like the one
below:

.. image:: /_static/images/hardware/android/new-application-3.png
   :width: 100%
   :alt: New Android Project
   :align: left

Import SiteWhere Libraries from GitHub
--------------------------------------
The SiteWhere GitHub repositiory includes `library projects <http://developer.android.com/tools/projects/index.html#LibraryProjects>`_
that provide a framework for building Android applications. By importing the projects into your application, the
majority of the interactions with SiteWhere are taken care of so you can focus on the core application code.
To import the SiteWhere Android library projects, click **Import > Git > Projects from Git** and then click **Next**.
In the *Select Repository Source* panel, choose **Clone URI** and click **Next**. In the *Source Git Respository*
panel, enter the following URL: 

	https://github.com/reveal-technologies/sitewhere-android

The user interface should automatically fill in the other fields based on the URL as shown below:

.. image:: /_static/images/hardware/android/import-from-git.png
   :width: 100%
   :alt: Choose Git Respository
   :align: left

Click **Next** to continue and choose which branches you want to check out. The best choice 
is the branch that is the exact match for the SiteWhere instance it will be 
communicating with. Higher numbered branches should be backward compatible with 
older SiteWhere servers. Click **Next** to move to the next step in the wizard, the *Local Destination* panel.
You can usually leave the destination values as the defaults unless you have a different location you would 
rather store the imported repository. Click **Next** to move to the next step in the wizard, where you will
choose how to import the Eclipse projects contained in the repository. Choose **Import existing projects**, using
the default *Working Directory* (which should be the location specified in the *Local Destination* panel).
Click **Next** to move to the **Import Projects** panel. Choose to import the projects that end with **Library** 
as shown below:

.. image:: /_static/images/hardware/android/import-projects.png
   :width: 100%
   :alt: Import Projects
   :align: left

Click **Finish** to import the projects into your local Eclipse workspace.

Reference SiteWhere Libraries from New Project
----------------------------------------------
In order to use the SiteWhere Android libraries, your new project must include the libraries on its classpath.
Open the properties for the project you created earlier in *Package Explorer* and choose **Android**
in the list of sections on the left. At the bottom of the panel in the *Library* group, 
click the **Add...** button to add references to the SiteWhere libraries as shown below:

.. image:: /_static/images/hardware/android/reference-projects.png
   :width: 50%
   :alt: Add Project References
   :align: center

Click **OK** to apply the changes and your resulting workspace should look something like the image below. Note
that if you open the *project.properties* file, there are now references to the SiteWhere libraries.

.. image:: /_static/images/hardware/android/after-reference-projects.png
   :width: 100%
   :alt: After Adding References
   :align: left

With the projects added, the SiteWhere Android framework elements may be used in your project.

Create Main Activity for Application
------------------------------------
When the Android application starts, a main activity is launched to present an interface to the user. 
The SiteWhere libraries provide classes that extend the base Android  
`Activity <http://developer.android.com/reference/android/app/Activity.html>`_ class and 
allow you to inherit many of the core interactions that a connected
device needs to communicate with SiteWhere. First, create a base Java package for the code by clicking 
the *src* folder and choosing **New > Package** from the menu. The package name should be the same as the
one specified when creating the project.

