## Paged Results
Many SiteWhere REST operations can result in a response that contains a very large
number of entities. When dealing with large domains, result sets can contain
thousands or millions of results. Rather than returning all results, SiteWhere
employs a consistent paging scheme for all list-oriented methods. Two parameters
are passed for any method that uses paging:

- **page** - The page number in the result set. This is used in conjunction with
the page size to calculate the starting record to return. The default page number
is **1** which points to the beginning of the result set.
- **pageSize** - The number of records that is considered a page. The default
page size is **100** records.

Clients that use the rest service can weigh out which page size makes sense for
the specific application. Small page sizes result in faster calls with less data,
but require more calls to process the result set. Large page sizes result in 
slower calls with more data, but are more efficient in terms of HTTP overhead
and total network traffic.