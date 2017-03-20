Open Geoportal Harvester
========================

[![Build Status](https://travis-ci.org/OpenGeoportal/ogpHarvester.png?branch=master)](https://travis-ci.org/OpenGeoportal/ogpHarvester)

The Open Geoportal Harvester is an open source web application that provides the automation of customized Solr harvesting from intuitional partner Solr, Geonetwork, CSW metadata nodes as well as direct metadata XML files within a web directory. As a result, an organization can create a custom OGP or Geonetwork instance including only selected records and criteria. The OGP Harvester supports a variety of metadata formats. It also has an administrative section to manager and register new node services.

In this branch, we add support for ingesting data into GeoServer, using the [Data-Ingest API](https://github.com/OpenGeoportal/Data-Ingest).

Development Environment
-----------------------
This application has a few dependencies on other services:
* PostgreSQL
* SOLR
* Data Ingest Service

If you want to take advantage of the **containerized development environment**, which runs and orchestrates these services, clone and run [this docker composition](https://github.com/OpenGeoportal/Data-Ingest/docker); more information is available in the accompanying [README](https://github.com/OpenGeoportal/Data-Ingest/docker/README.md) file.

The ogpharvester is already configured to work seamlessly with these services, and therefore no further setup is required. On the other hand, if you need to setup the ogpharvester, to work with a different solr instance or PostgreSQL database, be sure to review the configuration settings on `jdbc.properties` and `localSolr.properties`.
