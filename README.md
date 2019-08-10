[![Build Status](https://travis-ci.org/sitewhere/sitewhere.svg?branch=master)](https://travis-ci.org/sitewhere/sitewhere) 
[![Docker Pulls](https://img.shields.io/docker/pulls/sitewhere/service-web-rest.svg?label=Docker%20Pulls&style=flat-square)](https://hub.docker.com/u/sitewhere) 

![SiteWhere](https://s3.amazonaws.com/sitewhere-branding/SiteWhereLogo.svg)

---

SiteWhere is an industrial-strength open source IoT Application Enablement Platform 
that facilitates the ingestion, storage, processing, and integration of device data 
at massive scale. The platform has been designed from the ground up to take advantage of the latest
technologies in order to scale efficiently to the loads expected in large IoT
projects. 

![SiteWhere Electron App](https://s3.amazonaws.com/sitewhere-web/github-readme/vue-user-interface.png "SiteWhere Electron Application")

SiteWhere embraces a completely distributed architecture using [Kubernetes](https://kubernetes.io/)
as the infrastructure and a variety of microservices to build out the system.
This approach allows customization and scaling at a fine-grained level
so that the system may be tailored to many potential IoT use cases. SiteWhere
is built with a framework approach using clearly defined APIs so that new
technologies may easily be integrated as the IoT ecosystem evolves.

## Kubernetes

SiteWhere is composed of Java-based microservices which are built as
[Docker](https://www.docker.com/) images and deployed to Kubernetes for
orchestration. To simplify deployement, [Helm](https://helm.sh/) is used to
provide standard templates for various deployment scenarios. Helm
[charts](https://github.com/sitewhere/sitewhere-recipes/tree/master/charts)
are provided which supply all of the dependencies needed to run a complete
SiteWhere instance, including both the microservices and infrastructure
components such as Apache Zookeeper, Kafka, Mosquitto MQTT broker,
and other supporting technologies.

## Microservices

SiteWhere 2.0 introduces a much different architectural approach than was used
in the 1.x platform. While the core APIs are mostly unchanged, the system implementation
has moved from a monolithic structure to one based on microservices. This approach
provides a number of advantages over the previous architecture.

![SiteWhere Architecture](http://sitewhere.io/docs/en/2.0.EA1/_images/microservices-diagram.png "SiteWhere 2.0 Architecture")

### Separation of Concerns

Each microservice is a completely self-contained entity that has its
own configuration schema, internal components, data persistence, and
interactions with the event processing pipeline. SiteWhere microservices
are built on top of a custom microservice framework and run as separate
[Spring Boot](https://projects.spring.io/spring-boot/) processes, each
contained in its own [Docker](https://www.docker.com/) image.

Separating the system logic into microservices allows the interactions
between various areas of the system to be more clearly defined. This
transition has resulted in a more understandable and maintainable
system and should continue to pay dividends as more features are added.

### Scale What You Need. Leave Out What You Don't

The microservice architecture allows individual functional areas of the system to be scaled
independently or left out completely. In use cases where REST processing tends to
be a bottleneck, multiple REST microservices can be run concurrently to handle the load.
Conversely, services such as presence management that may not be required can be left
out so that processing power can be dedicated to other aspects of the system.

## Instance Management

The 2.0 architecture introduces the concept of a SiteWhere _instance_, which
allows the distributed system to act as a cohesive unit with some aspects
addressed at the global level. All of the microservices for a single SiteWhere
instance must be running on the same Kubernetes infrastucture, though the system
may be spread across tens or hundreds of machines to distribute the processing
load.

### Centralized Configuration Management with Apache ZooKeeper

SiteWhere 2.0 moves system configuration from the filesystem into
[Apache ZooKeeper](https://zookeeper.apache.org/) to allow for a centralized
approach to configuration management. ZooKeeper contains a hierarchical structure
which represents the configuration for one or more SiteWhere instances
and all of the microservices that are used to realize them.

Each microservice has a direct connection to ZooKeeper and uses the
hierarchy to determine its configuration at runtime. Microservices listen for changes
to the configuration data and react dynamically to updates. No configuration
is stored locally within the microservice, which prevents problems with
keeping services in sync as system configuration is updated.

### Distributed Storage with Rook.io

Since many of the system components such as Zookeeper, Kafka, and various
databases require access to persistent storage, SiteWhere 2.0 uses
[Rook.io](https://rook.io/) within Kubernetes to supply distributed,
replicated block storage that is resilient to hardware failures while
still offering good performance characteristics. As storage and throughput
needs increase over time, new storage devices can be made available
dynamically. The underlying [Ceph](https://ceph.com/) architecture
used by Rook.io can handle _exobytes_ of data while allowing data
to be resilient to failures at the node, rack, or even datacenter level.

### Service Discovery with HashiCorp Consul

With the dynamic nature of the microservices architecture, it is imporant
for microservices to be able to efficiently locate running instances of
the various other services they interact with. SiteWhere 2.0 leverages
[Consul](https://www.consul.io/) for service discovery. Each microservice
registers with Consul and provides a steady stream of updates to the
(potentially replicated) central store. As instances of microservices are
added or removed, SiteWhere dynamically adjusts connectivity to take
advantage of the available resources.

## High Performance Data Processing Pipeline

The event processing pipeline in SiteWhere 2.0 has been completely redesigned and uses
[Apache Kafka](https://kafka.apache.org/) to provide a resilient, high-performance
mechanism for progressively processing device event data. Microservices can plug in to
key points in the event processing pipeline, reading data from well-known inbound topics,
processing data, then sending data to well-known outbound topics. External entites that
are interested in data at any point in the pipeline can act as consumers of the SiteWhere
topics to use the data as it moves through the system.

### Fully Asynchronous Pipeline Processing

In the SiteWhere 1.x architecture, the pipeline for outbound processing used a blocking
approach which meant that any single outbound processor could block the outbound pipeline.
In SiteWhere 2.0, each outbound connector is a true Kafka consumer with its own offset
marker into the event stream. This mechanism allows for outbound processors to process data
at their own pace without slowing down other processors. It also allows services to
leverage Kafka's consumer groups to distribute load across multiple consumers and
scale processing accordingly.

Using Kafka also has other advantages that are leveraged by SiteWhere. Since all data for
the distributed log is stored on disk, it is possible to "replay" the event stream based
on previously gathered data. This is extremely valuable for aspects such as debugging
processing logic or load testing the system.

## Persistent API Connectivity Between Microservices

While device event data generally flows in a pipeline from microservice to microservice on
Kafka topics, there are also API operations that need to occur in real time between the
microservices. For instance, device management and event management functions are contained in
separate microservices, so as new events come in to the system, the inbound processing microservice
needs to interact with device management to look up existing devices in the system and event
management in order to persist the events to a datastore such as
[Apache Cassandra](http://cassandra.apache.org/).

### Using gRPC for a Performance Boost

Rather than solely using REST services based on HTTP 1.x, which tend to have significant
connection overhead, SiteWhere 2.0 uses [gRPC](https://grpc.io/) to establish a long-lived
connection between microservices that need to communicate with each other. Since gRPC uses
persistent HTTP2 connections, the overhead for interactions is greatly reduced, allowing
for decoupling without a significant performance penalty.

The entire SiteWhere data model has been captured in
[Google Protocol Buffers](https://developers.google.com/protocol-buffers/) format so that
it can be used within GRPC services. All of the SiteWhere APIs are now exposed directly as
gRPC services as well, allowing for high-performance, low-latency access to what was previously
only accessible via REST. The REST APIs are still made available via the Web/REST microservice,
but they use the gRPC APIs underneath to provide a consistent approach to accessing data.

Since the number of instances of a given microservice can change over time as the service is
scaled up or down, SiteWhere automatically handles the process of connecting/disconnecting the
gRPC pipes between microservices. Each outbound gRPC client is demulitplexed across the pool
of services that can satisfy the requests, allowing the requests to be processed in parallel.

## Distributed Multitenancy

The SiteWhere 1.x approach to multitenancy was to use a separate "tenant engine" for each tenant.
The engine supported all tenant-specific tasks such as data persistence, event processing, etc.
Since SiteWhere 2.0 has moved to a microservices architecture, the multitenant model has been
distributed as well. SiteWhere supports two types of microservices: global and multitenant.

### Global Microservices

Global microservices do not handle tenant-specific tasks. These services handle aspects such
as instance-wide user management and tenant management that are not specific to individual
system tenants. The Web/REST microservice that supports the REST services and Swagger user
interface is also a global service, since supporting a separate web container for each tenant
would be cumbersome and would break existing SiteWhere 1.x applications. There is also a
global instance management microservice that monitors various aspects of the entire instance
and reports updates to the individual microservces via Kafka.

### Multitenant Microservices

Most of the SiteWhere 2.0 services are multitenant microservices which delegate traffic
to tenant engines that do the actual processing. For instance, the inbound processing microservice
actually consists of many inbound processing tenant engines, each of which is configured separately
and can be started/stopped/reconfigured without affecting the other tenant engines.

The new approach to tenant engines changes the dynamics of SiteWhere event processing. It is now
possible to stop a single tenant engine without the need for stopping tenant engines running in
other microservices. For instance, inbound processing for a tenant can be stopped
and reconfigured while the rest of the tenant pipeline continues processing. Since new
events can be allowed to stack up in Kafka, the tenant engine can be stopped, reconfigured,
and restarted, then resume where it left off with no data loss.


* * * *

Copyright (c) 2009-2018 [SiteWhere LLC](http://www.sitewhere.com). All rights reserved.
