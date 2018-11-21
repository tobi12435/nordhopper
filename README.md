# Customized Routing Solution for Norderstedt

Berechnet den sicheren Schulweg!

## GraphHopper Routing Engine

GraphHopper is a fast and memory efficient Java routing engine, released under Apache License 2.0.
By default it uses OpenStreetMap and GTFS data, but it can import other data sources.

See [this website](https://github.com/graphhopper/graphhopper) more information

# Commits

Alle commits vor dem 20.11.2018 sind bzgl. der generischen Open Source
Routing Engine, inkl. des merge-commits "Merge branch 'refactor_enc_manager' into
nordhopper", was nur das customizen vereinfacht. 
Dann am 20.11.2018 und später kommen Norderstedt spezifische Arbeiten die
für die Anpassungen notwendig sind um den besten Schulweg zu finden.

# Installation

```
git clone https://github.com/karussell/nordhopper/
cd nordhopper
mvn clean install -DskipTests=true
java -Xms1g -Xmx1g -Dgraphhopper.datareader.file=data/norderstedt.osm.gz -Dgraphhopper.graph.location=graph-cache -Dgraphhopper.lit.location=data/Lampen_Convert.json -Dgraphhopper.tree.location=data/c07_Baeume_convert.json -Dgraphhopper.crash.location=data/unfall.json -jar web/target/graphhopper-web-0.12-SNAPSHOT.jar server config-example.yml
```

Get fresh map data from OpenStreetMap, e.g. download [the PBF file here](http://download.geofabrik.de/europe/germany/schleswig-holstein.html).
