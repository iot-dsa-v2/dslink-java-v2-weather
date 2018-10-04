# dslink-java-v2-Weather

* Version: 1.0.0
* Java - version 1.6 and up.
* [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)


## Overview

Weather DSLink for to get meteorological conditions and in order to report an accurate forecast weather information for next couple of days over a particular City. DSLink is built in Java SDK v2. 

After installing and staring the link, get weather and forecast information by using "Create Tracker" action on the root node. After Invoke, City name, Forecast Nodes will show up as child nodes of the root node. Forecast node is a child node on the City node.
By clicking on City node can see the weather information in metric panel for mentioned city.

Forecast node can visible by expanding City node and provides forecast weather information on date wise for next couple of days.

If you are not familiar with DSA and links, an overview can be found at
[here](http://iot-dsa.org/get-started/how-dsa-works).

This link was built using the DSLink Java V2 SDK which can be found
[here](https://github.com/iot-dsa-v2/sdk-dslink-java-v2).


## Link Architecture

This section outlines the hierarchy of nodes defined by this link.

- _MainNode_ - The root node of the link, has an action to get the weather information by providing city and Units.
  - WeatherCityNode - A node representing a specific city weather details. Used to call Yahoo Weather API and process the obtained weather details.
  - ForecastNode - A node representing forecast weather details.Used to process the Forecast weather details.
  - DateWeatherDetails - A node representing a specific Date Weather Details. Used to display weather forecast information on  date basis. One new DateWeatherDetails node will be created for each date.


## Node Guide

The following section provides detailed descriptions of each node in the link as well as
descriptions of actions, values and child nodes.


### MainNode

This is the root node of the link. It has actions to get the weather details based on City and Units.

**Actions**
- CreateTracerAction - Get the Weather Details and add a WeatherCityNode to root node to represent it.
  - `City` - Required. City name.
  - `Unit` - Required. Units can be imperial or metric. Imperial is default.


**Child Nodes**
 - any _WeatherCityNode _ that have been added.

### WeatherCityNode

A node representing a specific city weather details.

**Actions**
- makeWeatherDetails  : Call yahoo weather API and process the request.
  - `City` : Required City name. This value is based on city which selected in CreaterTracker Action in main node.
  - `Unit` : Required This value is based on Units which selected in CreaterTracker 
- makeForecastNode : Creates Forecast Node to process Forecast weather details.
  - `JSONArray` : Required. This parameter contains all the weather forecast details.
  - `units_temperature`- : Required. Temperature details.
  
**Child Nodes**
 - ForecastNode _ that have been added.
 
### ForecastNode

A node representing forecast weather details.Used to process the Forecast weather details.

**Actions**
- forcastDetails  : Process Forecast weather details and creates a new DateWeatherDetails node for every date based on API results.
  - `JSONArray` : Required. This parameter contains all the weather forecast details.
  - `units_temperature`- : Required. Temperature details.
- makeRemoveAction  : Removes specific ForecastNode.

**Child Nodes**

 - DateWeatherDetails _ that have been added.
 
### DateWeatherDetails

A node representing a specific Date Weather Details. Used to display weather forecast information on  date basis. One new DateWeatherDetails node will be created for each date.

**Actions**
- dateWeatherDetails  : Display all the required weather details on metrics panel for that selected date.
- makeRemoveAction  : Removes specific DateWeatherDetails.

## Acknowledgements

SDK-DSLINK-JAVA

This software contains unmodified binary redistributions of 
[sdk-dslink-java-v2](https://github.com/iot-dsa-v2/sdk-dslink-java-v2), which is licensed 
and available under the Apache License 2.0. An original copy of the license agreement can be found 
at https://github.com/iot-dsa-v2/sdk-dslink-java-v2/blob/master/LICENSE

## History

* Version 1.0.0
  - First Release
