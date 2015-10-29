Schedule Batch Command Operation Based on Criteria
--------------------------------------------------
Schedules a batch command invocation operation that targets all devices which meet
the given criteria. Criteria can include any combination of:

* Devices that implement a given specification
* Devices created within a date range
* Devices that belong to a device group
* Devices that belong to device groups with a given role
* Devices that do not have an active assignment

The list of devices is calculated based on the criteria and all matching devices
become part of the batch command invocation operation. Since this operation is
executed on a schedule, the list of matching elements may vary from on execution
to the next based on whether devices meet the criteria at that point in time.