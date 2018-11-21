# Customized Routing Solution for Norderstedt

Berechnet den sicheren Schulweg!

## GraphHopper Routing Engine

GraphHopper is a fast and memory efficient Java routing engine, released under Apache License 2.0.
By default it uses OpenStreetMap and GTFS data, but it can import other data sources.

See [this website](https://github.com/graphhopper/graphhopper) more information

# Data

 * street data from OpenStreetMap
 * tree and lit data from [Norderstedt](https://github.com/hackerstolz/smart-country-hacks-challenges/tree/master/cities/norderstedt)
 * crash data from [Unfallatlas](https://unfallatlas.statistikportal.de/_opendata.html)
 
Integrating new data should be simple as long as it is in GeoJSON. See [this commit](https://github.com/karussell/nordhopper/commit/b4c1592ed5dce0ecc26c56a514a5e4e8613cfc9d) that shows integration for the crash data.

# Installation

```
git clone https://github.com/karussell/nordhopper/
cd nordhopper
# currently only the test branch works out of the box
git checkout test
mvn clean install -DskipTests=true
java -Xms1g -Xmx1g -Dgraphhopper.datareader.file=data/norderstedt.osm.gz -Dgraphhopper.graph.location=graph-cache -Dgraphhopper.lit.location=data/Lampen_Convert.json -Dgraphhopper.tree.location=data/c07_Baeume_convert.json -Dgraphhopper.crash.location=data/unfall.json -jar web/target/graphhopper-web-0.12-SNAPSHOT.jar server config-example.yml
```

Append the following to the UI

```
&debug=true&details=crash&instructions=false
```

to show the crash data via the PathDetails, see [this commit](https://github.com/karussell/nordhopper/commit/ab841e659868045d1033ebc4699cf2d1180fb4ac) that makes this possible.

Get fresh map data from OpenStreetMap, e.g. download [the PBF file here](http://download.geofabrik.de/europe/germany/schleswig-holstein.html).
