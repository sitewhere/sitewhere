Refresh the List of Asset Modules
---------------------------------
Refreshes the list of asset modules. This includes querying the list
of asset categories and reloading them from the database into asset 
modules. This also includes calling the *refresh()* method on each
external module, which generally forces a reload of data from the
underlying datastore.