Create New Device Element Mapping
---------------------------------
Add a new mapping of a path within the device element schema to the hardware id
of a child device attached to that path. This method has the side effect of
setting up the reverse relationship with the parent id of the contained device
pointing back to the container device.

**This method only applies to devices that use a composite specification.**