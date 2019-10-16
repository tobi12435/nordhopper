# Customized Routing Solution for Norderstedt

Berechnet den sicheren Schulweg!

## GraphHopper Routing Engine

GraphHopper is a fast and memory efficient Java routing engine, released under Apache License 2.0.
By default it uses OpenStreetMap and GTFS data, but it can import other data sources.

See [this website](https://github.com/graphhopper/graphhopper) more information

# Data

 * street data from OpenStreetMap
 * tree and lit data from [Norderstedt](https://github.com/hackerstolz/smart-country-hacks-challenges/tree/master/cities/norderstedt)
 * crash data from [Unfallatlas](https://unfallatlas.statistikportal.de/_opendata2019.html)
 * site data from Stadt Norderstedt. Should be updated frequently and regularly 
 
Integrating new data should be simple as long as it is in GeoJSON. See [this commit](https://github.com/karussell/nordhopper/commit/b4c1592ed5dce0ecc26c56a514a5e4e8613cfc9d) that shows integration for the crash data.

# Installation

```
git clone https://github.com/tobi12435/schoolhoppernorderstedt/
# Note: This is a fork of https://github.com/karussell/nordhopper/
cd schoolhoppernorderstedt
git checkout nordhopper
mvn clean install -DskipTests=true
java -Xms1g -Xmx1g -Dgraphhopper.datareader.file=data/norderstedt.osm.gz -Dgraphhopper.graph.location=graph-cache -Dgraphhopper.lit.location=data/Lampen_Convert.json -Dgraphhopper.tree.location=data/c07_Baeume_convert.json -Dgraphhopper.crash.location=data/unfall.json --Dgraphhopper.site.location=data/baustellen.json jar web/target/graphhopper-web-0.12-SNAPSHOT.jar server config-example.yml
```

