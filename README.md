# README

This repository contains the code for the backend of the Helioviewer Timeline Project.

## Getting started

### Prerequisites
- [JDK 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) and [set JAVA_HOME](https://www.bing.com/search?q=set+JAVA_HOME) to its installation folder.
- [Gradle](https://gradle.org)

### Instalation
1. Open a terminal, PowerShell, ... and execute `git clone https://github.com/stby4/helioviewer-visualisation.git heliovis-back`.
2. Install the [.editorconfig plugin](http://editorconfig.org) for your preffered editor (we recommend [VSCode](https://code.visualstudio.com) or [IntelliJ](https://www.jetbrains.com/idea/)).
3. Open the `heliovis-back` directory with your editor.
4. Execute `gradle runImporter` in your terminal, PowerShell, ... to create your database for the first time.

## Projects
<table>
<thead>
<tr>
<th>Project folder</th>
<th>Content</th>
</tr>
</thead>
<tbody>
<tr>
<th>api</th>
<td>Web servlet for a data API to the database.</td>
</tr>
<tr>
<th>config</th>
<td>Stores global settings, e.g. path to database.</td>
</tr>
<tr>
<th>database</th>
<td>Handles access to the memory mapped files, offers read / write access.</td>
</tr>
<tr>
<th>importer</th>
<td>Downloads files with X-ray flux data from various sources, updates database.</td>
</tr>
<tr>
<th>api</th>
<td>Web servlet for a data API to the database.</td>
</tr>
</tbody>
</table>

## Gradle commands
- `gradle runImporter` starts the download of all required data from the NOAA servers. This might take several hours. Only needs to be executed once.
- `gradle runUpdater` updates the database.
- `gradle appRunDebug` starts an [Jetty](http://www.eclipse.org/jetty/) server for the API.
- Alternatively, `gradle tomcatRunDebug` starts a [Tomcat](http://tomcat.apache.org/) server for the API.
- `gradle api:war` builds _/api/build/libs/api.war_ for deployment on a server.

## Debugging with gretty
After starting a server with `gradle appRunDebug`, you can listen on communicate with the server on port 5005. Use the VS Code debugger and the `Debug (Attach)` launch configuration to start debugging.

## API
### Request
Example request: http://localhost:8080/api/?from=1009843200000&to=1528927023999&points=2926

<table>
<thead>
<tr>
<th>Parameter</th>
<th>Description</th>
<th>Valid values</th>
</tr>
</thead>
<tbody>
<tr>
<th>from</th>
<td>Sets the start date for the time series.</td>
<td>UNIX timestamp in milliseconds, any time</td>
</tr>
<tr>
<th>to</th>
<td>Sets the end date for the time series.</td>
<td>UNIX timestamp in milliseconds, any time larger than "from".</td>
</tr>
<tr>
<th>
points
</th>
<td>
Sets the amount of returned data points. This is only a maximum number, the amount of actually returned data points can be lower.
</td>
<td>
Any integer value larger than 0.
</td>
</tr>
</tbody>
</table>

### Response
The response is json formatted:
``` json
[
    ["timestamp in milliseconds", "flux value in Watts/m^2"],
    ...
]
```