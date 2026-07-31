<div align="center">

<a href="https://devicechain.io">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="https://raw.githubusercontent.com/devicechain-io/devicechain/main/branding/logos/logo.svg">
    <img alt="DeviceChain" src="https://raw.githubusercontent.com/devicechain-io/devicechain/main/branding/logos/logo-light.svg" width="180">
  </picture>
</a>

# SiteWhere is now DeviceChain

**A ground-up rebuild in Go — the same domain model, without the stack that made it hard to run.**

<a href="https://devicechain.io"><strong>Website</strong></a> ·
<a href="https://docs.devicechain.io"><strong>Documentation</strong></a> ·
<a href="https://github.com/devicechain-io/devicechain"><strong>Source</strong></a> ·
<a href="https://github.com/devicechain-io/devicechain/discussions"><strong>Discussions</strong></a>

</div>

> [!IMPORTANT]
> **This repository is no longer maintained.** The last release was SiteWhere CE 3.0.5 in March 2021.
> It stays here, read-only, so existing deployments, forks and links keep working — but it is not
> receiving fixes. **[DeviceChain](https://devicechain.io) is where the work continues.**

## Why DeviceChain

DeviceChain keeps what SiteWhere got right — devices, types, assignments, the event pipeline — and
rebuilds everything underneath it.

|  | SiteWhere | DeviceChain |
| --- | --- | --- |
| **Runtime** | Java / Spring microservices on the JVM | Go single-binary services, sub-second startup |
| **Infrastructure** | Zookeeper, Kafka, MongoDB, InfluxDB, Cassandra, an MQTT broker | **NATS and TimescaleDB.** That is the whole list. |
| **Orchestration** | Kubernetes + Istio service mesh + Helm charts | A Kubernetes operator with one declarative CRD — no service mesh required |
| **API** | REST | GraphQL per area, with generated TypeScript and C#/.NET SDKs (AOT/IL2CPP-safe, so it works in Unity) |
| **Device model** | Device types and a fixed assignment hierarchy | Versioned device profiles, a typed relationship graph, and dynamic groups resolved from a CEL selector |

### And things SiteWhere never had

- **A deterministic streaming rule engine.** Threshold, duration, rate-of-change, absence, windowed
  aggregate and area-correlation rules compiled from CEL — and because every rule lowers to one
  compiler and one engine, you can **replay real history through a rule and watch what it would have
  done** before it governs anything.
- **Versioned dashboards.** Draft, publish, roll back. Build one against synthetic data before a
  single device exists, then point the same layout at a live fleet — or embed it in your own app
  through the widget runtime.
- **Modern edge protocols.** A stateful Sparkplug B host application and an LwM2M terminator over
  CoAP/DTLS, both of which *assert* device presence rather than inferring it from silence.
- **An edge agent** that buffers durably through a WAN outage and replays when the link returns.
- **Outbound connectors** — SSRF-hardened webhooks plus publish to MQTT, Kafka, SNS and SQS.
- **Rules in plain language.** Describe what you want and it drafts a candidate — then runs it
  through the *same* compiler as every hand-written rule. If it does not compile, it does not ship.
- **A door built for AI agents.** A read-only MCP server that carries the caller's own token, so an
  agent can never see more than the person operating it.

And it stays **Apache-2.0 end to end** — no open-core split and no per-device metering. Device
inventory, twin state, two-way command delivery, dashboards, multi-tenancy, high availability and
SSO are part of the platform rather than a paid tier.

## If you ran SiteWhere, we want to hear from you

DeviceChain is **pre-1.0 and in active development**. It is built and exercised in CI, and its
disaster-recovery and high-availability behaviour has been drilled against a live cluster — but it
has not yet been run by many people who did not write it.

That is the gap, and people who ran SiteWhere are the best possible ones to close it: you already
know where this kind of platform tends to break. Installing it and telling us where you got stuck is
genuinely the most useful thing anyone can do for it right now — and interfaces can still change in
response to what you find, which stops being true at 1.0.

**[Start with the docs](https://docs.devicechain.io)** · **[Tell us what broke](https://docs.devicechain.io/getting-help)**

---

<sub>The original SiteWhere README follows, unchanged, for reference.</sub>

---

[![Build Status](https://travis-ci.org/sitewhere/sitewhere.svg?branch=master)](https://travis-ci.org/sitewhere/sitewhere) 
[![Docker Pulls](https://img.shields.io/docker/pulls/sitewhere/service-web-rest.svg?label=Docker%20Pulls&style=flat-square)](https://hub.docker.com/u/sitewhere) 

![SiteWhere](https://s3.amazonaws.com/sitewhere-branding/SiteWhereLogo.svg)

---

SiteWhere is an industrial strength, open source IoT Application Enablement Platform 
which facilitates the ingestion, storage, processing, and integration of IoT device data 
at massive scale. The platform leverages a microservices architecture which runs on top of 
cutting-edge technologies such as [Kubernetes](https://kubernetes.io/), [Istio](https://istio.io), 
and [Kafka](https://kafka.apache.org/) in order to scale efficiently 
to the loads expected in large IoT projects. 

SiteWhere embraces a distributed architecture which runs on Kubernetes and provides 
both infrastructure such as highly-available databases and MQTT brokers as well as 
microservices to facilitate various aspects of IoT project development. The platform is 
built with a framework approach using clearly defined APIs so that new technologies may easily 
be integrated as the IoT ecosystem evolves.

![SiteWhere Administration](https://sitewhere-web.s3.amazonaws.com/github-readme/admin-ui-2.1.0.png "SiteWhere Administration")

## Deployment and Orchestration

SiteWhere is composed of Java-based microservices which are built as
[Docker](https://www.docker.com/) images and deployed to Kubernetes for
orchestration. To simplify installation and configuration, [Helm](https://helm.sh/) 
is used to provide standard templates for various deployment scenarios. Helm
[charts](https://github.com/sitewhere/sitewhere-recipes/tree/master/charts)
are provided to supply both the microservices and the dependencies needed to 
run a complete SiteWhere deployment. Infrastructure components include 
technologies such as Apache Zookeeper and Kafka, highly available databases such
as MongoDB, InfluxDB, and Cassandra, and other supporting technologies 
such as MQTT brokers.

## Microservices

Rather than using a monolithic approach, SiteWhere is based on many microservices
running as a distributed system. Each microservice is a completely self-contained 
entity that has its own configuration schema, internal components, data persistence, 
and interactions with the event processing pipeline. SiteWhere microservices
are built on top of a custom microservice framework and run as separate
[Spring Boot](https://projects.spring.io/spring-boot/) processes, each
contained in its own [Docker](https://www.docker.com/) image.

![SiteWhere Architecture](https://sitewhere-web.s3.amazonaws.com/github-readme/sitewhere-microservices.png "SiteWhere 2.0 Architecture")

### Separation of Concerns

Separating the system logic into microservices allows the interactions
between various areas of the system to be more clearly defined. It also allows
parts of the pipeline to be shutdown or fail gracefully without preventing other
parts of the system from functioning. The event processing pipeline, which spans
many of the microservices, is buffered by Kafka so that data processing has
strong delivery guarantees while maintaining high throughput.

### Scale What You Need. Leave Out What You Don't

The microservice architecture allows individual functional areas of the system to be scaled
independently or left out completely. In use cases where REST processing tends to
be a bottleneck, multiple REST microservices can be run concurrently to handle the load.
Conversely, services such as presence management that may not be required can be left
out so that processing power can be dedicated to other aspects of the system.

## Instance Management

SiteWhere supports the concept of an _instance_, which allows the distributed system 
to act as a cohesive unit with some aspects addressed at the global level. All of the 
microservices for a single SiteWhere instance must be running on the same Kubernetes 
infrastucture, though the system may be spread across tens or hundreds of machines 
to distribute the processing load.

### Service Mesh with Istio

SiteWhere leverages [Istio](https://istio.io/) to provide a service mesh for
the system microservices, allowing the platform to be scaled dynamically while 
also providing a great deal of control over how data is routed. Istio allows
modern methods such as canary testing and fault injection to be used to 
provide a more robust and fault-tolerant system. It also allows for detailed
monitoring and tracing of the data flowing through the components.

### Centralized Configuration Management with Apache ZooKeeper

SiteWhere configuration is stored in [Apache ZooKeeper](https://zookeeper.apache.org/) 
to allow for a scalable, externalized approach to configuration management. ZooKeeper 
contains a hierarchical structure which represents the configuration for one or more 
SiteWhere instances and all of the microservices that are used to realize them. The 
configuration is replicated for high availabilty.

Each microservice has a direct connection to ZooKeeper and uses the hierarchy to 
determine its configuration at runtime. Microservices listen for changes to the 
configuration data and react dynamically to updates. No configuration
is stored locally within the microservice, which prevents problems with
keeping services in sync as system configuration is updated.

### Distributed Storage with Rook.io

Since many of the system components such as Zookeeper, Kafka, and various
databases require access to persistent storage, SiteWhere uses
[Rook.io](https://rook.io/) within Kubernetes to supply distributed,
replicated block storage that is resilient to hardware failures while
still offering good performance characteristics. As storage and throughput
needs increase over time, new storage devices can be made available
dynamically. The underlying [Ceph](https://ceph.com/) architecture
used by Rook.io can handle _exobytes_ of data while allowing data
to be resilient to failures at the node, rack, or even datacenter level.

## High Performance Data Processing Pipeline

The event processing pipeline in SiteWhere uses [Apache Kafka](https://kafka.apache.org/) 
to provide a resilient, high-performance mechanism for progressively processing device 
event data. Microservices can plug in to key points in the event processing pipeline, 
reading data from well-known inbound topics, processing data, then sending data to well-known 
outbound topics. External entites that are interested in data at any point in the pipeline 
can act as consumers of the SiteWhere topics to use the data as it moves through the system.

### Fully Asynchronous Pipeline Processing

The SiteWhere event processing pipeline leverages Kafka's messaging constructs to allow
device event data to be processed asynchronously. If a microservice shuts down and no other 
replicas are available to process the load. The data will be queued until a replica starts
and begins processing again. This acts as a guarantee against data loss as data is always
backed by Kafka's high-performance storage. SiteWhere microservices leverage Kafka's consumer 
groups concept to distribute load across multiple consumers and scale processing accordingly.

Using Kafka also has other advantages that are leveraged by SiteWhere. Since all data in
the distributed log is stored on disk, it is possible to "replay" the event stream based
on previously gathered data. This is extremely valuable for aspects such as debugging
processing logic or load testing the system.

## API Connectivity Between Microservices

While device event data generally flows in a pipeline from microservice to microservice on
Kafka topics, there are also API operations that need to occur in real time between the
microservices. For instance, device management and event management functions are contained in
their own microservices, but are required by many other components of the system. Many of the
SiteWhere microservices offer APIs which may be accessed by other microservices to
support aspects such as storing persistent data or initiating microservice-specific
services.

### Using gRPC for a Performance Boost

Rather than solely using REST services based on HTTP 1.x, which tend to have significant
connection overhead, SiteWhere uses [gRPC](https://grpc.io/) to establish a long-lived
connection between microservices that need to communicate with each other. Since gRPC uses
persistent HTTP2 connections, the overhead for interactions is greatly reduced, allowing
for decoupling without a significant performance penalty. Istio also allows the gRPC
connections to be multiplexed across multiple replicas of a microservice to scale 
processing and offer redundancy.

The entire SiteWhere data model has been captured in
[Google Protocol Buffers](https://developers.google.com/protocol-buffers/) format so that
it can be used within GRPC services. All of the SiteWhere APIs are exposed directly as
gRPC services as well, allowing for high-performance, low-latency access to all API
functions. The REST APIs are still made available via the Web/REST microservice (acting
as an API gateway), but they use the gRPC APIs underneath to provide a consistent approach 
to accessing data.

## Multitenancy

SiteWhere is designed for large-scale IoT projects which may involve many system tenants
sharing a single SiteWhere instance. A key differentiator for SiteWhere compared to most
IoT platforms is that each tenant runs in isolation from other tenants. By default, tenants
do not share database resources or pipeline processing and have a completely separate 
configuration lifecycles. With this approach, each tenant may use its own database 
technologies, external integrations, and other configuration options. Parts of the tenant's
processing pipeline may be reconfigured/restarted without causing an interruption to 
other tenants.

### Data Privacy

An important consequence of the way SiteWhere handles multitenancy is that each tenant's 
data is separated from the data of other tenants. Most platforms that offer multitenancy
store data for all tenants in shared tables, differentiated only by a tenant id. The shared
approach opens up the possibility of one tenant's data corrupting another, which is not
an acceptable risk in many IoT deployments. In addition, each tenant has its own processing
pipelines, so in-flight data is never co-mingled either.

Having dedicated resources for tenants can be expensive in terms of memory and processing
resources, so SiteWhere also offers the concept of _customers_ within each tenant. Customers
allow data to be differentiated within a tenant, but without having a separate dedicated
database and pipelines. In cases where colocated data is acceptable, the tenant can have
any number of customers, which shared the same database and processing pipeline. This allows 
the best of both worlds in terms of security and scalability.

* * * *

Copyright (c) 2009-2019 [SiteWhere LLC](http://www.sitewhere.com). All rights reserved.
