Device Assignments
==================
Events are not logged directly against devices, since a given device may serve in a number of contexts. 
For instance, a visitor badge may be assigned to a new person every day. Rather than intermingle event 
data from all the people a badge has been assigned to, the concept of a device assignment allows events 
to be associated with the asset they relate to. 

A device assignment is an association between a device and (optionally) a related asset. 
Some assignments do not specify an asset and are referred to as *unassociated*. A real-world example 
of this is a vending machine that has wireless connectivity to report inventory. The device *is* the 
asset, so there is no need to associate an external asset.