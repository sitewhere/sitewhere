# SiteWhere 1.4.0 CE Standalone Docker Image
This image includes a complete self-contained installation of SiteWhere 1.4.0, 
HiveMQ 3.0.2, and MongoDB. In most real-world scenarios, the compoents would
be running in different containers, but this arrangement allows for a 
simple one-command install.

## Generate a Docker Image
Execute the following command to create a local Docker image:

```
docker build -t sitewhere/standalone:1.4.0 .
```

## Start the Docker Image
After downloading the dependencies, an image will be created in your
local repository. To run the image from the command line execute:

```
docker run -p 80:8080 -p 61623:61623 sitewhere/standalone:1.4.0
```

The image will start HiveMQ, wait for MongoDB to become available, then will start
the SiteWhere instance. The instance is configured to load sample data
automatically.

## Open the Administrative Application
Once SiteWhere has started, navigate to the following URL to open the
administrative application:

```
http://your.server.name/sitewhere/admin
```
