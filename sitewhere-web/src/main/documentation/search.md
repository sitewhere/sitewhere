External Search Providers
-------------------------
SiteWhere supports an abstracted view of external search engines that operate on 
SiteWhere data. External search providers allow the SiteWhere REST services to take 
advantage of features particular to the underlying search engine while still returning 
results in a predictable format. For instance, the Solr external search provider 
allows a user to pass a Solr query string as part of the REST call, taking advantage of 
powerful Solr features while returning a result set in the same format SiteWhere uses 
for searches on its core datastores. This approach allows SiteWhere to enrich the 
result data if necessary, and presents a single view of the data whether stored in 
SiteWhere or indexed in an engine optimized for adhoc queries.