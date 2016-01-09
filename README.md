# README #

This repository contains the code for the entire i4Ds05 Project.

## Getting started ##

Depending on which part of the application has to be set up / built, different packages are required.
Appart from the client, all parts of the application require the path to the database and cache directories to be specified withing resources/config/config.properties



### Initializing the Database ###
Main class: timelines.importer.Importer.java

The following packages are required:

* timelines.importer
* timelines.utils
* timelines.database
* timelines.config

### Initializing the Cache ###
Requires the database to be initialized.
Main class: timelines.renderer.CacheRenderer.java

The following packages are required:

* timelines.renderer
* timelines.utils
* timelines.database
* timelines.config

### Server Web Application ###
Requires an existing database and cache.

The following packages are required:

* timelines.api
* timelines.renderer
* timelines.utils
* timelines.database
* timelines.config

### Updater ###
Requires an existing database and cache.
Can be configured as a cron job for regular database and cache updates.

The following packages are required:

* timelines.importer
* timelines.renderer
* timelines.utils
* timelines.config
* timelines.database

### Client ###
Requires a running server with its URL specified in timelines.gui.Image.java
Main class: timelines.gui.TimeLinesViewer.java

The following packages are required by the client Application:

* timelines.gui
* timelines.utils