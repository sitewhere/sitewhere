Batch Operations
================
Batch operations are actions that operate on multiple devices, executing asynchronously 
and providing a mechanism for monitoring progress over time. Examples of batch operations 
include executing commands on a large number of devices or applying firmware updates to a 
group of devices. The batch operation manager is responsible for taking a batch operation 
request and breaking it out into the actions necessary to complete the goal. Since batch 
operations can result in a large load on the system, the batch operation manager allows 
for throttling the execution of operations so that a reasonable load is achieved when 
dealing with thousands or millions of devices.