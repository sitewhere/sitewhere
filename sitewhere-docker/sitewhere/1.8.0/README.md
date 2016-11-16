# SiteWhere 1.8.0 CE Docker Image
This image includes SiteWhere CE without any external dependencies. The 
instance will need to be configured to connect to other containers
that provide data persistence and broker communication protocols.
Based on the default configuration, a MongoDB container and HiveMQ
(or other MQTT broker) container should be configured separately.

## Generate a Docker Image
Execute the following command to create a local Docker image:

```
docker build -t sitewhere/sitewhere:1.8.0 .
```

## Start the Docker Image
After downloading the dependencies, an image will be created in your
local repository. To run the image from the command line execute:

```
docker run --name sitewhere -p 80:8080 -p5701:5701 sitewhere/sitewhere:1.8.0
```

## Edit the Configuration
Before starting the SiteWhere server, it must be configured. By default
the server looks for a local MongoDB instance. Create a separate
MongoDB container and point the global configuration 
(conf/sitewhere-server.xml) to access it.

## Start the Server
Run the startup command at the following location to start the server.

```
bin/startup.sh
```
