================================
 Running SiteWhere in the Cloud
================================
Running SiteWhere locally is not a terribly complicated process, but you still need to be able to
get the prequisite components such as MongoDB/HBase and Apache Solr installed. There is also some
preliminary configuration in Spring to make sure everything connects properly. It is often 
preferable to have an out-of-the-box system that comes configured with all the components and just
runs without configuration. Also, in production environments that run in the cloud, it is nice to
have a solid starting point from which to create a custom installation. The following cloud 
deployments of SiteWhere should help you get up and running quickly.

.. toctree::
   :maxdepth: 2

   cloud/amazon_ec2
