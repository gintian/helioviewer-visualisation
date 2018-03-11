# README #

This repository contains the code for the entire i4Ds05 Project.

## Getting started ##

1. Download [http://openjdk.java.net/projects/jdk9/](http://openjdk.java.net/projects/jdk9/) and [set JAVA_HOME](www.bing.com/search?q=set+JAVA_HOME) to its installation folder.
2. Open a Terminal, Powershell, ... and execute `git clone https://github.com/stby4/helioviewer-visualisation.git heliovis`.
3. Install the [.editorconfig plugin](http://editorconfig.org) for your preffered editor (as long as it is [VSCode](https://code.visualstudio.com) or [IntelliJ](https://www.jetbrains.com/idea/)).
4. Open the `heliovis` directory with your editor.
5. Start the `timelines.importer.Importer` to create your database for the first time.

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
