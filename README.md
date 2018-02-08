[![Build Status](https://travis-ci.org/sitewhere/sitewhere.svg?branch=master)](https://travis-ci.org/sitewhere/sitewhere)

![SiteWhere](https://s3.amazonaws.com/sitewhere-demo/sitewhere-github.png)

---

## NOTE: SiteWhere 2.0 EA1 is an early access release of the new architecture and is considered an "alpha" quality preview. This release is not intended for production use! Some of the core functionality will change significantly before the 2.0 GA release

SiteWhere is an industrial-strength open source IoT Application Enablement Platform 
that facilitates the ingestion, storage, processing, and integration of device data 
at massive scale. The platform is based on a modern microservices architecture and has 
been designed from the ground up for reliable, high throughput, low latency processing
and dynamic scalability. SiteWhere takes advantage of proven technologies such as
Apache Kafka and Docker in order to scale efficiently to the loads expected in large IoT
projects. Rather than using a monolithic architecture, SiteWhere embraces a completely 
distributed approach using microservices to allow scaling at the component level so 
that the system may be tailored to the customer use case.

![SiteWhere Administration](http://sitewhere.io/docs/en/2.0.EA1/_images/vue-user-interface.png "SiteWhere Administration")

The SiteWhere microservices are built with a framework approach using clearly defined
APIs so that new technologies can easily be integrated as the IoT ecosystem
evolves. The remainder of this document covers the core technologies used by 
SiteWhere and how they fit together to build a comprehensive system.

## Microservices
SiteWhere 2.0 introduces a much different architectural approach than was used
in the 1.x platform. While the core APIs are mostly unchanged, the system implementation
has moved from a monolithic approach to one based on microservices. 

![SiteWhere Architecture](http://sitewhere.io/docs/en/2.0.EA1/_images/microservices-diagram.png "SiteWhere 2.0 Architecture")

This approach provides a number of advantages over the previous architecture.

### Separation of Concerns
Each microservice is a completely self-contained entity that has its
own unique configuration schema, internal components, data persistence,
and interactions with the event processing pipeline. SiteWhere microservices
are built on top of a custom microservice framework and run as separate
[Spring Boot](https://projects.spring.io/spring-boot) processes, each
contained in its own [Docker](https://www.docker.com) image.

Separating the system logic into microservices allows the interactions
between various areas of the system to be more clearly defined. This
transition has already resulted in a more understandable and maintainable
system and should continue to pay dividends as more features are added.

### Scale What You Need. Leave Out What You Don't
The microservice architecture allows individual functional areas of the system to be scaled
independently or left out completely. In use cases where REST processing tends to
be a bottleneck, multiple REST microservices can be run concurrently to handle the load.
Conversely, services such as presence management that may not be required can be left
out so that processing power can be dedicated to other aspects of the system.

## Instance Management
The 2.0 architecture introduces the concept of a SiteWhere *instance*, which
allows the distributed system to act as a cohesive unit with some aspects
addressed at the global level. All of the microservices for a single SiteWhere
instance must be running on the same Docker infrastucture, though the system
can be spread across tens or hundreds of machines using technologies such as
[Docker Swarm](https://github.com/docker/swarm) or [Kubernetes](https://kubernetes.io).

## Configuration Management with Apache ZooKeeper
SiteWhere 2.0 moves system configuration from the filesystem into
[Apache ZooKeeper](https://zookeeper.apache.org) to allow for a centralized,
coordinated approach to configuration management. ZooKeeper contains a
hierarchical structure which represents the configuration for a SiteWhere instance
and all of the microservices that are used to realize it.

Each microservice has a direct connection to ZooKeeper and uses the
hierarchy to determine information such as the instance it belongs to
and the configuration it should use. Microservices listen for changes to
the configuration data and react dynamically to updates. No configuration
is stored locally within the microservice, which prevents problems with
keeping services in sync as system configuration is updated.

## Event Processing with Apache Kafka
The event processing pipeline in SiteWhere 2.0 has been completely redesigned and uses
[Apache Kafka](https://kafka.apache.org) to provide a resilient, high-performance
mechanism for progressively processing device event data. Each microservice plugs into
key points in the event processing pipeline, reading data from well-known inbound topics,
processing data, then sending data to well-known outbound topics. External entites that
are interested in data at any point in the pipeline can act as consumers of the SiteWhere
topics to use the data as it move through the system.

In the SiteWhere 1.x architecture, the pipeline for outbound processing used a blocking
approach which meant that any single outbound processor could block the outbound pipeline.
In SiteWhere 2.0, each outbound consumer is a true Kafka consumer with its own offset 
marker into the event stream. This mechanism allows for outbound processors to process data
at their own pace without slowing down other processors.

Using Kafka also has other advantages that are leveraged by SiteWhere. Since all data for 
the distributed log is stored on disk, it is possible to "replay" the event stream based 
on previously gathered data. This is extremely valuable for aspects such as debugging
processing logic or load testing the system.

## Inter-Microservice Communication with GRPC
While device event data generally flows in a pipeline from microservice to microservice on
Kafka topics, there are some operations that need to occur directly between microservices.
For instance, device management and event management persistence are each contained in
separate microservices, so as new events come in to the system, the inbound processing microservice
has to connect with the event persistence microservice to store the events. SiteWhere 2.0
uses [GRPC](https://grpc.io/) to establish a long-lived connection between microservices
that need to communicate with each other. Since GRPC uses persistent HTTP2 connections,
the overhead for interactions is greatly reduced, allowing for decoupling without a
significant performance penalty.

The entire SiteWhere data model has been captured in 
[Google Protocol Buffers](https://developers.google.com/protocol-buffers/) format so that
it can be used within GRPC services. All of the SiteWhere APIs are now exposed directly as
GRPC services as well, allowing for high-performance, low-latency access to what was previously
only accessible via REST. The REST APIs are still made available via the Web/REST microservice,
but they use the GRPC APIs underneath to provide a consistent approach to accessing data.

Since the number of instances of a given microservice can change over time as the service is
scaled up or down, SiteWhere automatically handles the process of connecting/disconnecting the 
GRPC pipes between microservices. Each outbound GRPC client is demulitplexed across the pool 
of services that can satisfy the requests, allowing the requests to be processed in parallel.

## Distributed Multitenancy
The SiteWhere 1.x approach to multitenancy was to use a separate "tenant engine" for each tenant.
The engine supported all tenant-specific tasks such as data persistence, event processing, etc.
Since SiteWhere 2.0 has moved to a microservices architecture, the multitenant model has been
distributed as well. SiteWhere supports two types of microservices: global and multitenant.

### Global Microservices
Global microservices do not handle tenant-specific tasks. These services handle aspects such
as instance-wide user management and tenant management that are not specific to individual
system tenants. The Web/REST microservice that supports the administrative application and 
REST services is a global service, since supporting a separate web container for each tenant
would be cumbersome and would break existing SiteWhere 1.x applications. There is also a 
global instance management microservice that monitors various aspects of the entire instance
and reports updates to the individual microservces via Kafka.

### Multitenant Microservices
Most of the SiteWhere 2.0 services are multitenant microservices which delegate traffic
to tenant engines that do the actual processing. For instance, the inbound processing microservice
actually consists of many inbound processing tenant engines, each of which is configured separately 
and can be started/stopped/reconfigured without affecting other tenant engines.

The new approach to tenant engines changes the dynamics of SiteWhere event processing. It is now
possible to stop a single tenant engine without the need for stopping tenant engines running in 
other microservices. For instance, inbound processing for a tenant can be stopped 
and reconfigured while the rest of the tenant pipeline continues processing. 
Since new events can be allowed to stack up in Kafka, the tenant engine can be stopped, reconfigured,
and restarted, then resume where it left off with no data loss.

## Release Documentation
More documentation for this "early access" release can be found [here](http://sitewhere.io/docs/en/2.0.EA1/index.html).

* * * *

Copyright (c) 2009-2018 [SiteWhere LLC](http://www.sitewhere.com). All rights reserved.
