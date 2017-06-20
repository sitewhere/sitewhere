![SiteWhere](https://s3.amazonaws.com/sitewhere-demo/sitewhere-medium.png)

#### SiteWhere is an open source platform for capturing, storing, integrating, and analyzing data from IoT devices. ####
SiteWhere is a server application and framework used to develop applications for the Internet of Things. 
The core server uses the [Spring Boot](http://projects.spring.io/spring-boot/) architecture and provides
the following list of features:

* Support for multiple tenants with separate data storage and processing pipelines
* Device management including specifications, device groups, asset assignment, and much more
* Device connectivity via JSON, MQTT, AMQP, and most other common protocols
* Big data storage for device event data with support for MongoDB, HBase, and InfluxDB
* Configurable event-processing pipline with support for alerting, scripting, and other advanced functions
* Integration with Apache Sprark, Apache Solr, Mule Anypoint, Amazon SQS, Azure EventHubs, and many others

### Installing a Packaged Version
To download the latest version of SiteWhere server check out the [downloads] (http://www.sitewhere.org/downloads) page on [sitewhere.org] (http://www.sitewhere.org/).

### Building from Source
If you want to customize SiteWhere or otherwise have a need to build it from source code, use the following steps.

#### Required Tools #####
* [Gradle] (http://gradle.org/)
* A [GIT] (http://git-scm.com/) client

#### Clone and Build #####
Clone this repository locally using:

    git clone https://github.com/sitewhere/sitewhere.git
    
Navigate to the newly created directory and execute:

    gradle clean serverZip **For Windows**
    gradle clean serverTar **For Unix**

After the build completes, a file named **sitewhere-server-x.x.x.zip/tar** will have been created in the 
**build/distributions** folder. This archive is the equivalent of the archive that can be downloaded from
the website. It can be installed by unzipping into a folder and running the startup script in the **bin**
folder.

SiteWhere Complete Install for Ubuntu
-------------------------------------

    sudo su
    apt-get install -y software-properties-common
    add-apt-repository ppa:openjdk-r/ppa
    apt-get update -y
    apt-get install -y unzip wget openjdk-8-jdk

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
    wget --content-disposition https://s3.amazonaws.com/sitewhere-hivemq/hivemq-3.0.2.zip
    unzip hivemq-3.0.2.zip
    cd hivemq-3.0.2/bin
    ./run.sh &

###Install Sitewhere Release Version

> Download a SiteWhere server release from the sitewhere.org website

    cd /opt
    wget https://s3.amazonaws.com/sitewhere/sitewhere-server-1.9.0.tgz
    tar -zxvf sitewhere-server-1.9.0.tgz
    mv sitewhere-server-1.9.0 /opt/sitewhere
    export SITEWHERE_HOME=/opt/sitewhere
    cd /opt/sitewhere/bin
    ./startup.sh

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

Copyright (c) 2009-2017, [SiteWhere LLC](http://www.sitewhere.com). All rights reserved.
