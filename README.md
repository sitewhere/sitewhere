![SiteWhere] (http://www.sitewhere.org/sites/default/files/sitewhere_0.png)

SiteWhere Open Source M2M Integration Platform
----------------------------------------------

#### SiteWhere is an open source platform for storing, processing, and analyzing M2M device data. ####

### Installing a Packaged Version

Most of the functionality provided by SiteWhere is contained in a web archive (WAR) which is installed on a customized version of [Tomcat] (http://tomcat.apache.org/). To download the latest version of SiteWhere server (including the latest WAR) check out the [downloads] (http://www.sitewhere.org/downloads) page on [sitewhere.org] (http://www.sitewhere.org/).

### Building from Source
If you want to customize SiteWhere or otherwise have a need to build it from source code, use the following steps.

#### Required Tools #####
* [Apache Maven] (http://maven.apache.org/)
* A [GIT] (http://git-scm.com/) client

#### Clone and Build #####
Clone this repository locally using:

    git clone https://github.com/reveal-technologies/sitewhere.git
    
Navigate to the newly created directory and execute:

    mvn clean install

After the build completes, a file named **sitewhere.war** should have been created in the **deploy** folder. This archive can be copied to the sitewhere server **webapps** directory to execute the updated code.

* * * *

Copyright (c) 2014, [Reveal Technologies](http://www.reveal-tech.com), LLC. All rights reserved.
