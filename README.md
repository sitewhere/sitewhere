[![Build Status](https://travis-ci.org/sitewhere/sitewhere.svg?branch=sitewhere-1.4.0)](https://travis-ci.org/sitewhere/sitewhere)

![SiteWhere] (https://s3.amazonaws.com/sitewhere-demo/sitewhere-small.png)

The Open Platform for the Internet of Things™
-----------------------------------------------

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

    git clone https://github.com/sitewhere/sitewhere.git
    
Navigate to the newly created directory and execute:

    mvn clean install

After the build completes, a file named **sitewhere.war** will have been created in the **deploy** 
folder. 

#### Building a Full Server #####
Once the **sitewhere.war** file has been generated, you can create the full server distribution by using:

	mvn -P builderServer clean install
	
This will download a copy of Tomcat, copy the WAR to the webapps folder, and copy the default 
configuration files to the correct location. A zipped archive is generated and may be used 
as the packaged version downloaded from the SiteWhere.org website.

#### Copying into an Existing Tomcat Instance #####
Alternatively, the **sitewhere.war** archive can be copied to the **webapps** directory of an existing
Tomcat instance. The default SiteWhere loader expects configuration files to be available in the **TOMCAT/conf/sitewhere/** 
folder. Copy the files from [here] (https://github.com/sitewhere/sitewhere/tree/sitewhere-1.0.2/sitewhere-core/config/sitewhere) 
as a starting point. For more details, see the 
[installation guide] (http://docs.sitewhere.org/current/userguide/installation.html#using-an-existing-tomcat-instance).


SiteWhere Complete Install for Ubuntu
-------------------------------------

    sudo su
    apt-get update -y
    apt-get install unzip openjdk-7-jdk

###Install MongoDB

    sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 7F0CEB10
    echo 'deb http://downloads-distro.mongodb.org/repo/ubuntu-upstart dist 10gen' | sudo tee /etc/apt/sources.list.d/mongodb.list
    apt-get update
    apt-get install -y mongodb-org
    echo "mongodb-org hold" | sudo dpkg --set-selections
    echo "mongodb-org-server hold" | sudo dpkg --set-selections
    echo "mongodb-org-shell hold" | sudo dpkg --set-selections
    echo "mongodb-org-mongos hold" | sudo dpkg --set-selections
    echo "mongodb-org-tools hold" | sudo dpkg --set-selections
    service mongod start

###Install HiveMQ

    cd /opt
    wget --content-disposition http://www.hivemq.com/downloads/releases/latest
    unzip hivemq-2.2.1.zip
    cd hivemq-2.2.1/bin
    ./run.sh &

###Install Solr

> Optional if needed.

    cd ~
    wget http://archive.apache.org/dist/lucene/solr/4.7.2/solr-4.7.2.tgz
    tar -xvf solr-4.7.2.tgz
    cp -R solr-4.7.2/example /opt/solr
    export PWD=/opt/solr/webapps
    apt-get install rabbitmq-server -y
    cd /opt/sitewhere/bin
    ./startup.sh

###Install Sitewhere Release Version

> Download a SiteWhere server release from the sitewhere.org website

    cd /opt
    wget https://s3.amazonaws.com/sitewhere/sitewhere-server-1.0.1.tar.gz
    tar -zxvf sitewhere-server-1.0.1.tar.gz
    mv sitewhere-server-1.0.1 /opt/sitewhere
    sed -i -- 's/CATALINA_BASE/CATALINA_HOME/g' /opt/sitewhere/conf/sitewhere/sitewhere-server.xml
    export CATALINA_HOME=/opt/sitewhere
    cd /opt/sitewhere/bin
    sh startup.sh

> To build and install latest code from GitHub

    apt-get install maven git unzip -y
    apt-get install openjdk-7-jdk tomcat7 -y
    service tomcat7 stop
    git clone https://github.com/sitewhere/sitewhere.git
    cd sitewhere
    mvn clean install
    cp deploy/sitewhere $(YOUR_TOMCAT_PATH)/webapps/.
    cp -R sitewhere-core/config/* /var/lib/tomcat7/config
    service tomcat7 start

* * * *

Copyright (c) 2009-2015, [SiteWhere LLC](http://www.sitewhere.com). All rights reserved.
