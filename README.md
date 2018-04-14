# README

This repository contains the code for the backend of the Helioviewer Timeline Project.

## Getting started

### Prerequisites
- [JDK 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) and [set JAVA_HOME](https://www.bing.com/search?q=set+JAVA_HOME) to its installation folder.
- [Gradle](https://gradle.org)

### Instalation
1. Open a terminal, PowerShell, ... and execute `git clone https://github.com/stby4/helioviewer-visualisation.git heliovis-back`.
2. Install the [.editorconfig plugin](http://editorconfig.org) for your preffered editor (as long as it is [VSCode](https://code.visualstudio.com) or [IntelliJ](https://www.jetbrains.com/idea/)).
3. Open the `heliovis-back` directory with your editor.
4. Execute `gradle importer:runImporter` in your terminal, PowerShell, ... to create your database for the first time.

## Projects
TODO

## Gradle commands
- `gradle importer:runImporter` starts the download of all required data from the NOAA servers. This might take several hours. Only needs to be executed once.
- `gradle importer:runUpdater` updates the database.
- `gradle api:appRun` starts an [Jetty](http://www.eclipse.org/jetty/) server for the API.
- Alternatively, `gradle api:tomcatRun` starts a [Tomcat](http://tomcat.apache.org/) server for the API.
- `gradle api:war` builds a *.war file for deployment on a server.

## API
TODO
