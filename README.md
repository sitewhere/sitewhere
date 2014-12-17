![SiteWhere] (http://www.sitewhere.org/sites/default/files/sitewhere.png)

SiteWhere : The Open Platform for the Internet of Things™
---------------------------------------------------------

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

After the build completes, a file named **sitewhere.war** should have been created in the **deploy** folder. This archive can be copied to the sitewhere server **webapps** directory to execute the updated code. Note that the web archive has external dependencies and will not run on an unmodified
Tomcat instance, so using a compatible SiteWhere server release is the preferred approach. For more details, see the 
[installation guide] (http://docs.sitewhere.org/current/userguide/installation.html#using-an-existing-tomcat-instance).


Sitewhere Install for Ubuntu Server 14.04 64bit
-----------------------------------------------

    sudo su
    apt-get update -y

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

    wget --content-disposition http://www.hivemq.com/downloads/releases/latest
    unzip hivemq-2.1.1.zip
    cd hivemq-2.1.1/bin
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

    wget https://s3.amazonaws.com/sitewhere/sitewhere-server-1.0.0-rc2.tar.gz
    tar -zxvf sitewhere-server-1.0.0-rc2.tar.gz
    mv sitewhere-server-1.0.0-rc2 /opt/sitewhere
    cd /opt/sitewhere/bin
    sh startup.sh

> To build and install latest code from GitHub

    apt-get install maven git unzip -y
    apt-get install openjdk-7-jdk tomcat7 -y
    service tomcat7 stop
    git clone https://github.com/reveal-technologies/sitewhere.git
    cd sitewhere
    mvn clean install
    cp deploy/sitewhere $(YOUR_TOMCAT_PATH)/webapps/.
    cp -R sitewhere-core/config/* /var/lib/tomcat7/config
    service tomcat7 start

* * * *

Copyright (c) 2009-2014, [SiteWhere LLC](http://www.sitewhere.com). All rights reserved.
