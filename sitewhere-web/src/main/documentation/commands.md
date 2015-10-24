Device Commands
===============
Device specification commands specify methods SiteWhere can use to interact with 
a given hardware configuration. A device specification command has a unique name, 
a namespace, and zero or more parameters. Commands are intended to work in much 
the same way as calling an RPC method in a programming language. The command 
name and a list of strongly-typed parameters are encoded and sent to the device 
which interprets them and executes the corresponding logic. Each parameter has a name (which 
must be unique within the command), a type, and a flag indicating whether it is required.

**NOTE: These methods will be refactored into the device specifications REST service
as of SiteWhere 1.3.0. All other methods related to device specification commands are
already in that service.**
